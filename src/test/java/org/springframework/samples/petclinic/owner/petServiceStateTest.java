package org.springframework.samples.petclinic.owner;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.utility.PetTimedCache;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
public class petServiceStateTest {
	@Configuration
	static class ContextConfiguration {
		@Bean
		public PetService petService() {
			return new PetService(pets, ownerRepository, criticalLogger);
		}
	}

	private static OwnerRepository ownerRepository;
	private static PetTimedCache pets;
	private static Logger criticalLogger;


	@Autowired public PetService petService;

	private Pet pet;
	private Owner owner;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		pet = new Pet();
		owner = new Owner();
	}

	@Test(expected = java.lang.Exception.class)
	//Classical
	public void testNewPetNullInputState() {
		petService.newPet(null);
	}

	@Test
	//Classical
	public void testNewPetValidInputState() {
		owner.setId(1);
		Pet returnValue = petService.newPet(owner);

		//State verification
		assertEquals(returnValue, Pet.class);
	}

	@Test
	public void testSavePetValidInputState() {
		Integer ownerPetSize = owner.getPets().size();
		try {
			petService.savePet(pet, owner);
		}catch (Exception e) {
			System.out.println(e);
			fail("FAILED");
		}
		//State verification
		assertEquals(1,owner.getPets().size() - ownerPetSize);
	}
}


