package com.github.nill14.utils.moduledi.bean.snack;

import javax.inject.Inject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.github.nill14.utils.moduledi.service.ISnackService;

@Service
@Scope("conversation")
public class Snack {
	
	public Snack() {
		// TODO Auto-generated constructor stub
	}

	@Inject
	private Bread bread;
	
	@Inject
	private ISnackService snackService;

	public Bread getBread() {
		return bread;
	}
	
	public ISnackService getSnackService() {
		return snackService;
	}

	
	
	
}
