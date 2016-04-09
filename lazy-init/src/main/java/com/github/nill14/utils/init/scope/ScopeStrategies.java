package com.github.nill14.utils.init.scope;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.inject.Singleton;

import com.github.nill14.utils.init.api.IScope;
import com.github.nill14.utils.init.binding.impl.Binding;
import com.google.common.base.Preconditions;

public final class ScopeStrategies {
	
	private ScopeStrategies() {
	}

	public static IScopeStrategy prototype() {
		return new PrototypeScopeStrategy();
	}

	public static IScopeStrategy of(Class<? extends Annotation> scopeAnnotation) {
		if (scopeAnnotation == null) {
			return prototype();
		
		} else  if (Singleton.class.equals(scopeAnnotation)) {
			return new SingletonScopeStrategy();
		
		} else {
			return new AbstractAnnotationScopeStrategy(scopeAnnotation);
		}
	}
	
	private static IScopeStrategy of(Class<? extends Annotation> scopeAnnotation, IScope scope) {
		Preconditions.checkNotNull(scopeAnnotation, "scopeAnnotation");
		return new AnnotationScopeStrategy(scopeAnnotation, scope);
	}
	
	public static <T> Binding<T> replaceScopes(Binding<T> binding, Map<Class<? extends Annotation>, IScope> scopes) {
		if (binding.getScopeStrategy() instanceof AbstractAnnotationScopeStrategy) {
			Class<? extends Annotation> annotation = ((AbstractAnnotationScopeStrategy) binding.getScopeStrategy()).getScopeAnnotation();
			IScope scope = scopes.get(annotation);
			if (scope == null) {
				throw new RuntimeException(String.format("Annotation %s have no mapping to a scope", annotation));
			}
			return binding.withScopeStrategy(of(annotation, scope));
		}
		return binding;
	}

	static final class AbstractAnnotationScopeStrategy implements IScopeStrategy {
	
		private final Class<? extends Annotation> scopeAnnotation;
	
		public AbstractAnnotationScopeStrategy(Class<? extends Annotation> scopeAnnotation) {
			this.scopeAnnotation = Preconditions.checkNotNull(scopeAnnotation);
		}
	
		@Override
		public IScope resolveScope() {
			throw new UnsupportedOperationException();
		}
	
		@Override
		public boolean isPrototype() {
			return false;
		}
	
		@Override
		public boolean isSingleton() {
			return false;
		}
		
		public Class<? extends Annotation> getScopeAnnotation() {
			return scopeAnnotation;
		}
	
		@Override
		public boolean scopeEquals(IScopeStrategy obj) {
			if (obj instanceof AbstractAnnotationScopeStrategy) {
				return scopeAnnotation.equals(((AbstractAnnotationScopeStrategy) obj).scopeAnnotation);
			}
			return false;
		}
		
	}
	
	static final class AnnotationScopeStrategy implements IScopeStrategy {
	
		@SuppressWarnings("unused")
		private final Class<? extends Annotation> scopeAnnotation;
		private final IScope scope;
	
		private AnnotationScopeStrategy(Class<? extends Annotation> scopeAnnotation, IScope scope) {
			this.scope = Preconditions.checkNotNull(scope);
			this.scopeAnnotation = Preconditions.checkNotNull(scopeAnnotation);
		}
		
		@Override
		public IScope resolveScope() {
			return scope;
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
			if (obj instanceof AnnotationScopeStrategy) {
				return scope.equals(((AnnotationScopeStrategy) obj).scope);
			}
			return false;
		}
	}
	
	static final class PrototypeScopeStrategy implements IScopeStrategy {
	
		@Override
		public IScope resolveScope() {
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
			return obj.isPrototype();
		}
	}
	
	static final class SingletonScopeStrategy implements IScopeStrategy {
	
		@Override
		public IScope resolveScope() {
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
			return obj.isSingleton();
		}
	}
}