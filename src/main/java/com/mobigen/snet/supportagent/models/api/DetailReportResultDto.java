package com.mobigen.snet.supportagent.models.api;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class DetailReportResultDto {

    private String swType;                  // 점검군

    private String itemGrpNm;               // 분류항목
    private int    itemGrpCnt;              // 개별항목수
    private int    itemGrpAllCnt;           // 전체항목수

    private int    sChkweakHighCnt;         // 보안점검 점검결과.취약점.상
    private double sChkweakHighCntRate;     // 보안점검 점검결과.취약점.상 비율
    private int    sChkweakMidCnt;          // 보안점검 점검결과.취약점.중
    private int    sChkweakLowCnt;          // 보안점검 점검결과.취약점.하
    private int    sChkweakMidLowCnt;       // 보안점검 점검결과.취약점.중하
    private double sChkweakMidLowCntRate;   // 보안점검 점검결과.취약점.중하 비율
    private int    sChkweakTotalCnt;        // 보안점검 취약점 Total
    private double sChkitemGrpSecVal;       // 보안점검 보안지수
    private String sChkitemGrpSecLev;       // 보안점검 보안수준

    private int    pChkweakHighCnt;         // 이행점검 점검결과.취약점.상
    private int    pChkweakHighCntRate;     // 이행점검 점검결과.취약점.상 비율
    private int    pChkweakMidCnt;          // 이행점검 점검결과.취약점.중
    private int    pChkweakLowCnt;          // 이행점검 점검결과.취약점.하
    private int    pChkweakMidLowCnt;       // 이행점검 점검결과.취약점.중하
    private int    pChkweakMidLowCntRate;   // 이행점검 점검결과.취약점.중하 비율
    private int    pChkweakRmnCnt;          // 이행점검 점검결과.잔여 취약점 수
    private int    pChkweakMeaCnt;          // 이행점검 점검결과.조치된 취약점 수
    private int    pChkweakMeaCntRate;      // 이행점검 점검결과.취약점 조치율
    private int    pChkweakTotalCnt;        // 이행점검 취약점 Total
    private int    pChkitemGrpSecVal;       // 이행점검 보안지수
    private String pChkitemGrpSecLev;       // 이행점검 보안수준
}