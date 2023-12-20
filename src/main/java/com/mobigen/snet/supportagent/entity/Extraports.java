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
public class Extraports {

	private String state;
	private String count;
	private Extrareasons extrareasons;
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
	 * @return the count
	 */
	public String getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(String count) {
		this.count = count;
	}
	/**
	 * @return the extrareasons
	 */
	public Extrareasons getExtrareasons() {
		return extrareasons;
	}
	/**
	 * @param extrareasons the extrareasons to set
	 */
	public void setExtrareasons(Extrareasons extrareasons) {
		this.extrareasons = extrareasons;
	}
	
}
