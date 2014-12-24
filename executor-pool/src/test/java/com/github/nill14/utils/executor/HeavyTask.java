package com.github.nill14.utils.executor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeavyTask implements Callable<Void> {
	
	private static final Logger log = LoggerFactory.getLogger(HeavyTask.class);
	private final Random rand = new Random();
	private final long durationMillis;
	private final int index;
	
	public HeavyTask(int index, long durationMillis) {
		this.index = index;
		this.durationMillis = durationMillis;
	}
    
    @Override
    public Void call() throws Exception {
    	byte[] bytes = new byte[1024];
    	
    	long end = System.currentTimeMillis() + durationMillis;
    	log.info("Task {} - started", index);
    	
    	while (end > System.currentTimeMillis()) {
    		rand.nextBytes(bytes);
    		toSHA1(bytes);
    	}
    	log.info("Task {} - finished", index);
    	
    	return null;
    }
	
    
    public static String toSHA1(byte[] bytes) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        }
        catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } 
        return new String (md.digest(bytes));
    }
}
