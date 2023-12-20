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
public class Os {

	private List<Portused> portused;
	private Osmatch osmatch;
	/**
	 * @return the portused
	 */
	public List<Portused> getPortused() {
		return portused;
	}
	/**
	 * @param portused the portused to set
	 */
	public void setPortused(List<Portused> portused) {
		this.portused = portused;
	}
	/**
	 * @return the osmatch
	 */
	public Osmatch getOsmatch() {
		return osmatch;
	}
	/**
	 * @param osmatch the osmatch to set
	 */
	public void setOsmatch(Osmatch osmatch) {
		this.osmatch = osmatch;
	}
	
	
}
