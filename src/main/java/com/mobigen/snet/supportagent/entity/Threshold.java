/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.entity
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 8. 4.
 * description : 
 */
package com.mobigen.snet.supportagent.entity;

/**
 * @author Hyeon-sik Jung
 *
 */
public class Threshold {

	private String checkTarget;
	
	private String checkValue;
	
	private String alarmLevel;
	
	private String alarmDesc;

	/**
	 * @return the checkTarget
	 */
	public String getCheckTarget() {
		return checkTarget;
	}

	/**
	 * @param checkTarget the checkTarget to set
	 */
	public void setCheckTarget(String checkTarget) {
		this.checkTarget = checkTarget;
	}

	/**
	 * @return the checkValue
	 */
	public String getCheckValue() {
		return checkValue;
	}

	/**
	 * @param checkValue the checkValue to set
	 */
	public void setCheckValue(String checkValue) {
		this.checkValue = checkValue;
	}

	/**
	 * @return the alarmLevel
	 */
	public String getAlarmLevel() {
		return alarmLevel;
	}

	/**
	 * @param alarmLevel the alarmLevel to set
	 */
	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	/**
	 * @return the alarmDesc
	 */
	public String getAlarmDesc() {
		return alarmDesc;
	}

	/**
	 * @param alarmDesc the alarmDesc to set
	 */
	public void setAlarmDesc(String alarmDesc) {
		this.alarmDesc = alarmDesc;
	}
	
	
}
