package com.github.nill14.utils.init;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.google.common.collect.ImmutableList;

public class TimeService extends AbstractService implements ITimeService {

	private static final Logger log = LoggerFactory.getLogger(TimeService.class);
	
	@Inject
	private ZoneId zone;
	
	@Inject
	private Optional<Calendar> calendar;
	
	@Inject
	private InitSpy spy;
	
	@Inject
	private List<ICalculator> calcs;
	
	@Override
	@PostConstruct
	public void init() {
		log.debug("init");
		Assert.assertNotNull(zone);
		spy.init();
	}
	
	@Override
	public LocalDateTime getNow() {
		log.debug("getNow");
		return LocalDateTime.now(zone);
	}
	
	public InitSpy getSpy() {
		return spy;
	}
	
	public ZoneId getZone() {
		return zone;
	}
	
	@Override
	public List<ICalculator> getProviders() {
		return ImmutableList.copyOf(calcs);
	}
	
	@PreDestroy
	public void destroy() {
		log.debug("destroy");
		Assert.assertNotNull(zone);
		zone = null;
		spy.destroy();
	}

}
