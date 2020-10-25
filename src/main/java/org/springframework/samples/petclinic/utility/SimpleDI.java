package org.springframework.samples.petclinic.utility;

import org.springframework.samples.petclinic.owner.PetRepository;

import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * this simple class shows the main idea behind a Dependency Injection library
 */
public abstract class SimpleDI {

	private static MyDI DIContainer = null;
	static SimpleDI getDIContainer() {
		if (DIContainer == null){
			DIContainer = new MyDI();
			return DIContainer;
		}
		return DIContainer;
	}

	public abstract void provideByInstance(Class<?> typeClass, Object instanceOfType);

	public abstract void provideByAConstructorFunction(Class<?> typeClass, Callable<Object> providerFunction);

	public abstract Object getInstanceOf(Class<?> requiredType) throws Exception;
}
