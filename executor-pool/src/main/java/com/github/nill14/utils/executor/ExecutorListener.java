package com.github.nill14.utils.executor;

public interface ExecutorListener {
	
	void onSubmit();
	
	void beforeExecute(Thread t, Runnable r);
	
	void afterExecute(Runnable r, Throwable t);

}
