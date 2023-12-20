package com.mobigen.snet.supportagent.models;

import java.util.List;

public class ExcelReportDto extends Entity {

    private String hostNm;
    private String ipAddress;
    private String swNm;
    private String swInfo;
    private String diagnosisCd;
    private String itemGrpNm;
    private String itemNm;
    private String itemGradeView;
    private String weekCountFormula;
    private int swNmGrpSize;
    private String txtFileNm;

    public String getTxtFileNm() {
        return txtFileNm;
    }

    public void setTxtFileNm(String txtFileNm) {
        this.txtFileNm = txtFileNm;
    }

    public int getSwNmGrpSize() {
        return swNmGrpSize;
    }

    public void setSwNmGrpSize(int swNmGrpSize) {
        this.swNmGrpSize = swNmGrpSize;
    }

    List<SnetAssetSwAuditReportDto> reportList;

    public List<SnetAssetSwAuditReportDto> getReportList() {
        return reportList;
    }

    public void setReportList(List<SnetAssetSwAuditReportDto> reportList) {
        this.reportList = reportList;
    }

    public String getHostNm() {
        return hostNm;
    }

    public String getDiagnosisCd() {
        return diagnosisCd;
    }

    public void setDiagnosisCd(String diagnosisCd) {
        this.diagnosisCd = diagnosisCd;
    }

    public String getSwInfo() {
        return swInfo;
    }

    public void setSwInfo(String swInfo) {
        this.swInfo = swInfo;
    }

    public void setHostNm(String hostNm) {
        this.hostNm = hostNm;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSwNm() {
        return swNm;
    }

    public void setSwNm(String swNm) {
        this.swNm = swNm;
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

    public String getItemGradeView() {
        return itemGradeView;
    }

    public void setItemGradeView(String itemGradeView) {
        this.itemGradeView = itemGradeView;
    }

    public String getWeekCountFormula() {
        return weekCountFormula;
    }

    public void setWeekCountFormula(String weekCountFormula) {
        this.weekCountFormula = weekCountFormula;
    }
}
