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
public class Portused {

	private String state;
	private String proto;
	private String portid;
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
	 * @return the proto
	 */
	public String getProto() {
		return proto;
	}
	/**
	 * @param proto the proto to set
	 */
	public void setProto(String proto) {
		this.proto = proto;
	}
	/**
	 * @return the portid
	 */
	public String getPortid() {
		return portid;
	}
	/**
	 * @param portid the portid to set
	 */
	public void setPortid(String portid) {
		this.portid = portid;
	}
}
