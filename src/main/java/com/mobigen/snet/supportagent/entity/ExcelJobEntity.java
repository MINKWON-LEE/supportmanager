package com.mobigen.snet.supportagent.entity;

public class ExcelJobEntity {
	private String reqCd;
	private Integer jobFlag;
	private String jobFileNm;
	private String reqUser;
	/**
	 * @return the reqCd
	 */
	public String getReqCd() {
		return reqCd;
	}
	/**
	 * @param reqCd the reqCd to set
	 */
	public void setReqCd(String reqCd) {
		this.reqCd = reqCd;
	}
	/**
	 * @return the jobFlag
	 */
	public Integer getJobFlag() {
		return jobFlag;
	}
	/**
	 * @param jobFlag the jobFlag to set
	 */
	public void setJobFlag(Integer jobFlag) {
		this.jobFlag = jobFlag;
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
	/**
	 * @return the reqUser
	 */
	public String getReqUser() {
		return reqUser;
	}
	/**
	 * @param reqUser the reqUser to set
	 */
	public void setReqUser(String reqUser) {
		this.reqUser = reqUser;
	}
}
