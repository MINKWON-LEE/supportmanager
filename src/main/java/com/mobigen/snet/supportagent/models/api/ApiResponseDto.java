package com.mobigen.snet.supportagent.models.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponseDto {

    List<TotalOverallDto> totalOverallDtoList; // 종합보고서. 점검군별 전체 종합 결과 (보안점검, 이행점검)
}
