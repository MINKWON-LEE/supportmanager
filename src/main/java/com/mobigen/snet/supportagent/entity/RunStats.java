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
public class RunStats {

	private Finished finished;
	private Hosts hosts;
	/**
	 * @return the finished
	 */
	public Finished getFinished() {
		return finished;
	}
	/**
	 * @param finished the finished to set
	 */
	public void setFinished(Finished finished) {
		this.finished = finished;
	}
	/**
	 * @return the hosts
	 */
	public Hosts getHosts() {
		return hosts;
	}
	/**
	 * @param hosts the hosts to set
	 */
	public void setHosts(Hosts hosts) {
		this.hosts = hosts;
	}
}
