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
@Alias("SnetAssetSwAuditExcept")
public class SnetAssetSwAuditExceptDto {

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
    private String exceptState;
    private String exceptReqUserid;
    private String exceptReqDate;
    private String exceptReason;
    private String exceptActionDate;
    private String adminOkFlag;
    private String adminUserId;
    private String adminOkdate;
    private String swDir;
    private String swUser;
    private String swEtc;
}
