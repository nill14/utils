package com.github.nill14.utils.executor;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

public class ReschedulableTaskTest {

	private static final ExecutorService executor = Executors.newFixedThreadPool(4);
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
	
	private AtomicLong counter;
	private ReschedulableTask task;
	
	
	@Before
	public void setUp() {
		counter = new AtomicLong();
		task = new ReschedulableTask(scheduler, () -> counter.incrementAndGet());
	}
	
	@Test
	public void testReschedule() throws InterruptedException {
		int count = 10;
		
		Semaphore sph = new Semaphore(-count + 1);
		IntStream.range(0, count).forEach(i -> {
			executor.execute(() -> {
				task.reschedule(50, TimeUnit.MILLISECONDS);
				sph.release();
			}); 
		});
		
		sph.acquire();
		Thread.sleep(100);
		assertEquals(1L, counter.get());
	}

	@Test
	public void testScheduleOnce() throws InterruptedException {
		int count = 10;
		
		Semaphore sph = new Semaphore(0);
		IntStream.range(0, count).forEach(i -> {
			executor.execute(() -> {
				task.scheduleOnce(50, TimeUnit.MILLISECONDS);
				sph.release();
			}); 
		});
		
		sph.acquire();
		Thread.sleep(100);
		assertEquals(1L, counter.get());
		
		sph.acquire(count - 1);
		Thread.sleep(100);
		assertEquals(1L, counter.get());
	}
	
}
