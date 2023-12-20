/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.entity
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 5. 25.
 * description : 
 */
package com.mobigen.snet.supportagent.entity;

import java.util.List;


/**
 * @author Hyeon-sik Jung
 *
 */
public class Ports {

	private Extraports extraports;
	private List<Port> port;
	/**
	 * @return the extraports
	 */
	public Extraports getExtraports() {
		return extraports;
	}
	/**
	 * @param extraports the extraports to set
	 */
	public void setExtraports(Extraports extraports) {
		this.extraports = extraports;
	}
	/**
	 * @return the port
	 */
	public List<Port> getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(List<Port> port) {
		this.port = port;
	}
	
	
}
