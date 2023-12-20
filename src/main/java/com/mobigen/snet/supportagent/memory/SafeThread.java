package com.mobigen.snet.supportagent.memory;

public class SafeThread extends Thread {
	
	protected boolean isShutdown = false;
	protected long interval = 0;
	
	/**
	 * @return the interval
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	/**
	 * @return the isShutdown
	 */
	public boolean isShutdown() {
		return isShutdown;
	}

	public boolean startup() {
		boolean result = false;
		
		synchronized (this) {
			isShutdown = false;
		}
		
		if (!isAlive()) {
			start();
			result = true;
		}
		
		return result;
	}
	
	public void shutdownSafe() {
		synchronized (this) {
			isShutdown = true;
		}
	}
}
