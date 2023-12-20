package com.mobigen.snet.supportagent.models;

import org.apache.ibatis.type.Alias;

@Alias("SnetAssetSwAuditExcelList")
public class SnetAssetSwAuditExcelDto extends Entity {

    // 표지
    private String hostNm;
    private String swType;
    private String swNm;
    private String swNmLinux;
    private String swNmWindows;
    private String swInfo;
    private String swDir;
    private String swUser;
    private String swEtc;
    private String auditDay;
    private String maxAuditDay;
    private String minAuditDay;
    private String assetCd;
    private String ipAddress;
    private String hostGrade;

    // 점검 대상
    private int rownum;
    private int assetCnt;
    private int assetWindowsCnt;
    private int assetLinuxCnt;

    // hidden sheet
    private int weakCount;
    private double auditRate;
    private double auditRateByTotalMinus;
    private int auditRateFirewall;
    private double auditRateAvg;

    // 종합보고서 점검군별 장비별 취약점
    double weekHighRate;
    double weekMiddleLowRate;

    // 기타
    private String createDate;
    private String auditFileCd;
    private String fileType;
    private String itemResult;
    private int seq;
    private int notCnt;

    public int getNotCnt() {
        return notCnt;
    }

    public void setNotCnt(int notCnt) {
        this.notCnt = notCnt;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getItemResult() {
        return itemResult;
    }

    public void setItemResult(String itemResult) {
        this.itemResult = itemResult;
    }

    public String getHostNm() {
        return hostNm;
    }

    public void setHostNm(String hostNm) {
        this.hostNm = hostNm;
    }

    public String getAuditFileCd() {
        return auditFileCd;
    }

    public void setAuditFileCd(String auditFileCd) {
        this.auditFileCd = auditFileCd;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public double getWeekHighRate() {
        return weekHighRate;
    }

    public String getSwNmLinux() {
        return swNmLinux;
    }

    public void setSwNmLinux(String swNmLinux) {
        this.swNmLinux = swNmLinux;
    }

    public int getAssetCnt() {
        return assetCnt;
    }

    public void setAssetCnt(int assetCnt) {
        this.assetCnt = assetCnt;
    }

    public String getSwNmWindows() {
        return swNmWindows;
    }

    public void setSwNmWindows(String swNmWindows) {
        this.swNmWindows = swNmWindows;
    }

    public String getHostGrade() {
        return hostGrade;
    }

    public int getAssetWindowsCnt() {
        return assetWindowsCnt;
    }

    public void setAssetWindowsCnt(int assetWindowsCnt) {
        this.assetWindowsCnt = assetWindowsCnt;
    }

    public int getAssetLinuxCnt() {
        return assetLinuxCnt;
    }

    public void setAssetLinuxCnt(int assetLinuxCnt) {
        this.assetLinuxCnt = assetLinuxCnt;
    }

    public double getAuditRateByTotalMinus() {
        return auditRateByTotalMinus;
    }

    public void setAuditRateByTotalMinus(double auditRateByTotalMinus) {
        this.auditRateByTotalMinus = auditRateByTotalMinus;
    }

    public void setHostGrade(String hostGrade) {
        this.hostGrade = hostGrade;
    }

    public void setWeekHighRate(double weekHighRate) {
        this.weekHighRate = weekHighRate;
    }

    public String getMaxAuditDay() {
        return maxAuditDay;
    }

    public void setMaxAuditDay(String maxAuditDay) {
        this.maxAuditDay = maxAuditDay;
    }

    public String getMinAuditDay() {
        return minAuditDay;
    }

    public void setMinAuditDay(String minAuditDay) {
        this.minAuditDay = minAuditDay;
    }

    public double getWeekMiddleLowRate() {
        return weekMiddleLowRate;
    }

    public void setWeekMiddleLowRate(double weekMiddleLowRate) {
        this.weekMiddleLowRate = weekMiddleLowRate;
    }

    public String getSwNm() {
        return swNm;
    }

    public void setSwNm(String swNm) {
        this.swNm = swNm;
    }

    public double getAuditRateAvg() {
        return auditRateAvg;
    }

    public void setAuditRateAvg(double auditRateAvg) {
        this.auditRateAvg = auditRateAvg;
    }

    public String getSwInfo() {
        return swInfo;
    }

    public int getRownum() {
        return rownum;
    }

    public void setRownum(int rownum) {
        this.rownum = rownum;
    }

    public void setSwInfo(String swInfo) {
        this.swInfo = swInfo;
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

    public String getSwType() {
        return swType;
    }

    public void setSwType(String swType) {
        this.swType = swType;
    }

    public int getWeakCount() {
        return weakCount;
    }

    public void setWeakCount(int weakCount) {
        this.weakCount = weakCount;
    }

    public double getAuditRate() {
        return auditRate;
    }

    public void setAuditRate(double auditRate) {
        this.auditRate = auditRate;
    }

    public int getAuditRateFirewall() {
        return auditRateFirewall;
    }

    public void setAuditRateFirewall(int auditRateFirewall) {
        this.auditRateFirewall = auditRateFirewall;
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
}