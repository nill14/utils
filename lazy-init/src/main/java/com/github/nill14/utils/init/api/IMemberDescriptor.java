package com.github.nill14.utils.init.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Optional;

public interface IMemberDescriptor {

	Optional<Annotation> getAnnotation(Class<? extends Annotation> annotation);
	
	Collection<Annotation> getAnnotations();
	
	Collection<IParameterType> getParameterTypes();

	Object invoke(Object receiver, Object... args) throws InvocationTargetException, ReflectiveOperationException;
	
	/**
	 * 
	 * All members are @Nullable or Inject(optional) so that we can skip invoke
	 * {@link java.util.Optional} is not such a case
	 * @return whether the inject is optional
	 */
	boolean isOptionalInject();

}