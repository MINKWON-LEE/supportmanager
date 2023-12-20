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
public class SnetAssetMasterParamDto {

    private String branchId;
    private String teamId;
    private String userType;
    private String auditStandard;
    private int auditDayRange;
    private int count;

    private String swType;
    private String swNm;
    private String assetCd;
    private String auditDay;
    private String createDate;

    List<SnetAssetMasterResultDto> resultDtos;
    List<SnetAssetMasterResultDto> allDtos;
    List<SnetAssetMasterDto> masterDtos;
}
