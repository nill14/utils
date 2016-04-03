package com.github.nill14.utils.executor;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class PriorityExecutorTest {
	private static final int coreSize = 12;
	private static final int batches = 12;
	private static final int orderTolerance = 1;
	private static final ExecutorService executor = PriorityExecutor.newFixedThreadPool(coreSize);
	
	
	@BeforeMethod
	public void setUp() {
	}
	
	@Test
	public void testPriority() {
		SignalMachine signalMachine = new SignalMachine();
		List<Integer> results = Lists.newCopyOnWriteArrayList();
		
		for (int i = 0; i < batches; i++) {
			for (int j = 0; j < coreSize; j++) {
				int priority = i;
				int number = j;
				int jobId = i * coreSize + j;
				executor.submit(new PriorityRunnable(i, signalMachine.newRunnable(jobId))); 
				signalMachine.send(jobId, () -> System.out.printf("Executing Runnable(priority=%d, number=%d)\n", priority, number));
				signalMachine.blockingRead(jobId, 50);
				signalMachine.send(jobId, () -> results.add(priority));
			}
		}
		
		for (int j = 0; j < coreSize; j++) {
			signalMachine.awaitExecution(j); 
		}
		
		for (int i = 0; i < batches; i++) {
			for (int j = 0; j < coreSize; j++) {
				signalMachine.terminate(i * coreSize + j);
			}
		}
		signalMachine.awaitTermination();
		
		assertEquals(batches * coreSize, results.size());

		List<Double> averages = Lists.newArrayList();
		for (int i = 1; i < batches; i++) { //count from 1, skip the first batch with priority 0
			double avg = 0.0;
			for (int j = 0; j < coreSize; j++) {
				int priority = results.get(i * coreSize + j);
				avg += priority;
			}
			averages.add(avg /= coreSize);
			System.out.printf("Average priority: %f\n", avg);
		}
		
		int errorRate = 0;
		for (int i = 1; i < averages.size(); i++) {
			if (Double.compare(averages.get(i - 1 ), averages.get(i)) <  0) {
				if (errorRate++ >= orderTolerance) {
					assertTrue(false, String.format("Average priority should decrease: %f >= %f", averages.get(i - 1 ), averages.get(i)));
				}
			}
		}
	}
	
	private static class PriorityRunnable implements Runnable, Comparable<PriorityRunnable>{
		private final int priority;
		private Runnable delegate;
		public PriorityRunnable(int priority, Runnable delegate) {
			this.priority = priority;
			this.delegate = delegate;
		}
		
		@Override
		public int compareTo(PriorityRunnable o) {
			return Integer.compare(o.priority, priority);
		}
		@Override
		public void run() {
			delegate.run();
		}
	}


}
