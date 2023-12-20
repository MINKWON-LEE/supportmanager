package com.mobigen.snet.supportagent.models.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 'sg_was 프로젝트 - 보고서'
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImpkChkOfTargetSystemRequireImdActionDetailDto {

    private String hostNm;
    private double auditRate;
    private double weakHighRate;
    private double weakMiddleLowRate;
}
