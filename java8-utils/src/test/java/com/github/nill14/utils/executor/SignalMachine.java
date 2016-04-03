package com.github.nill14.utils.executor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.testng.collections.Lists;

import com.google.common.collect.Queues;

/**
 * 
 * A testing utility for concurrency related code.
 * 
 *
 */
public class SignalMachine {
	
	private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	private final List<JobContainer> jobs = Collections.synchronizedList(Lists.newArrayList());
	private final Runnable TERM = () -> {};
	private final AtomicInteger counter = new AtomicInteger();
	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	
	/**
	 * Create a new job with the given jobId. <br>
	 * Each job manages its own command queue and JobId can be used to send a command into the command queue.
	 * @param jobId The sequential id of the job starting from 0
	 * @return The new job
	 */
	public Runnable newRunnable(int jobId) {
		counter.incrementAndGet();
		JobContainer job = new JobContainer(jobId);
		jobs.add(jobId, job);
		return job;
	}
	
	/**
	 * Sends a code block to be executed on the job's thread.
	 * @param jobId The id of the job.
	 * @param codeBlock An arbitrary code to be executed on the job's thread.
	 */
	public void send(int jobId, Runnable codeBlock) {
		jobs.get(jobId).signalQueue.add(codeBlock);
	}
	
	/**
	 * Sends a termination signal. All commands submitted beforehand are executed first.
	 * @param jobId The id of the job.
	 */
	public void terminate(int jobId) {
		jobs.get(jobId).signalQueue.add(TERM);
	}
	
	/**
	 * Sends a blocking read command into the job's command queue. 
	 * It's purpose is to simulate a delay which blocks the thread but doesn't burn the CPU time.
	 * @param jobId The id of the job.
	 * @param delay An initial delay in milliseconds. Delay is not guaranteed to be precise.
	 */
	public void blockingRead(int jobId, long delay) {
		ReaderWriter readerWriter = new ReaderWriter();
		scheduledExecutor.schedule(() -> readerWriter.write(), delay, TimeUnit.MILLISECONDS);

		jobs.get(jobId).signalQueue.add(() -> readerWriter.read());
	}
	
	/**
	 * Awaits the start of the job's execution. 
	 * If the job was scheduled on an ExecutorService the job was removed from executor's queue 
	 * and started execution on the executor's thread pool.
	 * @param jobId The id of the job.
	 */
	public void awaitExecution(int jobId) {
		try {
			SignalMachine.JobContainer job = jobs.get(jobId);
			job.runningSemaphore.acquire();
			job.runningSemaphore.release();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Awaits the termination of the job's execution. 
	 * @param jobId The id of the job.
	 */
	public void awaitTermination(int jobId) {
		try {
			SignalMachine.JobContainer job = jobs.get(jobId);
			job.finishedSemaphore.acquire();
			job.finishedSemaphore.release();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Awaits the termination of all created jobs and cleans up all open resources.
	 */
	public void awaitTermination() {
		lock.lock();
		try {
			while (counter.get() > 0) {
				condition.await();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
			scheduledExecutor.shutdown();
		}
	}
	
	private static class ReaderWriter {
		private final PipedInputStream is;
		private final PipedOutputStream os;
		private final PrintWriter writer;
		private final BufferedReader reader;

		public ReaderWriter() {
			try {
				is = new PipedInputStream();
				os = new PipedOutputStream(is);
				writer = new PrintWriter(os);
				reader = new BufferedReader(new InputStreamReader(is));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		public void read() {
			try {
//				System.out.printf("Job %d about to read\n", id);
				while (reader.readLine() != null) { }
				is.close();
//				System.out.printf("Job %d finished reading\n", id);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		public void write() {
			try {
				writer.println("Hello."); 
				os.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private class JobContainer implements Runnable {
		final BlockingQueue<Runnable> signalQueue = Queues.newLinkedBlockingDeque();
		final Semaphore runningSemaphore = new Semaphore(0);
		final Semaphore finishedSemaphore = new Semaphore(0);
		final int id;
		public JobContainer(int id) {
			this.id = id;
		}
			
		@Override
		public void run() {
			try {
				runningSemaphore.release();
				
				Runnable block = signalQueue.take();
				while (block != TERM) {
					block.run();
					block = signalQueue.take();
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} finally {
				counter.decrementAndGet();
				finishedSemaphore.release();
				lock.lock();
				try {
					condition.signal();
				} finally {
					lock.unlock();
				}
			}
		}
		
	}

}
