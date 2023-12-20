package com.mobigen.snet.supportagent.models;

import lombok.*;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * 'sg_supprotmanager 프로젝트 - 스케줄러'
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Alias("SnetStatistics")
public class SnetStatisticsDto {

    private int     seq;
    private String  statisticsId;
    private String  branchId;
    private String  teamId;
    private String  createDay;
    private String  createTime;
    private String  auditStdCd;

    private int     adResultReq;
    private int     adResultOk;
    private int     adResultNok;
    private int     adResultReqD;
    private int     adResultOkD;
    private int     adResultNokD;

    private int     adExceptReq;
    private int     adExceptOk;
    private int     adExceptNok;
    private int     adExceptReqD;
    private int     adExceptOkD;
    private int     adExceptNokD;

    private int     equipCnt;
    private int     equipInstallNokCnt;
    private int     equipPlusCnt;
    private int     equipMinusCnt;
    private int     equipCntD;
    private int     equipInstallNokCntD;
    private int     equipPlusCntD;
    private int     equipMinusCntD;

    private String  swType;
    private int     assetNotAuditCnt;
    private int     assetNotCokCnt;
    private int     assetCnt;
    private int     assetPlusCnt;
    private int     assetMinusCnt;

    private String  swTypeD;
    private int     assetNotAuditCntD;
    private int     assetNotCokCntD;
    private int     assetCntD;
    private int     assetPlusCntD;
    private int     assetMinusCntD;

    private int     itemCnt;
    private int     allItemCnt;
    private double  schkSecVal;
    private String  schkSecLev;
    private double  pchkSecVal;
    private String  pchkSecLev;
    private double  auditRate;
    private double  allAuditRate;
    private double  allAuditRateAvg;
    private double  branchAuditRate;
    private double  branchAuditRateAvg;
    private double  teamAuditRate;
    private double  teamAuditRateAvg;

    private int     itemCntD;
    private int     allItemCntD;
    private double  schkSecValD;
    private String  schkSecLevD;
    private double  pchkSecValD;
    private String  pchkSecLevD;
    private double  auditRateD;
    private double  allAuditRateD;
    private double  allAuditRateAvgD;
    private double  branchAuditRateD;
    private double  branchAuditRateAvgD;
    private double  teamAuditRateD;
    private double  teamAuditRateAvgD;

    private String  division;

    List<SnetStatisticsDetailDto> detailList;

    // 기타
    private int equipAllCnt;
    private int equipAllCntD;
    private String assetCd;
    private String hostNm;
    private int weakCnt;
    private String swNm;
    private String today;
}
