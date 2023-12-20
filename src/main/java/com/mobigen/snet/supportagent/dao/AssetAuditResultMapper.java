package com.mobigen.snet.supportagent.dao;

import com.mobigen.snet.supportagent.models.SnetAssetSwAuditExcelDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 'sg_supprotmanager 프로젝트 - 스케줄러'
 */
@Repository
public interface AssetAuditResultMapper {

    List<SnetAssetSwAuditExcelDto> getAssetSwAuditResultList(Map paramMap);

    List<SnetAssetSwAuditExcelDto> getAssetSwAuditResultReportList(Map paramMap);
}
