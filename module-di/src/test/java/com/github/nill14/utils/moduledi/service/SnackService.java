package com.github.nill14.utils.moduledi.service;

import com.google.inject.Inject;

public class SnackService implements ISnackService {

	@Inject
	private IBreadService breadService;
	
	@Override
	public IBreadService getBreadService() {
		return breadService;
	}
	
}
