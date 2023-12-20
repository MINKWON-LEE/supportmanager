package com.mobigen.snet.supportagent.service;

import com.mobigen.snet.supportagent.models.DetailResultDto;
import com.mobigen.snet.supportagent.models.SnetAssetSwAuditExcelDto;
import com.mobigen.snet.supportagent.models.SnetAssetSwAuditReportDto;
import com.mobigen.snet.supportagent.models.TotalOverallResultDto;

import java.util.List;
import java.util.Map;

public interface TestService {


    // 보안점검 점검군에 대한 점검수량,점검항목수 리스트
    List<TotalOverallResultDto> getCheckTargetsList(List<SnetAssetSwAuditExcelDto> swTypeList, Map gAssetSwJob);

    // 이행점검 점검군에 대한 점검수량, 점검항목수 리스트
    List<TotalOverallResultDto> getMigTargetsList(List<SnetAssetSwAuditExcelDto> swTypeList, Map gAssetSwJob);

    // 점검군에 대한 점검결과 상세 리스트
    List<DetailResultDto> getDetailTargetsList(List<TotalOverallResultDto> swTypeList, Map gAssetSwJob, String str, boolean b) throws Exception;

    // 점검군에 대한 점검결과 상세 리스트
    List<DetailResultDto> getMigDetailTargetsList(List<TotalOverallResultDto> swTypeList, Map gAssetSwJob, String str, boolean b);

    // 즉시 조치 필요 대상시스템 리스트
    List<SnetAssetSwAuditExcelDto> getReportResultProcessTargets(Map gAssetSwJob, boolean b);

    // 취약점 상세 항목 리스트
    List<SnetAssetSwAuditReportDto> selectAssetSwAuditReportNew(Map gAssetSwJob, boolean b) throws Exception;

    // 예외 점검군에 대한 보안점검 점검결과 상세 리스트
    List<DetailResultDto> getDetailEtcTargetsList(List<TotalOverallResultDto> checkTargetsList, Map gAssetSwJob7, String url) throws Exception;

    // 예외 점검군에 대한 이행점검 점검결과 상세 리스트
    List<DetailResultDto> getMigDetailEtcTargetsList(List<TotalOverallResultDto> checkTargetsList, Map gAssetSwJob7, String nw) throws Exception;
}
