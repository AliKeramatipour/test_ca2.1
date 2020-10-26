package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.springframework.samples.petclinic.utility.PetTimedCache;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;


class PetServiceTest {
	//رویکرد ما در dependency injection رویکرد mockisty می‌باشد.
	//از آنجا که تمامی dependecy injection ها توسط constructor ممکن هست، تزریق را هنگام ساخته شدن با کمک @InjectMocks انجام میدهیم.
	//همچنین در یک جا از classical برای پوشش مبحث استفاده شده که آن را مشخص کرده‌ایم.

	//Mock is used for 'OwnerRepository ownerRepository' since this DOC feeds an Indirect input to PetService
	@Mock private static OwnerRepository ownerRepository;
	//Mock is used for 'PetTimedCache pets', just like 'ownerRepository', 'pets' feeds an Indirect input to PetService in 'findPet' method
	//Also 'PetTimedCache' needs an injection on construction, so we do need to use Mock
	@Mock private static PetTimedCache pets;
	//Spy is used for 'Logger log' since only indirect Output is fed into log, using spy we can verify its behaviour.
	@Spy private static Logger criticalLogger;
	//Mock is used for 'Pet pet' since it feeds indirect input into PetService, e.g. when 'pet.getId()' is called
	@Mock private Pet pet;
	//Mock is used for 'Owner owner', same reasoning behind this decision as 'Pet pet'
	@Spy private Owner owner;

	@InjectMocks
	private PetService petService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	//---------------------------------- Null Inputs

	@Test
	//Behavior verification
	public void testNewPetNullInputBehavior() {
		try {
			petService.newPet(null);
			fail("null input was accepted in newPet");
		}catch (Exception e) {
			Mockito.verify(criticalLogger, Mockito.times(0)).info(anyString(),anyInt());
		}
	}

	@Test
	//State verification
	public void testNewPetNullInputState() {
		Pet returnValue = null;
		try {
			returnValue = petService.newPet(null);
			fail("null input was accepted in newPet");
		}catch (Exception e) {
			//No value should be returned since Exception is caught mid-function
			assertEquals(returnValue, null);
		}
	}

	@Test
	//Behavior verification
	public void testSavePetNullPetBehavior() {
		try {
			petService.savePet(null, owner);
			fail("null input was accepted in savePet");
		}catch (Exception e) {
			Mockito.verify(criticalLogger, Mockito.times(0)).info(anyString(),anyInt());
			Mockito.verify(owner, Mockito.times(0)).addPet(any(Pet.class));
			Mockito.verify(pets, Mockito.times(0)).save(any(Pet.class));
		}
	}

	@Test
	//Behavior verification
	public void testSavePetNullOwnerBehavior() {
		Mockito.when(pet.getId()).thenReturn(10);
		try {
			petService.savePet(pet, null);
			fail("null input was accepted in savePet");
		}catch (Exception e) {
			Mockito.verify(criticalLogger, Mockito.times(1)).info(anyString(), anyInt());
			Mockito.verify(pet, Mockito.times(1)).getId();
			Mockito.verify(pets, Mockito.times(0)).save(any(Pet.class));
		}
	}

	//---------------------------------- Valid Inputs

	@Test
	//Behavior verification
	public void testFindOwnerValidInputBehavior() {
		try {
			Owner x = petService.findOwner(1);
		}catch (Exception e) {
			System.out.println(e);
			fail("testFindOwnerValidInputBehavior failed");
		}
		Mockito.verify(criticalLogger, Mockito.times(1)).info("find owner {}", 1);
		Mockito.verify(ownerRepository, Mockito.times(1)).findById(anyInt());
		//findById method test
		ArgumentCaptor<Integer> ownerId = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(ownerRepository, times(1)).findById(ownerId.capture());
		List<Integer> capturedValue = ownerId.getAllValues();
		assertEquals((Integer)1, capturedValue.get(0));
	}

	@Test
	//State verification
	public void testFindOwnerValidInputState() {
		when(ownerRepository.findById(anyInt())).thenReturn(owner);
		Owner returnValue = null;
		try {
			returnValue = petService.findOwner(1);
		}catch (Exception e) {
			fail("testFindOwnerValidInputState failed");
		}
		assertEquals(returnValue, owner);
	}

	@Test
	//Behavior verification
	public void testNewPetValidInputBehavior() {
		Mockito.when(owner.getId()).thenReturn(2);
		try {
			petService.newPet(owner);
		}catch (Exception e) {
			fail("testNewPetValidInputBehavior failed");
		}

		Mockito.verify(criticalLogger, Mockito.times(1)).info("add pet for owner {}", 2);
		Mockito.verify(owner, Mockito.times(1)).getId();
		Mockito.verify(owner, Mockito.times(1)).addPet(any(Pet.class));
		//
		ArgumentCaptor<Pet> p = ArgumentCaptor.forClass(Pet.class);
		Mockito.verify(owner).addPet(p.capture());
		assertEquals(Pet.class, p.getValue().getClass());
	}

	@Test
	//Classical - State verification
	public void testNewPetValidInputState() {
		//a real object is used with classical approach
		Owner realOwner = new Owner();
		Pet returnValue = null;
		try {
			returnValue = petService.newPet(realOwner);
		}catch (Exception e) {
			fail("FAILED");
		}
		assertEquals(1, realOwner.getPets().size());
		realOwner.removePet(returnValue);
		assertEquals(0, realOwner.getPets().size());
		assertNotNull(returnValue);
		assertEquals(returnValue.getClass(), Pet.class);
	}

	@Test
	//Behavior verification
	public void testFindPetValidInputBehavior() {
		try {
			petService.findPet(3);
		}catch (Exception e) {
			fail("FAILED");
		}
		Mockito.verify(criticalLogger, Mockito.times(1)).info("find pet by id {}", 3);
		Mockito.verify(pets, Mockito.times(1)).get(anyInt());

		ArgumentCaptor<Integer> petId = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(pets).get(petId.capture());
		assertEquals((Integer)3, petId.getValue());
	}

	@Test
	//State verification
	public void testFindPetValidInputState() {
		when(pets.get(anyInt())).thenReturn(pet);
		Pet returnValue = null;
		try {
			returnValue = petService.findPet(1);
		}catch (Exception e) {
			fail("FAILED");
		}
		assertEquals(returnValue, pet);
	}

	@Test
	//Behavior verification
	public void testSavePetValidInputBehavior() {
		Mockito.when(pet.getId()).thenReturn(4);
		try {
			petService.savePet(pet, owner);
		}catch (Exception e) {
			fail("testSavePetValidInputBehavior failed");
		}

		Mockito.verify(criticalLogger, Mockito.times(1)).info("save pet {}", 4);
		Mockito.verify(pet, Mockito.times(1)).getId();
		Mockito.verify(owner, Mockito.times(1)).addPet(any(Pet.class));
		Mockito.verify(pets, Mockito.times(1)).save(any(Pet.class));

		ArgumentCaptor<Pet> repoArgumentCaptor = ArgumentCaptor.forClass(Pet.class);
		Mockito.verify(owner).addPet(repoArgumentCaptor.capture());
		assertEquals(pet, repoArgumentCaptor.getValue());

		ArgumentCaptor<Pet> repoArgumentCaptor2 = ArgumentCaptor.forClass(Pet.class);
		Mockito.verify(pets).save(repoArgumentCaptor2.capture());
		assertEquals(pet, repoArgumentCaptor2.getValue());
	}
}

