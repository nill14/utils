package com.github.nill14.utils.init;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import com.github.nill14.utils.init.ICalculator;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class TimeService extends AbstractService implements ITimeService {

	private static final Logger log = LoggerFactory.getLogger(TimeService.class);
	
	@Inject
	private ZoneId zone;
	
	@Inject
	@Nullable
	private Calendar calendar;
	
	@Inject
	private TimeService spy;
	
	@Inject
	private ICalculator[] calcs;
	
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
	
	public TimeService getSpy() {
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
