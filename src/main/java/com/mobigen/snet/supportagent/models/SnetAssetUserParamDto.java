package com.mobigen.snet.supportagent.models;

import lombok.*;

import java.util.List;

/**
 * 'sg_supprotmanager 프로젝트 - 스케줄러'
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SnetAssetUserParamDto {

    private String branchId;
    private String userType;
    private String auditStandard;
    private int auditDayRange;

    List<SnetAssetUserDto> userDtos;
}
