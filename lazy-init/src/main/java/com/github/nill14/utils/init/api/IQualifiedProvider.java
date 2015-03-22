package com.github.nill14.utils.init.api;

import java.lang.annotation.Annotation;
import java.util.Optional;

import com.google.inject.Key;
import com.google.inject.name.Names;

public interface IQualifiedProvider<T> {

  /**
   * @see Names#named(String)
   *
   * Returns from registry the object of given type and qualified with name.
   * If object is not found, an exception is thrown.
   */
  T getNamed(String named);

  /**
   * @see Names#named(String)
   *
   * Returns from registry the object of given type and qualified with name.
   */
  Optional<T> getOptionalNamed(String named);

  /**
   * @see Key#get(Class, Class)
   *
   * Returns from registry the object of given type and qualified with the annotation.
   * If object is not found, an exception is thrown.
   * Generates an Annotation for the annotation class. Requires that the annotation is all optionals.
   */
  T getQualified(Class<? extends Annotation> annotationType);

  /**
   * @see Key#get(Class, Class)
   *
   * Returns from registry the object of given type and qualified with the annotation.
   * Generates an Annotation for the annotation class. Requires that the annotation is all optionals.
   */
  Optional<T> getOptionalQualified(Class<? extends Annotation> annotationType);

  /**
   * @see Key#get(Class, Annotation)
   *
   * Returns from registry the object of given type and qualified with the annotation.
   * If object is not found, an exception is thrown.
   */
  T getQualified(Annotation qualifier);

  /**
   * @see Key#get(Class, Annotation)
   *
   * Returns from registry the object of given type and qualified with the annotation.
   */
  Optional<T> getOptionalQualified(Annotation qualifier);

}