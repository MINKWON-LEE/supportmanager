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
@Alias("SnetAssetMaster")
public class SnetAssetMasterDto {

    private String assetCd;
    private String agentCd;
    private String branchId;
    private String branchNm;
    private String teamId;
    private String teamNm;
    private String ipAddress;
    private String hostNm;
    private String auditDay;
    private String auditRate;
    private String auditRateFirewall;
    private String sgwRegi;
    private String aliveCheck;
    private String svrRoomId;
    private String personalData;
    private String govFlag;
    private String createDate;
    private String updateDate;
    private String getDay;
    private String infraYn;
    private String ddd;
    private String autoAuditNDesc;
    private String autoAuditYn;
    private String auditSpeed;
    private String serviceId;
    private String ismsYn;
    private String vmYn;

    private String equipLength;
    private int equipCnt;
}
