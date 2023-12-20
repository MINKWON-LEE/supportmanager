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
public class DetailReportDto {

    private int    pChkweakHighTotal;       // 이행점검 상 합계
    private int    pChkweakMidTotal;        // 이행점검 중 합계
    private int    pChkweakLowTotal;        // 이행점검 하 합계

    List<DetailReportHostNmResultDto> resultHostNmList;
    List<DetailReportResultDto> resultDtoList;
}
