package com.github.nill14.utils.executor;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientEngine {
	
	private static final Logger log = LoggerFactory.getLogger(ReaderTask.class);
	private final ExecutorService threadPool;

	public ClientEngine(int poolSize) {
		threadPool = Executors.newFixedThreadPool(poolSize);
	}

	public Future<LocalDateTime> next(InputStream input, int taskCounter) throws IOException {
        return threadPool.submit(new ReaderTask(taskCounter, input));
	}
	
	public Future<LocalDateTime> nextInstant(final int taskCounter) throws IOException {
		log.info("InstantTask {} - scheduled", taskCounter);
        return threadPool.submit(new Callable<LocalDateTime>() {

			@Override
			public LocalDateTime call() throws Exception {
				log.info("InstantTask {} - finished", taskCounter);
				return LocalDateTime.now();
			}
		});
	}
	
	public Future<Void> nextHeavy(final int taskCounter, long durationMillis) throws IOException {
        return threadPool.submit(new HeavyTask(taskCounter, durationMillis));
	}
	
}
