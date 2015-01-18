package com.github.nill14.utils.executor;

import java.util.concurrent.Executor;

import javax.swing.SwingUtilities;

/**
 * 
 * Executor adapter scheduling on Swing event dispatch thread.
 *
 */
public class SwingExecutor implements Executor {

	private static final SwingExecutor INSTANCE = new SwingExecutor();
	
	public static final SwingExecutor instance() {
		return INSTANCE;
	}
	
	@Override
	public void execute(Runnable command) {
		SwingUtilities.invokeLater(command);
	}

}