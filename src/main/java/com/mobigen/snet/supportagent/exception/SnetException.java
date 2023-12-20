package com.mobigen.snet.supportagent.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnetException extends Exception{
	private static final long serialVersionUID = -2984059452751025552L;
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private String message;

	public SnetException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
