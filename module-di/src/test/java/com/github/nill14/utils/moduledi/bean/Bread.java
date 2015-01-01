package com.github.nill14.utils.moduledi.bean;

import org.springframework.stereotype.Service;

import com.github.nill14.utils.moduledi.annotation.ConversationScope;

@ConversationScope
@Service
public class Bread {
	
	public Bread() {
		// TODO Auto-generated constructor stub
	}
	
	public String getName() {
		return "bread";
	}

}
