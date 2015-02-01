package com.github.nill14.utils.moduledi.bean.customer;

import java.util.Optional;

public interface ITaskService {

	Optional<String> getAssignee();

	void setAssignee(Optional<String> assignee);

	Optional<String> getReporter();

	void setReporter(Optional<String> reporter);

}