/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.entity
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 4. 22.
 * description : 
 */
package com.mobigen.snet.supportagent.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Hyeon-sik Jung
 *
 */
public class ReportEntity {

	private String assetCd;
	private String ipAddress;
	private String hostNm;
	private String swType;
	private String swNm;
	private String swInfo;
	private String swDir;
	private String swUser;
	private String swEtc;
	private String auditDay;
	private String itemCokReason;
	private String diagnosisCd;
	private String itemGrpNm;
	private String itemNm;
	private String itemGrade;
	private String itemStandard;
	private String itemStatus;
	private String itemResult;
	private String itemCountermeasure; 
	private String itemCountermeasureDetail;
	private Date createDate;
	
	
	public String getAssetCd() {
		return assetCd;
	}


	public void setAssetCd(String assetCd) {
		this.assetCd = assetCd;
	}


	public String getIpAddress() {
		return ipAddress;
	}


	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	public String getHostNm() {
		return hostNm;
	}


	public void setHostNm(String hostNm) {
		this.hostNm = hostNm;
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


	public String getAuditDay() {
		return auditDay;
	}


	public void setAuditDay(String auditDay) {
		this.auditDay = auditDay;
	}


	public String getItemCokReason() {
		return itemCokReason;
	}


	public void setItemCokReason(String itemCokReason) {
		this.itemCokReason = itemCokReason;
	}


	public String getDiagnosisCd() {
		return diagnosisCd;
	}


	public void setDiagnosisCd(String diagnosisCd) {
		this.diagnosisCd = diagnosisCd;
	}


	public String getItemGrpNm() {
		return itemGrpNm;
	}


	public void setItemGrpNm(String itemGrpNm) {
		this.itemGrpNm = itemGrpNm;
	}


	public String getItemNm() {
		return itemNm;
	}


	public void setItemNm(String itemNm) {
		this.itemNm = itemNm;
	}


	public String getItemGrade() {
		return itemGrade;
	}


	public void setItemGrade(String itemGrade) {
		this.itemGrade = itemGrade;
	}


	public String getItemStandard() {
		return itemStandard;
	}


	public void setItemStandard(String itemStandard) {
		this.itemStandard = itemStandard;
	}


	public String getItemStatus() {
		return itemStatus;
	}


	public void setItemStatus(String itemStatus) {
		this.itemStatus = itemStatus;
	}


	public String getItemResult() {
		return itemResult;
	}


	public void setItemResult(String itemResult) {
		this.itemResult = itemResult;
	}


	public String getItemCountermeasure() {
		return itemCountermeasure;
	}


	public void setItemCountermeasure(String itemCountermeasure) {
		this.itemCountermeasure = itemCountermeasure;
	}


	public String getItemCountermeasureDetail() {
		return itemCountermeasureDetail;
	}


	public void setItemCountermeasureDetail(String itemCountermeasureDetail) {
		this.itemCountermeasureDetail = itemCountermeasureDetail;
	}


	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	public String getCreateDateToString(){
		SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return transFormat.format(this.createDate);
	}
}
