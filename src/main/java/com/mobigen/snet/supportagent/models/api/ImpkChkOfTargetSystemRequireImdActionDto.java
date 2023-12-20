package com.mobigen.snet.supportagent.models.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 'sg_was 프로젝트 - 보고서'
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImpkChkOfTargetSystemRequireImdActionDto {

    private String swType;
    List<ImpkChkOfTargetSystemRequireImdActionDetailDto> detailList;
}