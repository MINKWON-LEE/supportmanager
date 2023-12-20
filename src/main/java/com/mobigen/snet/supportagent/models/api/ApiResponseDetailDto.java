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
public class ApiResponseDetailDto {

    List<ImpkChkOfTargetSystemRequireImdActionDto> systemRequireImdActionDtoList;
}
