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
public class Times {

	private String srtt;
	private String rttvar;
	private String to;
	/**
	 * @return the srtt
	 */
	public String getSrtt() {
		return srtt;
	}
	/**
	 * @param srtt the srtt to set
	 */
	public void setSrtt(String srtt) {
		this.srtt = srtt;
	}
	/**
	 * @return the rttvar
	 */
	public String getRttvar() {
		return rttvar;
	}
	/**
	 * @param rttvar the rttvar to set
	 */
	public void setRttvar(String rttvar) {
		this.rttvar = rttvar;
	}
	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}
	/**
	 * @param to the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}
	
}
