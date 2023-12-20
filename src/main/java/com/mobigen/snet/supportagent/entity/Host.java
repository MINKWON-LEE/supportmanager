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
public class Host {

	private String starttime;
	private String endtime;
	
	private Status status;
	
	private Address address;
	
	private HostNames hostnames;
	
	private Ports ports;
	
	private Os os;
	
	private Uptime uptime;
	
	private Distance distance;
	
	private Tcpsequence tcpsequence;
	
	private Ipidsequence ipidsequence;
	
	private Tcptssequence tcptssequence;
	
	private Times times;

	/**
	 * @return the starttime
	 */
	public String getStarttime() {
		return starttime;
	}

	/**
	 * @param starttime the starttime to set
	 */
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	/**
	 * @return the endtime
	 */
	public String getEndtime() {
		return endtime;
	}

	/**
	 * @param endtime the endtime to set
	 */
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * @return the hostnames
	 */
	public HostNames getHostnames() {
		return hostnames;
	}

	/**
	 * @param hostnames the hostnames to set
	 */
	public void setHostnames(HostNames hostnames) {
		this.hostnames = hostnames;
	}

	/**
	 * @return the ports
	 */
	public Ports getPorts() {
		return ports;
	}

	/**
	 * @param ports the ports to set
	 */
	public void setPorts(Ports ports) {
		this.ports = ports;
	}

	/**
	 * @return the os
	 */
	public Os getOs() {
		return os;
	}

	/**
	 * @param os the os to set
	 */
	public void setOs(Os os) {
		this.os = os;
	}

	/**
	 * @return the uptime
	 */
	public Uptime getUptime() {
		return uptime;
	}

	/**
	 * @param uptime the uptime to set
	 */
	public void setUptime(Uptime uptime) {
		this.uptime = uptime;
	}

	/**
	 * @return the distance
	 */
	public Distance getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(Distance distance) {
		this.distance = distance;
	}

	/**
	 * @return the tcpsequence
	 */
	public Tcpsequence getTcpsequence() {
		return tcpsequence;
	}

	/**
	 * @param tcpsequence the tcpsequence to set
	 */
	public void setTcpsequence(Tcpsequence tcpsequence) {
		this.tcpsequence = tcpsequence;
	}

	/**
	 * @return the ipidsequence
	 */
	public Ipidsequence getIpidsequence() {
		return ipidsequence;
	}

	/**
	 * @param ipidsequence the ipidsequence to set
	 */
	public void setIpidsequence(Ipidsequence ipidsequence) {
		this.ipidsequence = ipidsequence;
	}

	/**
	 * @return the tcptssequence
	 */
	public Tcptssequence getTcptssequence() {
		return tcptssequence;
	}

	/**
	 * @param tcptssequence the tcptssequence to set
	 */
	public void setTcptssequence(Tcptssequence tcptssequence) {
		this.tcptssequence = tcptssequence;
	}

	/**
	 * @return the times
	 */
	public Times getTimes() {
		return times;
	}

	/**
	 * @param times the times to set
	 */
	public void setTimes(Times times) {
		this.times = times;
	}
}
