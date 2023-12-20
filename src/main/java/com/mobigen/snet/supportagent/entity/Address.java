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
public class Address {

	private String addr;
	private String addrtype;
	/**
	 * @return the addr
	 */
	public String getAddr() {
		return addr;
	}
	/**
	 * @param addr the addr to set
	 */
	public void setAddr(String addr) {
		this.addr = addr;
	}
	/**
	 * @return the addrtype
	 */
	public String getAddrtype() {
		return addrtype;
	}
	/**
	 * @param addrtype the addrtype to set
	 */
	public void setAddrtype(String addrtype) {
		this.addrtype = addrtype;
	}
}
