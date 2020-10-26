package org.springframework.samples.petclinic.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetRepository;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;

@RunWith(MockitoJUnitRunner.class)
class PetTimedCacheTest {
	//Classical
	//در این تست از روش classical برای object هایی مثل pet و ... استفاده می‌کنیم
	//در عین حال به ازای petRepository از mock آن استفاده میکنیم. این DOC از برنامه ما خارج است و استفاده از دیتابیس واقعی ما تاثیرات جانبی بر دیگر اطلاعات دارد.
	//همچنین دیتابیس ها مشکلات جانبی همانند زمان‌گیر بودن و نیاز به ستاپ و ... نیز دارند.

	@Mock private PetRepository petRepository;
	private long expiryInMillis = 100;
	int MISS = 1, HIT = 0;
	int ExpectedInvokeNumber = 0;
	private PetTimedCache petTimedCache;

	Pet[] pet = new Pet[2];

	@BeforeEach
	public void setup() {
		ExpectedInvokeNumber = 0;
		MockitoAnnotations.initMocks(this);
		petTimedCache = new PetTimedCache(expiryInMillis, petRepository);
		for (int i = 0 ; i < 2 ; i++)
		{
			pet[i] = new Pet();
			pet[i].setId(i);
			petTimedCache.save(pet[i]);
			//setting up our database mock
			Mockito.when(petRepository.findById(i)).thenReturn(pet[i]);
		}
	}

	@Test
	public void testCacheOneItem() throws InterruptedException {

		petTimedCache.get(0);
		//cache must MISS
		ExpectedInvokeNumber += MISS;
		Mockito.verify(petRepository, Mockito.times(ExpectedInvokeNumber)).findById(anyInt());

		petTimedCache.get(0);
		//cache must HIT
		ExpectedInvokeNumber += HIT;
		Mockito.verify(petRepository, Mockito.times(ExpectedInvokeNumber)).findById(anyInt());

		TimeUnit.MILLISECONDS.sleep(expiryInMillis * 2);
		petTimedCache.get(0);
		//cache must MISS
		ExpectedInvokeNumber += MISS;
		Mockito.verify(petRepository, Mockito.times(ExpectedInvokeNumber)).findById(anyInt());
	}



	@Test
	public void testCacheTwoItems() throws InterruptedException {
		petTimedCache.get(0);
		//cache must MISS
		ExpectedInvokeNumber += MISS;
		Mockito.verify(petRepository, Mockito.times(ExpectedInvokeNumber)).findById(anyInt());

		petTimedCache.get(1);
		//cache must MISS
		ExpectedInvokeNumber += MISS;
		Mockito.verify(petRepository, Mockito.times(ExpectedInvokeNumber)).findById(anyInt());

		petTimedCache.get(0);
		//cache must HIT
		ExpectedInvokeNumber += HIT;
		Mockito.verify(petRepository, Mockito.times(ExpectedInvokeNumber)).findById(anyInt());

		TimeUnit.MILLISECONDS.sleep(expiryInMillis/5);
		petTimedCache.get(0);
		//cache must HIT
		ExpectedInvokeNumber += HIT;
		Mockito.verify(petRepository, Mockito.times(ExpectedInvokeNumber)).findById(anyInt());


		TimeUnit.MILLISECONDS.sleep(expiryInMillis * 2);
		petTimedCache.get(0);
		//cache must MISS
		ExpectedInvokeNumber += MISS;
		Mockito.verify(petRepository, Mockito.times(ExpectedInvokeNumber)).findById(anyInt());

		petTimedCache.get(1);
		//cache must MISS
		ExpectedInvokeNumber += MISS;
		Mockito.verify(petRepository, Mockito.times(ExpectedInvokeNumber)).findById(anyInt());
	}

}
