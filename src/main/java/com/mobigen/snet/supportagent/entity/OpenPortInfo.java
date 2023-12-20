/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.entity
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 6. 20.
 * description : 
 */
package com.mobigen.snet.supportagent.entity;

/**
 * @author Hyeon-sik Jung
 *
 */
public class OpenPortInfo {
	
	private String hostNm;
	private String auditDay;
	private String ipAddress;
	private String openType;
	private String openPort;
	private String processNm;
	
	/**
	 * @return the hostNm
	 */
	public String getHostNm() {
		return hostNm;
	}
	/**
	 * @param hostNm the hostNm to set
	 */
	public void setHostNm(String hostNm) {
		this.hostNm = hostNm;
	}
	/**
	 * @return the auditDay
	 */
	public String getAuditDay() {
		return auditDay;
	}
	/**
	 * @param auditDay the auditDay to set
	 */
	public void setAuditDay(String auditDay) {
		this.auditDay = auditDay;
	}
	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}
	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	/**
	 * @return the openType
	 */
	public String getOpenType() {
		return openType;
	}
	/**
	 * @param openType the openType to set
	 */
	public void setOpenType(String openType) {
		this.openType = openType;
	}
	/**
	 * @return the openPort
	 */
	public String getOpenPort() {
		return openPort;
	}
	/**
	 * @param openPort the openPort to set
	 */
	public void setOpenPort(String openPort) {
		this.openPort = openPort;
	}
	/**
	 * @return the processNm
	 */
	public String getProcessNm() {
		return processNm;
	}
	/**
	 * @param processNm the processNm to set
	 */
	public void setProcessNm(String processNm) {
		this.processNm = processNm;
	}
}
