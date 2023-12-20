package com.mobigen.snet.supportagent.dao;

import com.mobigen.snet.supportagent.models.SnetAssetMasterDto;
import com.mobigen.snet.supportagent.models.SnetAssetSwAuditExceptDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 'sg_supprotmanager 프로젝트 - 스케줄러'
 */
@Repository
public interface AssetExceptMapper {

    int getAssetExceptReqCnt(Map paramMap);

    int getAssetExceptOkCnt(Map paramMap);

    int getAssetExceptNokCnt(Map paramMap);

    List<SnetAssetSwAuditExceptDto> getDiagnosisexclusionByImportanceStatus(Map paramMap);
}
