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
public class ScanInfo {

	private String protocol; 
	private String numservices; 
	private String services;
	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}
	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	/**
	 * @return the numservices
	 */
	public String getNumservices() {
		return numservices;
	}
	/**
	 * @param numservices the numservices to set
	 */
	public void setNumservices(String numservices) {
		this.numservices = numservices;
	}
	/**
	 * @return the services
	 */
	public String getServices() {
		return services;
	}
	/**
	 * @param services the services to set
	 */
	public void setServices(String services) {
		this.services = services;
	} 
}
