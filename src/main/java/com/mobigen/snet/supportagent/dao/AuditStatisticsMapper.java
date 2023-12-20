package com.mobigen.snet.supportagent.dao;

import com.mobigen.snet.supportagent.models.*;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 'sg_supprotmanager 프로젝트 - 스케줄러'
 */
@Repository
public interface AuditStatisticsMapper {

    void insertAuditStatistics(SnetStatisticsDto snetStatisticsDto) throws Exception;

    void insertAuditStatisticsDetail(SnetStatisticsDetailDto detailDto);

    SnetStatisticsDto getAssetCntByBeforeCreateDay(Map statisticsMap);

    void deleteAuditStatisticsDetail(SnetStatisticsDto dto);

    void deleteAuditStatistics(SnetStatisticsDto dto);

    int selectAuditStatisticsSeq(SnetStatisticsDto dto);

    int getEquipPlusCnt();

    int getEquipMinusCnt();

    void insertAuditStatisticsItemDetail(SnetStatisticsItemDetailDto itemdetailDto);

    void insertAuditStatisticsToday(SnetStatisticsDto statisticsDto);

    void deleteAuditStatisticsDetailToday(SnetStatisticsDto dto);

    void deleteAuditStatisticsToday(SnetStatisticsDto dto);

    void insertAuditStatisticsDetailToday(SnetStatisticsDetailDto dto);

    void insertAuditStatisticsItemDetailToday(SnetStatisticsItemDetailDto itemdetailDto);

    int getEquipPlusCntForDay(Map statisticsMap);

    int getEquipMinusCntForDay(Map statisticsMap);

    void deleteInsertAuditStatisticsMig(SnetStatisticsDto dto);

    void deleteInsertAuditStatisticsMigDetail(SnetStatisticsDto dto);

    SnetStatisticsDto getStatisticsTodayMaxDay(SnetAssetMasterResultDto resultDto);

    SnetStatisticsDto getStatisticsTodayMaxTime(SnetStatisticsDto dto);

    int selectIntroSecCategoryCnt();

    void deleteIntroSecCategory();

    void insertIntroSecCategory(SnetAssetSecCategoryDto resultDto);

    void deleteIntroCokStatus();

    void insertIntroCokStatus();

    void deleteIntroEquipStatus();

    void insertIntroEquipStatus();

    void deleteIntroAssetStatus();

    void insertIntroAssetStatus();

    void deleteIntroAssetStatusDetail();

    void insertIntroAssetStatusDetail();

    void deleteIntroAssetAuditRateTop10();

    void insertIntroAssetAuditRateTop10();

    void deleteIntroValnerabilityTop10();

    void insertIntroValnerabilityTop10();

    void deleteIntroValnerabilityStatus();

    void insertIntroValnerabilityStatus();

    void deleteIntroResultStatus();

    void insertIntroResultStatus();

    void deleteIntroAgentStatus();

    void insertIntroAgentStatus();

}
