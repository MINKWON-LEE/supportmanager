package com.mobigen.snet.supportagent.models.api;

import com.mobigen.snet.supportagent.models.TotalOverallResultDto;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TotalOverallDto {

    private int seq;
    private String swType;

    private int assetCntTotal;    // 보안점검.점검수량 Total
    private int itemCntTotal;     // 보안점검.점검항목수 Total
    private int allItemCntTotal;  // 보안점검.전체점검항목수 Total
    private int schkGoodCntTotal; // 보안점검.양호 Total
    private int schkWeakCntTotal; // 보안점검.취약 Total
    private int schkNaCntTotal;   // 보안점검.NA Total
    private double schkSecValTotal;  // 보안점검.보안지수 Total
    private String schkSecLevTotal;  // 보안점검.보안수준 Total

    private int pchkGoodCntTotal; // 이행점검.양호 Total
    private int pchkWeakCntTotal; // 이행점검.취약 Total
    private int pchkNaCntTotal;   // 이행점검.NA Total
    private int pchkRiskCntTotal; // 이행점검.위험수용 Total
    private double pchkSecValTotal;  // 이행점검.보안지수 Total
    private String pchkSecLevTotal;  // 이행점검.보안수준 Total

    List<TotalOverallResultDto> resultDtoList;
}