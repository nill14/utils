package com.github.nill14.utils.executor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * ReschedulableTask provides some extra methods over {@link ScheduledExecutorService}
 *
 */
public class ReschedulableTask {
	
	private static final long EMPTY = 0L; 
	
	private final ScheduledExecutorService executor;
	private final AtomicLong counter = new AtomicLong();
	private final Runnable command;
	
	public ReschedulableTask(ScheduledExecutorService executor, Runnable command) {
		this.executor = executor;
		this.command = command;
	}
	
	/**
	 * Re-schedule the given task so that it get invoked only once.
	 * The previous schedules are effectively cancelled. The last schedule wins.
	 * 
	 * The next execution might be scheduled already during the actual execution. This allows, 
	 * for example, re-scheduling in the command itself. But it also implies that in the worst case, 
	 * the command might still be executed simultaneously.
	 * If this scenario is not acceptable then explicit synchronization shall be used.
	 * 
     * @param delay the time from now to delay execution
     * @param unit the time unit of the delay parameter
	 */
	public void reschedule(long delay, TimeUnit unit) {
		long val = counter.incrementAndGet();
		
		executor.schedule(() -> {
			if (counter.compareAndSet(val, EMPTY)) {
				command.run();
			}
		}, delay, unit);
	}
	
	/**
	 * Schedule the execution of the command only once. Effectively, only the first schedule is executed.
	 * 
	 * The next execution might be scheduled already during the actual execution. This allows, 
	 * for example, re-scheduling in the command itself. But it also implies that in the worst case, 
	 * the command might still be executed simultaneously.
	 * If this scenario is not acceptable then explicit synchronization shall be used.
	 * 
     * @param delay the time from now to delay execution
     * @param unit the time unit of the delay parameter
	 */
	public void scheduleOnce(long delay, TimeUnit unit) {
		long val = counter.incrementAndGet();
		
		executor.schedule(() -> {
			if (val == 1L) {
				counter.set(EMPTY);
				command.run();
			}
		}, delay, unit);
	}
	

}
