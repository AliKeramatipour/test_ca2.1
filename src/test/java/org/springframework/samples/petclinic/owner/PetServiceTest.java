package org.springframework.samples.petclinic.owner;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.samples.petclinic.utility.PetTimedCache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class PetServiceTest {

	@Mock private static OwnerRepository ownerRepository;
	@Mock private static PetTimedCache pets;
	@Mock private static Logger criticalLogger;
	@Spy private Pet pet;
	@Spy private Owner owner;

	@InjectMocks
	private PetService petService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	//	Null Inputs

	@Test
	//Mockist
	public void testNewPetNullInputBehavior() {
		try {
			petService.newPet(null);
			fail("null input was accepted in newPet");
		}catch (Exception e) {
			//Behavior verification
			Mockito.verify(owner, Mockito.times(0)).getId();
			Mockito.verify(owner, Mockito.times(0)).addPet(any(Pet.class));
		}
	}

	@Test
	//Mockist	???
	public void testNewPetNullInputState() {
		Pet returnValue = null;
		try {
			returnValue = petService.newPet(null);
			fail("null input was accepted in newPet");
		}catch (Exception e) {
			//State verification
			assertEquals(returnValue, null);
		}
	}

	@Test
	//Mockist
	public void testSavePetNullPetBehavior() {
		try {
			petService.savePet(null, owner);
			fail("null input was accepted in savePet");
		}catch (Exception e) {
			//Behavior verification
			Mockito.verify(pet, Mockito.times(0)).getId();
			Mockito.verify(owner, Mockito.times(0)).addPet(any(Pet.class));
			Mockito.verify(pets, Mockito.times(0)).save(any(Pet.class));
		}
	}

	@Test
	//Mockist	???
	public void testSavePetNullPetState() {
		Owner realOwner = new Owner();
		Integer ownerPetSize = realOwner.getPets().size();
		try {
			petService.savePet(null, realOwner);
			fail("null input was accepted in savePet");
		}catch (Exception e) {
			//State verification
			assertEquals(ownerPetSize, realOwner.getPets().size());
		}
	}

	@Test
	//Mockist
	public void testSavePetNullOwnerBehavior() {
		try {
			petService.savePet(pet, null);
			fail("null input was accepted in savePet");
		}catch (Exception e) {
			//Behavior verification
			Mockito.verify(pet, Mockito.times(1)).getId();
			Mockito.verify(owner, Mockito.times(0)).addPet(any(Pet.class));
			Mockito.verify(pets, Mockito.times(0)).save(any(Pet.class));
		}
	}

	//	Valid Inputs

	@Test
	//Mockist
	public void testFindOwnerValidInputBehavior() {
		try {
			petService.findOwner(1);
		}catch (Exception e) {
			System.out.println(e);
			fail("testFindOwnerValidInputBehavior failed");
		}
		//Behavior
		Mockito.verify(criticalLogger, Mockito.times(1)).info("find owner {}", 1);
		Mockito.verify(ownerRepository, Mockito.times(1)).findById(anyInt());
		//
		ArgumentCaptor<Integer> ownerId = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(ownerRepository).findById(ownerId.capture());
		Assert.assertEquals(java.util.Optional.of(1), java.util.Optional.ofNullable(ownerId.getValue()));
	}

	@Test
	//Mockist ???
	public void testFindOwnerValidInputState() {
		when(ownerRepository.findById(anyInt())).thenReturn(owner);
		Owner returnValue = null;
		try {
			returnValue = petService.findOwner(1);
		}catch (Exception e) {
			fail("testFindOwnerValidInputState failed");
		}
		//State
		assertEquals(returnValue, owner);
	}

	@Test
	//Mockist
	public void testNewPetValidInputBehavior() {
		Mockito.when(owner.getId()).thenReturn(2);
		try {
			petService.newPet(owner);
		}catch (Exception e) {
			fail("testNewPetValidInputBehavior failed");
		}
		//Behavior verification
		Mockito.verify(criticalLogger, Mockito.times(1)).info("add pet for owner {}", 2);
		Mockito.verify(owner, Mockito.times(1)).getId();
		Mockito.verify(owner, Mockito.times(1)).addPet(any(Pet.class));
		//
		ArgumentCaptor<Pet> p = ArgumentCaptor.forClass(Pet.class);
		Mockito.verify(owner).addPet(p.capture());
		Assert.assertEquals(Pet.class, p.getValue());
	}

	@Test
	//Classical
	public void testNewPetValidInputState() {
		Owner realOwner = new Owner();
		Pet returnValue = null;
		try {
			returnValue = petService.newPet(realOwner);
		}catch (Exception e) {
			fail("FAILED");
		}
		//State verification
		assertEquals(returnValue, Pet.class);
	}

	@Test
	//Mockist
	public void testFindPetValidInputBehavior() {
		try {
			petService.findPet(3);
		}catch (Exception e) {
			fail("FAILED");
		}
		//Behavior verification
		Mockito.verify(criticalLogger, Mockito.times(1)).info("find pet by id {}", 3);
		Mockito.verify(pets, Mockito.times(1)).get(anyInt());
		//
		ArgumentCaptor<Integer> petId = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(pets).get(petId.capture());
		Assert.assertEquals(java.util.Optional.of(3), java.util.Optional.ofNullable(petId.getValue()));
	}

	@Test
	//Mockist ???
	public void testFindPetValidInputState() {
		when(pets.get(anyInt())).thenReturn(pet);
		Pet returnValue = null;
		try {
			returnValue = petService.findPet(1);
		}catch (Exception e) {
			fail("FAILED");
		}
		//State verification
		assertEquals(returnValue, pet);
	}

	@Test
	//Mockist
	public void testSavePetValidInputBehavior() {
		Mockito.when(pet.getId()).thenReturn(4);
		try {
			petService.savePet(pet, owner);
		}catch (Exception e) {
			fail("testSavePetValidInputBehavior failed");
		}
		//Behavior verification
		Mockito.verify(criticalLogger, Mockito.times(1)).info("save pet {}", 4);
		Mockito.verify(pet, Mockito.times(1)).getId();
		Mockito.verify(owner, Mockito.times(1)).addPet(any(Pet.class));
		Mockito.verify(pets, Mockito.times(1)).save(any(Pet.class));
		//
		ArgumentCaptor<Pet> repoArgumentCaptor = ArgumentCaptor.forClass(Pet.class);
		Mockito.verify(owner).addPet(repoArgumentCaptor.capture());
		Assert.assertEquals(pet, repoArgumentCaptor.getValue());
		//
		ArgumentCaptor<Pet> repoArgumentCaptor2 = ArgumentCaptor.forClass(Pet.class);
		Mockito.verify(pets).save(repoArgumentCaptor2.capture());
		Assert.assertEquals(pet, repoArgumentCaptor2.getValue());
	}
}

