/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.entity.MailEntity.java
 * company : Mobigen
 * @author : Je Joong Lee
 * created at : 2016. 3. 15.
 * description : 
 */

package com.mobigen.snet.supportagent.entity;

public class MailEntity {
	private String mailFrom;
	private String mailCc;
	private String mailTitle;
	private String sendMsg;
	private String fileList;
	
	/**
	 * @return the From Address
	 */
	public String getMailFrom(){
		return mailFrom;
	}
	
	/**
	 * @param mailFrom the From Address to set
	 */
	public void setMailFrom(String mailFrom){
		this.mailFrom = mailFrom;
	}
	
	/**
	 * @return the CC Address
	 */
	public String getMailCc(){
		return mailCc;
	}
	
	/**
	 * @param mailCc the CC Address to set
	 */
	public void setMailCc(String mailCc){
		this.mailCc = mailCc;
	}
	
	/**
	 * @return the mailTitle
	 */
	public String getMailTitle() {
		return mailTitle;
	}
	
	/**
	 * @param mailTitle the mailTitle to set
	 */
	public void setMailTitle(String mailTitle) {
		this.mailTitle = mailTitle;
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
	 * @return the File List
	 */
	public String getFileList(){
		return fileList;
	}
	
	/**
	 * @param fileList thet File List to set
	 */
	public void setFileList(String fileList){
		this.fileList = fileList;
	}
	
}
