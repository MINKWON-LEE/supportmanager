/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.entity.SMSEntity.java
 * company : Mobigen
 * @author : Je Joong Lee
 * created at : 2016. 3. 16.
 * description : 
 */

package com.mobigen.snet.supportagent.entity;

import java.util.Date;

public class SMSEntity {

	/**
	 * PK
	 */
	private String sendKey;
	private String userMs;
	private String sendMsg;
	private int sendFlag;
	private Date sendDate;
	private Date reqDate;
	
	/**
	 * @return the sendKey
	 */
	public String getSendKey() {
		return sendKey;
	}
	/**
	 * @param sendKey the sendKey to set
	 */
	public void setSendKey(String sendKey) {
		this.sendKey = sendKey;
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
	 * @return the sendMsg
	 */
	public String getSendMsg() {
		return sendMsg;
	}
	/**
	 * @param sendMsg the sendMsg to set
	 */
	public void setSendMsg(String sendMsg) {
		this.sendMsg = sendMsg;
	}
	/**
	 * @return the sendFlag
	 */
	public int getSendFlag() {
		return sendFlag;
	}
	/**
	 * @param sendFlag the sendFlag to set
	 */
	public void setSendFlag(int sendFlag) {
		this.sendFlag = sendFlag;
	}
	/**
	 * @return the sendDate
	 */
	public Date getSendDate() {
		return sendDate;
	}
	/**
	 * @param sendDate the sendDate to set
	 */
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
	/**
	 * @return the reqDate
	 */
	public Date getReqDate() {
		return reqDate;
	}
	/**
	 * @param reqDate the reqDate to set
	 */
	public void setReqDate(Date reqDate) {
		this.reqDate = reqDate;
	}
	
}
