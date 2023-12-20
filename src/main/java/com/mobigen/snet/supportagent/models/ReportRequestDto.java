package com.mobigen.snet.supportagent.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ReportRequestDto extends Entity {

    private String jobType ;
    private String excelType;
    private String reqCd;
    private String reqUser;
    private String fileType;
    private String startAuditDay;
    private String endAuditDay;
}
