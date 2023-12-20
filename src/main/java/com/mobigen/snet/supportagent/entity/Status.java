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
public class Status {

	private String state;
	private String reason;
	private String reson_ttl;
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
	 * @return the reson_ttl
	 */
	public String getReson_ttl() {
		return reson_ttl;
	}
	/**
	 * @param reson_ttl the reson_ttl to set
	 */
	public void setReson_ttl(String reson_ttl) {
		this.reson_ttl = reson_ttl;
	}
}
