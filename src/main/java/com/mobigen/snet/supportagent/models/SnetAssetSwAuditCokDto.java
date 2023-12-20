package com.mobigen.snet.supportagent.models;

import lombok.*;
import org.apache.ibatis.type.Alias;


/**
 * 'sg_supprotmanager 프로젝트 - 스케줄러'
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Alias("SnetAssetSwAuditCok")
public class SnetAssetSwAuditCokDto {

    // report
    private String itemResult;
    private String itemGrade;
    private String auditDay;
    private String assetCd;
    private String swType;
    private String swNm;

    // cok, auditDay
    private String cokAuditDay;
    private String cokAssetCd;
    private String diagnosisCd;
    private String adminOkFlag;
    private String adminUserId;
    private String adminOkDate;
    private String itemCokReqUserId;
    private String itemCokReqDate;
    private String itemCokReason;
    private String itemCokReqValidDate;
    private String actionItemResult;
    private String swDir;
    private String swUser;
    private String swEtc;
    private int count;
}
