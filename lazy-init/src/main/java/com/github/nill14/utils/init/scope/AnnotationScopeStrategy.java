package com.github.nill14.utils.init.scope;

import java.lang.annotation.Annotation;

import javax.inject.Singleton;

import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.impl.CallerContext;
import com.google.common.base.Preconditions;

public final class AnnotationScopeStrategy implements IScopeStrategy {

	private final Class<? extends Annotation> scopeAnnotation;

	private AnnotationScopeStrategy(Class<? extends Annotation> scopeAnnotation) {
		this.scopeAnnotation = Preconditions.checkNotNull(scopeAnnotation);
	}

	@Override
	public IScope resolveScope(CallerContext context) {
		if (Singleton.class.equals(scopeAnnotation)) {
			return SingletonScope.instance();
		}
//		IScope scope = scopes.get(annotationType);
//		if (scope == null) {
//			throw new RuntimeException("Scope is missing: " + annotationType);
//		}
//		return scope;
		return PrototypeScope.instance();
	}

	@Override
	public boolean isPrototype() {
		return false;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public static IScopeStrategy prototype() {
		return new PrototypeScopeStrategy();
	}


	public static IScopeStrategy of(Class<? extends Annotation> scopeAnnotation) {
		if (Singleton.class.equals(scopeAnnotation)) {
			return new SingletonScopeStrategy();
		} else {
			return new AnnotationScopeStrategy(scopeAnnotation);
		}
	}
	
	@Override
	public boolean scopeEquals(IScopeStrategy obj) {
		
		// TODO Auto-generated method stub
		return false;
	}
	
}


final class PrototypeScopeStrategy implements IScopeStrategy {

	@Override
	public IScope resolveScope(CallerContext context) {
		return PrototypeScope.instance();
	}

	@Override
	public boolean isPrototype() {
		return true;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	@Override
	public boolean scopeEquals(IScopeStrategy obj) {
		return isPrototype() == obj.isPrototype() && isSingleton() == obj.isSingleton();
	}
}

final class SingletonScopeStrategy implements IScopeStrategy {

	@Override
	public IScope resolveScope(CallerContext context) {
		return SingletonScope.instance();
	}

	@Override
	public boolean isPrototype() {
		return false;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public boolean scopeEquals(IScopeStrategy obj) {
		return isPrototype() == obj.isPrototype() && isSingleton() == obj.isSingleton();
	}
}