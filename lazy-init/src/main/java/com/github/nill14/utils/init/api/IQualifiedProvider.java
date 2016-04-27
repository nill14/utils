package com.github.nill14.utils.init.api;

import java.lang.annotation.Annotation;
import java.util.Optional;

import com.github.nill14.utils.init.meta.Annotations;
import com.google.inject.Key;

public interface IQualifiedProvider<T> {

  /**
   * @param named 
   * @return a named object from the registry
   * @see Annotations#named(String)
   *
   * Returns from registry the object of given type and qualified with name.
   * If object is not found, an exception is thrown.
   */
  T getNamed(String named);

  /**
   * @param named 
   * @return an optional object from the registry
   * @see Annotations#named(String)
   *
   * Returns from registry the object of given type and qualified with name.
   */
  Optional<T> getOptionalNamed(String named);

  /**
   * @param annotationType 
   * @return a qualified object from the registry
   * @see Annotations#annotation(Class)
   *
   * Returns from registry the object of given type and qualified with the annotation.
   * If object is not found, an exception is thrown.
   * Generates an Annotation for the annotation class. Requires that the annotation is all optionals.
   */
  T getQualified(Class<? extends Annotation> annotationType);

  /**
   * @param annotationType 
   * @return an optional qualified object from the registry
   * @see Annotations#annotation(Class)
   *
   * Returns from registry the object of given type and qualified with the annotation.
   * Generates an Annotation for the annotation class. Requires that the annotation is all optionals.
   */
  Optional<T> getOptionalQualified(Class<? extends Annotation> annotationType);

  /**
   *
   * Returns from registry the object of given type and qualified with the annotation.
   * If object is not found, an exception is thrown.
   * 
   * @param qualifier 	
   * @return a qualified object from the registry
   * @see Key#get(Class, Annotation)
   */
  T getQualified(Annotation qualifier);

  /**
   * @param qualifier 	
   * @return an optional qualified object from the registry
   * @see Key#get(Class, Annotation)
   *
   * Returns from registry the object of given type and qualified with the annotation.
   */
  Optional<T> getOptionalQualified(Annotation qualifier);

}