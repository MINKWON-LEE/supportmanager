package com.mobigen.snet.supportagent.entity;

public class BatchDiagnosis {

	private String assetCd;
	private String agentCd;
	private String swType;
	private String swNm;
	private String swInfo;
	private String swDir;
	private String swUser= "SYSTEM";
	private String swEtc;
	private String auditTime;
	private String auditType;
	private String auditDayList;
	private String createUserId;
	private String auditMonthList;
	private int delayTime;

	public String getAssetCd() {
		return assetCd;
	}
	public void setAssetCd(String assetCd) {
		this.assetCd = assetCd;
	}
	public String getAgentCd() {
		return agentCd;
	}
	public void setAgentCd(String agentCd) {
		this.agentCd = agentCd;
	}
	public String getSwType() {
		return swType;
	}
	public void setSwType(String swType) {
		this.swType = swType;
	}
	public String getSwNm() {
		return swNm;
	}
	public void setSwNm(String swNm) {
		this.swNm = swNm;
	}
	public String getSwInfo() {
		return swInfo;
	}
	public void setSwInfo(String swInfo) {
		this.swInfo = swInfo;
	}
	public String getSwDir() {
		return swDir;
	}
	public void setSwDir(String swDir) {
		this.swDir = swDir;
	}
	public String getSwUser() {
		return swUser;
	}
	public void setSwUser(String swUser) {
		this.swUser = swUser;
	}
	public String getSwEtc() {
		return swEtc;
	}
	public void setSwEtc(String swEtc) {
		this.swEtc = swEtc;
	}
	public String getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(String auditTime) {
		this.auditTime = auditTime;
	}
	public String getAuditType() {
		return auditType;
	}
	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}
	public String getAuditDayList() {
		return auditDayList;
	}
	public void setAuditDayList(String auditDayList) {
		this.auditDayList = auditDayList;
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public String getAuditMonthList() {
		return auditMonthList;
	}

	public void setAuditMonthList(String auditMonthList) {
		this.auditMonthList = auditMonthList;
	}
	public int getDelayTime() { return delayTime; }
	public void setDelayTime(int delayTime) { this.delayTime = delayTime; }
}
