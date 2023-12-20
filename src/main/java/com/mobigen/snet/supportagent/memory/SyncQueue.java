package com.mobigen.snet.supportagent.memory;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SyncQueue {
	
	private final ConcurrentLinkedQueue<Object> queue = new ConcurrentLinkedQueue<Object>();
	private String name = "";
	private int maxSize = 0;
	
	public SyncQueue() {
	}
	
	/**
	 * @param name
	 */
	public SyncQueue(String name) {
		this.name = name;
	}
	
	/**
	 * @param name
	 * @param maxSize
	 */
	public SyncQueue(String name, int maxSize) {
		this.name = name;
		this.maxSize = maxSize;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the maxSize
	 */
	public int getMaxSize() {
		return maxSize;
	}
	
	/**
	 * @param maxSize the maxSize to set
	 */
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public int getSize() {
		return queue.size();
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public Object pop() {
		return queue.poll();
	}
	
	public Object[] toArray() {
		return queue.toArray();
	}

	public void push(Object obj) {
		if (maxSize != 0 && queue.size() >= maxSize) {
			queue.clear();
		}
		
		queue.add(obj);
	}
	
	public void push(List<Object> list) {
		if (maxSize != 0 && queue.size() >= maxSize) {
			queue.clear();
		}
		
		queue.addAll(list);
	}

}
