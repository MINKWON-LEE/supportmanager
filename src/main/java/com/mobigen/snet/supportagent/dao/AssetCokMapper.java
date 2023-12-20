package com.mobigen.snet.supportagent.dao;

import com.mobigen.snet.supportagent.entity.ExcelListEntity;
import com.mobigen.snet.supportagent.models.SnetAssetMasterDto;
import com.mobigen.snet.supportagent.models.SnetAssetSwAuditCokDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 'sg_supprotmanager 프로젝트 - 스케줄러'
 */
@Repository
public interface AssetCokMapper {

    List<String> getAssetCokReqCnt(Map paramMap);

    List<String> getAssetCokWaitCnt(Map paramMap);

    List<String> getAssetCokOkCnt(Map paramMap);

    List<String> getAssetCokNokCnt(Map paramMap);

    List<SnetAssetSwAuditCokDto> getDisanosisresultByImportanceStatus(Map paramMap);

    List<SnetAssetSwAuditCokDto> getAssetSwAuditCokInfo(ExcelListEntity excelEntityNotAudit);

    List<SnetAssetSwAuditCokDto> getDisanosisresultByImportanceExceptStatus(Map paramMap);
}
