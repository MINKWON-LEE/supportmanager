package com.mobigen.snet.supportagent.models;

import org.apache.ibatis.type.Alias;

@Alias("CheckAnalysis")
public class CheckAnalysisDto extends Entity {

    private String auditFileCd;
    private String diagnosisCd;
    private String diagnosisType;
    private String swNm;
    private String itemGrpNm;
    private int    cntItemGrp;
    private String itemNm;
    private String itemGrade;

    public String getAuditFileCd() {
        return auditFileCd;
    }

    public void setAuditFileCd(String auditFileCd) {
        this.auditFileCd = auditFileCd;
    }

    public String getDiagnosisCd() {
        return diagnosisCd;
    }

    public void setDiagnosisCd(String diagnosisCd) {
        this.diagnosisCd = diagnosisCd;
    }

    public String getDiagnosisType() {
        return diagnosisType;
    }

    public void setDiagnosisType(String diagnosisType) {
        this.diagnosisType = diagnosisType;
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

    public int getCntItemGrp() {
        return cntItemGrp;
    }

    public void setCntItemGrp(int cntItemGrp) {
        this.cntItemGrp = cntItemGrp;
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
}
