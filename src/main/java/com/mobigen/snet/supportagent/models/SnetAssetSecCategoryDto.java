package com.mobigen.snet.supportagent.models;

import lombok.*;
import org.apache.ibatis.type.Alias;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Alias("SnetAssetSecCategory")
public class SnetAssetSecCategoryDto {

    private String swType;
    private String swNm;
    private String auditDay;
    private String createDay;
    private int auditStdCd;
    private String lnotflag;
    private String notflag;
}
