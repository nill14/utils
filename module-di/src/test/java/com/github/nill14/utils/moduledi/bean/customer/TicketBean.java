package com.github.nill14.utils.moduledi.bean.customer;

import javax.inject.Inject;

import com.github.nill14.utils.moduledi.scope.SLAScoped;

public class TicketBean {
	
	@Inject
	private SLABean sla;
	
	
	public SLABean getSLA() {
		return sla;
	}

}
