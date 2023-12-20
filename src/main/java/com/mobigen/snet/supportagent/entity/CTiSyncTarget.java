package com.mobigen.snet.supportagent.entity;

public class CTiSyncTarget {
	private String cveCodeCd = "";
	private String ctiCd = "";
	private String assetSwCd = "";
	private String swType = "";
	private String orgSwType = "";
	private String swNm = "";
	private String orgSwNm = "";
	private String swInfo = "";
	private String majorSwInfo = "";
	private String orgSwInfo = "";
	private String cveCodes = "";
	private int cveCount= 0;

	private String cveId = "";
	private String cveCode = "";
	private String cweCode = "";
	private String description = "";
	private int id = 0;
	private String publishDate = "";
	private String reference = "";
	private String timeRegDate = "";
	private String updateDate = "";
	private String v2Score = "";
	private String v2Severity = "";
	private String v3Score = "";
	private String v3Severity = "";
	private String cpe = "";

	public String getCveCodeCd() {
		return cveCodeCd;
	}

	public void setCveCodeCd(String cveCodeCd) {
		this.cveCodeCd = cveCodeCd;
	}

	public String getCtiCd() {
		return ctiCd;
	}

	public void setCtiCd(String ctiCd) {
		this.ctiCd = ctiCd;
	}

	public String getAssetSwCd() {
		return assetSwCd;
	}

	public void setAssetSwCd(String assetSwCd) {
		this.assetSwCd = assetSwCd;
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

	public String getCveCodes() {
		return cveCodes;
	}

	public void setCveCodes(String cveCodes) {
		this.cveCodes = cveCodes;
	}

	public int getCveCount() {
		return cveCount;
	}

	public void setCveCount(int cveCount) {
		this.cveCount = cveCount;
	}

	public String getCveId() {
		return cveId;
	}

	public void setCveId(String cveId) {
		this.cveId = cveId;
	}

	public String getCveCode() {
		return cveCode;
	}

	public void setCveCode(String cveCode) {
		this.cveCode = cveCode;
	}

	public String getCweCode() {
		return cweCode;
	}

	public void setCweCode(String cweCode) {
		this.cweCode = cweCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getTimeRegDate() {
		return timeRegDate;
	}

	public void setTimeRegDate(String timeRegDate) {
		this.timeRegDate = timeRegDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getV2Score() {
		return v2Score;
	}

	public void setV2Score(String v2Score) {
		this.v2Score = v2Score;
	}

	public String getV2Severity() {
		return v2Severity;
	}

	public void setV2Severity(String v2Severity) {
		this.v2Severity = v2Severity;
	}

	public String getV3Score() {
		return v3Score;
	}

	public void setV3Score(String v3Score) {
		this.v3Score = v3Score;
	}

	public String getV3Severity() {
		return v3Severity;
	}

	public void setV3Severity(String v3Severity) {
		this.v3Severity = v3Severity;
	}

	public String getCpe() {
		return cpe;
	}

	public void setCpe(String cpe) {
		this.cpe = cpe;
	}

	public String getOrgSwType() {
		return orgSwType;
	}

	public void setOrgSwType(String orgSwType) {
		this.orgSwType = orgSwType;
	}

	public String getOrgSwNm() {
		return orgSwNm;
	}

	public void setOrgSwNm(String orgSwNm) {
		this.orgSwNm = orgSwNm;
	}

	public String getOrgSwInfo() {
		return orgSwInfo;
	}

	public void setOrgSwInfo(String orgSwInfo) {
		this.orgSwInfo = orgSwInfo;
	}

	public String getMajorSwInfo() {
		return majorSwInfo;
	}

	public void setMajorSwInfo(String majorSwInfo) {
		this.majorSwInfo = majorSwInfo;
	}

	@Override
	public String toString() {
		return "CTiSyncTarget{" +
				"swType='" + swType + '\'' +
				", swNm='" + swNm + '\'' +
				", swInfo='" + swInfo + '\'' +
				", cveCodes='" + cveCodes + '\'' +
				", cveCount=" + cveCount +
				", cveCode='" + cveCode + '\'' +
				", cweCode='" + cweCode + '\'' +
				", description='" + description + '\'' +
				", id='" + id + '\'' +
				", publishDate='" + publishDate + '\'' +
				", reference='" + reference + '\'' +
				", timeRegDate='" + timeRegDate + '\'' +
				", updateDate='" + updateDate + '\'' +
				", v2Score='" + v2Score + '\'' +
				", v2Severity='" + v2Severity + '\'' +
				", v3Score='" + v3Score + '\'' +
				", v3Severity='" + v3Severity + '\'' +
				", cpe='" + cpe + '\'' +
				'}';
	}
}
