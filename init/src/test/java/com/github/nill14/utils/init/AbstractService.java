package com.github.nill14.utils.init;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractService {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractService.class);

	@Inject
	private String greeting;
	
	@PostConstruct
	public void init() {
		log.debug("init");
		Assert.assertNotNull(greeting);
	}

	
	public String getGreeting() {
		log.debug("getGreeting");
		return greeting;
	}
}
