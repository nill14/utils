package com.github.nill14.utils.moduledi.bean.customer;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.Optional;

public class TaskBean {

	@Inject
	@Named("assignee") //otherwise reporter is injected
	private Optional<String> assignee;
	
	@Inject
	@Named("reporter")
	private Optional<String> reporter;

	public Optional<String> getAssignee() {
		return assignee;
	}

	public void setAssignee(Optional<String> assignee) {
		this.assignee = assignee;
	}

	public Optional<String> getReporter() {
		return reporter;
	}

	public void setReporter(Optional<String> reporter) {
		this.reporter = reporter;
	}
	
	
	
	
}
