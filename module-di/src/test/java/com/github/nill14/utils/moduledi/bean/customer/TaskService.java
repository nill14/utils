package com.github.nill14.utils.moduledi.bean.customer;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.Optional;

public class TaskService implements ITaskService {

	@Inject
	@Named("assignee") //otherwise reporter is injected
	private Optional<String> assignee;
	
	@Inject
	@Named("reporter")
	private Optional<String> reporter;
	
	@Inject
	private TaskBean taskBean;

	@Override
	public Optional<String> getAssignee() {
		return assignee;
	}

	@Override
	public void setAssignee(Optional<String> assignee) {
		this.assignee = assignee;
	}

	@Override
	public Optional<String> getReporter() {
		return reporter;
	}

	@Override
	public void setReporter(Optional<String> reporter) {
		this.reporter = reporter;
	}

	public TaskBean getTaskBean() {
		return taskBean;
	}

	public void setTaskBean(TaskBean taskBean) {
		this.taskBean = taskBean;
	}
	
	
	
	
	
	
}
