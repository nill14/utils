package com.github.nill14.utils.executor;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReaderTask implements Callable<LocalDateTime> {
	
	private static final Logger log = LoggerFactory.getLogger(ReaderTask.class);
	private final InputStream input;
	private final int index;
	public ReaderTask(int index, InputStream input) {
		this.index = index;
		this.input = input;
	}
    
    @Override
    public LocalDateTime call() throws Exception {
    	log.info("Task {} - started", index);
    	int data = input.read();
    	
    	while(data != -1) {
    		data = input.read();
    	}
    	input.close();
    	log.info("Task {} - finished", index);
    	return LocalDateTime.now();
    }
	
}
