package com.mobigen.snet.supportagent.dao;

import com.mobigen.snet.supportagent.models.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TestMapper {

    // 장비별 진단코드에 대한 점검결과 리스트 조회
    List<SnetAssetSwAuditReportDto> selectAssetSwAuditReportNew(Map params);

    // 위험도별 발견 취약점 수
    List<SnetAssetSwAuditReportDto> selectAssetSwAuditReportBargraphMapping(Map params);

    // 장비 리스트
    List<SnetAssetSwAuditExcelDto> selectAssetSwExcelListNew(Map gAssetSwJob);

    // 종합보고서 보안점검을 위한 최초진단일 구함
    SnetAssetSwAuditExcelDto selectAssetSwExcelGroupbyAuditday(Map gAssetSwJob);

    // 종합보고서 보안점검을 위한 최종진단일 구함
    SnetAssetSwAuditExcelDto selectAssetSwExcelGroupbyMaxAuditday(Map gAssetSwJob);

    // 장비 리스트 (장비별 취약점 개수에 사용)
    List<SnetAssetSwAuditExcelDto> selectAssetSwExcelListGroupbyNew(Map params);

    // 장비별 취약점 수
    int selectAssetSwProblemCountListNew(Map params);

    // 점검 항목 : 취약점 분석 평가 기준 n개
    int getCheckAnalysisStandard(Map checkParam);

    // 점검 항목 : 취약점 분류 n개 항목
    int getCheckClassifyItems(Map gAssetSwJob);

    // 점검 대상 : n대
    int getCheckTargets(Map gAssetSwJob);

    // WEB 서버의 보안점검 결과 전체 점검항목 개수
    int getItemTotal(Map gAssetSwJob);

    // 상 취약점 개수 : (weakLevelHigh)
    // 중 취약점 개수 : (weakLevelMiddle)
    // 하 취약점 개수 : (weakLevelLow)
    List<SnetAssetSwAuditReportDto> getWeakLevels(Map gAssetSwJob);

    //점검결과별 가중치 계산
    List<SnetAssetSwAuditReportDto> getReportTot(Map gAssetSwJob);

    // 장비별 취약점 수, 평가점수
    SnetAssetSwAuditExcelDto selectAssetSwExcelListGroupbyWeak(Map gAssetSwJob);

    // 분류 항목별 취약점 수
    List<SnetAssetSwAuditReportDto> getGroupbyReportGrpNmList(Map gAssetSwJob);

    // 점검군별 상세 취약점 개수
    List<ResultDto> getDetailResultList(Map gAssetSwJob);

    // 점검항목별 취약점 개수
    List<ResultDto> getDetailItemResultList(Map gAssetSwJob);

    // 별첨2. 점검 항목(MSIT) 리스트
    List<SnetAssetSwAuditReportDto> getCheckAnalysisStandardListByFiletype1(Map gAssetSwJob10);

    List<SnetAssetSwAuditReportDto> getCheckAnalysisStandardListByFiletype2(Map gAssetSwJob10);

    List<SnetAssetSwAuditReportDto> getCheckAnalysisStandardListByFiletype3(Map gAssetSwJob10);

    List<SnetAssetSwAuditReportDto> getCheckAnalysisStandardListByFiletype4(Map gAssetSwJob10);

    List<SnetAssetSwAuditReportDto> getCheckAnalysisStandardListByFiletype5(Map gAssetSwJob10);

    List<SnetAssetSwAuditReportDto> getCheckAnalysisStandardListByFiletype6(Map gAssetSwJob10);

    List<SnetAssetSwAuditReportDto> getCheckAnalysisStandardListByFiletype7(Map gAssetSwJob10);

    List<SnetAssetSwAuditReportDto> getCheckAnalysisStandardListByFiletype8(Map gAssetSwJob10);

    // EXCEL LIST 존재여부 체크
    int getCountExcelListByReqcdSwType(Map gAssetSwJob);

    // EXCEL LIST audit_day 와 REPORT audit_day 다를 경우 (max)
    SnetAssetSwAuditReportDto selectAssetSwReportGroupbyAuditday(Map gAssetSwJob);

    // EXCEL LIST audit_day 와 REPORT audit_day 다를 경우 (min)
    SnetAssetSwAuditReportDto selectAssetSwReportGroupbyMinAuditday(Map gAssetSwJob);

    // REPORT 조회 (1건)
    SnetAssetSwAuditReportDto selectAssetSwAuditReportNewGet(Map gAssetSwJob);

    // REPORT 조회 (max audit day)
    SnetAssetSwAuditReportDto selectAssetSwAuditReportNewGetMaxAuditDay(Map paramMap);

    // REPORT 조회 (min audit day)
    SnetAssetSwAuditReportDto selectAssetSwAuditReportNewGetMinAuditDay(Map paramMap);

    // REPORT 조회 (분류항목별)
    List<SnetAssetSwAuditReportDto> getGroupbyReportGrpNmListGet(Map paramMap);

    List<ResultDto> getDetailResultList2(Map paramMap);

    List<SnetAssetSwAuditReportDto> getGroupbyReportItemNmListGet(Map paramMap);

    // sw_nm 찾기
    CheckAnalysisDto getCheckSwnmAnalysisStandardGet(Map checkParam);

    // 진단기준 sw_nm 체크
    List<CheckAnalysisDto> getCheckSwnmAnalysisStandard(Map checkParam);

    // 진단항목코드 조회 (판단기준, 조치방법)
    SnetConfigAuditItem selectConfigAuditItem(Map gAssetSwReportMap);

    // 진단기준 명 조회
    String getCheckAnalysisStandardDesc(Map checkParam);

    // 취약점 없는 장비 조회
    SnetAssetSwAuditExcelDto selectAssetSwExcelListGroupbyNotWeak(Map weakLevelsParamMap);

    // 진단일 검색조건 범위에서 최초 진단일, 최종 진단일
    SnetAssetSwAuditExcelDto getExcelListGroupbyNewMinMaxAuditDay(Map gAssetSwJob);

    // 자산기준 데이터 조회
    SnetAssetSwAuditReportDto selectAssetMstAuditReportGroupingBy(Map tempParamMap);

    SnetAssetSwAuditReportDto selectAssetMstAuditReportGetMaxAuditDay(Map tempParamMap);

    List<SnetAssetSwAuditReportDto> getWeakLevelsAssetMstAuditReportGetMaxAuditDay(Map reportWeakMap);

    List<SnetAssetSwAuditReportDto> selectAssetMstAuditReportGetMaxAuditDayItemGrpNmList(Map paramMap);

    List<SnetAssetSwAuditReportDto> selectAssetMstAuditReportGetMaxAuditDayItemGrpNmCntList(Map paramMap);

    List<SnetAssetSwAuditReportDto> getCheckAnalysisStandardListByFiletype1Linux(Map gAssetSwJob10);

    List<ResultDto> getDetailResultListItemGrpOrigin(Map paramMap);
}
