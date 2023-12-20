package com.mobigen.snet.supportagent.service;

import com.mobigen.snet.supportagent.models.*;

import java.util.List;

/**
 * 'sg_supprotmanager 프로젝트 - 스케줄러'
 */
public interface StatisticsService {

    List<SnetAssetUserDto> getAssetUserFirstList(SnetAssetUserDto paramMap);

    List<SnetAssetUserParamDto> getAssetUserList(List<SnetAssetUserDto> userParamDto);

    List<SnetAssetMasterResultDto> getAssetMasterList(List<SnetAssetUserParamDto> userLists);

    SnetStatisticsDto getStatisticsDto2(SnetAssetMasterResultDto resultDto2, String userType, boolean todayFlag) throws Exception;

    SnetStatisticsDto getStatisticsDto3(SnetStatisticsDto snetStatisticsDto2, SnetAssetMasterResultDto resultDto2);

    List<SnetStatisticsDto> getAssetSwTypeNmStatusList5(SnetStatisticsDto snetStatisticsDto4, SnetAssetMasterResultDto resultDto2, String userType);

    List<SnetStatisticsDto> getAssetSwAuditRateList6(List<SnetStatisticsDto> reportList5, SnetAssetMasterResultDto resultDto2, String auditRateFlag, boolean dailyFlag, boolean reportCheckFlag, String userType, boolean todayFlag) throws Exception;

    List<SnetStatisticsDto> getAuditStatistics1(String branchId, List<SnetStatisticsDto> reportList6, SnetAssetMasterResultDto resultDto2);

    List<SnetStatisticsDto> getAssetSwAuditRateList7(List<SnetStatisticsDto> snetStatisticsCommonDtoList1, SnetAssetMasterResultDto resultDto2);

    List<SnetStatisticsDto> getAssetSwAuditRateList8(List<SnetStatisticsDto> snetStatisticsCommonDtoList1, SnetAssetMasterResultDto resultDto2);

    void deleteAuditStatistics11(SnetAssetMasterResultDto resultDto, boolean todayFlag);

    void insertAuditStatistics12(List<SnetStatisticsDto> snetStatisticsCommonDtoList1, SnetAssetMasterResultDto resultDto2, boolean todayFlag) throws Exception;

    List<SnetStatisticsDto> getDisanosisresultByImportanceStatus9(List<SnetStatisticsDto> statisticsDetailList2, SnetAssetMasterResultDto resultDto2);

    List<SnetStatisticsDto> getDiagnosisexclusionByImportanceStatus10(List<SnetStatisticsDto> statisticsDetailList3, SnetAssetMasterResultDto resultDto2);

    void insertAuditStatistics13(List<SnetStatisticsDto> statisticsDetailList10, SnetAssetMasterResultDto resultDto2, boolean todayFlag) throws Exception;

    List<SnetStatisticsDto> getAssetSwAuditRateList6B(List<SnetStatisticsDto> reportList52, SnetAssetMasterResultDto resultDto2, String b, boolean b1, boolean b2, String se, List<SnetStatisticsDto> reportListSU, boolean todayFlag) throws Exception;

    List<SnetStatisticsDto> getAssetSwAuditRateList6T(List<SnetStatisticsDto> reportList53, SnetAssetMasterResultDto resultDto2, String t, boolean b, boolean b1, String sv, List<SnetStatisticsDto> reportListSU, List<SnetStatisticsDto> reportListSE, boolean todayFlag) throws Exception;

    void insertAuditStatistics12Report(List<SnetStatisticsDto> statisticsDetailList7) throws Exception;

    List<SnetStatisticsDto> getAssetSwAuditRateList7Report(List<SnetStatisticsDto> snetStatisticsCommonDtoList1, SnetAssetMasterResultDto resultDto2);

    void insertAuditStatisticsMig(SnetAssetMasterResultDto resultDto);

    void deleteAuditStatisticsMig(SnetAssetMasterResultDto resultDto);

    void secCategoryBatch(String createDate);
}
