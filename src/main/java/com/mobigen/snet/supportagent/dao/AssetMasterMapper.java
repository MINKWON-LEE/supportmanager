package com.mobigen.snet.supportagent.dao;

import com.mobigen.snet.supportagent.models.SnetAssetMasterDto;
import com.mobigen.snet.supportagent.models.SnetAssetSwAuditExcelDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 'sg_supprotmanager 프로젝트 - 스케줄러'
 */
@Repository
public interface AssetMasterMapper {

    List<SnetAssetMasterDto> getAssetMasterList(Map paramMap);

    List<SnetAssetMasterDto> getAssetMasterGroupingAgentCd(Map paramMap);

    List<SnetAssetSwAuditExcelDto> getAssetMasterAuditDayList(Map userDtoParamMap);

    SnetAssetSwAuditExcelDto getAssetNotAuditCntList(Map userDtoParamMap);

    SnetAssetSwAuditExcelDto getAssetMasterHstAuditDayCntList2(Map userDtoParamMap);
}
