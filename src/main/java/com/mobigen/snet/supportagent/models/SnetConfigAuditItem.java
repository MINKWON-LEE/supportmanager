package com.mobigen.snet.supportagent.models;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Setter
@Getter
@Alias("SnetConfigAuditItem")
public class SnetConfigAuditItem {

    private String auditFileCd;
    private String diagnosisCd;
    private int diagnosisType;
    private String swNm;
    private String itemgrpNm;
    private String itemNm;
    private String itemGrade;
    private String itemStandard;
    private String itemCountermeasure;
    private String itemCountermeasureDetail;
}
