package org.springframework.samples.petclinic.utility;

import net.minidev.asm.ex.NoSuchFieldException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetRepository;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
class PetTimedCacheTest {

	@Mock private PetRepository petRepository;
	@Mock private Pet pet;
	@Mock private Pet anotherPet;
	private ConcurrentHashMap timeMap = new ConcurrentHashMap();
//	private ConcurrentHashMap realActualMap = new ConcurrentHashMap();
//	@Mock private Date date;
	@Mock private ConcurrentHashMap actualMap;
	@Mock private PetTimedCache.CleanerThread cleanerThread;
	private long expiryInMillis = 10000L;



	public static void setField(String fieldName, Object value, Object testObj) throws IllegalAccessException, NoSuchFieldException, java.lang.NoSuchFieldException {
		Field field = testObj.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(testObj, value);
	}


	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getTestValidInputEmptyActualMapNullPet() throws NoSuchFieldException, IllegalAccessException, java.lang.NoSuchFieldException {
		PetTimedCache petTimedCache = new PetTimedCache(petRepository);
		setField("actualMap", actualMap, petTimedCache);

		Mockito.when(petRepository.findById(anyInt())).thenReturn(null);
		Mockito.when(actualMap.containsKey(anyInt())).thenReturn(false);

		petTimedCache.get(anyInt());

		Mockito.verify(actualMap, Mockito.times(1)).containsKey(anyInt());
		Mockito.verify(petRepository, Mockito.times(1)).findById(anyInt());
		Mockito.verify(actualMap, Mockito.times(0)).put(anyInt(), any(Pet.class));
	}

	@Test
	public void getTestValidInputEmptyActualMapNotNullPet() throws NoSuchFieldException, IllegalAccessException, java.lang.NoSuchFieldException {
		PetTimedCache petTimedCache = new PetTimedCache(petRepository);
		setField("actualMap", actualMap, petTimedCache);

		Mockito.when(petRepository.findById(anyInt())).thenReturn(pet);
		Mockito.when(actualMap.containsKey(anyInt())).thenReturn(false);

		petTimedCache.get(anyInt());

		Mockito.verify(actualMap, Mockito.times(1)).containsKey(anyInt());
		Mockito.verify(petRepository, Mockito.times(1)).findById(anyInt());
		Mockito.verify(actualMap, Mockito.times(1)).put(anyInt(), any(Pet.class));	//What do you think about it?
	}

	@Test
	public void getTestValidInputNotEmptyActualMap() throws NoSuchFieldException, IllegalAccessException, java.lang.NoSuchFieldException {
		PetTimedCache petTimedCache = new PetTimedCache(petRepository);
		setField("actualMap", actualMap, petTimedCache);

		Mockito.when(actualMap.get(anyInt())).thenReturn(pet);
		Mockito.when(actualMap.containsKey(anyInt())).thenReturn(true);

		petTimedCache.get(anyInt());

		Mockito.verify(actualMap, Mockito.times(1)).containsKey(anyInt());
		Mockito.verify(actualMap, Mockito.times(1)).get(anyInt());
	}


	@Test
	public void runTestFirstConstructor() throws NoSuchFieldException, IllegalAccessException, java.lang.NoSuchFieldException, InterruptedException {
		PetTimedCache petTimedCache = new PetTimedCache(petRepository);
		Mockito.when(actualMap.remove(anyInt())).thenReturn(pet).thenReturn(anotherPet);
		Mockito.doNothing().when(cleanerThread).run();
		setField("timeMap", timeMap, petTimedCache);
		setField("actualMap", actualMap, petTimedCache);
		timeMap.put(0, new Date().getTime());
		timeMap.put(1, new Date().getTime()-2000);
//		Mockito.when(timeMap.keySet().toArray()[0]).thenReturn(0).thenReturn(1).thenReturn(2).thenReturn(3);

		petTimedCache.initialize();
		Thread t = new Thread();
		t.sleep((expiryInMillis/2)+1);

		Mockito.verify(actualMap, Mockito.times(1)).remove(anyInt());
		ArgumentCaptor<Integer> key = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(actualMap).remove(key.capture());
		Assert.assertEquals(java.util.Optional.of(1), java.util.Optional.ofNullable(key.getValue()));
	}

	@Test
	public void runTestSecondConstructor() throws NoSuchFieldException, IllegalAccessException, java.lang.NoSuchFieldException, InterruptedException {
		expiryInMillis = 5000L;
		PetTimedCache petTimedCache = new PetTimedCache(expiryInMillis, petRepository);
		Mockito.when(actualMap.remove(anyInt())).thenReturn(pet).thenReturn(anotherPet);
		Mockito.doNothing().when(cleanerThread).run();
		setField("timeMap", timeMap, petTimedCache);
		setField("actualMap", actualMap, petTimedCache);
		timeMap.put(0, new Date().getTime()-500);
		timeMap.put(1, new Date().getTime()-1000);

		petTimedCache.initialize();
		Thread t = new Thread();
		t.sleep(12000L);

		ArgumentCaptor<Integer> key = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(actualMap, Mockito.times(2)).remove(key.capture());
		//
		System.out.println("key.getValue()");
		System.out.println(key.getValue());
		Assert.assertEquals(java.util.Optional.of(1), java.util.Optional.ofNullable(key.getValue()));
//		ArgumentCaptor<Integer> key1 = ArgumentCaptor.forClass(Integer.class);
//		Mockito.verify(actualMap).remove(key1.capture());
		System.out.println(key.getValue());
		Assert.assertEquals(java.util.Optional.of(0), java.util.Optional.ofNullable(key.getValue()));
	}

}
