package com.mobigen.snet.supportagent.models.api;

import lombok.*;

/**
 * 'sg_was 프로젝트 - 인트로'
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class DetailReportHostNmResultDto {

    private String swType;
    private String hostNm;
    private double auditRate;
    private int weakCnt;
}
