/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.entity
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 5. 27.
 * description : 
 */
package com.mobigen.snet.supportagent.entity;

/**
 * @author Hyeon-sik Jung
 *
 */
public class NmapJob {

	private String jobKey;
	private String reqUserId;
	private String rangePort;
	private String rangeIp;
	private String jobStatus;
	private String jobFileNm;
	/**
	 * @return the jobKey
	 */
	public String getJobKey() {
		return jobKey;
	}
	/**
	 * @param jobKey the jobKey to set
	 */
	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}
	/**
	 * @return the reqUserId
	 */
	public String getReqUserId() {
		return reqUserId;
	}
	/**
	 * @param reqUserId the reqUserId to set
	 */
	public void setReqUserId(String reqUserId) {
		this.reqUserId = reqUserId;
	}
	/**
	 * @return the rangePort
	 */
	public String getRangePort() {
		return rangePort;
	}
	/**
	 * @param rangePort the rangePort to set
	 */
	public void setRangePort(String rangePort) {
		this.rangePort = rangePort;
	}
	/**
	 * @return the rangeIp
	 */
	public String getRangeIp() {
		return rangeIp;
	}
	/**
	 * @param rangeIp the rangeIp to set
	 */
	public void setRangeIp(String rangeIp) {
		this.rangeIp = rangeIp;
	}
	/**
	 * @return the jobStatus
	 */
	public String getJobStatus() {
		return jobStatus;
	}
	/**
	 * @param jobStatus the jobStatus to set
	 */
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	/**
	 * @return the jobFileNm
	 */
	public String getJobFileNm() {
		return jobFileNm;
	}
	/**
	 * @param jobFileNm the jobFileNm to set
	 */
	public void setJobFileNm(String jobFileNm) {
		this.jobFileNm = jobFileNm;
	}
}
