package com.github.nill14.utils.moduledi.spring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;

public class ConversationScope implements Scope {

//	private final UUID uuid = UUID.randomUUID();
	

	private final ThreadLocal<Map<String, Object>> threadScope =
			new NamedThreadLocal<Map<String, Object>>("ConversationScope") {
				@Override
				protected Map<String, Object> initialValue() {
					return new ConcurrentHashMap<String, Object>();
				}
			};
	
	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		Map<String, Object> scope = this.threadScope.get();
		Object object = scope.get(name);
		if (object == null) {
			object = objectFactory.getObject();
			scope.put(name, object);
		}
		return object;
	}

	@Override
	public Object remove(String name) {
		Map<String, Object> scope = this.threadScope.get();
		return scope.remove(name);
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object resolveContextualObject(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getConversationId() {
		return Thread.currentThread().getName();
//		return uuid.toString();
	}

}
