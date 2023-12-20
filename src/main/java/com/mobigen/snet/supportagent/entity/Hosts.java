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
public class Hosts {

	private String up;
	private String down;
	private String total;
	/**
	 * @return the up
	 */
	public String getUp() {
		return up;
	}
	/**
	 * @param up the up to set
	 */
	public void setUp(String up) {
		this.up = up;
	}
	/**
	 * @return the down
	 */
	public String getDown() {
		return down;
	}
	/**
	 * @param down the down to set
	 */
	public void setDown(String down) {
		this.down = down;
	}
	/**
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(String total) {
		this.total = total;
	}
}
