package com.mobigen.snet.supportagent.models;

import lombok.*;
import org.apache.ibatis.type.Alias;

/**
 * 'sg_supprotmanager 프로젝트 - 스케줄러'
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Alias("SnetAssetUser")
public class SnetAssetUserDto {

    private String assetCd;
    private String teamId;
    private String teamNm;
    private String userId;
    private String userNm;
    private String userType;
    private String userAuth;
    private String userMs;
    private String userMail;
    private String createDate;

    private String branchId;
}