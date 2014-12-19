package com.github.nill14.utils.moduledi;

import com.google.inject.Inject;

public class SnackService implements ISnackService {

	@Inject
	private IBreadService breadService;
	
}
