package org.springframework.samples.petclinic.utility;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class MyDI extends SimpleDI {

	private HashMap<Class<?>, Callable<Object>> byConstructor = new HashMap<>();
	private HashMap<Class<?>, Object> byInstance = new HashMap<>();

	@Override
	public void provideByInstance(Class<?> typeClass, Object instanceOfType) {
		byInstance.put(typeClass, instanceOfType);
	}

	@Override
	public void provideByAConstructorFunction(Class<?> typeClass, Callable<Object> providerFunction) {
		byConstructor.put(typeClass, providerFunction);
	}

	@Override
	public Object getInstanceOf(Class<?> requiredType) throws Exception {
		if (byInstance.get(requiredType) != null)
			return byInstance.get(requiredType);
		return byConstructor.get(requiredType).call();
	}
}
