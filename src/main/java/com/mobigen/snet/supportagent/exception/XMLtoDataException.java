/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.exception
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 6. 1.
 * description : 
 */
package com.mobigen.snet.supportagent.exception;

/**
 * @author Hyeon-sik Jung
 *
 */
public class XMLtoDataException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message;

	public XMLtoDataException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	

}
