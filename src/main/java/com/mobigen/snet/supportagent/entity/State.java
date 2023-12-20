/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.entity
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 5. 25.
 * description : 
 */
package com.mobigen.snet.supportagent.entity;

/**
 * @author Hyeon-sik Jung
 *
 */
public class State {

	private String state;
	private String reason;
	private String reason_ttl;
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}
	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
	/**
	 * @return the reason_ttl
	 */
	public String getReason_ttl() {
		return reason_ttl;
	}
	/**
	 * @param reason_ttl the reason_ttl to set
	 */
	public void setReason_ttl(String reason_ttl) {
		this.reason_ttl = reason_ttl;
	}
	
}
