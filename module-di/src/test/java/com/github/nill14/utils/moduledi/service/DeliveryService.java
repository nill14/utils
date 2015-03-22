package com.github.nill14.utils.moduledi.service;

import com.google.inject.Inject;

public class DeliveryService implements IDeliveryService {

	@Inject
	private ISnackService snackService;
	
	@Override
	public ISnackService getSnackService() {
		return snackService;
	}
	
}
