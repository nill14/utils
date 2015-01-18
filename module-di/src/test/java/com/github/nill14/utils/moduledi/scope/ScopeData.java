package com.github.nill14.utils.moduledi.scope;

import java.util.concurrent.ConcurrentHashMap;

public class ScopeData {

	public ScopeData(String conversationId) {
		this.conversationId = conversationId;
	}
	
	private final ConcurrentHashMap<String, Object> beans = new ConcurrentHashMap<>();
	private final String conversationId;
	public ConcurrentHashMap<String, Object> getBeans() {
		return beans;
	}
	public String getConversationId() {
		return conversationId;
	}
	
	
}
