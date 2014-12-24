package com.github.nill14.utils.executor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerEngine {
	
	private final ExecutorService threadPool;
	private final int beatMillis;
	private final long durationMillis;

	public ServerEngine(int poolSize, int beatMillis, long durationMillis) {
		this.beatMillis = beatMillis;
		this.durationMillis = durationMillis;
		threadPool = Executors.newFixedThreadPool(poolSize);
	}

	public InputStream getNext(int taskCounter) throws IOException {
		final PipedOutputStream output = new PipedOutputStream();
        final PipedInputStream  input  = new PipedInputStream(output);
        
        threadPool.submit(new BlockingWriterTask(taskCounter, output, beatMillis, durationMillis));
        
        return input;
	}
	
}
