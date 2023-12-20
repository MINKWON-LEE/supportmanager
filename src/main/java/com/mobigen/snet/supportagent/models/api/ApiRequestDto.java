package com.mobigen.snet.supportagent.models.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiRequestDto {

    private String userId;
    private String startDay;
    private String endDay;
    private String auditType;
    private String swType;
}
