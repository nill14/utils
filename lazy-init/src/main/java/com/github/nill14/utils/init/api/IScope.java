package com.github.nill14.utils.init.api;

import javax.inject.Provider;

public interface IScope {

  public <T> Provider<T> scope(IParameterType type, Provider<T> unscoped);

  
}
