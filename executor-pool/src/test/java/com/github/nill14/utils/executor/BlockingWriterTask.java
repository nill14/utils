package com.github.nill14.utils.executor;

import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockingWriterTask implements Callable<Void> {
	
	private static final Logger log = LoggerFactory.getLogger(BlockingWriterTask.class);
	private final Random rand = new Random();
	private final OutputStream output;
	private final int beatMillis;
	private final long durationMillis;
	private final int index;
	
	public BlockingWriterTask(int index, OutputStream output, int beatMillis, long durationMillis) {
		this.index = index;
		this.output = output;
		this.beatMillis = beatMillis;
		this.durationMillis = durationMillis;
	}
    
    @Override
    public Void call() throws Exception {
    	long end = System.currentTimeMillis() + durationMillis;
    	log.info("Task {} - started", index);
    	
    	while (end > System.currentTimeMillis()) {
			output.write(rand.nextInt(256));
    		Thread.sleep(beatMillis);
    	}
    	output.close();
    	log.info("Task {} - finished", index);
    	
    	return null;
    }
	
}
