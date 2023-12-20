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
@Alias("SnetStatisticsDetail")
public class SnetStatisticsDetailDto {

    private int seq;
    private String statisticsId;
    private String branchId;
    private String teamId;
    private String createDay;
    private String createTime;
    private String auditStdCd;

    private String swTypeNmKey;
    private String assetCd;
    private String auditDay;
    private String swType;

    private String swNm;
    private String hostNm;
    private double auditRate;
    private int weakCnt;

    private String swNmD;
    private String hostNmD;
    private double auditRateD;
    private int weakCntD;

    List<SnetStatisticsItemDetailDto> itemDetailList;

    // SnetStatisticsItemDetailDto
    private String  itemGrpNm;
    private double  itemGrpSecVal;
    private int     itemGrpCnt;
    private int     itemGrpGoodCnt;
    private int     itemGrpWeakCnt;
    private int     itemGrpNaCnt;
    private int     itemGrpRiskCnt;
    private int     itemGrpAllCnt;

    private String  itemGrpNmD;
    private double  itemGrpSecValD;
    private int     itemGrpCntD;
    private int     itemGrpGoodCntD;
    private int     itemGrpWeakCntD;
    private int     itemGrpNaCntD;
    private int     itemGrpRiskCntD;
    private int     itemGrpAllCntD;

    private double  weakHighRate;
    private double  weakMidRate;
    private double  weakLowRate;
    private double  weakHighRateD;
    private double  weakMidRateD;
    private double  weakLowRateD;

    private int     weakHighCnt;
    private int     weakMidCnt;
    private int     weakLowCnt;
    private int     weakHighCntD;
    private int     weakMidCntD;
    private int     weakLowCntD;

    private int     adResultHighAweekCnt;
    private int     adResultHighBweekCnt;
    private int     adResultHighCweekCnt;
    private int     adResultHighDweekCnt;
    private int     adResultMidAweekCnt;
    private int     adResultMidBweekCnt;
    private int     adResultMidCweekCnt;
    private int     adResultMidDweekCnt;
    private int     adResultLowAweekCnt;
    private int     adResultLowBweekCnt;
    private int     adResultLowCweekCnt;
    private int     adResultLowDweekCnt;
    private int     adResultHighAweekCntD;
    private int     adResultHighBweekCntD;
    private int     adResultHighCweekCntD;
    private int     adResultHighDweekCntD;
    private int     adResultMidAweekCntD;
    private int     adResultMidBweekCntD;
    private int     adResultMidCweekCntD;
    private int     adResultMidDweekCntD;
    private int     adResultLowAweekCntD;
    private int     adResultLowBweekCntD;
    private int     adResultLowCweekCntD;
    private int     adResultLowDweekCntD;

    private int     adExceptHighAweekCnt;
    private int     adExceptHighBweekCnt;
    private int     adExceptHighCweekCnt;
    private int     adExceptHighDweekCnt;
    private int     adExceptMidAweekCnt;
    private int     adExceptMidBweekCnt;
    private int     adExceptMidCweekCnt;
    private int     adExceptMidDweekCnt;
    private int     adExceptLowAweekCnt;
    private int     adExceptLowBweekCnt;
    private int     adExceptLowCweekCnt;
    private int     adExceptLowDweekCnt;
    private int     adExceptHighAweekCntD;
    private int     adExceptHighBweekCntD;
    private int     adExceptHighCweekCntD;
    private int     adExceptHighDweekCntD;
    private int     adExceptMidAweekCntD;
    private int     adExceptMidBweekCntD;
    private int     adExceptMidCweekCntD;
    private int     adExceptMidDweekCntD;
    private int     adExceptLowAweekCntD;
    private int     adExceptLowBweekCntD;
    private int     adExceptLowCweekCntD;
    private int     adExceptLowDweekCntD;

    private String division;

    private String today;
}
