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
public class Uptime {

	private String seconds;
	private String lastboot;
	/**
	 * @return the seconds
	 */
	public String getSeconds() {
		return seconds;
	}
	/**
	 * @param seconds the seconds to set
	 */
	public void setSeconds(String seconds) {
		this.seconds = seconds;
	}
	/**
	 * @return the lastboot
	 */
	public String getLastboot() {
		return lastboot;
	}
	/**
	 * @param lastboot the lastboot to set
	 */
	public void setLastboot(String lastboot) {
		this.lastboot = lastboot;
	}
}
