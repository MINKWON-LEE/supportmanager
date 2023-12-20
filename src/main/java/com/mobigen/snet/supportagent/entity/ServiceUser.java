/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.entity
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 5. 18.
 * description : 
 */
package com.mobigen.snet.supportagent.entity;

/**
 * @author Hyeon-sik Jung
 *
 */
public class ServiceUser {
	private String svcCd;
	private String teamId;
	private String teamNm;
	private String userId;
	private String userNm;
	private String userType;
	private String userMs;
	private String userMail;
	/**
	 * @return the svcCd
	 */
	public String getSvcCd() {
		return svcCd;
	}
	/**
	 * @param svcCd the svcCd to set
	 */
	public void setSvcCd(String svcCd) {
		this.svcCd = svcCd;
	}
	/**
	 * @return the teamId
	 */
	public String getTeamId() {
		return teamId;
	}
	/**
	 * @param teamId the teamId to set
	 */
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	/**
	 * @return the teamNm
	 */
	public String getTeamNm() {
		return teamNm;
	}
	/**
	 * @param teamNm the teamNm to set
	 */
	public void setTeamNm(String teamNm) {
		this.teamNm = teamNm;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the userNm
	 */
	public String getUserNm() {
		return userNm;
	}
	/**
	 * @param userNm the userNm to set
	 */
	public void setUserNm(String userNm) {
		this.userNm = userNm;
	}
	/**
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}
	/**
	 * @param userType the userType to set
	 */
	public void setUserType(String userType) {
		this.userType = userType;
	}
	/**
	 * @return the userMs
	 */
	public String getUserMs() {
		return userMs;
	}
	/**
	 * @param userMs the userMs to set
	 */
	public void setUserMs(String userMs) {
		this.userMs = userMs;
	}
	/**
	 * @return the userMail
	 */
	public String getUserMail() {
		return userMail;
	}
	/**
	 * @param userMail the userMail to set
	 */
	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}
}
