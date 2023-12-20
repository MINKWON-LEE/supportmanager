package com.mobigen.snet.supportagent.service.scheduler;

import com.google.common.collect.Lists;
import com.mobigen.snet.supportagent.models.SnetAssetMasterResultDto;
import com.mobigen.snet.supportagent.models.SnetAssetUserDto;
import com.mobigen.snet.supportagent.models.SnetAssetUserParamDto;
import com.mobigen.snet.supportagent.models.SnetStatisticsDto;
import com.mobigen.snet.supportagent.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Component("auditStatisticsManager")
@Slf4j
public class AuditStatisticsManager {

    @Autowired(required = false)
    StatisticsService statisticsService;

    /**
     * 통계 데이터 생성
     */
    public void introBatch(String createDate, boolean todayFlag) throws Exception {

        log.info("*[IntroBatch4] auditStatisticsJobScheduler start [---------------------------------------------------------------------------------------------]");

        // 특정 user_id 에 대해서 확인할 경우 조건 추가
        SnetAssetUserDto userParamDto = SnetAssetUserDto.builder()
//                .userId("test1")
                .build();

        // (0) 배치대상 사용자 리스트 조회
        List<SnetAssetUserDto> assetUserDtoFirstList = statisticsService.getAssetUserFirstList(userParamDto);

        // (1-1) 부서 리스트 조회
        List<SnetAssetUserParamDto> assetUserParamDtoList = statisticsService.getAssetUserList(assetUserDtoFirstList);

        // (1-2) 부서의 팀별 자산리스트 조회
        List<SnetAssetMasterResultDto> assetMasterDtoList = statisticsService.getAssetMasterList(assetUserParamDtoList);

        /*------------------------------ < SNET_AUDIT_STATISTICS > 정보 구성 ------------------------------*/
        SnetStatisticsDto snetStatisticsDto1 = null;
        SnetStatisticsDto snetStatisticsDto2 = null;
        SnetStatisticsDto snetStatisticsDto3 = null;
        SnetStatisticsDto snetStatisticsDto4 = null;
        SnetStatisticsDto snetStatisticsDto5 = null;

        // CREATE_TIME 생성 (현재 시간의 시각)
        SimpleDateFormat dateFormatTime = new SimpleDateFormat("HH");
        Calendar calTime = Calendar.getInstance();
        String createTime = dateFormatTime.format(calTime.getTime());
        log.info("*[createTime] : {}", createTime);

        for (SnetAssetMasterResultDto resultDto2 : assetMasterDtoList) {

            resultDto2.setCreateDate(createDate);
            resultDto2.setCreateTime(createTime);

            // todayFlag(true)  : createTime (=오늘날짜, 현재시각) 에 해당하는 DB 테이블 (SNET_AUDIT_STATISTICS_TODAY) 에서 loop 로 delete
            // todayFlag(false) : createDate (=오늘날짜) 에 해당하는 DB 테이블 (SNET_AUDIT_STATISTICS) 에서 loop 로 delete
            statisticsService.deleteAuditStatistics11(resultDto2, todayFlag);
            break;
        } // end for

        for (SnetAssetMasterResultDto resultDto2 : assetMasterDtoList) {

            String branchId = resultDto2.getBranchId();
            String teamId = resultDto2.getTeamId();
            log.info("*[---------------------------------------------------------------------------------------------]");
            log.info("*[branchId] : {}", branchId);
            log.info("*[teamId] : {}", teamId);
            resultDto2.setCreateDate(createDate);
            resultDto2.setCreateTime(createTime);

            // 권한별 통계데이터 조회
            //   - 진단결과 변경 신청 현황
            //   - 진단제외 신청 현황
            //   - 장비 현황
            snetStatisticsDto1 = statisticsService.getStatisticsDto2(resultDto2, "SU", todayFlag);
            snetStatisticsDto2 = statisticsService.getStatisticsDto2(resultDto2, "SE", todayFlag);
            snetStatisticsDto3 = statisticsService.getStatisticsDto2(resultDto2, "SV", todayFlag);
            snetStatisticsDto4 = statisticsService.getStatisticsDto2(resultDto2, "OS", todayFlag);
            snetStatisticsDto5 = statisticsService.getStatisticsDto2(resultDto2, "OP", todayFlag);
        } // end for

        List<SnetStatisticsDto> reportListSU = Lists.newArrayList();
        List<SnetStatisticsDto> reportListSE = Lists.newArrayList();

        for (SnetAssetMasterResultDto resultDto2 : assetMasterDtoList) {

            String branchId = resultDto2.getBranchId();
            resultDto2.setCreateDate(createDate);

            //   - 자산 현황 (점검군, 자산개수 (점검대상=점검수량))
            List<SnetStatisticsDto> reportList51 = statisticsService.getAssetSwTypeNmStatusList5(snetStatisticsDto1, resultDto2, "SU");
            //   - 점검항목수, 전체항목수, 미진단, 미조치, 전체보안준수율 평균, 부서 전체보안준수율 평균, 팀 전체보안준수율 평균 조회
            List<SnetStatisticsDto> reportListAll = statisticsService.getAssetSwAuditRateList6(reportList51, resultDto2, "ALL", false, false, "SU", todayFlag);

            reportListSU = reportListAll.stream().collect(Collectors.toList());
        } // end for


        for (SnetAssetMasterResultDto resultDto2 : assetMasterDtoList) {

            String branchId = resultDto2.getBranchId();
            resultDto2.setCreateDate(createDate);

            //   - 자산 현황 (점검군, 자산개수 (점검대상=점검수량))
            List<SnetStatisticsDto> reportList52 = statisticsService.getAssetSwTypeNmStatusList5(snetStatisticsDto2, resultDto2, "SE");
            //   - 점검항목수, 전체항목수, 미진단, 미조치, 전체보안준수율 평균, 부서 전체보안준수율 평균, 팀 전체보안준수율 평균 조회
            List<SnetStatisticsDto> reportListB = statisticsService.getAssetSwAuditRateList6B(reportList52, resultDto2, "B", false, false, "SE", reportListSU, todayFlag);

            reportListSE = reportListB.stream().collect(Collectors.toList());
        } // end for

        for (SnetAssetMasterResultDto resultDto2 : assetMasterDtoList) {

            String branchId = resultDto2.getBranchId();
            resultDto2.setCreateDate(createDate);

            //   - 자산 현황 (점검군, 자산개수 (점검대상=점검수량))
            List<SnetStatisticsDto> reportList53 = statisticsService.getAssetSwTypeNmStatusList5(snetStatisticsDto3, resultDto2, "SV");
            //   - 점검항목수, 전체항목수, 미진단, 미조치, 전체보안준수율 평균, 부서 전체보안준수율 평균, 팀 전체보안준수율 평균 조회
            List<SnetStatisticsDto> reportListT1 = statisticsService.getAssetSwAuditRateList6T(reportList53, resultDto2, "T", false, false, "SV", reportListSU, reportListSE, todayFlag);

        } // end for

        for (SnetAssetMasterResultDto resultDto2 : assetMasterDtoList) {

            String branchId = resultDto2.getBranchId();
            resultDto2.setCreateDate(createDate);

            //   - 자산 현황 (점검군, 자산개수 (점검대상=점검수량))
            List<SnetStatisticsDto> reportList54 = statisticsService.getAssetSwTypeNmStatusList5(snetStatisticsDto4, resultDto2, "OS");

            //   - 점검항목수, 전체항목수, 미진단, 미조치, 전체보안준수율 평균, 부서 전체보안준수율 평균, 팀 전체보안준수율 평균 조회
            List<SnetStatisticsDto> reportListT2 = statisticsService.getAssetSwAuditRateList6T(reportList54, resultDto2, "T", false, false, "OS", reportListSU, reportListSE, todayFlag);


        } // end for

        for (SnetAssetMasterResultDto resultDto2 : assetMasterDtoList) {

            String branchId = resultDto2.getBranchId();
            resultDto2.setCreateDate(createDate);

            //   - 자산 현황 (점검군, 자산개수 (점검대상=점검수량))
            List<SnetStatisticsDto> reportList55 = statisticsService.getAssetSwTypeNmStatusList5(snetStatisticsDto5, resultDto2, "OP");

            //   - 점검항목수, 전체항목수, 미진단, 미조치, 전체보안준수율 평균, 부서 전체보안준수율 평균, 팀 전체보안준수율 평균 조회
            List<SnetStatisticsDto> reportListT3 = statisticsService.getAssetSwAuditRateList6T(reportList55, resultDto2, "T", false, false, "OP", reportListSU, reportListSE, todayFlag);
        } // end for

        /*------------------------------ < SNET_AUDIT_STATISTICS_DETAIL > 정보 구성 ------------------------------*/
        for (SnetAssetMasterResultDto resultDto2 : assetMasterDtoList) {

            String branchId = resultDto2.getBranchId();
            resultDto2.setCreateDate(createDate);
            resultDto2.setCreateTime(createTime);

            // SNET_AUDIT_STATISTICS_DETAIL 입력을 위한 팀, 부서, 등록날짜 세팅
            //   - 각 팀,부서 ID 저장
            //   - 배치처리 하는 현재날짜를 등록날짜로 저장
            List<SnetStatisticsDto> snetStatisticsCommonDtoList1 = statisticsService.getAuditStatistics1(branchId, reportListSU, resultDto2);

            // 통계 데이터 상세
            //   - 중요도별 취약점 현황
            List<SnetStatisticsDto> statisticsDetailList7 = statisticsService.getAssetSwAuditRateList7(snetStatisticsCommonDtoList1, resultDto2);

            // 통계 데이터 입력
            statisticsService.insertAuditStatistics12(statisticsDetailList7, resultDto2, todayFlag);
        } // end for branch, team

        for (SnetAssetMasterResultDto resultDto2 : assetMasterDtoList) {

            String branchId = resultDto2.getBranchId();
            resultDto2.setCreateDate(createDate);
            List<SnetStatisticsDto> snetStatisticsCommonDtoList1 = statisticsService.getAuditStatistics1(branchId, reportListSU, resultDto2);
            resultDto2.setUserType("SU");
            // 통계 데이터 상세
            //   - 분류 항목별 취약점 수
            List<SnetStatisticsDto> statisticsDetailList8 = statisticsService.getAssetSwAuditRateList8(snetStatisticsCommonDtoList1, resultDto2);

            // 통계 데이터 입력
            statisticsService.insertAuditStatistics13(statisticsDetailList8, resultDto2, todayFlag);
        } // end for SU

        for (SnetAssetMasterResultDto resultDto2 : assetMasterDtoList) {
            String branchId = resultDto2.getBranchId();
            resultDto2.setCreateDate(createDate);
            List<SnetStatisticsDto> snetStatisticsCommonDtoList1 = statisticsService.getAuditStatistics1(branchId, reportListSU, resultDto2);
            resultDto2.setUserType("SE");
            List<SnetStatisticsDto> statisticsDetailList8 = statisticsService.getAssetSwAuditRateList8(snetStatisticsCommonDtoList1, resultDto2);

            // 통계 데이터 입력
            statisticsService.insertAuditStatistics13(statisticsDetailList8, resultDto2, todayFlag);
        } // end for SE

        for (SnetAssetMasterResultDto resultDto2 : assetMasterDtoList) {

            String branchId = resultDto2.getBranchId();
            resultDto2.setCreateDate(createDate);
            List<SnetStatisticsDto> snetStatisticsCommonDtoList1 = statisticsService.getAuditStatistics1(branchId, reportListSU, resultDto2);
            resultDto2.setUserType("SV");
            List<SnetStatisticsDto> statisticsDetailList8 = statisticsService.getAssetSwAuditRateList8(snetStatisticsCommonDtoList1, resultDto2);

            // 통계 데이터 입력
            statisticsService.insertAuditStatistics13(statisticsDetailList8, resultDto2, todayFlag);
        } // end for SV

        for (SnetAssetMasterResultDto resultDto2 : assetMasterDtoList) {

            String branchId = resultDto2.getBranchId();
            resultDto2.setCreateDate(createDate);
            List<SnetStatisticsDto> snetStatisticsCommonDtoList1 = statisticsService.getAuditStatistics1(branchId, reportListSU, resultDto2);
            resultDto2.setUserType("OS");
            List<SnetStatisticsDto> statisticsDetailList8 = statisticsService.getAssetSwAuditRateList8(snetStatisticsCommonDtoList1, resultDto2);

            // 통계 데이터 입력
            statisticsService.insertAuditStatistics13(statisticsDetailList8, resultDto2, todayFlag);
        } // end for OS

        for (SnetAssetMasterResultDto resultDto2 : assetMasterDtoList) {

            String branchId = resultDto2.getBranchId();
            resultDto2.setCreateDate(createDate);
            List<SnetStatisticsDto> snetStatisticsCommonDtoList1 = statisticsService.getAuditStatistics1(branchId, reportListSU, resultDto2);
            resultDto2.setUserType("OP");
            List<SnetStatisticsDto> statisticsDetailList8 = statisticsService.getAssetSwAuditRateList8(snetStatisticsCommonDtoList1, resultDto2);

            // 통계 데이터 입력
            statisticsService.insertAuditStatistics13(statisticsDetailList8, resultDto2, todayFlag);
        } // end for OP

        log.info("*[IntroBatch4] auditStatisticsJobScheduler end [---------------------------------------------------------------------------------------------]");

    } // end method

    /**
     * 통계 데이터 이관
     */
    public void migBatch(String createDate, String beforeCreateDate) {

        // CREATE_TIME 생성
        SimpleDateFormat dateFormatTime = new SimpleDateFormat("HH");
        Calendar calTime = Calendar.getInstance();

        String createTime = dateFormatTime.format(calTime.getTime());
        log.info("*[createTime] : {}", createTime);

        // 인트로 어제날짜 데이터 입력시 사용
        // -> 실제 배치 처리는 오늘날짜로 입력
        calTime.add(Calendar.HOUR, -1);

        String beforeCreateTime = dateFormatTime.format(calTime.getTime());
        log.info("*[beforeCreateTime] : {}", beforeCreateTime);

        SnetAssetMasterResultDto resultDto = SnetAssetMasterResultDto.builder()
                .createDate(beforeCreateDate)
                .createTime(beforeCreateTime)
                .build();

        // daily 통계 데이터 삭제 (23:00)
        statisticsService.deleteAuditStatistics11(resultDto, false);

        // today -> daily 통계 데이터 입력 (23:00 -> 00:30)
        statisticsService.insertAuditStatisticsMig(resultDto);

        // today 통계 데이터 삭제 (23:00)
        statisticsService.deleteAuditStatisticsMig(resultDto);
    } // end method

}
