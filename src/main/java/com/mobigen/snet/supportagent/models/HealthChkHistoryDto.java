package com.mobigen.snet.supportagent.models;

import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

/**
 * 'sg_supprotmanager 프로젝트 - 스케줄러'
 */
@Data
@ToString
@Alias("HealthChkHistory")
public class HealthChkHistoryDto {

    private String assetCd;
    private String agentCd;

    //알림관련 추가
    private String hostNm;
    private String ipAddress;
    private String userId;
}
