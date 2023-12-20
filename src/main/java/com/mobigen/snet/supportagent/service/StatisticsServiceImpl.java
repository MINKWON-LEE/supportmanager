package com.mobigen.snet.supportagent.service;

import com.google.common.collect.Lists;
import com.mobigen.snet.supportagent.dao.*;
import com.mobigen.snet.supportagent.models.*;
import com.mobigen.snet.supportagent.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 'sg_supprotmanager 프로젝트 - 스케줄러'
 */
@Slf4j
@Service("statisticsService")
@Transactional(timeout = 6 * 10 * 10 * 6, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired (required = false)
    AssetUserMapper assetUserMapper;
    @Autowired (required = false)
    AssetMasterMapper assetMasterMapper;
    @Autowired (required = false)
    AssetCokMapper assetCokMapper;
    @Autowired (required = false)
    AssetExceptMapper assetExceptMapper;
    @Autowired (required = false)
    AssetAuditResultMapper assetAuditResultMapper;
    @Autowired (required = false)
    TestMapper testMapper;
    @Autowired (required = false)
    AuditStatisticsMapper auditStatisticsMapper;
    @Autowired (required = false)
    DaoMapper daoMapper;

    public void init() {
       int cnt = auditStatisticsMapper.selectIntroSecCategoryCnt();
       if (cnt == 0) {
           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
           Calendar cal = Calendar.getInstance();
           cal.add(Calendar.DATE, -1);
           String createDate = dateFormat.format(cal.getTime());

           secCategoryBatch(createDate);
       }

    }

    /**
     * 사용자 (userId) 에 리스트
     */
    @Override
    public List<SnetAssetUserDto> getAssetUserFirstList(SnetAssetUserDto paramMap) {

        List<SnetAssetUserDto> firstAssetUserDtoList = assetUserMapper.getAssetUserFirstList(paramMap);
        return firstAssetUserDtoList;
    } // end method getAssetUserList

    /**
     * 사용자 (userId) 에 대한 부서, 팀, 자산 리스트
     */
    @Override
    public List<SnetAssetUserParamDto> getAssetUserList(List<SnetAssetUserDto> userParamDtoList) {

        List<SnetAssetUserParamDto> snetAssetUserParamDtoList = Lists.newArrayList();

        List<String> assetCdInList = Lists.newArrayList();

        for (SnetAssetUserDto userParamDto : userParamDtoList) {
            assetCdInList.add(userParamDto.getTeamId());
        } // end for


        String[] assetCdInArr = assetCdInList.stream().distinct().toArray(String[]::new);

        Map paramMap = new HashMap();
        paramMap.put("assetCdInArr", assetCdInArr);
//        paramMap.put("ALL_FLAG", "Y");

        List<SnetAssetUserDto> tempAssetUserDtoList = assetUserMapper.getAssetUserAllList(paramMap);

        tempAssetUserDtoList = tempAssetUserDtoList.stream().filter(x -> StringUtils.isNotEmpty(x.getBranchId())).collect(Collectors.toList());

        // grouping branchId
        TreeMap<String, List<SnetAssetUserDto>> groupbyUsertypeEntries =
                tempAssetUserDtoList.stream().collect(Collectors.groupingBy(SnetAssetUserDto::getBranchId, TreeMap::new, Collectors.toList()));


        for (Map.Entry<String, List<SnetAssetUserDto>> grpUsertypeVo : groupbyUsertypeEntries.entrySet()) {

            String branchId = grpUsertypeVo.getKey();
            List<SnetAssetUserDto> userDtos = grpUsertypeVo.getValue();

            List<SnetAssetUserDto> resultUserDto = userDtos.stream().map(this::mapToUserDto).collect(Collectors.toList());

            SnetAssetUserParamDto snetAssetUserParamDto = new SnetAssetUserParamDto();

            snetAssetUserParamDto.setBranchId(branchId);
            snetAssetUserParamDto.setUserDtos(resultUserDto);
            snetAssetUserParamDto.setAuditStandard("ALL"); // default ALL (전체. 1~6 존재함)
            snetAssetUserParamDto.setAuditDayRange(9999); // default ALL

            snetAssetUserParamDtoList.add(snetAssetUserParamDto);

        } // end for
        return snetAssetUserParamDtoList;
    } // end method getAssetUserList

    /**
     * 마스터 자산 (assetCd) 리스트
     */
    @Override
    public List<SnetAssetMasterResultDto> getAssetMasterList(List<SnetAssetUserParamDto> userLists) {

        List<SnetAssetMasterParamDto> tempAssetMasterDtoLists = Lists.newArrayList();

        // (1) 부서별 자산리스트 조회
        for (SnetAssetUserParamDto userParamDto : userLists) {

            SnetAssetMasterParamDto assetMasterParamDto = new SnetAssetMasterParamDto();

            Map userDtoParamMap = new HashMap();
            userDtoParamMap.put("BRANCH_ID", userParamDto.getBranchId());

            List<SnetAssetMasterDto> tempAssetMasterDtoParamList = assetMasterMapper.getAssetMasterList(userDtoParamMap);
            assetMasterParamDto.setBranchId(userParamDto.getBranchId());
            assetMasterParamDto.setCount(tempAssetMasterDtoParamList.size());
            assetMasterParamDto.setAuditDayRange(userParamDto.getAuditDayRange());
            assetMasterParamDto.setAuditStandard(userParamDto.getAuditStandard());
            assetMasterParamDto.setMasterDtos(tempAssetMasterDtoParamList);

            tempAssetMasterDtoLists.add(assetMasterParamDto);

        } // end for

        // (2) 부서별 자산리스트를 groupingby 팀
        List<SnetAssetMasterResultDto> resultDtos = Lists.newArrayList();
        for (SnetAssetMasterParamDto paramDto : tempAssetMasterDtoLists) {

            TreeMap<String, List<SnetAssetMasterDto>> groupbyUsertypeEntries =
                    paramDto.getMasterDtos().stream().collect(Collectors.groupingBy(SnetAssetMasterDto::getTeamId, TreeMap::new, Collectors.toList()));

            for (Map.Entry<String, List<SnetAssetMasterDto>> grpUsertypeVo : groupbyUsertypeEntries.entrySet()) {

                SnetAssetMasterResultDto resultDto = new SnetAssetMasterResultDto();

                String teamId = grpUsertypeVo.getKey();
                List<SnetAssetMasterDto> teamAssetMasterDtoParamList = grpUsertypeVo.getValue();

                resultDto.setTeamId(teamId);
                resultDto.setBranchId(paramDto.getBranchId());
                resultDto.setCount(teamAssetMasterDtoParamList.size());
                resultDto.setAuditDayRange(paramDto.getAuditDayRange());
                resultDto.setAuditStandard(paramDto.getAuditStandard());
                resultDto.setMasterDtos(teamAssetMasterDtoParamList);

                resultDtos.add(resultDto);
            } // end for
        } // end for

        return resultDtos;
    } // end method getAssetMasterList

    /**
     * 부서	        BRANCH_ID
     * 팀	        TEAM_ID
     * 등록 날짜	    CREATE_DAY
     * 진단기준	    AUDIT_STD_CD
     */
    @Override
    public List<SnetStatisticsDto> getAuditStatistics1 (String branchId, List<SnetStatisticsDto> statisticsDtoList, SnetAssetMasterResultDto resultDto2) {

        String createDay = resultDto2.getCreateDate();

        for (SnetStatisticsDto masterDto : statisticsDtoList) {

            masterDto.setCreateDay(createDay);
            masterDto.setBranchId(branchId);
            masterDto.setTeamId(resultDto2.getTeamId());
            masterDto.setAuditStdCd(resultDto2.getAuditStandard());

        } // end for

        return statisticsDtoList;
    }

    /**
     * 진단결과 변경처리 요청	AD_RESULT_REQ
     * 진단결과 변경처리 승인	AD_RESULT_OK
     * 진단결과 변경처리 반려	AD_RESULT_NOK
     */
    @Override
    public SnetStatisticsDto getStatisticsDto2(SnetAssetMasterResultDto masterParamDto
            , String userType, boolean todayFlag) throws Exception {

        List<String> assetCdInList = Lists.newArrayList();

        // master 의 자산리스트 가져옴 : masterParamDto.getMasterDtos()
        for (SnetAssetMasterDto dto : masterParamDto.getMasterDtos()) {
            assetCdInList.add(dto.getAssetCd());
        } // end for

        String[] assetCdInArr = assetCdInList.stream().distinct().toArray(String[]::new);

        Map paramMap = new HashMap();

        if (StringUtils.equals("SU", userType)) {

        } else if (StringUtils.equals("SE", userType)) {
            paramMap.put("branchId", masterParamDto.getBranchId());
        } else if (StringUtils.equals("SV", userType)) {
            paramMap.put("branchId", masterParamDto.getBranchId());
            paramMap.put("teamId", masterParamDto.getTeamId());
        } else if (StringUtils.equals("OP", userType)) {
            paramMap.put("branchId", masterParamDto.getBranchId());
            paramMap.put("teamId", masterParamDto.getTeamId());
        } else if (StringUtils.equals("OS", userType)) {
            paramMap.put("branchId", masterParamDto.getBranchId());
            paramMap.put("teamId", masterParamDto.getTeamId());
        } else {
            paramMap.put("branchId", masterParamDto.getBranchId());
            paramMap.put("teamId", masterParamDto.getTeamId());
        }

        // 진단결과 변경 신청 현황
        List<String> reqCnt = assetCokMapper.getAssetCokReqCnt(paramMap);
        List<String> waitCnt = assetCokMapper.getAssetCokWaitCnt(paramMap);
        List<String> okCnt = assetCokMapper.getAssetCokOkCnt(paramMap);
        List<String> nokCnt = assetCokMapper.getAssetCokNokCnt(paramMap);

        paramMap.put("createDate", masterParamDto.getCreateDate());

        List<String> reqCntD = assetCokMapper.getAssetCokReqCnt(paramMap);
        List<String> waitCntD = assetCokMapper.getAssetCokReqCnt(paramMap);
        List<String> okCntD = assetCokMapper.getAssetCokOkCnt(paramMap);
        List<String> nokCntD = assetCokMapper.getAssetCokNokCnt(paramMap);

        paramMap.remove("createDate");

        int seq = 1;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Calendar time = Calendar.getInstance();
        String statisticsId = format.format(time.getTime()) + StringUtils.left(StringUtils.replace(UUID.randomUUID().toString(), "-",""),15);

        // 장비 현황
        List<SnetAssetMasterDto> snetAssetMasterDtoList = assetMasterMapper.getAssetMasterGroupingAgentCd(paramMap);

        int equipCnt = 0;
        int equipNotInstallCnt = 0;
        int equipAllCnt = 0;
        int equipPlusCnt = 0;
        int equipMinusCnt = 0;

        int equipCntD = 0;
        int equipNotInstallCntD = 0;
        int equipAllCntD = 0;
        int equipPlusCntD = 0;
        int equipMinusCntD = 0;

        // 장비 (누적) 개수
        for (SnetAssetMasterDto snetAssetMasterDto : snetAssetMasterDtoList) {

            if (StringUtils.isEmpty(snetAssetMasterDto.getEquipLength())) {
                equipNotInstallCnt ++;
            } else {
                equipCnt ++;
            }
        }

        equipAllCnt = equipCnt + equipNotInstallCnt;

        // 장비 증가 개수, 장비 감소 개수
        boolean equipFlag = false;

        try {

            String createDay = masterParamDto.getCreateDate();

            SimpleDateFormat formatDay2 = new SimpleDateFormat("yyyyMMdd");
            Calendar cal = new GregorianCalendar(Locale.KOREA);
            cal.add(Calendar.DATE, -1);
            String beforeCreateDay = formatDay2.format(cal.getTime());

            Map statisticsMap = new HashMap();

            /*--- ---*/
            statisticsMap.put("CREATE_DAY", createDay);

            equipPlusCnt = auditStatisticsMapper.getEquipPlusCnt();
            equipMinusCnt = auditStatisticsMapper.getEquipMinusCnt();
            /*--- ---*/

            paramMap.put("createDate", createDay);
            List<SnetAssetMasterDto> snetAssetMasterDtoListD = assetMasterMapper.getAssetMasterGroupingAgentCd(paramMap);

            // 장비 (누적) 개수
            for (SnetAssetMasterDto snetAssetMasterDto : snetAssetMasterDtoListD) {

                if (StringUtils.isEmpty(snetAssetMasterDto.getEquipLength())) {
                    equipNotInstallCntD ++;
                } else {
                    equipCntD ++;
                }
            }
            equipAllCntD = equipCntD + equipNotInstallCntD;

            statisticsMap.put("CREATE_DAY", createDay);

            equipPlusCntD = auditStatisticsMapper.getEquipPlusCntForDay(statisticsMap);
            equipMinusCntD = auditStatisticsMapper.getEquipMinusCntForDay(statisticsMap);

        } catch (Exception e) {
            e.printStackTrace();
        }

        SnetStatisticsDto statisticsDto =  SnetStatisticsDto.builder()
                .seq(seq)
                .statisticsId(statisticsId)
                .auditStdCd(masterParamDto.getAuditStandard())
                .branchId(masterParamDto.getBranchId())
                .teamId(masterParamDto.getTeamId())
                .createDay(masterParamDto.getCreateDate())
                .adResultReq(reqCnt.size()) // 신청
                .adResultNok(waitCnt.size()) // 대기
                .adResultOk (okCnt.size()) // 승인
                .adExceptOk (nokCnt.size()) // 반려
                .adResultReqD(reqCntD.size())
                .adResultNokD(waitCntD.size())
                .adResultOkD(okCntD.size())
                .adExceptOkD(nokCntD.size())
                .equipCnt(equipCnt)
                .equipInstallNokCnt(equipNotInstallCnt)
                .equipAllCnt(equipAllCnt)
                .equipPlusCnt(equipPlusCnt)
                .equipMinusCnt(equipMinusCnt)
                .equipCntD(equipCntD)
                .equipInstallNokCntD(equipNotInstallCntD)
                .equipAllCntD(equipAllCntD)
                .equipPlusCntD(equipPlusCntD)
                .equipMinusCntD(equipMinusCntD)
                .division(userType)
                .build();

        if (todayFlag) {
            statisticsDto.setToday("TODAY");
            statisticsDto.setCreateTime(masterParamDto.getCreateTime());
            auditStatisticsMapper.insertAuditStatisticsToday(statisticsDto);
        } else {

            auditStatisticsMapper.insertAuditStatistics(statisticsDto);
        }


        paramMap.remove("branchId");
        paramMap.remove("teamId");

        return statisticsDto;
    } // end method getStatisticsDto2

    /**
     * 진단제외 신청처리 요청	AD_EXCEPT_REQ
     * 진단제외 신청처리 승인	AD_EXCEPT_OK
     * 진단제외 신청처리 반려	AD_EXCEPT_NOK
     */
    @Override
    public SnetStatisticsDto getStatisticsDto3(SnetStatisticsDto snetStatisticsDto2, SnetAssetMasterResultDto masterParamDto) {

        List<String> assetCdInList = Lists.newArrayList();

        for (SnetAssetMasterDto dto : masterParamDto.getMasterDtos()) {
            assetCdInList.add(dto.getAssetCd());
        } // end for

        String[] assetCdInArr = assetCdInList.stream().distinct().toArray(String[]::new);

        Map paramMap = new HashMap();
        paramMap.put("assetCdInArr", assetCdInArr);

        int reqExceptCnt = assetExceptMapper.getAssetExceptReqCnt(paramMap);
        int okExceptCnt = assetExceptMapper.getAssetExceptOkCnt(paramMap);
        int nokExceptCnt = assetExceptMapper.getAssetExceptNokCnt(paramMap);

        paramMap.put("createDate", masterParamDto.getCreateDate());

        int reqExceptCntD = assetExceptMapper.getAssetExceptReqCnt(paramMap);
        int okExceptCntD = assetExceptMapper.getAssetExceptOkCnt(paramMap);
        int nokExceptCntD = assetExceptMapper.getAssetExceptNokCnt(paramMap);

        snetStatisticsDto2 = SnetStatisticsDto.builder()
                .adResultReq(snetStatisticsDto2.getAdResultReq())
                .adResultNok(snetStatisticsDto2.getAdResultNok())
                .adResultOk(snetStatisticsDto2.getAdResultOk())
                .adResultReqD(snetStatisticsDto2.getAdResultReqD())
                .adResultOkD(snetStatisticsDto2.getAdResultOkD())
                .adResultNokD(snetStatisticsDto2.getAdResultNokD())
                .adExceptReq(reqExceptCnt)
                .adExceptOk(okExceptCnt)
                .adExceptNok(nokExceptCnt)
                .adExceptReqD(reqExceptCntD)
                .adExceptOkD(okExceptCntD)
                .adExceptNokD(nokExceptCntD)
                .build();

        return snetStatisticsDto2;
    } // end method getStatisticsDto3

    /**
     * 자산 조회
     */
    @Override
    public List<SnetStatisticsDto> getAssetSwTypeNmStatusList5(SnetStatisticsDto snetStatisticsDto4, SnetAssetMasterResultDto masterParamDto, String userType) {

        List<SnetStatisticsDto> resultDtoList = Lists.newArrayList();
        List<SnetStatisticsDto> tempSnetStatisticsDtoList = Lists.newArrayList();
        List<String> assetCdInAllList = Lists.newArrayList();

        for (SnetAssetMasterDto dto : masterParamDto.getMasterDtos()) {
            assetCdInAllList.add(dto.getAssetCd());
        } // end for

        String[] assetCdInArr = assetCdInAllList.stream().distinct().toArray(String[]::new);

        Map paramMap = new HashMap();

        if (StringUtils.equals("SU", userType)) {

        } else if (StringUtils.equals("SE", userType)) {

            paramMap.put("branchId", masterParamDto.getBranchId());
        } else if (StringUtils.equals("SV", userType)) {

            paramMap.put("branchId", masterParamDto.getBranchId());
            paramMap.put("teamId", masterParamDto.getTeamId());
        } else if (StringUtils.equals("OP", userType)) {

            paramMap.put("branchId", masterParamDto.getBranchId());
            paramMap.put("teamId", masterParamDto.getTeamId());
        } else if (StringUtils.equals("OS", userType)) {

            paramMap.put("branchId", masterParamDto.getBranchId());
            paramMap.put("teamId", masterParamDto.getTeamId());
        } else {

            paramMap.put("branchId", masterParamDto.getBranchId());
            paramMap.put("teamId", masterParamDto.getTeamId());
        }

        paramMap.put("endDay", masterParamDto.getCreateDate());

        // 자산 조회
        List<SnetAssetSwAuditExcelDto> list7 = assetAuditResultMapper.getAssetSwAuditResultList(paramMap);

        if (list7 != null && list7.size() > 0) {

            boolean linuxFlag = true;

            // 전체 점검군 리스트
            List<SnetAssetSwAuditExcelDto> swTypeList = Lists.newArrayList();
            List<SnetAssetSwAuditExcelDto> swTypeListNew = Lists.newArrayList();

            // 진단일 점검군 리스트
            List<SnetAssetSwAuditExcelDto> swTypeListD = Lists.newArrayList();
            List<SnetAssetSwAuditExcelDto> swTypeListNewD = Lists.newArrayList();

            String createDate = masterParamDto.getCreateDate();

            /*--- list7 ---*/
            for (SnetAssetSwAuditExcelDto vo : list7) {

                String swType = vo.getSwType();

                if (StringUtils.equalsIgnoreCase("OS", swType)) {

                    if (StringUtils.equalsIgnoreCase("Windows", vo.getSwNm())) {

                        SnetAssetSwAuditExcelDto swType2Vo = new SnetAssetSwAuditExcelDto();
                        swType2Vo.setSwNm(vo.getSwNm());
                        swType2Vo.setSwType(swType);
                        swTypeList.add(swType2Vo);
                    } else {

                        SnetAssetSwAuditExcelDto swType1Vo = new SnetAssetSwAuditExcelDto();

                        swType1Vo.setSwNm(vo.getSwNm());
                        swType1Vo.setSwType(swType);
                        swTypeList.add(swType1Vo);
                    }
                } else {

                    SnetAssetSwAuditExcelDto swTypeVo = new SnetAssetSwAuditExcelDto();
                    swTypeVo.setSwNm(vo.getSwNm());
                    swTypeVo.setSwType(swType);
                    swTypeList.add(swTypeVo);
                }
            } // end for list7

            for (SnetAssetSwAuditExcelDto excelDto : swTypeList) {

                if (StringUtils.equalsIgnoreCase("OS", excelDto.getSwType())) {

                    if (!StringUtils.equalsIgnoreCase("Windows", excelDto.getSwNm())) {

                        boolean flag;

                        if ("Linux".equalsIgnoreCase(excelDto.getSwNm())) {
                            flag = true;
                        } else {
                            flag = false;
                        }

                        if (flag) {
                            linuxFlag = true;
                        } else {
                            linuxFlag = false;
                        }

                        excelDto.setSwType("Linux");
                    } else {

                        excelDto.setSwType("Windows");
                    }
                } // end if
            } // end for

            TreeMap<String, List<SnetAssetSwAuditExcelDto>> groupbySwtype =
                    swTypeList.stream().collect(Collectors.groupingBy(SnetAssetSwAuditExcelDto::getSwType, TreeMap::new, Collectors.toList()));

            // not OS
            for (Map.Entry<String, List<SnetAssetSwAuditExcelDto>> grpNmVo : groupbySwtype.entrySet()) {

                SnetAssetSwAuditExcelDto resultExcelDto = new SnetAssetSwAuditExcelDto();

                String key = grpNmVo.getKey();
                List<SnetAssetSwAuditExcelDto> values = grpNmVo.getValue();
                int assetCnt = 0;
                int assetWindowsCnt = 0;
                int assetLinuxCnt = 0;

                String swNmKey1 = null;
                String swNmKey2 = null;

                if (!StringUtils.equalsIgnoreCase("OS", key) && !StringUtils.equalsIgnoreCase("Linux", key) && !StringUtils.equalsIgnoreCase("Windows", key)) {

                    assetCnt += grpNmVo.getValue().size();

                    resultExcelDto.setSwType(key);
                    resultExcelDto.setSwNmLinux(swNmKey1);
                    resultExcelDto.setSwNmWindows(swNmKey2);
                    resultExcelDto.setAssetCnt(assetCnt);
                    resultExcelDto.setAssetWindowsCnt(assetWindowsCnt);
                    resultExcelDto.setAssetLinuxCnt(assetLinuxCnt);
                    swTypeListNew.add(resultExcelDto);
                }
            } // end for

            // OS.Windows
            for (Map.Entry<String, List<SnetAssetSwAuditExcelDto>> grpNmVo : groupbySwtype.entrySet()) {

                SnetAssetSwAuditExcelDto resultExcelDto = new SnetAssetSwAuditExcelDto();

                String key = grpNmVo.getKey();
                List<SnetAssetSwAuditExcelDto> values = grpNmVo.getValue();
                int assetCnt = 0;

                String swNmKey2 = null;

                if (StringUtils.equalsIgnoreCase("Windows", key)) {

                    for (SnetAssetSwAuditExcelDto excelDto : values) {

                        if (StringUtils.equalsIgnoreCase("Windows", excelDto.getSwNm())) {
                            swNmKey2 = "Windows";
                            assetCnt++;
                        }
                    }

                    resultExcelDto.setSwType("OS");
                    resultExcelDto.setSwNm(swNmKey2);
                    resultExcelDto.setAssetCnt(assetCnt);
                    swTypeListNew.add(resultExcelDto);
                }
            } // end for

            // OS.Linux
            for (Map.Entry<String, List<SnetAssetSwAuditExcelDto>> grpNmVo : groupbySwtype.entrySet()) {

                SnetAssetSwAuditExcelDto resultExcelDto = new SnetAssetSwAuditExcelDto();

                String key = grpNmVo.getKey();
                List<SnetAssetSwAuditExcelDto> values = grpNmVo.getValue();

                int assetCnt = 0;

                String swNmKey1 = null;

                if (StringUtils.equalsIgnoreCase("Linux", key)) {

                    for (SnetAssetSwAuditExcelDto excelDto : values) {

                        if (!StringUtils.equalsIgnoreCase("Windows", excelDto.getSwNm())) {
                            swNmKey1 = "Linux";
                            assetCnt++;
                        }
                    }

                    if (StringUtils.isEmpty(swNmKey1)) {

                    } else {

                    }

                    resultExcelDto.setSwType("OS");
                    resultExcelDto.setSwNm(swNmKey1);
                    resultExcelDto.setAssetCnt(assetCnt);
                    swTypeListNew.add(resultExcelDto);
                } // end if
            } // end for
            /*--- list7 ---*/
            /*--- list7D ---*/
            // 진단일 자산 조회
            paramMap.put("createDate", createDate);
            List<SnetAssetSwAuditExcelDto> list7D = assetAuditResultMapper.getAssetSwAuditResultList(paramMap);
            paramMap.remove("createDate");

            for (SnetAssetSwAuditExcelDto vo : list7D) {

                String swType = vo.getSwType();

                if (StringUtils.equalsIgnoreCase("OS", swType)) {

                    if (StringUtils.equalsIgnoreCase("Windows", vo.getSwNm())) {

                        SnetAssetSwAuditExcelDto swType2Vo = new SnetAssetSwAuditExcelDto();
                        swType2Vo.setSwNm(vo.getSwNm());
                        swType2Vo.setSwType(swType);
                        swTypeListD.add(swType2Vo);
                    } else {

                        SnetAssetSwAuditExcelDto swType1Vo = new SnetAssetSwAuditExcelDto();

                        swType1Vo.setSwNm(vo.getSwNm());
                        swType1Vo.setSwType(swType);
                        swTypeListD.add(swType1Vo);
                    }
                } else {

                    SnetAssetSwAuditExcelDto swTypeVo = new SnetAssetSwAuditExcelDto();
                    swTypeVo.setSwNm(vo.getSwNm());
                    swTypeVo.setSwType(swType);
                    swTypeListD.add(swTypeVo);
                }
            } // end for

            for (SnetAssetSwAuditExcelDto excelDto : swTypeListD) {

                if (StringUtils.equalsIgnoreCase("OS", excelDto.getSwType())) {

                    if (!StringUtils.equalsIgnoreCase("Windows", excelDto.getSwNm())) {

                        boolean flag;

                        if ("Linux".equalsIgnoreCase(excelDto.getSwNm())) {
                            flag = true;
                        } else {
                            flag = false;
                        }

                        if (flag) {
                            linuxFlag = true;
                        } else {
                            linuxFlag = false;
                            break;
                        }

                        excelDto.setSwType("Linux");
                    } else {

                        excelDto.setSwType("Windows");
                    }
                } // end if
            } // end for

            TreeMap<String, List<SnetAssetSwAuditExcelDto>> groupbySwtypeD =
                    swTypeListD.stream().collect(Collectors.groupingBy(SnetAssetSwAuditExcelDto::getSwType, TreeMap::new, Collectors.toList()));


            // not OS
            for (Map.Entry<String, List<SnetAssetSwAuditExcelDto>> grpNmVo : groupbySwtypeD.entrySet()) {

                SnetAssetSwAuditExcelDto resultExcelDto = new SnetAssetSwAuditExcelDto();

                String key = grpNmVo.getKey();
                List<SnetAssetSwAuditExcelDto> values = grpNmVo.getValue();
                int assetCnt = 0;
                int assetWindowsCnt = 0;
                int assetLinuxCnt = 0;

                String swNmKey1 = null;
                String swNmKey2 = null;

                if (!StringUtils.equalsIgnoreCase("OS", key) && !StringUtils.equalsIgnoreCase("Linux", key) && !StringUtils.equalsIgnoreCase("Windows", key)) {

                    assetCnt += grpNmVo.getValue().size();

                    resultExcelDto.setSwType(key);
                    resultExcelDto.setSwNmLinux(swNmKey1);
                    resultExcelDto.setSwNmWindows(swNmKey2);
                    resultExcelDto.setAssetCnt(assetCnt);
                    resultExcelDto.setAssetWindowsCnt(assetWindowsCnt);
                    resultExcelDto.setAssetLinuxCnt(assetLinuxCnt);
                    swTypeListNewD.add(resultExcelDto);
                }
            } // end for

            // OS.Windows
            for (Map.Entry<String, List<SnetAssetSwAuditExcelDto>> grpNmVo : groupbySwtypeD.entrySet()) {

                SnetAssetSwAuditExcelDto resultExcelDto = new SnetAssetSwAuditExcelDto();

                String key = grpNmVo.getKey();
                List<SnetAssetSwAuditExcelDto> values = grpNmVo.getValue();
                int assetCnt = 0;

                String swNmKey2 = null;

                if (StringUtils.equalsIgnoreCase("Windows", key)) {

                    for (SnetAssetSwAuditExcelDto excelDto : values) {

                        if (StringUtils.equalsIgnoreCase("Windows", excelDto.getSwNm())) {
                            swNmKey2 = "Windows";
                            assetCnt++;
                        }
                    }

                    resultExcelDto.setSwType("OS");
                    resultExcelDto.setSwNm(swNmKey2);
                    resultExcelDto.setAssetCnt(assetCnt);
                    swTypeListNewD.add(resultExcelDto);
                }
            } // end for
            // OS.Linux
            for (Map.Entry<String, List<SnetAssetSwAuditExcelDto>> grpNmVo : groupbySwtypeD.entrySet()) {

                SnetAssetSwAuditExcelDto resultExcelDto = new SnetAssetSwAuditExcelDto();

                String key = grpNmVo.getKey();
                List<SnetAssetSwAuditExcelDto> values = grpNmVo.getValue();
                int assetCnt = 0;

                String swNmKey1 = null;

                if (StringUtils.equalsIgnoreCase("Linux", key)) {

                    for (SnetAssetSwAuditExcelDto excelDto : values) {


                        if (!StringUtils.equalsIgnoreCase("Windows", excelDto.getSwNm())) {
                            swNmKey1 = "Linux";
                            assetCnt++;
                        }
                    }

                    if (StringUtils.isEmpty(swNmKey1)) {

                    } else {

                    }

                    resultExcelDto.setSwType("OS");
                    resultExcelDto.setSwNm(swNmKey1);
                    resultExcelDto.setAssetCnt(assetCnt);
                    swTypeListNewD.add(resultExcelDto);
                }
            } // end for
            /*--- list7D ---*/

            /*--- list7 swTypeListNew ---*/
            for (SnetAssetSwAuditExcelDto dto : swTypeListNew) {

                // 점검군 자산개수
                int assetCnt = 0;
                // 자산 증가
                int assetPlusCnt = 0;
                // 자산 감소
                int assetMinusCnt = 0;

                // 전체보안준수율
                double auditRateAvg = 0L;
                double allAuditRateAvg = 0L;

                allAuditRateAvg = list7.stream()
                        .filter(x -> x.getAuditRate() > 0)
                        .mapToDouble(x -> x.getAuditRate())
                        .average()
                        .orElseGet(() -> 0.0);

                // 점검군 보안준수율 평균
                if (StringUtils.equalsIgnoreCase("OS", dto.getSwType())) {

                    if (StringUtils.equalsIgnoreCase("Windows", dto.getSwNm())) {

                        String swTypeStr = dto.getSwType();
                        String swNmStr = dto.getSwNm();
                        auditRateAvg = list7.stream()
                                .filter(x -> x.getAuditRate() > 0)
                                .filter(y -> swTypeStr.equalsIgnoreCase(y.getSwType()))
                                .filter(y -> swNmStr.equalsIgnoreCase(y.getSwNm()))
                                .mapToDouble(x -> x.getAuditRate())
                                .average()
                                .orElseGet(() -> 0.0);
                    } else {

                        String swTypeStr = dto.getSwType();
                        String swNmStr = dto.getSwNm();
                        auditRateAvg = list7.stream()
                                .filter(x -> x.getAuditRate() > 0)
                                .filter(y -> swTypeStr.equalsIgnoreCase(y.getSwType()))
                                .filter(y -> swNmStr.equalsIgnoreCase(y.getSwNm()))
                                .mapToDouble(x -> x.getAuditRate())
                                .average()
                                .orElseGet(() -> 0.0);
                    }
                } else {

                    String swTypeStr = dto.getSwType();
                    auditRateAvg = list7.stream()
                            .filter(x -> x.getAuditRate() > 0)
                            .filter(y -> swTypeStr.equalsIgnoreCase(y.getSwType()))
                            .mapToDouble(x -> x.getAuditRate())
                            .average()
                            .orElseGet(() -> 0.0);
                }

                String keyStr = null;
                if (StringUtils.equalsIgnoreCase("OS", dto.getSwType())) {

                    if (StringUtils.equalsIgnoreCase("Windows", dto.getSwNm())) {

                        keyStr = "Windows";
                    } else {

                        keyStr = "Linux";
                    }
                } else {

                    keyStr = dto.getSwType();
                }

                assetCnt = dto.getAssetCnt();

                try {

                    String createDay = masterParamDto.getCreateDate();

                    SimpleDateFormat formatDay2 = new SimpleDateFormat("yyyyMMdd");
                    Date setDate = formatDay2.parse(createDay);
                    Calendar cal = new GregorianCalendar(Locale.KOREA);
                    cal.setTime(setDate);
                    cal.add(Calendar.DATE, -1);
                    String beforeCreateDay = formatDay2.format(cal.getTime());

                    Map statisticsMap = new HashMap();
                    statisticsMap.put("BEFORE_CREATE_DAY", beforeCreateDay);

                    if (StringUtils.equals("Linux", keyStr)) {

                        statisticsMap.put("SW_TYPE", "Linux");
                    } else if (StringUtils.equals("Windows", keyStr)) {

                        statisticsMap.put("SW_TYPE", "Windows");
                    } else if (StringUtils.equals("DB", keyStr)) {

                        statisticsMap.put("SW_TYPE", "DB");
                    } else if (StringUtils.equals("WEB", keyStr)) {

                        statisticsMap.put("SW_TYPE", "WEB");
                    } else if (StringUtils.equals("WAS", keyStr)) {

                        statisticsMap.put("SW_TYPE", "WAS");
                    } else if (StringUtils.equals("URL", keyStr)) {

                        statisticsMap.put("SW_TYPE", "Web App");
                    } else {

                        statisticsMap.put("SW_TYPE", "ETC");
                    }
                    statisticsMap.put("DIVISION", userType);
                    SnetStatisticsDto statisticsDto = auditStatisticsMapper.getAssetCntByBeforeCreateDay(statisticsMap);

                    if (statisticsDto != null) {

                        if (statisticsDto.getAssetCnt() > assetCnt) {

                            assetPlusCnt = 0;
                            assetMinusCnt = statisticsDto.getAssetCnt() - assetCnt;

                        } else if (statisticsDto.getAssetCnt() < assetCnt) {

                            assetPlusCnt = assetCnt - statisticsDto.getAssetCnt();
                            assetMinusCnt = 0;

                        } else {

                            assetPlusCnt = 0;
                            assetMinusCnt = 0;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // double 값 NaN 일 경우 처리
                if (Double.isNaN(auditRateAvg)) {

                    auditRateAvg = 0.0;
                } // end if

                SnetStatisticsDto snetStatisticsDto = SnetStatisticsDto.builder()
                        .swType(keyStr) // 점검군
                        .swNm(dto.getSwNm()) // swNm
                        .assetCnt(assetCnt) // 점검군 자산개수
                        .assetPlusCnt(assetPlusCnt) // 점검군 자산증가개수
                        .assetMinusCnt(assetMinusCnt) // 점검군 자산감소개수
                        .auditRate(auditRateAvg) // 점검군 보안준수율
                        .allAuditRate(auditRateAvg) // 전체보안준수율 평균
                        .allAuditRateAvg(allAuditRateAvg) // 전체보안준수율 평균
                        .division(userType)
                        .build();
                tempSnetStatisticsDtoList.add(snetStatisticsDto);
            } // end for
            /*--- list7 swTypeListNew ---*/

            /*--- list7D swTypeListNewD ---*/
            if (list7D != null && list7D.size() > 0) {

                for (SnetStatisticsDto dto : tempSnetStatisticsDtoList) {

                    // 점검군 자산개수
                    int assetCntD = 0;
                    // 자산 증가
                    int assetPlusCntD = 0;
                    // 자산 감소
                    int assetMinusCntD = 0;

                    String keyStrD = dto.getSwType();

                    for (SnetAssetSwAuditExcelDto dtoD : swTypeListNewD) {

                        String swTypeNmKeyD = null;

                        if (StringUtils.equalsIgnoreCase("OS", dtoD.getSwType())) {

                            if (StringUtils.equalsIgnoreCase("Windows", dtoD.getSwNm())) {

                                swTypeNmKeyD = "Windows";
                            } else {

                                swTypeNmKeyD = "Linux";
                            }
                        } else {

                            swTypeNmKeyD = dtoD.getSwType();
                        }

                        if (StringUtils.equals(keyStrD, swTypeNmKeyD)) {

                            assetCntD += dtoD.getAssetCnt();
                        }
                    } // end for

                    // 전체보안준수율
                    double auditRateAvgD = 0.0;
                    double allAuditRateAvgD = 0.0;

                    allAuditRateAvgD = list7D.stream()
                            .mapToDouble(x -> x.getAuditRate())
                            .average()
                            .orElseGet(() -> 0.0);
                    // OptionalDouble.empty 체크


                    // 점검군 보안준수율 평균
                    double asDoubleAvgD = 0.0;

                    if (StringUtils.equalsIgnoreCase("OS", dto.getSwType())) {

                        if (StringUtils.equalsIgnoreCase("Windows", dto.getSwNm())) {

                            String swTypeStrD = dto.getSwType();
                            String swNmStrD = dto.getSwNm();
                            asDoubleAvgD = list7D.stream()
                                    .filter(y -> swTypeStrD.equals(y.getSwType()))
                                    .filter(y -> swNmStrD.equals(y.getSwNm()))
                                    .mapToDouble(x -> x.getAuditRate())
                                    .average()
                                    .orElseGet(() -> 0.0);
                            // OptionalDouble.empty 체크
                            auditRateAvgD = asDoubleAvgD;
                        } else {

                            String swTypeStrD = dto.getSwType();
                            String swNmStrD = dto.getSwNm();
                            asDoubleAvgD = list7D.stream()
                                    .filter(y -> swTypeStrD.equals(y.getSwType()))
                                    .filter(y -> swNmStrD.equals(y.getSwNm()))
                                    .mapToDouble(x -> x.getAuditRate())
                                    .average()
                                    .orElseGet(() -> 0.0);
                            // OptionalDouble.empty 체크
                            auditRateAvgD = asDoubleAvgD;
                        }
                    } else {

                        String swTypeStrD = dto.getSwType();
                        String swNmStrD = dto.getSwNm();
                        asDoubleAvgD = list7D.stream()
                                .filter(y -> swTypeStrD.equals(y.getSwType()))
                                .mapToDouble(x -> x.getAuditRate())
                                .average()
                                .orElseGet(() -> 0.0);

                        // OptionalDouble.empty 체크
                        auditRateAvgD = asDoubleAvgD;

                    } // end if else

                    try {

                        String createDay = masterParamDto.getCreateDate();

                        SimpleDateFormat formatDay2 = new SimpleDateFormat("yyyyMMdd");
                        Date setDate = formatDay2.parse(createDay);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(setDate);
                        cal.add(Calendar.DATE, -1);
                        String beforeCreateDay = formatDay2.format(cal.getTime());

                        Map statisticsMap = new HashMap();
                        statisticsMap.put("BEFORE_CREATE_DAY", beforeCreateDay);
                        statisticsMap.put("SW_TYPE", keyStrD);
                        statisticsMap.put("DIVISION", userType);
                        SnetStatisticsDto statisticsDtoD = auditStatisticsMapper.getAssetCntByBeforeCreateDay(statisticsMap);

                        if (statisticsDtoD != null) {

                            if (statisticsDtoD.getAssetCnt() > assetCntD) {

                                assetPlusCntD = 0;
                                assetMinusCntD = statisticsDtoD.getAssetCnt() - assetCntD;

                            } else if (statisticsDtoD.getAssetCnt() < assetCntD) {

                                assetPlusCntD = assetCntD - statisticsDtoD.getAssetCnt();
                                assetMinusCntD = 0;

                            } else {

                                assetPlusCntD = 0;
                                assetMinusCntD = 0;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // double 값 NaN 일 경우 처리
                    if (Double.isNaN(auditRateAvgD)) {

                        auditRateAvgD = 0.0;
                    } // end if

                    dto.setSwType(keyStrD);
                    dto.setAssetCntD(assetCntD);
                    dto.setAssetPlusCntD(assetPlusCntD);
                    dto.setAssetMinusCntD(assetMinusCntD);
                    dto.setAuditRateD(auditRateAvgD);
                    dto.setAllAuditRateD(auditRateAvgD);
                    dto.setAllAuditRateAvgD(allAuditRateAvgD);
                } // end for
                /*--- list7D swTypeListNewD ---*/
            }

            // 기타(ETC) 점검군 처리
            String[] listArr = {"Linux", "Windows", "DB", "WEB", "WAS", "NW", "URL"};
            boolean listAddFlag = false;
            boolean etcAddFlag = false;
            String etcSwTypeStr = null;
            for (SnetStatisticsDto strVo : tempSnetStatisticsDtoList) {

                etcAddFlag = Arrays.stream(listArr).noneMatch(x -> StringUtils.equals(x, strVo.getSwType()));

                if (etcAddFlag) {

                    etcSwTypeStr = strVo.getSwType();
                    break;
                }
            } // end for
            for (String strArr : listArr) {

                SnetStatisticsDto resultDto = new SnetStatisticsDto();

                listAddFlag = tempSnetStatisticsDtoList.stream().noneMatch(x -> StringUtils.equals(x.getSwType(), strArr));

                if (StringUtils.equals("Linux", strArr) && !listAddFlag) {


                    resultDto = tempSnetStatisticsDtoList.stream().filter(x -> x.getSwType().equals("Linux")).findFirst().orElse(null);
                    resultDto = setSnetStatisticsDto(resultDto, snetStatisticsDto4);


                    resultDtoList.add(resultDto);
                } else if (StringUtils.equals("Linux", strArr) && listAddFlag) {

                    SnetStatisticsDto snetStatisticsDto = SnetStatisticsDto.builder()
                            .swType(strArr)
                            .assetCnt(0)
                            .assetPlusCnt(0)
                            .assetMinusCnt(0)
                            .allAuditRate(0)
                            .auditRate(0)
                            .build();
                    resultDtoList.add(snetStatisticsDto);
                }

                if (StringUtils.equals("Windows", strArr) && !listAddFlag) {

                    resultDto = tempSnetStatisticsDtoList.stream().filter(x -> x.getSwType().equals("Windows")).findFirst().orElse(null);
                    resultDto = setSnetStatisticsDto(resultDto, snetStatisticsDto4);

                    resultDtoList.add(resultDto);
                } else if (StringUtils.equals("Windows", strArr) && listAddFlag) {

                    SnetStatisticsDto snetStatisticsDto = SnetStatisticsDto.builder()
                            .swType(strArr)
                            .assetCnt(0)
                            .assetPlusCnt(0)
                            .assetMinusCnt(0)
                            .allAuditRate(0)
                            .auditRate(0)
                            .build();
                    resultDtoList.add(snetStatisticsDto);
                }

                if (StringUtils.equals("DB", strArr) && !listAddFlag) {

                    resultDto = tempSnetStatisticsDtoList.stream().filter(x -> x.getSwType().equals("DB")).findFirst().orElse(null);
                    resultDto = setSnetStatisticsDto(resultDto, snetStatisticsDto4);

                    resultDtoList.add(resultDto);
                } else if (StringUtils.equals("DB", strArr) && listAddFlag) {

                    SnetStatisticsDto snetStatisticsDto = SnetStatisticsDto.builder()
                            .swType(strArr)
                            .assetCnt(0)
                            .assetPlusCnt(0)
                            .assetMinusCnt(0)
                            .allAuditRate(0)
                            .auditRate(0)
                            .build();
                    resultDtoList.add(snetStatisticsDto);
                }

                if (StringUtils.equals("WEB", strArr) && !listAddFlag) {

                    resultDto = tempSnetStatisticsDtoList.stream().filter(x -> x.getSwType().equals("WEB")).findFirst().orElse(null);
                    resultDto = setSnetStatisticsDto(resultDto, snetStatisticsDto4);

                    resultDtoList.add(resultDto);
                } else if (StringUtils.equals("WEB", strArr) && listAddFlag) {

                    SnetStatisticsDto snetStatisticsDto = SnetStatisticsDto.builder()
                            .swType(strArr)
                            .assetCnt(0)
                            .assetPlusCnt(0)
                            .assetMinusCnt(0)
                            .allAuditRate(0)
                            .auditRate(0)
                            .build();
                    resultDtoList.add(snetStatisticsDto);
                }

                if (StringUtils.equals("WAS", strArr) && !listAddFlag) {

                    resultDto = tempSnetStatisticsDtoList.stream().filter(x -> x.getSwType().equals("WAS")).findFirst().orElse(null);
                    resultDto = setSnetStatisticsDto(resultDto, snetStatisticsDto4);

                    resultDtoList.add(resultDto);
                } else if (StringUtils.equals("WAS", strArr) && listAddFlag) {

                    SnetStatisticsDto snetStatisticsDto = SnetStatisticsDto.builder()
                            .swType(strArr)
                            .assetCnt(0)
                            .assetPlusCnt(0)
                            .assetMinusCnt(0)
                            .allAuditRate(0)
                            .auditRate(0)
                            .build();
                    resultDtoList.add(snetStatisticsDto);
                }

                if (StringUtils.equals("NW", strArr) && !listAddFlag) {

                    resultDto = tempSnetStatisticsDtoList.stream().filter(x -> x.getSwType().equals("NW")).findFirst().orElse(null);
                    resultDto = setSnetStatisticsDto(resultDto, snetStatisticsDto4);

                    resultDtoList.add(resultDto);
                } else if (StringUtils.equals("NW", strArr) && listAddFlag) {

                    SnetStatisticsDto snetStatisticsDto = SnetStatisticsDto.builder()
                            .swType(strArr)
                            .assetCnt(0)
                            .assetPlusCnt(0)
                            .assetMinusCnt(0)
                            .allAuditRate(0)
                            .auditRate(0)
                            .build();
                    resultDtoList.add(snetStatisticsDto);
                }

                if (StringUtils.equals("URL", strArr) && !listAddFlag) {

                    resultDto = tempSnetStatisticsDtoList.stream().filter(x -> x.getSwType().equals("URL")).findFirst().orElse(null);
                    resultDto = setSnetStatisticsDto(resultDto, snetStatisticsDto4);

                    resultDto.setSwType("Web App");
                    resultDtoList.add(resultDto);
                } else if (StringUtils.equals("URL", strArr) && listAddFlag) {

                    SnetStatisticsDto snetStatisticsDto = SnetStatisticsDto.builder()
                            .swType("Web App")
                            .assetCnt(0)
                            .assetPlusCnt(0)
                            .assetMinusCnt(0)
                            .allAuditRate(0)
                            .auditRate(0)
                            .build();
                    resultDtoList.add(snetStatisticsDto);
                }
            } // end for

            // 기타 추가
            if (etcAddFlag && StringUtils.isNotEmpty(etcSwTypeStr)) {

                SnetStatisticsDto resultDto = new SnetStatisticsDto();

                String finalEtcSwTypeStr = etcSwTypeStr;
                resultDto = tempSnetStatisticsDtoList.stream()
                        .filter(x -> x.getSwType().equals(finalEtcSwTypeStr))
                        .findFirst().orElse(null);

                resultDto = setSnetStatisticsDto(resultDto, snetStatisticsDto4);
                resultDto.setSwType("ETC");

                resultDtoList.add(resultDto);
            } else {

                SnetStatisticsDto snetStatisticsDto = SnetStatisticsDto.builder()
                        .swType("ETC")
                        .assetCnt(0)
                        .assetPlusCnt(0)
                        .assetMinusCnt(0)
                        .allAuditRate(0)
                        .auditRate(0)
                        .build();
                resultDtoList.add(snetStatisticsDto);
            } // end if

        } else {

        } // end if else

        return resultDtoList;
    } // end method getAssetSwTypeNmStatusList5


    /**
     * 소스 코드 중복 제거 - setSnetStatisticsDto
     */
    private SnetStatisticsDto setSnetStatisticsDto(SnetStatisticsDto resultDto, SnetStatisticsDto snetStatisticsDto4) {

        return SnetStatisticsDto.builder()
                .swType(resultDto.getSwType())
                .adResultReq(snetStatisticsDto4.getAdResultReq())
                .adResultOk(snetStatisticsDto4.getAdResultOk())
                .adResultNok(snetStatisticsDto4.getAdResultNok())
                .adResultReqD(snetStatisticsDto4.getAdResultReqD())
                .adResultOkD(snetStatisticsDto4.getAdResultOkD())
                .adResultNokD(snetStatisticsDto4.getAdResultNokD())
                .adExceptReq(snetStatisticsDto4.getAdExceptReq())
                .adExceptOk(snetStatisticsDto4.getAdExceptOk())
                .adExceptNok(snetStatisticsDto4.getAdExceptNok())
                .adExceptReqD(snetStatisticsDto4.getAdExceptReqD())
                .adExceptOkD(snetStatisticsDto4.getAdExceptOkD())
                .adExceptNokD(snetStatisticsDto4.getAdExceptNokD())
                .equipCnt(snetStatisticsDto4.getEquipCnt())
                .equipInstallNokCnt(snetStatisticsDto4.getEquipInstallNokCnt())
                .equipAllCnt(snetStatisticsDto4.getEquipAllCnt())
                .equipPlusCnt(snetStatisticsDto4.getEquipPlusCnt())
                .equipMinusCnt(snetStatisticsDto4.getEquipMinusCnt())
                .equipCntD(snetStatisticsDto4.getEquipCntD())
                .equipInstallNokCntD(snetStatisticsDto4.getEquipInstallNokCntD())
                .equipAllCntD(snetStatisticsDto4.getEquipAllCntD())
                .equipPlusCntD(snetStatisticsDto4.getEquipPlusCntD())
                .equipMinusCntD(snetStatisticsDto4.getEquipMinusCntD())
                .assetCnt(resultDto.getAssetCnt())
                .assetPlusCnt(resultDto.getAssetPlusCnt())
                .assetMinusCnt(resultDto.getAssetMinusCnt())
                .auditRate(resultDto.getAuditRate())
                .allAuditRate(resultDto.getAuditRate())
                .allAuditRateAvg(resultDto.getAllAuditRate())
                .assetCntD(resultDto.getAssetCntD())
                .assetPlusCntD(resultDto.getAssetPlusCntD())
                .assetMinusCntD(resultDto.getAssetMinusCntD())
                .auditRateD(resultDto.getAuditRateD())
                .allAuditRateD(resultDto.getAuditRateD())
                .allAuditRateAvgD(resultDto.getAllAuditRateD())
                .division(resultDto.getDivision())
                .build();
    } // end method

    /**
     * 점검항목수, 전체항목수, 미진단, 미조치 조회
     */
    @Override
    public List<SnetStatisticsDto> getAssetSwAuditRateList6(List<SnetStatisticsDto> reportList5, SnetAssetMasterResultDto masterParamDto, String auditRateDivision, boolean dailyFlag, boolean reportCheckFlag
            , String userType, boolean todayFlag) throws Exception {

        String auditRateBranchId = masterParamDto.getBranchId();
        String auditRateTeamId = masterParamDto.getTeamId();
        String Flag = auditRateDivision;
        List<String> assetCdInAllList = Lists.newArrayList();

        List<SnetAssetSwAuditExcelDto> tempAssetMasterDtoParamList = null;
        SnetAssetSwAuditExcelDto tempAssetMasterHstAuditDto1 = null;
        SnetAssetSwAuditExcelDto tempAssetMasterHstAuditDto2 = null;


        if (StringUtils.equalsIgnoreCase("ALL", Flag)) {

            Map userDtoParamMap = new HashMap();
            userDtoParamMap.put("endDay", masterParamDto.getCreateDate());
            // 자산 조회
            tempAssetMasterDtoParamList = assetMasterMapper.getAssetMasterAuditDayList(userDtoParamMap);
            // 미진단 조회
            tempAssetMasterHstAuditDto1 = assetMasterMapper.getAssetNotAuditCntList(userDtoParamMap);
            // 미조치 조회
            tempAssetMasterHstAuditDto2 = assetMasterMapper.getAssetMasterHstAuditDayCntList2(userDtoParamMap);

        } else {

            if (StringUtils.equalsIgnoreCase("B", Flag)) {

                Map userDtoParamMap = new HashMap();
                userDtoParamMap.put("endDay", masterParamDto.getCreateDate());
                userDtoParamMap.put("BRANCH_ID", auditRateBranchId);
                tempAssetMasterDtoParamList = assetMasterMapper.getAssetMasterAuditDayList(userDtoParamMap);
                tempAssetMasterHstAuditDto1 = assetMasterMapper.getAssetNotAuditCntList(userDtoParamMap); // 미진단
                tempAssetMasterHstAuditDto2 = assetMasterMapper.getAssetMasterHstAuditDayCntList2(userDtoParamMap); // 미조치

            } else if (StringUtils.equalsIgnoreCase("T", Flag)) {

                Map userDtoParamMap = new HashMap();
                userDtoParamMap.put("endDay", masterParamDto.getCreateDate());
                userDtoParamMap.put("BRANCH_ID", auditRateBranchId);
                userDtoParamMap.put("TEAM_ID", auditRateTeamId);
                tempAssetMasterDtoParamList = assetMasterMapper.getAssetMasterAuditDayList(userDtoParamMap);
                tempAssetMasterHstAuditDto1 = assetMasterMapper.getAssetNotAuditCntList(userDtoParamMap); // 미진단
                tempAssetMasterHstAuditDto2 = assetMasterMapper.getAssetMasterHstAuditDayCntList2(userDtoParamMap); // 미조치
            }
        } // end if else
        String[] assetCdInArr = assetCdInAllList.stream().distinct().toArray(String[]::new);

        // ETC 구분 처리
        String[] listArr = {"Linux", "Windows", "DB", "WEB", "WAS", "NW", "Web App"};
        String etcSwTypeStr = null;
        boolean etcAddFlag = false;
        for (SnetStatisticsDto strVo : reportList5) {

            etcAddFlag = Arrays.stream(listArr).noneMatch(x -> StringUtils.equalsIgnoreCase(x, strVo.getSwType()));

            if (etcAddFlag) {

                etcSwTypeStr = strVo.getSwType();
                break;
            }
        } // end for


        List<SnetAssetSwAuditExcelDto> list7 = tempAssetMasterDtoParamList.stream().collect(Collectors.toList());
        log.info("*[---------------------------------------------------------------------------------------------]");
        for (SnetAssetSwAuditExcelDto dto : list7) {

            log.info("*[list7] : {},{},{},{},{}", dto.getAssetCd(), dto.getSwType(), dto.getSwNm(), dto.getAuditDay(), dto.getCreateDate());
        }

        String createDate = masterParamDto.getCreateDate();
        List<SnetAssetSwAuditExcelDto> list7D = tempAssetMasterDtoParamList.stream().filter(x -> x.getAuditDay().equals(createDate)).collect(Collectors.toList());
        log.info("*[---------------------------------------------------------------------------------------------]");
        for (SnetAssetSwAuditExcelDto dto : list7D) {

            log.info("*[list7D] : {},{},{},{},{}", dto.getAssetCd(), dto.getSwType(), dto.getSwNm(), dto.getAuditDay(), dto.getCreateDate());
        }

        // 점검항목수
        boolean itemTotalFlag = false;
        int itemTotal = 0;
        /*--- 전체 ---*/
        for (SnetStatisticsDto dto : reportList5) {

            String swTypeNmKey = dto.getSwType();
            if (list7 != null && list7.size() > 0) {

                for (SnetAssetSwAuditExcelDto excelDto : list7) {

                    if (StringUtils.equals(swTypeNmKey, getSwNmKey(excelDto.getSwType(),excelDto.getSwNm()))) {

                        itemTotalFlag = true;
                        break;
                    } else {
                        itemTotalFlag = false;
                    }
                }
                if (itemTotalFlag) {

                    log.info("*[---------------------------------------------------------------------------------------------]");
                    SnetAssetSwAuditExcelDto excelDto = list7.stream().filter(x -> StringUtils.equals(swTypeNmKey, getSwNmKey(x.getSwType(), x.getSwNm()))).findFirst().get();
                    log.info("*[excelDto] : {}", excelDto);
                    if (excelDto != null) {

                        Map paramMap = new HashMap();
                        paramMap.put("startAuditDay", "19990101");
                        paramMap.put("endAuditDay", masterParamDto.getCreateDate());

                        paramMap.put("ASSET_CD", excelDto.getAssetCd());
                        paramMap.put("AUDIT_DAY", excelDto.getAuditDay());
//                    paramMap.put("CREATE_DATE", excelDto.getCreateDate());

                        if (StringUtils.equalsIgnoreCase("OS", excelDto.getSwType())) {

                            if (StringUtils.equalsIgnoreCase("Windows", excelDto.getSwNm())) {

                                paramMap.put("SW_TYPE", "OS");
                                paramMap.put("SW_NM", "Windows");
                            } else {

                                paramMap.put("SW_TYPE", "OS");
                                paramMap.put("SW_NM", excelDto.getSwNm());
                            }
                        } else {

                            paramMap.put("SW_TYPE", excelDto.getSwType());
                            paramMap.remove("SW_NM");
                        }

                        itemTotal = testMapper.getItemTotal(paramMap);
                        dto.setItemCnt(itemTotal);
                    } // end if
                } // end if
            } // end if
        } // end for
        /*--- 전체 ---*/

        /*--- 매일 ---*/
        int itemTotalD = 0;
        for (SnetStatisticsDto dto : reportList5) {

            String swTypeNmKey = dto.getSwType();
            if (list7D != null && list7D.size() > 0) {

                for (SnetAssetSwAuditExcelDto excelDto : list7D) {

                    if (StringUtils.equals(swTypeNmKey, getSwNmKey(excelDto.getSwType(),excelDto.getSwNm()))) {

                        itemTotalFlag = true;
                        break;
                    } else {
                        itemTotalFlag = false;
                    }
                }
                if (itemTotalFlag) {

                    log.info("*[---------------------------------------------------------------------------------------------]");
                    SnetAssetSwAuditExcelDto excelDto = list7D.stream().filter(x -> StringUtils.equals(swTypeNmKey, getSwNmKey(x.getSwType(), x.getSwNm()))).findFirst().get();
                    log.info("*[excelDto] : {}", excelDto);
                    if (excelDto != null) {

                        Map paramMap = new HashMap();
                        paramMap.put("startAuditDay", "19990101");
                        paramMap.put("endAuditDay", masterParamDto.getCreateDate());

                        paramMap.put("ASSET_CD", excelDto.getAssetCd());
                        paramMap.put("AUDIT_DAY", excelDto.getAuditDay());
//                    paramMap.put("CREATE_DATE", excelDto.getCreateDate());

                        if (StringUtils.equalsIgnoreCase("OS", excelDto.getSwType())) {

                            if (StringUtils.equalsIgnoreCase("Windows", excelDto.getSwNm())) {

                                paramMap.put("SW_TYPE", "OS");
                                paramMap.put("SW_NM", "Windows");
                            } else {

                                paramMap.put("SW_TYPE", "OS");
                                paramMap.put("SW_NM", excelDto.getSwNm());
                            }
                        } else {

                            paramMap.put("SW_TYPE", excelDto.getSwType());
                            paramMap.remove("SW_NM");
                        }

                        itemTotal = testMapper.getItemTotal(paramMap);
                        dto.setItemCntD(itemTotal);
                    } // end if
                } // end if
            } // end if
        } // end for
        /*--- 매일 ---*/

        /*--- 전체 ---*/
        int notAuditCnt = tempAssetMasterHstAuditDto1.getNotCnt(); // 미진단
        int notCokCnt = tempAssetMasterHstAuditDto2.getNotCnt(); // 미조치


        // 전체보안준수율 평균
        double allAuditRateAvg = 0L;
        int allAuditRateLen = 0;

        // 점검군 보안준수율
        for (SnetStatisticsDto dto : reportList5) {

            // 전체항목수
            int overAllMultiple = dto.getItemCnt() * dto.getAssetCnt();

            // 점검군 보안준수율
            double allAuditRate = 0L;
            allAuditRate = dto.getAuditRate();

            dto.setSwType(dto.getSwType());
            dto.setAssetCnt(dto.getAssetCnt());
            dto.setAllItemCnt(overAllMultiple);
            if (StringUtils.equalsIgnoreCase("B", Flag)) {

                dto.setBranchAuditRate(allAuditRate);
//                dto.setBranchAuditRateAvg(allAuditRateAvg);
                allAuditRateAvg += allAuditRate;
                dto.setAssetNotAuditCnt(notAuditCnt); // 자산현황.미진단
                dto.setAssetNotCokCnt(notCokCnt); // 자산현황.미조치
            } else if (StringUtils.equalsIgnoreCase("T", Flag)) {

                dto.setTeamAuditRate(allAuditRate);
//                dto.setTeamAuditRateAvg(allAuditRateAvg);
                allAuditRateAvg += allAuditRate;
                dto.setAssetNotAuditCnt(notAuditCnt); // 자산현황.미진단
                dto.setAssetNotCokCnt(notCokCnt); // 자산현황.미조치
            } else {

                dto.setAllAuditRate(allAuditRate);
//                dto.setAllAuditRateAvg(allAuditRateAvg);
                allAuditRateAvg += allAuditRate;
                dto.setAssetNotAuditCnt(notAuditCnt); // 자산현황.미진단
                dto.setAssetNotCokCnt(notCokCnt); // 자산현황.미조치
            }

            if (allAuditRate != 0) {
                allAuditRateLen++;
            }
        } // end for

        allAuditRateAvg = allAuditRateAvg / allAuditRateLen;
        allAuditRateAvg = Math.round( allAuditRateAvg * 100d ) / 100d;

        for (SnetStatisticsDto dto : reportList5) {

            if (StringUtils.equalsIgnoreCase("B", Flag)) {

                dto.setAllAuditRateAvg(allAuditRateAvg);
                dto.setBranchAuditRateAvg(allAuditRateAvg);
            } else if (StringUtils.equalsIgnoreCase("T", Flag)) {

                dto.setAllAuditRateAvg(allAuditRateAvg);
                dto.setBranchAuditRateAvg(allAuditRateAvg);
                dto.setTeamAuditRateAvg(allAuditRateAvg);
            } else {

                dto.setAllAuditRateAvg(allAuditRateAvg);
            }
        } // end for
        /*--- 전체 ---*/

        /*--- 매일 ---*/
        for (SnetStatisticsDto dto : reportList5) {

            // 전체항목수
            int overAllMultipleD = dto.getItemCntD() * dto.getAssetCntD();

            // 점검군 보안준수율
            double allAuditRateD = 0.0;
            allAuditRateD = dto.getAuditRateD();

            // 전체보안준수율 평균
            double allAuditRateAvgD = 0.0;
            if (list7D.size() > 0) {
                allAuditRateAvgD = list7D.stream()
                        .mapToDouble(x -> x.getAuditRate())
                        .average()
                        .orElseGet(() -> 0.0);

            } else {
                allAuditRateAvgD = 0.0;
            }

            dto.setSwType(dto.getSwType());
            dto.setAssetCntD(dto.getAssetCntD());
            dto.setAllItemCntD(overAllMultipleD);
            dto.setBranchId(auditRateBranchId);
            dto.setTeamId(auditRateTeamId);

            if (StringUtils.equalsIgnoreCase("B", Flag)) {

                dto.setBranchAuditRateD(allAuditRateD);
                dto.setBranchAuditRateAvgD(allAuditRateAvgD);
                dto.setAssetNotAuditCntD(0); // 자산현황.미진단
                dto.setAssetNotCokCntD(0); // 자산현황.미조치

            } else if (StringUtils.equalsIgnoreCase("T", Flag)) {

                dto.setTeamAuditRateD(allAuditRateD);
                dto.setTeamAuditRateAvgD(allAuditRateAvgD);
                dto.setAssetNotAuditCntD(0); // 자산현황.미진단
                dto.setAssetNotCokCntD(0); // 자산현황.미조치
            } else {

                dto.setAllAuditRateD(allAuditRateD);
                dto.setAllAuditRateAvgD(allAuditRateAvgD);
                dto.setAssetNotAuditCntD(0); // 자산현황.미진단
                dto.setAssetNotCokCntD(0); // 자산현황.미조치
            }

            int seq = 2;
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Calendar time = Calendar.getInstance();
            String statisticsId = format.format(time.getTime()) + StringUtils.left(StringUtils.replace(UUID.randomUUID().toString(), "-",""),15);

            dto.setSeq(seq);
            dto.setStatisticsId(statisticsId);
            dto.setAuditStdCd(masterParamDto.getAuditStandard());
            dto.setBranchId(masterParamDto.getBranchId());
            dto.setTeamId(masterParamDto.getTeamId());
            dto.setCreateDay(masterParamDto.getCreateDate());

            String division = dto.getDivision();
            if (StringUtils.isEmpty(division)) {
                dto.setDivision(userType);
            }

            if (todayFlag) {
                dto.setToday("TODAY");
                dto.setCreateTime(masterParamDto.getCreateTime());
                auditStatisticsMapper.insertAuditStatisticsToday(dto);
            } else {

                auditStatisticsMapper.insertAuditStatistics(dto);
            }

        } // end for
        /*--- 매일 ---*/

        return reportList5;
    } // end method getAssetSwAuditRateList6

    /**
     * SE 에 대한 점검군별 평균 보안 준수율
     */
    public List<SnetStatisticsDto> getAssetSwAuditRateList6B(List<SnetStatisticsDto> reportList5
            , SnetAssetMasterResultDto masterParamDto, String auditRateDivision
            , boolean dailyFlag
            , boolean reportCheckFlag
            , String userType
            , List<SnetStatisticsDto> reportListSU
            , boolean todayFlag) throws Exception {

        String auditRateBranchId = masterParamDto.getBranchId();
        String auditRateTeamId = masterParamDto.getTeamId();
        String Flag = auditRateDivision;
        List<String> assetCdInAllList = Lists.newArrayList();

        List<SnetAssetSwAuditExcelDto> tempAssetMasterDtoParamList = null;
        SnetAssetSwAuditExcelDto tempAssetMasterHstAuditDto1 = null;
        SnetAssetSwAuditExcelDto tempAssetMasterHstAuditDto2 = null;

        if (StringUtils.equalsIgnoreCase("ALL", Flag)) {

            Map userDtoParamMap = new HashMap();
            tempAssetMasterDtoParamList = assetMasterMapper.getAssetMasterAuditDayList(userDtoParamMap);
            tempAssetMasterHstAuditDto1 = assetMasterMapper.getAssetNotAuditCntList(userDtoParamMap); // 미진단
            tempAssetMasterHstAuditDto2 = assetMasterMapper.getAssetMasterHstAuditDayCntList2(userDtoParamMap); // 미조치

        } else {

            if (StringUtils.equalsIgnoreCase("B", Flag)) {

                Map userDtoParamMap = new HashMap();
                userDtoParamMap.put("BRANCH_ID", auditRateBranchId);
                tempAssetMasterDtoParamList = assetMasterMapper.getAssetMasterAuditDayList(userDtoParamMap);
                tempAssetMasterHstAuditDto1 = assetMasterMapper.getAssetNotAuditCntList(userDtoParamMap); // 미진단
                tempAssetMasterHstAuditDto2 = assetMasterMapper.getAssetMasterHstAuditDayCntList2(userDtoParamMap); // 미조치

            } else if (StringUtils.equalsIgnoreCase("T", Flag)) {

                Map userDtoParamMap = new HashMap();
                userDtoParamMap.put("BRANCH_ID", auditRateBranchId);
                userDtoParamMap.put("TEAM_ID", auditRateTeamId);
                tempAssetMasterDtoParamList = assetMasterMapper.getAssetMasterAuditDayList(userDtoParamMap);
                tempAssetMasterHstAuditDto1 = assetMasterMapper.getAssetNotAuditCntList(userDtoParamMap); // 미진단
                tempAssetMasterHstAuditDto2 = assetMasterMapper.getAssetMasterHstAuditDayCntList2(userDtoParamMap); // 미조치
            }
        } // end if else

        String[] assetCdInArr = assetCdInAllList.stream().distinct().toArray(String[]::new);

        // ETC 구분 처리
        String[] listArr = {"Linux", "Windows", "DB", "WEB", "WAS", "NW", "Web App"};
        String etcSwTypeStr = null;
        boolean etcAddFlag = false;
        for (SnetStatisticsDto strVo : reportList5) {

            etcAddFlag = Arrays.stream(listArr).noneMatch(x -> StringUtils.equalsIgnoreCase(x, strVo.getSwType()));

            if (etcAddFlag) {

                etcSwTypeStr = strVo.getSwType();
                break;
            }
        } // end for


        List<SnetAssetSwAuditExcelDto> list7 = tempAssetMasterDtoParamList.stream().collect(Collectors.toList());

        String createDate = masterParamDto.getCreateDate();
        List<SnetAssetSwAuditExcelDto> list7D = tempAssetMasterDtoParamList.stream().filter(x -> x.getAuditDay().equals(createDate)).collect(Collectors.toList());

        // 점검항목수
        int itemTotal = 0;
        /*--- 전체 ---*/
        // -> to do test : 종합보고서 생성시 사용
        /*--- 전체 ---*/

        /*--- 매일 ---*/
        int itemTotalD = 0;
        // -> to do test : 종합보고서 생성시 사용
        /*--- 매일 ---*/

        // 보안준수율
        /*--- 전체 ---*/

        int notAuditCnt = tempAssetMasterHstAuditDto1.getNotCnt(); // 미진단
        int notCokCnt = tempAssetMasterHstAuditDto2.getNotCnt(); // 미조치


        // 전체보안준수율 평균
        double allAuditRateAvg = 0L;
        int allAuditRateLen = 0;
        double allAuditRateAvgSU = 0L;
        int allAuditRateLenSU = 0;

        // 점검군 보안준수율
        for (SnetStatisticsDto dto : reportList5) {

            // 전체항목수
            int overAllMultiple = dto.getItemCnt() * dto.getAssetCnt();

            // 점검군 보안준수율
            double allAuditRate = 0L;
            double allAuditRateSU = 0L;
            allAuditRate = dto.getAuditRate();

            dto.setSwType(dto.getSwType());
            dto.setAssetCnt(dto.getAssetCnt());
            dto.setAllItemCnt(overAllMultiple);
            if (StringUtils.equalsIgnoreCase("B", Flag)) {

                // 점검군 보안준수율 (SU)
                for (SnetStatisticsDto dtoSU : reportListSU) {

                    if (StringUtils.equals(dto.getSwType(), dtoSU.getSwType())) {

                        allAuditRateSU = dtoSU.getAllAuditRate();
                        dto.setAllAuditRate(allAuditRateSU);
                    } // end if
                } // end for

                allAuditRateAvgSU += allAuditRateSU;

                dto.setBranchAuditRate(allAuditRate);
//                dto.setBranchAuditRateAvg(allAuditRateAvg);
                allAuditRateAvg += allAuditRate;
                dto.setAssetNotAuditCnt(notAuditCnt); // 자산현황.미진단
                dto.setAssetNotCokCnt(notCokCnt); // 자산현황.미조치
            } else if (StringUtils.equalsIgnoreCase("T", Flag)) {

                dto.setTeamAuditRate(allAuditRate);
//                dto.setTeamAuditRateAvg(allAuditRateAvg);
                allAuditRateAvg += allAuditRate;
                dto.setAssetNotAuditCnt(notAuditCnt); // 자산현황.미진단
                dto.setAssetNotCokCnt(notCokCnt); // 자산현황.미조치
            } else {

                dto.setAllAuditRate(allAuditRate);
//                dto.setAllAuditRateAvg(allAuditRateAvg);
                allAuditRateAvg += allAuditRate;
                dto.setAssetNotAuditCnt(notAuditCnt); // 자산현황.미진단
                dto.setAssetNotCokCnt(notCokCnt); // 자산현황.미조치
            }

            if (allAuditRate != 0) {
                allAuditRateLen++;
            }
            if (allAuditRateSU != 0) {
                allAuditRateLenSU++;
            }
        } // end for


        allAuditRateAvgSU = allAuditRateAvgSU / allAuditRateLenSU;
        allAuditRateAvgSU = Math.round( allAuditRateAvgSU * 100d ) / 100d;

        allAuditRateAvg = allAuditRateAvg / allAuditRateLen;
        allAuditRateAvg = Math.round( allAuditRateAvg * 100d ) / 100d;

        for (SnetStatisticsDto dto : reportList5) {

            if (StringUtils.equalsIgnoreCase("B", Flag)) {

                dto.setAllAuditRateAvg(allAuditRateAvgSU);
                dto.setBranchAuditRateAvg(allAuditRateAvg);
            } else if (StringUtils.equalsIgnoreCase("T", Flag)) {

                dto.setTeamAuditRateAvg(allAuditRateAvg);
            } else {

                dto.setAllAuditRateAvg(allAuditRateAvg);
            }
        } // end for
        /*--- 전체 ---*/

        /*--- 매일 ---*/
        for (SnetStatisticsDto dto : reportList5) {

            // 전체항목수
            int overAllMultipleD = dto.getItemCntD() * dto.getAssetCntD();

            // 점검군 보안준수율
            double allAuditRateD = 0.0;
            allAuditRateD = dto.getAuditRateD();

            // 전체보안준수율 평균
            double allAuditRateAvgD = 0.0;
            if (list7D.size() > 0) {
                allAuditRateAvgD = list7D.stream()
                        .mapToDouble(x -> x.getAuditRate())
                        .average()
                        .orElseGet(() -> 0.0);

            } else {
                allAuditRateAvgD = 0.0;
            }

            dto.setSwType(dto.getSwType());
            dto.setAssetCntD(dto.getAssetCntD());
            dto.setAllItemCntD(overAllMultipleD);
            dto.setBranchId(auditRateBranchId);
            dto.setTeamId(auditRateTeamId);

            if (StringUtils.equalsIgnoreCase("B", Flag)) {

                dto.setBranchAuditRateD(allAuditRateD);
                dto.setBranchAuditRateAvgD(allAuditRateAvgD);
                dto.setAssetNotAuditCntD(0); // 자산현황.미진단
                dto.setAssetNotCokCntD(0); // 자산현황.미조치

            } else if (StringUtils.equalsIgnoreCase("T", Flag)) {

                dto.setTeamAuditRateD(allAuditRateD);
                dto.setTeamAuditRateAvgD(allAuditRateAvgD);
                dto.setAssetNotAuditCntD(0); // 자산현황.미진단
                dto.setAssetNotCokCntD(0); // 자산현황.미조치
            } else {

                dto.setAllAuditRateD(allAuditRateD);
                dto.setAllAuditRateAvgD(allAuditRateAvgD);
                dto.setAssetNotAuditCntD(0); // 자산현황.미진단
                dto.setAssetNotCokCntD(0); // 자산현황.미조치
            }


            int seq = 2;
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Calendar time = Calendar.getInstance();
            String statisticsId = format.format(time.getTime()) + StringUtils.left(StringUtils.replace(UUID.randomUUID().toString(), "-",""),15);

            dto.setSeq(seq);
            dto.setStatisticsId(statisticsId);
            dto.setAuditStdCd(masterParamDto.getAuditStandard());
            dto.setBranchId(masterParamDto.getBranchId());
            dto.setTeamId(masterParamDto.getTeamId());
            dto.setCreateDay(masterParamDto.getCreateDate());

            String division = dto.getDivision();
            if (StringUtils.isEmpty(division)) {
                dto.setDivision(userType);
            }

            if (todayFlag) {
                dto.setToday("TODAY");
                dto.setCreateTime(masterParamDto.getCreateTime());
                auditStatisticsMapper.insertAuditStatisticsToday(dto);
            } else {

                auditStatisticsMapper.insertAuditStatistics(dto);
            }

        } // end for
        /*--- 매일 ---*/

        return reportList5;
    } // end method getAssetSwAuditRateList6

    /**
     * swType, swNm 을 swNmKey 로 변환
     */
    private String getSwTypeNmKey(String swType, String swNm) {

        String swNmKey = null;

        if (StringUtils.equalsIgnoreCase("OS", swType)) {

            if (!StringUtils.equalsIgnoreCase("Windows", swNm)) {

                swNmKey = "Linux";
            } else {

                swNmKey = "Windows";
            }
        } else if (StringUtils.equalsIgnoreCase("DB", swType)){

            swNmKey = "DB";
        } else if (StringUtils.equalsIgnoreCase("WEB", swType)){

            swNmKey = "WEB";
        } else if (StringUtils.equalsIgnoreCase("WAS", swType)){

            swNmKey = "WAS";
        } else if (StringUtils.equalsIgnoreCase("NW", swType)){

            swNmKey = "NW";
        } else if (StringUtils.equalsIgnoreCase("URL", swType)){

            swNmKey = "Web App";
        } else {

            swNmKey = "ETC";
        }

        return swNmKey;
    } // end method

    /**
     * SV,OS,OP 에 대한 점검군별 평균 보안 준수율
     */
    public List<SnetStatisticsDto> getAssetSwAuditRateList6T(List<SnetStatisticsDto> reportList5
            , SnetAssetMasterResultDto masterParamDto, String auditRateDivision
            , boolean dailyFlag
            , boolean reportCheckFlag
            , String userType
            , List<SnetStatisticsDto> reportListSU
            , List<SnetStatisticsDto> reportListSE
            , boolean todayFlag) throws Exception {

        String auditRateBranchId = masterParamDto.getBranchId();
        String auditRateTeamId = masterParamDto.getTeamId();

        String Flag = auditRateDivision;
        List<String> assetCdInAllList = Lists.newArrayList();

        List<SnetAssetSwAuditExcelDto> tempAssetMasterDtoParamList = null;
        SnetAssetSwAuditExcelDto tempAssetMasterHstAuditDto1 = null;
        SnetAssetSwAuditExcelDto tempAssetMasterHstAuditDto2 = null;

        if (StringUtils.equalsIgnoreCase("ALL", Flag)) {

            Map userDtoParamMap = new HashMap();
            tempAssetMasterDtoParamList = assetMasterMapper.getAssetMasterAuditDayList(userDtoParamMap);
            tempAssetMasterHstAuditDto1 = assetMasterMapper.getAssetNotAuditCntList(userDtoParamMap); // 미진단
            tempAssetMasterHstAuditDto2 = assetMasterMapper.getAssetMasterHstAuditDayCntList2(userDtoParamMap); // 미조치

        } else {

            if (StringUtils.equalsIgnoreCase("B", Flag)) {

                Map userDtoParamMap = new HashMap();
                userDtoParamMap.put("BRANCH_ID", auditRateBranchId);
                tempAssetMasterDtoParamList = assetMasterMapper.getAssetMasterAuditDayList(userDtoParamMap);

                tempAssetMasterHstAuditDto1 = assetMasterMapper.getAssetNotAuditCntList(userDtoParamMap); // 미진단
                tempAssetMasterHstAuditDto2 = assetMasterMapper.getAssetMasterHstAuditDayCntList2(userDtoParamMap); // 미조치

            } else if (StringUtils.equalsIgnoreCase("T", Flag)) {

                Map userDtoParamMap = new HashMap();
                userDtoParamMap.put("BRANCH_ID", auditRateBranchId);
                userDtoParamMap.put("TEAM_ID", auditRateTeamId);
                tempAssetMasterDtoParamList = assetMasterMapper.getAssetMasterAuditDayList(userDtoParamMap);
                tempAssetMasterHstAuditDto1 = assetMasterMapper.getAssetNotAuditCntList(userDtoParamMap); // 미진단
                tempAssetMasterHstAuditDto2 = assetMasterMapper.getAssetMasterHstAuditDayCntList2(userDtoParamMap); // 미조치
            }
        } // end if else

        String[] assetCdInArr = assetCdInAllList.stream().distinct().toArray(String[]::new);

        // ETC 구분 처리
        String[] listArr = {"Linux", "Windows", "DB", "WEB", "WAS", "NW", "Web App"};
        String etcSwTypeStr = null;
        boolean etcAddFlag = false;
        for (SnetStatisticsDto strVo : reportList5) {

            etcAddFlag = Arrays.stream(listArr).noneMatch(x -> StringUtils.equalsIgnoreCase(x, strVo.getSwType()));

            if (etcAddFlag) {

                etcSwTypeStr = strVo.getSwType();
                break;
            }
        } // end for

        List<SnetAssetSwAuditExcelDto> list7 = tempAssetMasterDtoParamList.stream().collect(Collectors.toList());


        String createDate = masterParamDto.getCreateDate();
        List<SnetAssetSwAuditExcelDto> list7D = tempAssetMasterDtoParamList.stream().filter(x -> x.getAuditDay().equals(createDate)).collect(Collectors.toList());

        // 점검항목수
        int itemTotal = 0;
        /*--- 전체 ---*/
        // -> to do test : 종합보고서 생성시 사용
        /*--- 전체 ---*/

        /*--- 매일 ---*/
        int itemTotalD = 0;
        // -> to do test : 종합보고서 생성시 사용
        /*--- 매일 ---*/

        // 보안준수율
        /*--- 전체 ---*/
        int notAuditCnt = tempAssetMasterHstAuditDto1.getNotCnt(); // 미진단
        int notCokCnt = tempAssetMasterHstAuditDto2.getNotCnt(); // 미조치

        // 전체보안준수율 평균
        double allAuditRateAvg = 0L;
        int allAuditRateLen = 0;
        double allAuditRateAvgSU = 0L;
        int allAuditRateLenSU = 0;
        double allAuditRateAvgSE = 0L;
        int allAuditRateLenSE = 0;

        // 점검군 보안준수율
        for (SnetStatisticsDto dto : reportList5) {

            // 전체항목수
            int overAllMultiple = dto.getItemCnt() * dto.getAssetCnt();

            // 점검군 보안준수율
            double allAuditRate = 0L;
            double allAuditRateSU = 0L;
            double allAuditRateSE = 0L;
            allAuditRate = dto.getAuditRate();

            dto.setSwType(dto.getSwType());
            dto.setAssetCnt(dto.getAssetCnt());
            dto.setAllItemCnt(overAllMultiple);

            if (StringUtils.equalsIgnoreCase("B", Flag)) {

                // 점검군 보안준수율 (SU)
                for (SnetStatisticsDto dtoSU : reportListSU) {

                    if (StringUtils.equals(dto.getSwType(), dtoSU.getSwType())) {

                        allAuditRateSU = dtoSU.getAllAuditRate();
                        dto.setAllAuditRate(allAuditRateSU);
                    } // end if
                } // end for
                allAuditRateAvgSU += allAuditRateSU;

                dto.setBranchAuditRate(allAuditRate);
//                dto.setBranchAuditRateAvg(allAuditRateAvg);
                allAuditRateAvg += allAuditRate;
                dto.setAssetNotAuditCnt(notAuditCnt); // 자산현황.미진단
                dto.setAssetNotCokCnt(notCokCnt); // 자산현황.미조치
            } else if (StringUtils.equalsIgnoreCase("T", Flag)) {

                // 점검군 보안준수율 (SU)
                for (SnetStatisticsDto dtoSU : reportListSU) {

                    if (StringUtils.equals(dto.getSwType(), dtoSU.getSwType())) {

                        allAuditRateSU = dtoSU.getAllAuditRate();
                        dto.setAllAuditRate(allAuditRateSU);
                    } // end if
                } // end for
                allAuditRateAvgSU += allAuditRateSU;

                // 점검군 보안준수율 (SE)
                for (SnetStatisticsDto dtoSE : reportListSE) {

                    if (StringUtils.equals(dto.getSwType(), dtoSE.getSwType())) {

                        allAuditRateSE = dtoSE.getBranchAuditRate();
                        dto.setBranchAuditRate(allAuditRateSE);
                    } // end if
                } // end for
                allAuditRateAvgSE += allAuditRateSE;

                dto.setTeamAuditRate(allAuditRate);
                allAuditRateAvg += allAuditRate;

                dto.setAssetNotAuditCnt(notAuditCnt); // 자산현황.미진단
                dto.setAssetNotCokCnt(notCokCnt); // 자산현황.미조치
            } else {

                dto.setAllAuditRate(allAuditRate);
//                dto.setAllAuditRateAvg(allAuditRateAvg);
                allAuditRateAvg += allAuditRate;
                dto.setAssetNotAuditCnt(notAuditCnt); // 자산현황.미진단
                dto.setAssetNotCokCnt(notCokCnt); // 자산현황.미조치
            }

            if (allAuditRate != 0) {
                allAuditRateLen++;
            }
            if (allAuditRateSU != 0) {
                allAuditRateLenSU++;
            }
            if (allAuditRateSE != 0) {
                allAuditRateLenSE++;
            }
        } // end for

        allAuditRateAvgSU = allAuditRateAvgSU / allAuditRateLenSU;
        allAuditRateAvgSU = Math.round( allAuditRateAvgSU * 100d ) / 100d;

        allAuditRateAvgSE = allAuditRateAvgSE / allAuditRateLenSE;
        allAuditRateAvgSE = Math.round( allAuditRateAvgSE * 100d ) / 100d;

        allAuditRateAvg = allAuditRateAvg / allAuditRateLen;
        allAuditRateAvg = Math.round( allAuditRateAvg * 100d ) / 100d;

        for (SnetStatisticsDto dto : reportList5) {

            if (StringUtils.equalsIgnoreCase("B", Flag)) {

                dto.setAllAuditRateAvg(allAuditRateAvgSU);
                dto.setBranchAuditRateAvg(allAuditRateAvg);
            } else if (StringUtils.equalsIgnoreCase("T", Flag)) {

                dto.setAllAuditRateAvg(allAuditRateAvgSU);
                dto.setBranchAuditRateAvg(allAuditRateAvgSE);
                dto.setTeamAuditRateAvg(allAuditRateAvg);
            } else {

                dto.setAllAuditRateAvg(allAuditRateAvg);
            }
        } // end for
        /*--- 전체 ---*/

        /*--- 매일 ---*/
        for (SnetStatisticsDto dto : reportList5) {

            // 전체항목수
            int overAllMultipleD = dto.getItemCntD() * dto.getAssetCntD();

            // 점검군 보안준수율
            double allAuditRateD = 0.0;
            allAuditRateD = dto.getAuditRateD();

            // 전체보안준수율 평균
            double allAuditRateAvgD = 0.0;
            if (list7D.size() > 0) {
                allAuditRateAvgD = list7D.stream()
                        .mapToDouble(x -> x.getAuditRate())
                        .average()
                        .orElseGet(() -> 0.0);

            } else {
                allAuditRateAvgD = 0.0;
            }

            dto.setSwType(dto.getSwType());
            dto.setAssetCntD(dto.getAssetCntD());
            dto.setAllItemCntD(overAllMultipleD);
            dto.setBranchId(auditRateBranchId);
            dto.setTeamId(auditRateTeamId);

            if (StringUtils.equalsIgnoreCase("B", Flag)) {

                dto.setBranchAuditRateD(allAuditRateD);
                dto.setBranchAuditRateAvgD(allAuditRateAvgD);
                dto.setAssetNotAuditCntD(0); // 자산현황.미진단
                dto.setAssetNotCokCntD(0); // 자산현황.미조치

            } else if (StringUtils.equalsIgnoreCase("T", Flag)) {

                dto.setTeamAuditRateD(allAuditRateD);
                dto.setTeamAuditRateAvgD(allAuditRateAvgD);
                dto.setAssetNotAuditCntD(0); // 자산현황.미진단
                dto.setAssetNotCokCntD(0); // 자산현황.미조치
            } else {

                dto.setAllAuditRateD(allAuditRateD);
                dto.setAllAuditRateAvgD(allAuditRateAvgD);
                dto.setAssetNotAuditCntD(0); // 자산현황.미진단
                dto.setAssetNotCokCntD(0); // 자산현황.미조치
            }


            int seq = 2;
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Calendar time = Calendar.getInstance();
            String statisticsId = format.format(time.getTime()) + StringUtils.left(StringUtils.replace(UUID.randomUUID().toString(), "-",""),15);

            dto.setSeq(seq);
            dto.setStatisticsId(statisticsId);
            dto.setAuditStdCd(masterParamDto.getAuditStandard());
            dto.setBranchId(masterParamDto.getBranchId());
            dto.setTeamId(masterParamDto.getTeamId());
            dto.setCreateDay(masterParamDto.getCreateDate());

            String division = dto.getDivision();
            if (StringUtils.isEmpty(division)) {
                dto.setDivision(userType);
            }

            if (todayFlag) {
                dto.setToday("TODAY");
                dto.setCreateTime(masterParamDto.getCreateTime());
                auditStatisticsMapper.insertAuditStatisticsToday(dto);
            } else {

                auditStatisticsMapper.insertAuditStatistics(dto);
            }

        } // end for
        /*--- 매일 ---*/

        return reportList5;
    } // end method getAssetSwAuditRateList6

    /**
     * REPORT 생성시 사용하는 통계 데이터
     */
    @Override
    public void insertAuditStatistics12Report(List<SnetStatisticsDto> list) throws Exception {

        for (SnetStatisticsDto dto : list) {

            int seq = 2;
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Calendar time = Calendar.getInstance();
            String statisticsId = format.format(time.getTime()) + StringUtils.left(StringUtils.replace(UUID.randomUUID().toString(), "-",""),15);

            dto.setSeq(seq);
            dto.setStatisticsId(statisticsId);
            if (StringUtils.isEmpty(dto.getDivision())) {
                dto.setDivision("NONE");
            }


            dto.setDivision("REPORT");

            auditStatisticsMapper.insertAuditStatistics(dto);

            for (SnetStatisticsDetailDto itemdetailDto : dto.getDetailList()) {

                itemdetailDto.setSeq(seq);
                itemdetailDto.setStatisticsId(statisticsId);
                itemdetailDto.setBranchId(dto.getBranchId());
                itemdetailDto.setTeamId(dto.getTeamId());
                itemdetailDto.setCreateDay(dto.getCreateDay());
                itemdetailDto.setAssetCd(itemdetailDto.getAssetCd());

                itemdetailDto.setSwNm(itemdetailDto.getSwTypeNmKey());
                itemdetailDto.setHostNm(itemdetailDto.getHostNm());
                itemdetailDto.setAuditRate(itemdetailDto.getAuditRate());

                itemdetailDto.setDivision(dto.getDivision());
                if (StringUtils.isNotEmpty(itemdetailDto.getAssetCd())) {

                    if (Double.isNaN(itemdetailDto.getAuditRateD())) {
                        itemdetailDto.setAuditRateD(0.0);
                    }
                    auditStatisticsMapper.insertAuditStatisticsDetail(itemdetailDto);
                } // end if
            } // end for
            seq++;
        } // end for
    }

    /**
     * 통계 데이터 상세
     *   - 중요도별 취약점 현황
     */
    @Override
    public List<SnetStatisticsDto> getAssetSwAuditRateList7(List<SnetStatisticsDto> snetStatisticsCommonDtoList1, SnetAssetMasterResultDto masterParamDto) {

        List<String> assetCdInList = Lists.newArrayList();

        for (SnetAssetMasterDto dto : masterParamDto.getMasterDtos()) {
            assetCdInList.add(dto.getAssetCd());
        } // end for

        String[] assetCdInArr = assetCdInList.stream().distinct().toArray(String[]::new);

        Map paramMap = new HashMap();
//        paramMap.put("assetCdInArr", assetCdInArr);

        paramMap.put("ALL_FLAG", "Y");
        List<SnetAssetSwAuditExcelDto> list7 = assetAuditResultMapper.getAssetSwAuditResultList(paramMap);

        String createDate = masterParamDto.getCreateDate();
        paramMap.put("createDate", createDate);
        List<SnetAssetSwAuditExcelDto> list7D = assetAuditResultMapper.getAssetSwAuditResultList(paramMap);
        paramMap.remove("ALL_FLAG");
        paramMap.remove("createDate");

        // 자산별 보안 준수율 하위 10
        /*--- ---*/
        List<SnetAssetSwAuditReportDto> reportDtoList = Lists.newArrayList();
        String tempAssetCd = null;
        String tempAuditDay = null;

        for (SnetAssetSwAuditExcelDto excelDto : list7) {

            tempAssetCd = excelDto.getAssetCd();
            Map tempParamMap = new HashMap();

            if (StringUtils.equalsIgnoreCase("OS", excelDto.getSwType())) {

                if (StringUtils.equalsIgnoreCase("Windows", excelDto.getSwNm())) {

                    tempParamMap.put("SW_TYPE", "OS");
                    tempParamMap.put("SW_NM", "Windows");
                } else {

                    tempParamMap.put("SW_TYPE", "OS");
                    tempParamMap.put("SW_NM", excelDto.getSwNm());
                }
            } else {

                tempParamMap.put("SW_TYPE", excelDto.getSwType());
                tempParamMap.remove("SW_NM");
            }

            tempParamMap.put("ASSET_CD", tempAssetCd);

            SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetMstAuditReportGetMaxAuditDay(tempParamMap);

            if (reportDto1 != null) {
                tempAuditDay = reportDto1.getAuditDay();
                tempParamMap.put("AUDIT_DAY", tempAuditDay);
                SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetMstAuditReportGroupingBy(tempParamMap);

                if (reportDto2 != null) {
                    reportDto2.setAuditRateAssetMaster(reportDto2.getAuditRate());

                    reportDtoList.add(reportDto2);
                } // end if
            } // end if
        } // end for

        /*--- ---*/
        /*--- ---*/
        List<SnetAssetSwAuditReportDto> reportDtoListD = Lists.newArrayList();
        String tempAssetCdD = null;
        String tempAuditDayD = null;
        for (SnetAssetSwAuditExcelDto excelDto : list7D) {

            tempAssetCdD = excelDto.getAssetCd();
            Map tempParamMap = new HashMap();

            if (StringUtils.equalsIgnoreCase("OS", excelDto.getSwType())) {

                if (StringUtils.equalsIgnoreCase("Windows", excelDto.getSwNm())) {

                    tempParamMap.put("SW_TYPE", "OS");
                    tempParamMap.put("SW_NM", "Windows");
                } else {

                    tempParamMap.put("SW_TYPE", "OS");
                    tempParamMap.put("SW_NM", excelDto.getSwNm());
                }
            } else {

                tempParamMap.put("SW_TYPE", excelDto.getSwType());
                tempParamMap.remove("SW_NM");
            }

            tempParamMap.put("ASSET_CD", tempAssetCdD);

            SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetMstAuditReportGetMaxAuditDay(tempParamMap);

            if (reportDto1 != null) {
                tempAuditDayD = reportDto1.getAuditDay();
                tempParamMap.put("AUDIT_DAY", createDate);
                SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetMstAuditReportGroupingBy(tempParamMap);

                if (reportDto2 != null) {
                    reportDto2.setAuditRateAssetMaster(reportDto2.getAuditRate());

                    reportDtoListD.add(reportDto2);
                } // end if
            } // end if
        } // end for
        /*--- ---*/

        // 중요도별 취약점 현황
        /*--- ---*/
        int weaktotal = 0;
        int weakLevelHigh = 0;
        int weakLevelMiddle = 0;
        int weakLevelLow = 0;

        for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

            Map reportWeakMap = new HashMap();
            if (StringUtils.equalsIgnoreCase("OS", reportDto.getSwType())) {

                reportWeakMap.put("SW_TYPE", reportDto.getSwType());

                if (StringUtils.equalsIgnoreCase("Windows", reportDto.getSwNm())) {

                    reportWeakMap.put("SW_TYPE", "OS");
                    reportWeakMap.put("SW_NM", "Windows");
                } else {

                    reportWeakMap.put("SW_TYPE", "OS");
                    reportWeakMap.put("SW_NM", reportDto.getSwNm());
                }
            } else {

                reportWeakMap.put("SW_TYPE", reportDto.getSwType());
                reportWeakMap.remove("SW_NM");
            }
            reportWeakMap.put("F_FLAG", "Y");
            reportWeakMap.put("ASSET_CD", reportDto.getAssetCd());
            reportWeakMap.put("AUDIT_DAY", reportDto.getAuditDay());

            // 최종 진단일에 대한 점검군별 중요도별 취약점 조회
            List<SnetAssetSwAuditReportDto> tempReportDto = testMapper.getWeakLevelsAssetMstAuditReportGetMaxAuditDay(reportWeakMap);

            for (SnetAssetSwAuditReportDto resultDto : tempReportDto) {

                if (StringUtils.equalsIgnoreCase("OS", reportDto.getSwType())) {

                    if (StringUtils.equalsIgnoreCase("Windows", reportDto.getSwNm())) {

                        reportDto.setSwTypeNmKey(reportDto.getSwNm());
                    } else {

                        reportDto.setSwTypeNmKey(reportDto.getSwNm());
                    }
                } else {

                    reportDto.setSwTypeNmKey(reportDto.getSwType());
                } // end if

                if (StringUtils.equals("H", resultDto.getItemGrade())) {

                    weakLevelHigh += resultDto.getCount();
                }
                if (StringUtils.equals("M", resultDto.getItemGrade())) {

                    weakLevelMiddle += resultDto.getCount();
                }
                if (StringUtils.equals("L", resultDto.getItemGrade())) {

                    weakLevelLow += resultDto.getCount();
                } // end if
            } // end for
            weaktotal = weakLevelHigh + weakLevelMiddle + weakLevelLow;
            reportDto.setWeakHighCount(weakLevelHigh);
            reportDto.setWeakMidCount(weakLevelMiddle);
            reportDto.setWeakLowCount(weakLevelLow);
            reportDto.setWeakCount(weaktotal);

            String swTypeNmKey = getSwTypeNmKey(reportDto.getSwType(), reportDto.getSwNm());
            reportDto.setSwTypeNmKey(swTypeNmKey);

            weakLevelHigh = 0;
            weakLevelMiddle = 0;
            weakLevelLow = 0;
            weaktotal = 0;
        } // end for
        /*--- ---*/

        /*--- ---*/
        int weaktotalD = 0;
        int weakLevelHighD = 0;
        int weakLevelMiddleD = 0;
        int weakLevelLowD = 0;

        for (SnetAssetSwAuditReportDto reportDto : reportDtoListD) {

            Map reportWeakMap = new HashMap();
            if (StringUtils.equalsIgnoreCase("OS", reportDto.getSwType())) {

                reportWeakMap.put("SW_TYPE", reportDto.getSwType());

                if (StringUtils.equalsIgnoreCase("Windows", reportDto.getSwNm())) {

                    reportWeakMap.put("SW_TYPE", "OS");
                    reportWeakMap.put("SW_NM", "Windows");
                } else {

                    reportWeakMap.put("SW_TYPE", "OS");
                    reportWeakMap.put("SW_NM", reportDto.getSwNm());
                }
            } else {

                reportWeakMap.put("SW_TYPE", reportDto.getSwType());
                reportWeakMap.remove("SW_NM");
            }
            reportWeakMap.put("F_FLAG", "Y");
            reportWeakMap.put("ASSET_CD", reportDto.getAssetCd());
            reportWeakMap.put("AUDIT_DAY", createDate);

            // 진단일 점검군별 중요도별 취약점 조회
            List<SnetAssetSwAuditReportDto> tempReportDto = testMapper.getWeakLevelsAssetMstAuditReportGetMaxAuditDay(reportWeakMap);

            for (SnetAssetSwAuditReportDto resultDto : tempReportDto) {

                if (StringUtils.equalsIgnoreCase("OS", reportDto.getSwType())) {

                    if (StringUtils.equalsIgnoreCase("Windows", reportDto.getSwNm())) {

                        reportDto.setSwTypeNmKey(reportDto.getSwNm());
                    } else {

                        reportDto.setSwTypeNmKey(reportDto.getSwNm());
                    }
                } else {

                    reportDto.setSwTypeNmKey(reportDto.getSwType());
                } // end if

                if (StringUtils.equals("H", resultDto.getItemGrade())) {

                    weakLevelHighD += resultDto.getCount();
                }
                if (StringUtils.equals("M", resultDto.getItemGrade())) {

                    weakLevelMiddleD += resultDto.getCount();
                }
                if (StringUtils.equals("L", resultDto.getItemGrade())) {

                    weakLevelLowD += resultDto.getCount();
                } // end if
            } // end for

            weaktotalD = weakLevelHighD + weakLevelMiddleD + weakLevelLowD;

            if (reportDto.getWeakHighCountD() == 0 && weakLevelHighD > 0) {
                reportDto.setWeakHighCountD(weakLevelHighD);
            }
            if (reportDto.getWeakMidCountD() == 0 && weakLevelMiddleD > 0) {

                reportDto.setWeakMidCountD(weakLevelMiddleD);
            }
            if (reportDto.getWeakLowCountD() == 0 && weakLevelLowD > 0) {

                reportDto.setWeakLowCountD(weakLevelLowD);
            }
            if (reportDto.getWeakCountD() == 0 && weaktotalD > 0) {
                reportDto.setWeakCountD(weaktotalD);
            }

            weakLevelHighD = 0;
            weakLevelMiddleD = 0;
            weakLevelLowD = 0;
            weaktotalD = 0;
        } // end for
        /*--- ---*/
        /*--- ---*/
        for (SnetStatisticsDto statDto : snetStatisticsCommonDtoList1) {

            String swTypeNmKey = statDto.getSwType();

            List<SnetStatisticsDetailDto> detailDtoList = Lists.newArrayList();

            for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

                if (StringUtils.equalsIgnoreCase(swTypeNmKey, reportDto.getSwTypeNmKey())) {

                    double auditRateAssetMaster = 0.0;
                    auditRateAssetMaster = reportDto.getAuditRateAssetMaster();
                    // double 값 NaN 일 경우 처리
                    if (Double.isNaN(auditRateAssetMaster)) {

                        auditRateAssetMaster = 0.0;
                    } // end if

                    SnetStatisticsDetailDto statisticsDetailDto = SnetStatisticsDetailDto.builder()
                            .seq(statDto.getSeq())
                            .statisticsId(statDto.getStatisticsId())
                            .createDay(statDto.getCreateDay())
                            .branchId(statDto.getBranchId())
                            .teamId(statDto.getTeamId())
                            .auditStdCd(statDto.getAuditStdCd())
                            .swTypeNmKey(reportDto.getSwTypeNmKey())
                            .swType(reportDto.getSwType())
                            .assetCd(reportDto.getAssetCd())
                            .auditDay(reportDto.getAuditDay())
                            .division(statDto.getDivision())
                            .swNm(reportDto.getSwNm())
                            .hostNm(reportDto.getHostNM())
                            .auditRate(auditRateAssetMaster)
                            .weakCnt(reportDto.getWeakCount())
                            .weakHighCnt(reportDto.getWeakHighCount())
                            .weakMidCnt(reportDto.getWeakMidCount())
                            .weakLowCnt(reportDto.getWeakLowCount())
                            .build();
                    detailDtoList.add(statisticsDetailDto);

                } // end if
            } // end for

            statDto.setDetailList(detailDtoList);
        } // end for
        /*--- ---*/

        /*--- ---*/
        for (SnetStatisticsDto statDto : snetStatisticsCommonDtoList1) {

            String swTypeNmKey = statDto.getSwType();

            for (SnetStatisticsDetailDto detailDto : statDto.getDetailList()) {

                for (SnetAssetSwAuditReportDto reportDto : reportDtoListD) {

                    if (StringUtils.equalsIgnoreCase(swTypeNmKey, reportDto.getSwTypeNmKey())) {

                        double auditRateAssetMaster = 0.0;
                        auditRateAssetMaster = reportDto.getAuditRateAssetMaster();
                        // double 값 NaN 일 경우 처리
                        if (Double.isNaN(auditRateAssetMaster)) {

                            auditRateAssetMaster = 0.0;
                        } // end if

                        detailDto.setSwNmD(reportDto.getSwNm());
                        detailDto.setHostNmD(reportDto.getHostNM());
                        detailDto.setAuditRateD(auditRateAssetMaster);

                        if (detailDto.getWeakHighCntD() == 0 && reportDto.getWeakHighCountD() > 0) {
                            detailDto.setWeakHighCntD(reportDto.getWeakHighCountD());
                        }
                        if (detailDto.getWeakMidCntD() == 0 && reportDto.getWeakMidCountD() > 0) {

                            detailDto.setWeakMidCntD(reportDto.getWeakMidCountD());
                        }
                        if (detailDto.getWeakLowCntD() == 0 && reportDto.getWeakLowCountD() > 0) {

                            detailDto.setWeakLowCntD(reportDto.getWeakLowCountD());
                        }
                        if (detailDto.getWeakCntD() == 0 && reportDto.getWeakCountD() > 0) {
                            detailDto.setWeakCntD(reportDto.getWeakCountD());
                        }
                    } // end if
                } // end for
            } // end for

            statDto.setDetailList(statDto.getDetailList());
        } // end for
        /*--- ---*/
        return snetStatisticsCommonDtoList1;

    } // end method getAssetSwAuditRateList7

    /**
     * REPORT
     */
    @Override
    public List<SnetStatisticsDto> getAssetSwAuditRateList7Report(List<SnetStatisticsDto> snetStatisticsCommonDtoList1, SnetAssetMasterResultDto masterParamDto) {

        List<String> assetCdInList = Lists.newArrayList();

        for (SnetAssetMasterDto dto : masterParamDto.getMasterDtos()) {
            assetCdInList.add(dto.getAssetCd());
        } // end for

        String[] assetCdInArr = assetCdInList.stream().distinct().toArray(String[]::new);

        Map paramMap = new HashMap();
//        paramMap.put("assetCdInArr", assetCdInArr);

        paramMap.put("ALL_FLAG", "Y");
        List<SnetAssetSwAuditExcelDto> list7 = assetAuditResultMapper.getAssetSwAuditResultReportList(paramMap);

        String createDate = masterParamDto.getCreateDate();
        paramMap.put("createDate", createDate);
        List<SnetAssetSwAuditExcelDto> list7D = assetAuditResultMapper.getAssetSwAuditResultReportList(paramMap);
        paramMap.remove("ALL_FLAG");
        paramMap.remove("createDate");

        // 자산별 보안 준수율 하위 10
        /*--- ---*/
        List<SnetAssetSwAuditReportDto> reportDtoList = Lists.newArrayList();
        String tempAssetCd = null;
        String tempAuditDay = null;

        for (SnetAssetSwAuditExcelDto excelDto : list7) {

            tempAssetCd = excelDto.getAssetCd();
            Map tempParamMap = new HashMap();

            if (StringUtils.equalsIgnoreCase("OS", excelDto.getSwType())) {

                if (StringUtils.equalsIgnoreCase("Windows", excelDto.getSwNm())) {

                    tempParamMap.put("SW_TYPE", "OS");
                    tempParamMap.put("SW_NM", "Windows");
                } else {

                    tempParamMap.put("SW_TYPE", "OS");
                    tempParamMap.put("SW_NM", excelDto.getSwNm());
                }
            } else {

                tempParamMap.put("SW_TYPE", excelDto.getSwType());
                tempParamMap.remove("SW_NM");
            }

            tempParamMap.put("ASSET_CD", tempAssetCd);

            SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetMstAuditReportGetMaxAuditDay(tempParamMap);

            if (reportDto1 != null) {
                tempAuditDay = reportDto1.getAuditDay();
                tempParamMap.put("AUDIT_DAY", tempAuditDay);
                SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetMstAuditReportGroupingBy(tempParamMap);

                if (reportDto2 != null) {
                    reportDto2.setAuditRateAssetMaster(reportDto2.getAuditRate());

                    reportDtoList.add(reportDto2);
                } // end if
            } // end if
        } // end for

        /*--- ---*/
        /*--- ---*/
        List<SnetAssetSwAuditReportDto> reportDtoListD = Lists.newArrayList();
        String tempAssetCdD = null;
        String tempAuditDayD = null;
        for (SnetAssetSwAuditExcelDto excelDto : list7D) {

            tempAssetCdD = excelDto.getAssetCd();
            Map tempParamMap = new HashMap();

            if (StringUtils.equalsIgnoreCase("OS", excelDto.getSwType())) {

                if (StringUtils.equalsIgnoreCase("Windows", excelDto.getSwNm())) {

                    tempParamMap.put("SW_TYPE", "OS");
                    tempParamMap.put("SW_NM", "Windows");
                } else {

                    tempParamMap.put("SW_TYPE", "OS");
                    tempParamMap.put("SW_NM", excelDto.getSwNm());
                }
            } else {

                tempParamMap.put("SW_TYPE", excelDto.getSwType());
                tempParamMap.remove("SW_NM");
            }

            tempParamMap.put("ASSET_CD", tempAssetCdD);

            SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetMstAuditReportGetMaxAuditDay(tempParamMap);

            if (reportDto1 != null) {
                tempAuditDayD = reportDto1.getAuditDay();
                tempParamMap.put("AUDIT_DAY", tempAuditDayD);
                SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetMstAuditReportGroupingBy(tempParamMap);

                if (reportDto2 != null) {
                    reportDto2.setAuditRateAssetMaster(reportDto2.getAuditRate());

                    reportDtoListD.add(reportDto2);
                } // end if
            } // end if
        } // end for
        /*--- ---*/

        // 중요도별 취약점 현황
        /*--- ---*/
        int weaktotal = 0;
        int weakLevelHigh = 0;
        int weakLevelMiddle = 0;
        int weakLevelLow = 0;

        for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

            Map reportWeakMap = new HashMap();
            if (StringUtils.equalsIgnoreCase("OS", reportDto.getSwType())) {

                reportWeakMap.put("SW_TYPE", reportDto.getSwType());

                if (StringUtils.equalsIgnoreCase("Windows", reportDto.getSwNm())) {

                    reportWeakMap.put("SW_TYPE", "OS");
                    reportWeakMap.put("SW_NM", "Windows");
                } else {

                    reportWeakMap.put("SW_TYPE", "OS");
                    reportWeakMap.put("SW_NM", reportDto.getSwNm());
                }
            } else {

                reportWeakMap.put("SW_TYPE", reportDto.getSwType());
                reportWeakMap.remove("SW_NM");
            }
            reportWeakMap.put("F_FLAG", "Y");
            reportWeakMap.put("ASSET_CD", reportDto.getAssetCd());
            reportWeakMap.put("AUDIT_DAY", reportDto.getAuditDay());

            List<SnetAssetSwAuditReportDto> tempReportDto = testMapper.getWeakLevelsAssetMstAuditReportGetMaxAuditDay(reportWeakMap);

            for (SnetAssetSwAuditReportDto resultDto : tempReportDto) {

                if (StringUtils.equalsIgnoreCase("OS", reportDto.getSwType())) {

                    if (StringUtils.equalsIgnoreCase("Windows", reportDto.getSwNm())) {

                        reportDto.setSwTypeNmKey(reportDto.getSwNm());
                    } else {

                        reportDto.setSwTypeNmKey(reportDto.getSwNm());
                    }
                } else {

                    reportDto.setSwTypeNmKey(reportDto.getSwType());
                } // end if

                if (StringUtils.equals("H", resultDto.getItemGrade())) {

                    weakLevelHigh += resultDto.getCount();
                }
                if (StringUtils.equals("M", resultDto.getItemGrade())) {

                    weakLevelMiddle += resultDto.getCount();
                }
                if (StringUtils.equals("L", resultDto.getItemGrade())) {

                    weakLevelLow += resultDto.getCount();
                } // end if
            } // end for
            weaktotal = weakLevelHigh + weakLevelMiddle + weakLevelLow;
            reportDto.setWeakHighCount(weakLevelHigh);
            reportDto.setWeakMidCount(weakLevelMiddle);
            reportDto.setWeakLowCount(weakLevelLow);
            reportDto.setWeakCount(weaktotal);

            String swTypeNmKey = getSwTypeNmKey(reportDto.getSwType(), reportDto.getSwNm());
            reportDto.setSwTypeNmKey(swTypeNmKey);

            weakLevelHigh = 0;
            weakLevelMiddle = 0;
            weakLevelLow = 0;
            weaktotal = 0;
        } // end for
        /*--- ---*/

        /*--- ---*/
        int weaktotalD = 0;
        int weakLevelHighD = 0;
        int weakLevelMiddleD = 0;
        int weakLevelLowD = 0;

        for (SnetAssetSwAuditReportDto reportDto : reportDtoListD) {

            Map reportWeakMap = new HashMap();
            if (StringUtils.equalsIgnoreCase("OS", reportDto.getSwType())) {

                reportWeakMap.put("SW_TYPE", reportDto.getSwType());

                if (StringUtils.equalsIgnoreCase("Windows", reportDto.getSwNm())) {

                    reportWeakMap.put("SW_TYPE", "OS");
                    reportWeakMap.put("SW_NM", "Windows");
                } else {

                    reportWeakMap.put("SW_TYPE", "OS");
                    reportWeakMap.put("SW_NM", reportDto.getSwNm());
                }
            } else {

                reportWeakMap.put("SW_TYPE", reportDto.getSwType());
                reportWeakMap.remove("SW_NM");
            }
            reportWeakMap.put("F_FLAG", "Y");
            reportWeakMap.put("ASSET_CD", reportDto.getAssetCd());
            reportWeakMap.put("AUDIT_DAY", reportDto.getAuditDay());

            List<SnetAssetSwAuditReportDto> tempReportDto = testMapper.getWeakLevelsAssetMstAuditReportGetMaxAuditDay(reportWeakMap);

            for (SnetAssetSwAuditReportDto resultDto : tempReportDto) {

                if (StringUtils.equalsIgnoreCase("OS", reportDto.getSwType())) {

                    if (StringUtils.equalsIgnoreCase("Windows", reportDto.getSwNm())) {

                        reportDto.setSwTypeNmKey(reportDto.getSwNm());
                    } else {

                        reportDto.setSwTypeNmKey(reportDto.getSwNm());
                    }
                } else {

                    reportDto.setSwTypeNmKey(reportDto.getSwType());
                } // end if

                if (StringUtils.equals("H", resultDto.getItemGrade())) {

                    weakLevelHighD += resultDto.getCount();
                }
                if (StringUtils.equals("M", resultDto.getItemGrade())) {

                    weakLevelMiddleD += resultDto.getCount();
                }
                if (StringUtils.equals("L", resultDto.getItemGrade())) {

                    weakLevelLowD += resultDto.getCount();
                } // end if
            } // end for

            weaktotalD = weakLevelHighD + weakLevelMiddleD + weakLevelLowD;
            reportDto.setWeakHighCountD(weakLevelHighD);
            reportDto.setWeakMidCountD(weakLevelMiddleD);
            reportDto.setWeakLowCountD(weakLevelLowD);
            reportDto.setWeakCountD(weaktotalD);
        } // end for
        /*--- ---*/

        int j = 1;
        /*--- ---*/
        for (SnetStatisticsDto statDto : snetStatisticsCommonDtoList1) {

            String swTypeNmKey = statDto.getSwType();

            List<SnetStatisticsDetailDto> detailDtoList = Lists.newArrayList();

            for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

                if (StringUtils.equalsIgnoreCase(swTypeNmKey, reportDto.getSwTypeNmKey())) {

                    double auditRateAssetMaster = 0.0;
                    auditRateAssetMaster = reportDto.getAuditRateAssetMaster();
                    // double 값 NaN 일 경우 처리
                    if (Double.isNaN(auditRateAssetMaster)) {

                        auditRateAssetMaster = 0.0;
                    } // end if

                    SnetStatisticsDetailDto statisticsDetailDto = SnetStatisticsDetailDto.builder()
                            .seq(statDto.getSeq())
                            .statisticsId(statDto.getStatisticsId())
                            .createDay(statDto.getCreateDay())
                            .branchId(statDto.getBranchId())
                            .teamId(statDto.getTeamId())
                            .auditStdCd(statDto.getAuditStdCd())
                            .swTypeNmKey(reportDto.getSwTypeNmKey())
                            .swType(reportDto.getSwType())
                            .assetCd(reportDto.getAssetCd())
                            .auditDay(reportDto.getAuditDay())
                            .division(statDto.getDivision())
                            .swNm(reportDto.getSwNm())
                            .hostNm(reportDto.getHostNM())
                            .auditRate(auditRateAssetMaster)
                            .weakCnt(reportDto.getWeakCount())
                            .weakHighCnt(reportDto.getWeakHighCount())
                            .weakMidCnt(reportDto.getWeakMidCount())
                            .weakLowCnt(reportDto.getWeakLowCount())
                            .build();
                    detailDtoList.add(statisticsDetailDto);

                } // end if
            } // end for

            // 미진단 세팅
            // -> 앞에서 권한별로 세팅함

            // 미조치 세팅
            // -> 앞에서 권한별로 세팅함

            statDto.setDetailList(detailDtoList);
        } // end for
        /*--- ---*/

        /*--- ---*/
        for (SnetStatisticsDto statDto : snetStatisticsCommonDtoList1) {

            String swTypeNmKey = statDto.getSwType();

            for (SnetStatisticsDetailDto detailDto : statDto.getDetailList()) {

                for (SnetAssetSwAuditReportDto reportDto : reportDtoListD) {

                    if (StringUtils.equalsIgnoreCase(swTypeNmKey, reportDto.getSwTypeNmKey())) {

                        double auditRateAssetMaster = 0.0;
                        auditRateAssetMaster = reportDto.getAuditRateAssetMaster();
                        // double 값 NaN 일 경우 처리
                        if (Double.isNaN(auditRateAssetMaster)) {

                            auditRateAssetMaster = 0.0;
                        } // end if

                        detailDto.setSwNmD(reportDto.getSwNm());
                        detailDto.setHostNmD(reportDto.getHostNM());
                        detailDto.setAuditRateD(auditRateAssetMaster);
                        detailDto.setWeakCntD(reportDto.getWeakCountD());
                        detailDto.setWeakHighCntD(reportDto.getWeakHighCountD());
                        detailDto.setWeakMidCntD(reportDto.getWeakMidCountD());
                        detailDto.setWeakLowCntD(reportDto.getWeakLowCountD());
                    } // end if
                } // end for

            } // end for

        } // end for
        /*--- ---*/
        return snetStatisticsCommonDtoList1;

    } // end method getAssetSwAuditRateList7Report

    /**
     * 분류 항목별 보안지수
     * 중요도별 취약점 현황
     * -> detail 의 division 은 모두 INTERWORK
     * -> 권한별로 입력해야 하므로 INTERWORK + 권한 (userType : SU,SE,SV,OS,OP)
     */
    @Override
    public List<SnetStatisticsDto> getAssetSwAuditRateList8(List<SnetStatisticsDto> snetStatisticsCommonDtoList1, SnetAssetMasterResultDto resultDto2) {

        /*--- ---*/
        for (SnetStatisticsDto dtos : snetStatisticsCommonDtoList1) {

            Map paramMap = new HashMap();
            String userType = resultDto2.getUserType();
            String branchId = resultDto2.getBranchId();
            String teamId = resultDto2.getTeamId();

            if (StringUtils.equals("SU", userType)) {

            } else if (StringUtils.equals("SE", userType)) {

                paramMap.put("branchId", branchId);
            } else if (StringUtils.equals("SV", userType)) {

                paramMap.put("branchId", branchId);
                paramMap.put("teamId", teamId);
            } else if (StringUtils.equals("OP", userType)) {

                paramMap.put("branchId", branchId);
                paramMap.put("teamId", teamId);
            } else if (StringUtils.equals("OS", userType)) {

                paramMap.put("branchId", branchId);
                paramMap.put("teamId", teamId);
            }

            if (StringUtils.equals("Linux", dtos.getSwType())) {

                paramMap.remove("SW_TYPE");
                paramMap.remove("SW_NM");
                paramMap.put("L_NOT_FLAG", "Y");
            } else if (StringUtils.equals("Windows", dtos.getSwType())) {

                paramMap.put("SW_TYPE", "OS");
                paramMap.put("SW_NM", "Windows");
            } else if (StringUtils.equals("DB", dtos.getSwType())) {

                paramMap.put("SW_TYPE", "DB");
            } else if (StringUtils.equals("WEB", dtos.getSwType())) {

                paramMap.put("SW_TYPE", "WEB");
            } else if (StringUtils.equals("WAS", dtos.getSwType())) {

                paramMap.put("SW_TYPE", "WAS");
            } else if (StringUtils.equals("NW", dtos.getSwType())) {

                paramMap.put("SW_TYPE", "NW");
            } else if (StringUtils.equals("Web App", dtos.getSwType())) {

                paramMap.put("SW_TYPE", "URL");
            } else {

                paramMap.remove("SW_TYPE");
                paramMap.remove("SW_NM");
                paramMap.put("NOT_FLAG", "Y");
            }

            // 점검군별 itemGrpNm 전체 grouping 조회
            List<SnetAssetSwAuditReportDto> groupbyReportGrpNmCntList = testMapper.selectAssetMstAuditReportGetMaxAuditDayItemGrpNmList(paramMap);

            double schkSecValRate = 0.0;
            int schkSecValLen = 0;
            double schkSecValRateAvg = 0.0;

            double pchkSecValRate = 0.0;
            int pchkSecValLen = 0;
            double pchkSecValRateAvg = 0.0;

            // 점검군 별로 장비 대수 별로 loop
            for (SnetStatisticsDetailDto detailDto : dtos.getDetailList()) {

                List<SnetStatisticsItemDetailDto> itemDetailDtoList = Lists.newArrayList();

                for (SnetAssetSwAuditReportDto reportDto : groupbyReportGrpNmCntList) {

                    SnetStatisticsItemDetailDto itemDetailDto = new SnetStatisticsItemDetailDto();
                    itemDetailDto.setStatisticsId(detailDto.getStatisticsId());
                    itemDetailDto.setBranchId(detailDto.getBranchId());
                    itemDetailDto.setTeamId(detailDto.getTeamId());
                    itemDetailDto.setCreateDay(detailDto.getCreateDay());
                    itemDetailDto.setAuditStdCd(detailDto.getAuditStdCd());
                    itemDetailDto.setSwTypeNmKey(detailDto.getSwTypeNmKey());
                    itemDetailDto.setAssetCd(reportDto.getAssetCd());
                    itemDetailDto.setAuditDay(reportDto.getAuditDay());
                    itemDetailDto.setSwType(reportDto.getSwType());
                    itemDetailDto.setSwNm(reportDto.getSwNm());

                    itemDetailDto.setItemGrpNm(reportDto.getItemGrpNm());
                    itemDetailDto.setItemGrpCnt(reportDto.getTotalCnt());
                    itemDetailDto.setItemGrpAllCnt(reportDto.getTotalCnt());

                    itemDetailDtoList.add(itemDetailDto);
                } // end for

                for (SnetStatisticsItemDetailDto itemDetailDto : itemDetailDtoList) {

                    int itemGrpWeakCnt = 0;
                    int itemGrpNaCnt = 0;
                    int itemGrpGoodCnt = 0;
                    int itemRiskGoodCnt = 0;
                    int weakHighCnt = 0;
                    int weakMidCnt = 0;
                    int weakLowCnt = 0;
                    int weakHighTotalCnt = 0;
                    int weakMidTotalCnt = 0;
                    int weakLowTotalCnt = 0;
                    int highTotalCnt = 0;
                    int midTotalCnt = 0;
                    int lowTotalCnt = 0;

                    double itemGrpSecVal1 = 0.0;
                    double itemGrpSecVal2 = 0.0;
                    double itemGrpSecVal3 = 0.0;
                    double itemGrpSecValsum = 0.0;

                    double weakHighSecVal1 = 0.0;
                    double weakHighSecVal2 = 0.0;
                    double weakHighSecVal3 = 0.0;
                    double weakHighSecValsum = 0.0;

                    double weakMidSecVal1 = 0.0;
                    double weakMidSecVal2 = 0.0;
                    double weakMidSecVal3 = 0.0;
                    double weakMidSecValsum = 0.0;

                    double weakLowSecVal1 = 0.0;
                    double weakLowSecVal2 = 0.0;
                    double weakLowSecVal3 = 0.0;
                    double weakLowSecValsum = 0.0;

                    paramMap.put("F_FLAG", "Y");
                    paramMap.put("endDay", itemDetailDto.getCreateDay());

                    List<SnetAssetSwAuditReportDto> getItemGrpWeakCnt1 = testMapper.selectAssetMstAuditReportGetMaxAuditDayItemGrpNmCntList(paramMap);
                    paramMap.remove("F_FLAG");
                    for (SnetAssetSwAuditReportDto reportDto : getItemGrpWeakCnt1) {

                        if (StringUtils.equals(itemDetailDto.getItemGrpNm(), reportDto.getItemGrpNm())) {

                            itemGrpWeakCnt += reportDto.getItemGradeTotal();
                        }
                    }
                    itemDetailDto.setItemGrpWeakCnt(itemGrpWeakCnt);


                    paramMap.put("NA_FLAG", "Y");
                    List<SnetAssetSwAuditReportDto> getItemGrpWeakCnt2 = testMapper.selectAssetMstAuditReportGetMaxAuditDayItemGrpNmCntList(paramMap);
                    paramMap.remove("NA_FLAG");
                    for (SnetAssetSwAuditReportDto reportDto : getItemGrpWeakCnt2) {

                        if (StringUtils.equals(itemDetailDto.getItemGrpNm(), reportDto.getItemGrpNm())) {

                            itemGrpNaCnt += reportDto.getItemGradeTotal();
                        }
                    }
                    itemDetailDto.setItemGrpNaCnt(itemGrpNaCnt);

                    paramMap.put("T_FLAG", "Y");
                    List<SnetAssetSwAuditReportDto> getItemGrpWeakCnt5 = testMapper.selectAssetMstAuditReportGetMaxAuditDayItemGrpNmCntList(paramMap);
                    paramMap.remove("T_FLAG");
                    for (SnetAssetSwAuditReportDto reportDto : getItemGrpWeakCnt5) {

                        if (StringUtils.equals(itemDetailDto.getItemGrpNm(), reportDto.getItemGrpNm())) {

                            itemGrpGoodCnt += reportDto.getItemGradeTotal();
                        }
                    }
                    itemDetailDto.setItemGrpGoodCnt(itemGrpGoodCnt);

                    paramMap.put("C_FLAG", "Y");
                    List<SnetAssetSwAuditReportDto> getItemGrpWeakCnt3 = testMapper.selectAssetMstAuditReportGetMaxAuditDayItemGrpNmCntList(paramMap);
                    paramMap.remove("C_FLAG");
                    for (SnetAssetSwAuditReportDto reportDto : getItemGrpWeakCnt3) {

                        if (StringUtils.equals(itemDetailDto.getItemGrpNm(), reportDto.getItemGrpNm())) {

                            itemRiskGoodCnt += reportDto.getItemGradeTotal();
                        }
                    }
                    itemDetailDto.setItemGrpRiskCnt(itemRiskGoodCnt); // 계산

                    for (SnetAssetSwAuditReportDto reportDto : getItemGrpWeakCnt1) {

                        if (StringUtils.equals(itemDetailDto.getItemGrpNm(), reportDto.getItemGrpNm())) {

                            if (StringUtils.equals("H", reportDto.getItemGrade())) {

                                weakHighCnt += reportDto.getTotalCnt(); // 상 취약 개수
                                weakHighTotalCnt += reportDto.getItemGradeTotal(); // 상 취약 보안지수 점수
                            } else if (StringUtils.equals("M", reportDto.getItemGrade())) {

                                weakMidCnt += reportDto.getTotalCnt(); // 중 취약 개수
                                weakMidTotalCnt += reportDto.getItemGradeTotal(); // 중 취약 보안지수 점수
                            } else if (StringUtils.equals("L", reportDto.getItemGrade())) {

                                weakLowCnt += reportDto.getTotalCnt(); // 하 취약 개수
                                weakLowTotalCnt += reportDto.getItemGradeTotal(); // 하 취약 보안지수 점수
                            }
                        }
                    } // end for

                    itemDetailDto.setWeakHighCnt(weakHighCnt);
                    itemDetailDto.setWeakMidCnt(weakMidCnt);
                    itemDetailDto.setWeakLowCnt(weakLowCnt);

                    paramMap.put("NA_NOT_FLAG", "Y");
                    List<SnetAssetSwAuditReportDto> getItemGrpWeakCnt4 = testMapper.selectAssetMstAuditReportGetMaxAuditDayItemGrpNmCntList(paramMap);
                    paramMap.remove("NA_NOT_FLAG");
                    for (SnetAssetSwAuditReportDto reportDto : getItemGrpWeakCnt4) {

                        if (StringUtils.equals(itemDetailDto.getItemGrpNm(), reportDto.getItemGrpNm())) {

                            if (StringUtils.equals("H", reportDto.getItemGrade())) {

                                highTotalCnt += reportDto.getItemGradeTotal(); // 상 NA 제외 전체 개수
                            } else if (StringUtils.equals("M", reportDto.getItemGrade())) {

                                midTotalCnt += reportDto.getItemGradeTotal(); // 중 NA 제외 전체 개수
                            } else if (StringUtils.equals("L", reportDto.getItemGrade())) {

                                lowTotalCnt += reportDto.getItemGradeTotal(); // 하 NA 제외 전체 개수
                            }
                        }
                    } // end for

                    weakHighSecVal1 = (highTotalCnt) - (weakHighTotalCnt);
                    weakHighSecVal2 = (highTotalCnt);
                    weakHighSecVal3 = weakHighSecVal1 / weakHighSecVal2;
                    weakHighSecVal3 = weakHighSecVal3 * 100;
                    weakHighSecValsum = Math.round( weakHighSecVal3 * 100d ) / 100d; // 소수 둘째 자리
                    itemDetailDto.setWeakHighRate(weakHighSecValsum);

                    weakMidSecVal1 = (midTotalCnt) - (weakMidTotalCnt);
                    weakMidSecVal2 = (midTotalCnt);
                    weakMidSecVal3 = weakMidSecVal1 / weakMidSecVal2;
                    weakMidSecVal3 = weakMidSecVal3 * 100;
                    weakMidSecValsum = Math.round( weakMidSecVal3 * 100d ) / 100d; // 소수 둘째 자리
                    itemDetailDto.setWeakMidRate(weakMidSecValsum);

                    weakLowSecVal1 = (lowTotalCnt) - (weakLowTotalCnt);
                    weakLowSecVal2 = (lowTotalCnt);
                    weakLowSecVal3 = weakLowSecVal1 / weakLowSecVal2;
                    weakLowSecVal3 = weakLowSecVal3 * 100;
                    weakLowSecValsum = Math.round( weakLowSecVal3 * 100d ) / 100d; // 소수 둘째 자리
                    itemDetailDto.setWeakLowRate(weakLowSecValsum);

                    itemGrpSecVal1 = (highTotalCnt+midTotalCnt+lowTotalCnt) - (weakHighTotalCnt+weakMidTotalCnt+weakLowTotalCnt);
                    itemGrpSecVal2 = (highTotalCnt+midTotalCnt+lowTotalCnt);
                    itemGrpSecVal3 = itemGrpSecVal1 / itemGrpSecVal2;
                    itemGrpSecVal3 = itemGrpSecVal3 * 100;
                    itemGrpSecValsum = Math.round( itemGrpSecVal3 * 100d ) / 100d; // 소수 둘째 자리

                    schkSecValRate += itemGrpSecValsum;
                    schkSecValLen++;

                    itemDetailDto.setItemGrpSecVal(itemGrpSecValsum); // 계산
                } // end for

                detailDto.setItemDetailList(itemDetailDtoList);
            } // end for
            // 장비 1대에 점검군 별 정보 모두 세팅

            /*
                보고서 보안점검 보안지수	SCHK_SEC_VAL
                보고서 보안점검 보안수준	SCHK_SEC_LEV
                보고서 이행점검 보안지수	PCHK_SEC_VAL
                보고서 이행점검 보안수준	PCHK_SEC_LEV
             */
            schkSecValRateAvg = schkSecValRate / schkSecValLen;
            schkSecValRateAvg = Math.round( schkSecValRateAvg * 100d ) / 100d; // 소수 둘째 자리

            dtos.setSchkSecVal(schkSecValRateAvg);
            dtos.setPchkSecVal(schkSecValRateAvg);
            dtos.setSchkSecLev(getSchkSecLevByValRate(schkSecValRateAvg));
            dtos.setPchkSecLev(getSchkSecLevByValRate(schkSecValRateAvg));
        } // end for
        /*--- ---*/

        /*--- ---*/
        for (SnetStatisticsDto dtos : snetStatisticsCommonDtoList1) {

            Map paramMap = new HashMap();
            if (StringUtils.equals("Linux", dtos.getSwType())) {

                paramMap.remove("SW_TYPE");
                paramMap.remove("SW_NM");
                paramMap.put("L_NOT_FLAG", "Y");
            } else if (StringUtils.equals("Windows", dtos.getSwType())) {

                paramMap.put("SW_TYPE", "OS");
                paramMap.put("SW_NM", "Windows");
            } else if (StringUtils.equals("DB", dtos.getSwType())) {

                paramMap.put("SW_TYPE", "DB");
            } else if (StringUtils.equals("WEB", dtos.getSwType())) {

                paramMap.put("SW_TYPE", "WEB");
            } else if (StringUtils.equals("WAS", dtos.getSwType())) {

                paramMap.put("SW_TYPE", "WAS");
            } else if (StringUtils.equals("NW", dtos.getSwType())) {

                paramMap.put("SW_TYPE", "NW");
            } else if (StringUtils.equals("Web App", dtos.getSwType())) {

                paramMap.put("SW_TYPE", "URL");
            } else {

                paramMap.remove("SW_TYPE");
                paramMap.remove("SW_NM");
                paramMap.put("NOT_FLAG", "Y");
            }

            String createDate = resultDto2.getCreateDate();
            paramMap.put("createDate", createDate);

            // count 조회
            List<SnetAssetSwAuditReportDto> groupbyReportGrpNmCntListD = testMapper.selectAssetMstAuditReportGetMaxAuditDayItemGrpNmList(paramMap);

            double schkSecValRateD = 0.0;
            int schkSecValLenD = 0;
            double schkSecValRateAvgD = 0.0;

            double pchkSecValRateD = 0.0;
            int pchkSecValLenD = 0;
            double pchkSecValRateAvgD = 0.0;

            // 점검군 별로 장비 대수 별로 loop
            for (SnetStatisticsDetailDto detailDtoD : dtos.getDetailList()) {

                String swTypeNmKey = detailDtoD.getSwNm();

                for (SnetAssetSwAuditReportDto reportDtoD : groupbyReportGrpNmCntListD) {

                    String targetSwTypeNmKey = getSwTypeNmKey(reportDtoD.getSwType(), reportDtoD.getSwNm());

                    for (SnetStatisticsItemDetailDto itemDetailDtoD : detailDtoD.getItemDetailList()) {

                        if (StringUtils.equalsIgnoreCase(swTypeNmKey, targetSwTypeNmKey) ) {

                            if (StringUtils.equals(itemDetailDtoD.getItemGrpNm(), reportDtoD.getItemGrpNm())) {

                                itemDetailDtoD.setItemGrpNmD(reportDtoD.getItemGrpNm());
                                itemDetailDtoD.setItemGrpCntD(reportDtoD.getTotalCnt());
                                itemDetailDtoD.setItemGrpAllCntD(reportDtoD.getTotalCnt());
                            }
                        } // end if
                    } // end for
                } // end for

                for (SnetStatisticsItemDetailDto itemDetailDtoD : detailDtoD.getItemDetailList()) {

                    int itemGrpWeakCnt = 0;
                    int itemGrpNaCnt = 0;
                    int itemGrpGoodCnt = 0;
                    int itemRiskGoodCnt = 0;
                    int weakHighCnt = 0;
                    int weakMidCnt = 0;
                    int weakLowCnt = 0;
                    int weakHighTotalCnt = 0;
                    int weakMidTotalCnt = 0;
                    int weakLowTotalCnt = 0;
                    int highTotalCnt = 0;
                    int midTotalCnt = 0;
                    int lowTotalCnt = 0;

                    double itemGrpSecVal1 = 0.0;
                    double itemGrpSecVal2 = 0.0;
                    double itemGrpSecVal3 = 0.0;
                    double itemGrpSecValsum = 0.0;

                    double weakHighSecVal1 = 0.0;
                    double weakHighSecVal2 = 0.0;
                    double weakHighSecVal3 = 0.0;
                    double weakHighSecValsum = 0.0;

                    double weakMidSecVal1 = 0.0;
                    double weakMidSecVal2 = 0.0;
                    double weakMidSecVal3 = 0.0;
                    double weakMidSecValsum = 0.0;

                    double weakLowSecVal1 = 0.0;
                    double weakLowSecVal2 = 0.0;
                    double weakLowSecVal3 = 0.0;
                    double weakLowSecValsum = 0.0;

                    paramMap.put("F_FLAG", "Y");
                    List<SnetAssetSwAuditReportDto> getItemGrpWeakCnt1D = testMapper.selectAssetMstAuditReportGetMaxAuditDayItemGrpNmCntList(paramMap);
                    paramMap.remove("F_FLAG");
                    for (SnetAssetSwAuditReportDto reportDtoD : getItemGrpWeakCnt1D) {

                        if (StringUtils.equals(itemDetailDtoD.getItemGrpNmD(), reportDtoD.getItemGrpNm())) {

                            itemGrpWeakCnt += reportDtoD.getItemGradeTotal();
                        }
                    }
                    itemDetailDtoD.setItemGrpWeakCntD(itemGrpWeakCnt);

                    paramMap.put("NA_FLAG", "Y");
                    List<SnetAssetSwAuditReportDto> getItemGrpWeakCnt2D = testMapper.selectAssetMstAuditReportGetMaxAuditDayItemGrpNmCntList(paramMap);
                    paramMap.remove("NA_FLAG");
                    for (SnetAssetSwAuditReportDto reportDtoD : getItemGrpWeakCnt2D) {

                        if (StringUtils.equals(itemDetailDtoD.getItemGrpNmD(), reportDtoD.getItemGrpNm())) {

                            itemGrpNaCnt += reportDtoD.getItemGradeTotal();
                        }
                    }
                    itemDetailDtoD.setItemGrpNaCntD(itemGrpNaCnt);

                    paramMap.put("T_FLAG", "Y");
                    List<SnetAssetSwAuditReportDto> getItemGrpWeakCnt5D = testMapper.selectAssetMstAuditReportGetMaxAuditDayItemGrpNmCntList(paramMap);
                    paramMap.remove("T_FLAG");
                    for (SnetAssetSwAuditReportDto reportDtoD : getItemGrpWeakCnt5D) {

                        if (StringUtils.equals(itemDetailDtoD.getItemGrpNmD(), reportDtoD.getItemGrpNm())) {

                            itemGrpGoodCnt += reportDtoD.getItemGradeTotal();
                        }
                    }
                    itemDetailDtoD.setItemGrpGoodCntD(itemGrpGoodCnt); // 계산

                    paramMap.put("C_FLAG", "Y");
                    List<SnetAssetSwAuditReportDto> getItemGrpWeakCnt3D = testMapper.selectAssetMstAuditReportGetMaxAuditDayItemGrpNmCntList(paramMap);
                    paramMap.remove("C_FLAG");
                    for (SnetAssetSwAuditReportDto reportDtoD : getItemGrpWeakCnt3D) {

                        if (StringUtils.equals(itemDetailDtoD.getItemGrpNmD(), reportDtoD.getItemGrpNm())) {

                            itemRiskGoodCnt += reportDtoD.getItemGradeTotal();
                        }
                    }
                    itemDetailDtoD.setItemGrpRiskCntD(itemRiskGoodCnt); // 계산

                    for (SnetAssetSwAuditReportDto reportDtoD : getItemGrpWeakCnt1D) {

                        if (StringUtils.equals(itemDetailDtoD.getItemGrpNmD(), reportDtoD.getItemGrpNm())) {

                            if (StringUtils.equals("H", reportDtoD.getItemGrade())) {

                                weakHighCnt += reportDtoD.getTotalCnt(); // 상 취약 개수
                                weakHighTotalCnt += reportDtoD.getItemGradeTotal(); // 상 취약 보안지수 점수
                            } else if (StringUtils.equals("M", reportDtoD.getItemGrade())) {

                                weakMidCnt += reportDtoD.getTotalCnt(); // 중 취약 개수
                                weakMidTotalCnt += reportDtoD.getItemGradeTotal(); // 중 취약 보안지수 점수
                            } else if (StringUtils.equals("L", reportDtoD.getItemGrade())) {

                                weakLowCnt += reportDtoD.getTotalCnt(); // 하 취약 개수
                                weakLowTotalCnt += reportDtoD.getItemGradeTotal(); // 하 취약 보안지수 점수
                            }
                        }
                    } // end for

                    itemDetailDtoD.setWeakHighCntD(weakHighCnt);
                    itemDetailDtoD.setWeakMidCntD(weakMidCnt);
                    itemDetailDtoD.setWeakLowCntD(weakLowCnt);

                    paramMap.put("NA_NOT_FLAG", "Y");
                    List<SnetAssetSwAuditReportDto> getItemGrpWeakCnt4D = testMapper.selectAssetMstAuditReportGetMaxAuditDayItemGrpNmCntList(paramMap);
                    paramMap.remove("NA_NOT_FLAG");

                    for (SnetAssetSwAuditReportDto reportDtoD : getItemGrpWeakCnt4D) {

                        if (StringUtils.equals(itemDetailDtoD.getItemGrpNmD(), reportDtoD.getItemGrpNm())) {

                            if (StringUtils.equals("H", reportDtoD.getItemGrade())) {

                                highTotalCnt += reportDtoD.getItemGradeTotal(); // 상 NA 제외 전체 개수
                            } else if (StringUtils.equals("M", reportDtoD.getItemGrade())) {

                                midTotalCnt += reportDtoD.getItemGradeTotal(); // 중 NA 제외 전체 개수
                            } else if (StringUtils.equals("L", reportDtoD.getItemGrade())) {

                                lowTotalCnt += reportDtoD.getItemGradeTotal(); // 하 NA 제외 전체 개수
                            }
                        }
                    } // end for

                    weakHighSecVal1 = (highTotalCnt) - (weakHighTotalCnt);
                    weakHighSecVal2 = (highTotalCnt);
                    weakHighSecVal3 = weakHighSecVal1 / weakHighSecVal2;
                    weakHighSecVal3 = weakHighSecVal3 * 100;
                    weakHighSecValsum = Math.round( weakHighSecVal3 * 100d ) / 100d; // 소수 둘째 자리
                    itemDetailDtoD.setWeakHighRateD(weakHighSecValsum);

                    weakMidSecVal1 = (midTotalCnt) - (weakMidTotalCnt);
                    weakMidSecVal2 = (midTotalCnt);
                    weakMidSecVal3 = weakMidSecVal1 / weakMidSecVal2;
                    weakMidSecVal3 = weakMidSecVal3 * 100;
                    weakMidSecValsum = Math.round( weakMidSecVal3 * 100d ) / 100d; // 소수 둘째 자리
                    itemDetailDtoD.setWeakMidRateD(weakMidSecValsum);

                    weakLowSecVal1 = (lowTotalCnt) - (weakLowTotalCnt);
                    weakLowSecVal2 = (lowTotalCnt);
                    weakLowSecVal3 = weakLowSecVal1 / weakLowSecVal2;
                    weakLowSecVal3 = weakLowSecVal3 * 100;
                    weakLowSecValsum = Math.round( weakLowSecVal3 * 100d ) / 100d; // 소수 둘째 자리
                    itemDetailDtoD.setWeakLowRateD(weakLowSecValsum);

                    itemGrpSecVal1 = (highTotalCnt+midTotalCnt+lowTotalCnt) - (weakHighTotalCnt+weakMidTotalCnt+weakLowTotalCnt);
                    itemGrpSecVal2 = (highTotalCnt+midTotalCnt+lowTotalCnt);
                    itemGrpSecVal3 = itemGrpSecVal1 / itemGrpSecVal2;
                    itemGrpSecVal3 = itemGrpSecVal3 * 100;
                    itemGrpSecValsum = Math.round( itemGrpSecVal3 * 100d ) / 100d; // 소수 둘째 자리

                    schkSecValRateD += itemGrpSecValsum;
                    schkSecValLenD++;

                    itemDetailDtoD.setItemGrpSecValD(itemGrpSecValsum); // 계산
                } // end for

            } // end for
            // 장비 1대에 점검군 별 정보 모두 세팅

            /*
                보고서 보안점검 보안준수율	SCHK_SEC_VAL_D
                보고서 보안점검 보안준수율	SCHK_SEC_LEV_D
                보고서 이행점검 보안준수율	PCHK_SEC_VAL_D
                보고서 이행점검 보안준수율	PCHK_SEC_LEV_D
             */

            schkSecValRateAvgD = schkSecValRateD / schkSecValLenD;
            schkSecValRateAvgD = Math.round( schkSecValRateAvgD * 100d ) / 100d; // 소수 둘째 자리

            dtos.setSchkSecValD(schkSecValRateAvgD);
            dtos.setPchkSecValD(schkSecValRateAvgD);
            dtos.setSchkSecLevD(getSchkSecLevByValRate(schkSecValRateAvgD));
            dtos.setPchkSecLevD(getSchkSecLevByValRate(schkSecValRateAvgD));

            dtos.setDetailList(dtos.getDetailList());

            if (StringUtils.isEmpty(resultDto2.getUserType())) {

                dtos.setDivision("INTERWORK");
            } else {

                dtos.setDivision("INTERWORK" + resultDto2.getUserType());
            }

        } // end for
        /*--- ---*/
        return snetStatisticsCommonDtoList1;
    } // end method

    /**
     * schkSecValRate to SchkSecLev
     * (보안지수) to (보안수준)
     */
    private String getSchkSecLevByValRate(double schkSecValRate) {

        String chkSecLev = null;

        if (schkSecValRate >= 90) {

            chkSecLev = "우수";
        } else if (schkSecValRate >= 80 && schkSecValRate < 90) {

            chkSecLev = "양호";
        } else if (schkSecValRate >= 70 && schkSecValRate < 80) {

            chkSecLev = "보통";
        } else if (schkSecValRate > 0 && schkSecValRate < 70) {

            chkSecLev = "미흡";
        } else if (schkSecValRate ==  0.0) {

            chkSecLev = "해당사항없음";
        }

        return chkSecLev;
    }

    /**
     * SnetStatisticsDto 삭제 (createDay 중복 입력 방지)
     */
    @Override
    public void deleteAuditStatistics11(SnetAssetMasterResultDto resultDto, boolean todayFlag) {

        String deleteCreateDay = resultDto.getCreateDate();
        String deleteCreateTime = resultDto.getCreateTime();

        SnetStatisticsDto dto = SnetStatisticsDto.builder()
                .createDay(deleteCreateDay)
                .createTime(deleteCreateTime)
                .build();

        if (todayFlag) {

            dto.setToday("TODAY");
            dto.setCreateTime(deleteCreateTime);
            auditStatisticsMapper.deleteAuditStatisticsDetailToday(dto);
            auditStatisticsMapper.deleteAuditStatisticsToday(dto);

        } else {

            auditStatisticsMapper.deleteAuditStatisticsDetail(dto);
            auditStatisticsMapper.deleteAuditStatistics(dto);
        }

    } // end method

    /**
     * SnetStatisticsDto today 삭제
     */
    @Override
    public void deleteAuditStatisticsMig(SnetAssetMasterResultDto resultDto) {

        String deleteCreateDay = resultDto.getCreateDate();

        SnetStatisticsDto dto = SnetStatisticsDto.builder()
                .createDay(deleteCreateDay)
                .build();

        auditStatisticsMapper.deleteAuditStatisticsDetailToday(dto);
        auditStatisticsMapper.deleteAuditStatisticsToday(dto);

    } // end method

    /**
     * 통계 데이터 입력
     */
    @Override
    public void insertAuditStatistics12(List<SnetStatisticsDto> list, SnetAssetMasterResultDto masterParamDto, boolean todayFlag) throws Exception {

        for (SnetStatisticsDto dto : list) {

            int seq = 2;
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Calendar time = Calendar.getInstance();
            String statisticsId = format.format(time.getTime()) + StringUtils.left(StringUtils.replace(UUID.randomUUID().toString(), "-",""),15);

            dto.setSeq(seq);
            dto.setStatisticsId(statisticsId);
            if (StringUtils.isEmpty(dto.getDivision())) {
                dto.setDivision("NONE");
            }

            dto.setDivision("DETAIL");

            if (todayFlag) {
                dto.setToday("TODAY");
                dto.setCreateTime(masterParamDto.getCreateTime());
                auditStatisticsMapper.insertAuditStatisticsToday(dto);
            } else {

                auditStatisticsMapper.insertAuditStatistics(dto);
            }

            for (SnetStatisticsDetailDto itemdetailDto : dto.getDetailList()) {

                itemdetailDto.setSeq(seq);
                itemdetailDto.setStatisticsId(statisticsId);
                itemdetailDto.setBranchId(dto.getBranchId());
                itemdetailDto.setTeamId(dto.getTeamId());
                itemdetailDto.setCreateDay(dto.getCreateDay());
                itemdetailDto.setAssetCd(itemdetailDto.getAssetCd());

                itemdetailDto.setSwNm(itemdetailDto.getSwTypeNmKey());
                itemdetailDto.setHostNm(itemdetailDto.getHostNm());
                itemdetailDto.setAuditRate(itemdetailDto.getAuditRate());

                itemdetailDto.setDivision(dto.getDivision());
                if (StringUtils.isNotEmpty(itemdetailDto.getAssetCd())) {

                    if (Double.isNaN(itemdetailDto.getAuditRateD())) {
                        itemdetailDto.setAuditRateD(0.0);
                    }

                    if (todayFlag) {
                        itemdetailDto.setToday("TODAY");
                        itemdetailDto.setCreateTime(masterParamDto.getCreateTime());
                        auditStatisticsMapper.insertAuditStatisticsDetailToday(itemdetailDto);
                    } else {

                        auditStatisticsMapper.insertAuditStatisticsDetail(itemdetailDto);
                    }
                } // end if
            } // end for
            seq++;
        } // end for
    } // end method

    /**
     * 중요도별 진단 결과 변경 처리(변경처리 승인) 현황
     */
    @Override
    public List<SnetStatisticsDto> getDisanosisresultByImportanceStatus9(List<SnetStatisticsDto> statisticsDetailList2, SnetAssetMasterResultDto masterParamDto) {

        List<String> assetCdInList = Lists.newArrayList();

        for (SnetAssetMasterDto dto : masterParamDto.getMasterDtos()) {
            assetCdInList.add(dto.getAssetCd());
        } // end for

        String[] assetCdInArr = assetCdInList.stream().distinct().toArray(String[]::new);

        Map paramMap = new HashMap();
//        paramMap.put("assetCdInArr", assetCdInArr);

        // report.item_result -> cok.action_item_result
        // audit_day : 진단일
        // ADMIN_OK_DATE : 변경처리승인날짜
        // item_cok_req_valid_date : 변경처리유효날짜
        List<SnetAssetSwAuditCokDto> assetSwAuditCokDtoList = assetCokMapper.getDisanosisresultByImportanceStatus(paramMap);

        int adResultAHighCnt = 0;
        int adResultBHighCnt = 0;
        int adResultCHighCnt = 0;
        int adResultDHighCnt = 0;
        int adResultAMidCnt = 0;
        int adResultBMidCnt = 0;
        int adResultCMidCnt = 0;
        int adResultDMidCnt = 0;
        int adResultALowCnt = 0;
        int adResultBLowCnt = 0;
        int adResultCLowCnt = 0;
        int adResultDLowCnt = 0;

        for (SnetAssetSwAuditCokDto cokDto : assetSwAuditCokDtoList) {

            String auditDay = cokDto.getAuditDay();
            String adminOkDay = cokDto.getAdminOkDate();

            int thisWeek = getWeekOfMonth(auditDay, adminOkDay);

            if (thisWeek == 1) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adResultAHighCnt++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adResultAMidCnt++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adResultALowCnt++;
                }
            } else if (thisWeek == 2) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adResultBHighCnt++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adResultBMidCnt++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adResultBLowCnt++;
                }
            } else if (thisWeek == 3) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adResultCHighCnt++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adResultCMidCnt++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adResultCLowCnt++;
                }
            } else if (thisWeek >= 4) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adResultDHighCnt++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adResultDMidCnt++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adResultDLowCnt++;
                }
            }
        } // end for

        for (SnetStatisticsDto statisticsDtos : statisticsDetailList2) {

            if (statisticsDtos.getDetailList() != null) {

                for (SnetStatisticsDetailDto itemDetailDto : statisticsDtos.getDetailList()) {

                    itemDetailDto.setAdResultHighAweekCnt(adResultAHighCnt);
                    itemDetailDto.setAdResultHighBweekCnt(adResultBHighCnt);
                    itemDetailDto.setAdResultHighCweekCnt(adResultCHighCnt);
                    itemDetailDto.setAdResultHighDweekCnt(adResultDHighCnt);
                    itemDetailDto.setAdResultMidAweekCnt(adResultAMidCnt);
                    itemDetailDto.setAdResultMidBweekCnt(adResultBMidCnt);
                    itemDetailDto.setAdResultMidCweekCnt(adResultCMidCnt);
                    itemDetailDto.setAdResultMidDweekCnt(adResultDMidCnt);
                    itemDetailDto.setAdResultLowAweekCnt(adResultALowCnt);
                    itemDetailDto.setAdResultLowBweekCnt(adResultBLowCnt);
                    itemDetailDto.setAdResultLowCweekCnt(adResultCLowCnt);
                    itemDetailDto.setAdResultLowDweekCnt(adResultDLowCnt);
                } // end for
            } else {

            }
        } // end for

        /*--- ---*/
        String createDate = masterParamDto.getCreateDate();
        paramMap.put("createDate", createDate);
        List<SnetAssetSwAuditCokDto> assetSwAuditCokDtoListD = assetCokMapper.getDisanosisresultByImportanceStatus(paramMap);

        int adResultAHighCntD = 0;
        int adResultBHighCntD = 0;
        int adResultCHighCntD = 0;
        int adResultDHighCntD = 0;
        int adResultAMidCntD = 0;
        int adResultBMidCntD = 0;
        int adResultCMidCntD = 0;
        int adResultDMidCntD = 0;
        int adResultALowCntD = 0;
        int adResultBLowCntD = 0;
        int adResultCLowCntD = 0;
        int adResultDLowCntD = 0;

        for (SnetAssetSwAuditCokDto cokDto : assetSwAuditCokDtoListD) {

            String auditDay = cokDto.getAuditDay();
            String adminOkDay = cokDto.getAdminOkDate();

            int thisWeek = getWeekOfMonth(auditDay, adminOkDay);

            if (thisWeek == 1) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adResultAHighCntD++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adResultAMidCntD++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adResultALowCntD++;
                }
            } else if (thisWeek == 2) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adResultBHighCntD++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adResultBMidCntD++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adResultBLowCntD++;
                }
            } else if (thisWeek == 3) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adResultCHighCntD++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adResultCMidCntD++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adResultCLowCntD++;
                }
            } else if (thisWeek >= 4) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adResultDHighCntD++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adResultDMidCntD++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adResultDLowCntD++;
                }
            }
        } // end for

        for (SnetStatisticsDto statisticsDtos : statisticsDetailList2) {

            if (statisticsDtos.getDetailList() != null) {

                for (SnetStatisticsDetailDto itemDetailDto : statisticsDtos.getDetailList()) {

                    itemDetailDto.setAdResultHighAweekCntD(adResultAHighCntD);
                    itemDetailDto.setAdResultHighBweekCntD(adResultBHighCntD);
                    itemDetailDto.setAdResultHighCweekCntD(adResultCHighCntD);
                    itemDetailDto.setAdResultHighDweekCntD(adResultDHighCntD);
                    itemDetailDto.setAdResultMidAweekCntD(adResultAMidCntD);
                    itemDetailDto.setAdResultMidBweekCntD(adResultBMidCntD);
                    itemDetailDto.setAdResultMidCweekCntD(adResultCMidCntD);
                    itemDetailDto.setAdResultMidDweekCntD(adResultDMidCntD);
                    itemDetailDto.setAdResultLowAweekCntD(adResultALowCntD);
                    itemDetailDto.setAdResultLowBweekCntD(adResultBLowCntD);
                    itemDetailDto.setAdResultLowCweekCntD(adResultCLowCntD);
                    itemDetailDto.setAdResultLowDweekCntD(adResultDLowCntD);
                } // end for
            } else {

            }
        } // end for
        /*--- ---*/

        return statisticsDetailList2;
    } // end method

    /**
     * 특정 날짜 기준으로 특정 날짜가 몇주차인지 구함
     */
    private int getWeekOfMonth(String stdFormat, String format) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(StringUtils.left(stdFormat, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(StringUtils.mid(stdFormat, 4, 2)));
        calendar.set(Calendar.DATE, Integer.parseInt(StringUtils.right(stdFormat, 2)));

        int year = Integer.parseInt(StringUtils.left(format, 4));
        int month = Integer.parseInt(StringUtils.mid(format, 4, 2));
        int day = Integer.parseInt(StringUtils.right(format, 2));

        calendar.set(year, month-1, day);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    } // end method

    /**
     * 중요도별 진단 제외(변경처리 반려) 신청 처리 현황
     */
    @Override
    public List<SnetStatisticsDto> getDiagnosisexclusionByImportanceStatus10(List<SnetStatisticsDto> statisticsDetailList3, SnetAssetMasterResultDto masterParamDto) {

        List<String> assetCdInList = Lists.newArrayList();

        for (SnetAssetMasterDto dto : masterParamDto.getMasterDtos()) {
            assetCdInList.add(dto.getAssetCd());
        } // end for

        String[] assetCdInArr = assetCdInList.stream().distinct().toArray(String[]::new);

        Map paramMap = new HashMap();
//        paramMap.put("assetCdInArr", assetCdInArr);

        // report.item_result -> cok.action_item_result
        // audit_day : 진단일
        // ADMIN_OK_DATE : 변경처리승인날짜
        // item_cok_req_valid_date : 변경처리유효날짜
        List<SnetAssetSwAuditCokDto> assetSwAuditCokDtoList = assetCokMapper.getDisanosisresultByImportanceExceptStatus(paramMap);

        int adExceptAHighCnt = 0;
        int adExceptBHighCnt = 0;
        int adExceptCHighCnt = 0;
        int adExceptDHighCnt = 0;
        int adExceptAMidCnt = 0;
        int adExceptBMidCnt = 0;
        int adExceptCMidCnt = 0;
        int adExceptDMidCnt = 0;
        int adExceptALowCnt = 0;
        int adExceptBLowCnt = 0;
        int adExceptCLowCnt = 0;
        int adExceptDLowCnt = 0;

        for (SnetAssetSwAuditCokDto cokDto : assetSwAuditCokDtoList) {

            String auditDay = cokDto.getAuditDay();
            String adminOkDay = cokDto.getAdminOkDate();

            int thisWeek = getWeekOfMonth(auditDay, adminOkDay);

            if (thisWeek == 1) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adExceptAHighCnt++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adExceptAMidCnt++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adExceptALowCnt++;
                }
            } else if (thisWeek == 2) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adExceptBHighCnt++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adExceptBMidCnt++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adExceptBLowCnt++;
                }
            } else if (thisWeek == 3) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adExceptCHighCnt++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adExceptCMidCnt++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adExceptCLowCnt++;
                }
            } else if (thisWeek >= 4) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adExceptDHighCnt++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adExceptDMidCnt++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adExceptDLowCnt++;
                }
            }
        } // end for

        for (SnetStatisticsDto statisticsDtos : statisticsDetailList3) {

            if (statisticsDtos.getDetailList() != null) {

                for (SnetStatisticsDetailDto itemDetailDto : statisticsDtos.getDetailList()) {

                    itemDetailDto.setAdExceptHighAweekCnt(adExceptAHighCnt);
                    itemDetailDto.setAdExceptHighBweekCnt(adExceptBHighCnt);
                    itemDetailDto.setAdExceptHighCweekCnt(adExceptCHighCnt);
                    itemDetailDto.setAdExceptHighDweekCnt(adExceptDHighCnt);
                    itemDetailDto.setAdExceptMidAweekCnt (adExceptAMidCnt);
                    itemDetailDto.setAdExceptMidBweekCnt (adExceptBMidCnt);
                    itemDetailDto.setAdExceptMidCweekCnt (adExceptCMidCnt);
                    itemDetailDto.setAdExceptMidDweekCnt (adExceptDMidCnt);
                    itemDetailDto.setAdExceptLowAweekCnt (adExceptALowCnt);
                    itemDetailDto.setAdExceptLowBweekCnt (adExceptBLowCnt);
                    itemDetailDto.setAdExceptLowCweekCnt (adExceptCLowCnt);
                    itemDetailDto.setAdExceptLowDweekCnt (adExceptDLowCnt);
                } // end for
            } else {

            }
        } // end for

        /*--- ---*/
        String createDate = masterParamDto.getCreateDate();
        paramMap.put("createDate", createDate);
        List<SnetAssetSwAuditCokDto> assetSwAuditCokDtoListD = assetCokMapper.getDisanosisresultByImportanceExceptStatus(paramMap);

        int adExceptAHighCntD = 0;
        int adExceptBHighCntD = 0;
        int adExceptCHighCntD = 0;
        int adExceptDHighCntD = 0;
        int adExceptAMidCntD = 0;
        int adExceptBMidCntD = 0;
        int adExceptCMidCntD = 0;
        int adExceptDMidCntD = 0;
        int adExceptALowCntD = 0;
        int adExceptBLowCntD = 0;
        int adExceptCLowCntD = 0;
        int adExceptDLowCntD = 0;

        for (SnetAssetSwAuditCokDto cokDto : assetSwAuditCokDtoListD) {

            String auditDay = cokDto.getAuditDay();
            String adminOkDay = cokDto.getAdminOkDate();

            int thisWeek = getWeekOfMonth(auditDay, adminOkDay);

            if (thisWeek == 1) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adExceptAHighCntD++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adExceptAMidCntD++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adExceptALowCntD++;
                }
            } else if (thisWeek == 2) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adExceptBHighCntD++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adExceptBMidCntD++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adExceptBLowCntD++;
                }
            } else if (thisWeek == 3) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adExceptCHighCntD++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adExceptCMidCntD++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adExceptCLowCntD++;
                }
            } else if (thisWeek >= 4) {
                if (StringUtils.equals("H", cokDto.getItemGrade())) {

                    adExceptDHighCntD++;
                } else if (StringUtils.equals("M", cokDto.getItemGrade())) {

                    adExceptDMidCntD++;

                } else if (StringUtils.equals("L", cokDto.getItemGrade())) {

                    adExceptDLowCntD++;
                }
            }
        } // end for


        for (SnetStatisticsDto statisticsDtos : statisticsDetailList3) {

            if (statisticsDtos.getDetailList() != null) {

                for (SnetStatisticsDetailDto itemDetailDto : statisticsDtos.getDetailList()) {

                    itemDetailDto.setAdExceptHighAweekCntD(adExceptAHighCntD);
                    itemDetailDto.setAdExceptHighBweekCntD(adExceptBHighCntD);
                    itemDetailDto.setAdExceptHighCweekCntD(adExceptCHighCntD);
                    itemDetailDto.setAdExceptHighDweekCntD(adExceptDHighCntD);
                    itemDetailDto.setAdExceptMidAweekCntD (adExceptAMidCntD);
                    itemDetailDto.setAdExceptMidBweekCntD (adExceptBMidCntD);
                    itemDetailDto.setAdExceptMidCweekCntD (adExceptCMidCntD);
                    itemDetailDto.setAdExceptMidDweekCntD (adExceptDMidCntD);
                    itemDetailDto.setAdExceptLowAweekCntD (adExceptALowCntD);
                    itemDetailDto.setAdExceptLowBweekCntD (adExceptBLowCntD);
                    itemDetailDto.setAdExceptLowCweekCntD (adExceptCLowCntD);
                    itemDetailDto.setAdExceptLowDweekCntD (adExceptDLowCntD);
                } // end for
            } else {

            }
        } // end for
        /*--- ---*/

        return statisticsDetailList3;
    } // end method

    /**
     * 보고서 API 에서 사용할 통계 데이터 입력
     */
    @Override
    public void insertAuditStatistics13(List<SnetStatisticsDto> list, SnetAssetMasterResultDto masterResultDto, boolean todayFlag) throws Exception {

        for (SnetStatisticsDto dto : list) {

            int seq = 2;
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Calendar time = Calendar.getInstance();
            String statisticsId = format.format(time.getTime()) + StringUtils.left(StringUtils.replace(UUID.randomUUID().toString(), "-",""),15);

            dto.setSeq(seq);
            dto.setStatisticsId(statisticsId);
            if (StringUtils.isEmpty(dto.getDivision())) {
                dto.setDivision("NONE");
            }
            dto.setDivision(dto.getDivision());


            if (todayFlag) {
                dto.setToday("TODAY");
                dto.setCreateTime(masterResultDto.getCreateTime());
                auditStatisticsMapper.insertAuditStatisticsToday(dto);
            } else {

                auditStatisticsMapper.insertAuditStatistics(dto);
            }


            for (SnetStatisticsDetailDto detailDto : dto.getDetailList()) {


                for (SnetStatisticsItemDetailDto itemdetailDto : detailDto.getItemDetailList()) {

                    itemdetailDto.setSeq(seq);
                    itemdetailDto.setStatisticsId(statisticsId);
                    itemdetailDto.setBranchId(dto.getBranchId());
                    itemdetailDto.setTeamId(dto.getTeamId());
                    itemdetailDto.setCreateDay(dto.getCreateDay());

                    itemdetailDto.setAssetCd(itemdetailDto.getAssetCd());
                    itemdetailDto.setSwNm(itemdetailDto.getSwTypeNmKey());
                    itemdetailDto.setHostNm(detailDto.getHostNm());

                    itemdetailDto.setDivision(dto.getDivision());

                    if (StringUtils.isNotEmpty(itemdetailDto.getAssetCd())) {



                        if (todayFlag) {
                            itemdetailDto.setToday("TODAY");
                            itemdetailDto.setCreateTime(masterResultDto.getCreateTime());
                            auditStatisticsMapper.insertAuditStatisticsItemDetailToday(itemdetailDto);
                        } else {

                            auditStatisticsMapper.insertAuditStatisticsItemDetail(itemdetailDto);
                        }
                    } // end if
                } // end for
            } // end for
            seq++;
        } // end for
    }

    /**
     * 두 날짜 차이 일수로 구하기
     */
    private int getDays(String stdFormat, String format) {


        int count = 0;

        Calendar calendar = Calendar.getInstance();

        int year1 = Integer.parseInt(StringUtils.left(stdFormat, 4));
        int month1 = Integer.parseInt(StringUtils.mid(stdFormat, 4, 2));
        int day1 = Integer.parseInt(StringUtils.right(stdFormat, 2));
        calendar.set(year1,month1-1,day1);

        Calendar calendar2 = Calendar.getInstance();

        int year2 = Integer.parseInt(StringUtils.left(format, 4));
        int month2 = Integer.parseInt(StringUtils.mid(format, 4, 2));
        int day2 = Integer.parseInt(StringUtils.right(format, 2));
        calendar2.set(year2,month2-1,day2);

        while (!calendar.after(calendar2)) {
            count++;
            calendar.add(Calendar.DATE,1);
        }

        return count;
    } // end method

    /**
     * SnetAssetUserDto 클래스에 대해 값 변경 없이 매핑
     */
    private SnetAssetUserDto mapToUserDto(SnetAssetUserDto snetAssetUserDto) {

        ModelMapper modelMapper = new ModelMapper();

        SnetAssetUserDto resultSnetAssetUserDto = modelMapper.map(snetAssetUserDto, SnetAssetUserDto.class);

        return resultSnetAssetUserDto;
    } // end method

    /**
     * SnetAssetMasterDto 클래스에 대해 값 변경 매핑
     * user_auth (임시) : SU(수퍼), SE(시스템), SY(자산)=OS(조치)
     */
    private SnetAssetMasterParamDto mapToMasterDto(SnetAssetMasterResultDto snetAssetMasterDto) {

        ModelMapper modelMapper = new ModelMapper();

        SnetAssetMasterParamDto resultSnetAssetMasterDto = modelMapper.map(snetAssetMasterDto, SnetAssetMasterParamDto.class);
        resultSnetAssetMasterDto.setMasterDtos(snetAssetMasterDto.getMasterDtos());

        // 통계 테이블에 insert 할 때 에는 모든 branchID, teamID 가 대상임
        //  -> API 호출시 userId 에 대한 branchID, teamID 를 조건으로 filter 하여 해당 자산에 대한 결과 리턴 함

        return resultSnetAssetMasterDto;
    } // end method

    @Override
    public void insertAuditStatisticsMig(SnetAssetMasterResultDto resultDto) {

        String deleteCreateDay = resultDto.getCreateDate();
        String deleteCreateTime = resultDto.getCreateTime();
        log.info("*[deleteCreateDay] : {}", deleteCreateDay);
        log.info("*[deleteCreateTime] : {}", deleteCreateTime);

        SnetStatisticsDto statisticsDtoToday = auditStatisticsMapper.getStatisticsTodayMaxDay(resultDto);
        log.info("*[statisticsDtoToday] : {}", statisticsDtoToday);

        if (statisticsDtoToday != null) {

            if (StringUtils.isNotEmpty(statisticsDtoToday.getCreateDay())) {

                SnetStatisticsDto dto = SnetStatisticsDto.builder()
                        .createDay(statisticsDtoToday.getCreateDay())
                        .build();

                SnetStatisticsDto statisticsDtoTodayTime = auditStatisticsMapper.getStatisticsTodayMaxTime(dto);
                log.info("*[statisticsDtoTodayTime] : {}", statisticsDtoTodayTime);

                if (statisticsDtoTodayTime != null) {

                    if (StringUtils.isNotEmpty(statisticsDtoTodayTime.getCreateTime())) {

                        // createDay max, 현재 비교
                        // -> 같으면 어제
                        // -> 다르면 max CreateDay
                        SnetStatisticsDto dto2 = new SnetStatisticsDto();
                        log.info("*[createDay max, 현재 비교] : {}", StringUtils.equals(deleteCreateDay, statisticsDtoTodayTime.getCreateDay()));
                        if (StringUtils.equals(deleteCreateDay, statisticsDtoTodayTime.getCreateDay())) {

                            dto2.setCreateDay(deleteCreateDay);
                        } else {

                            dto2.setCreateDay(statisticsDtoToday.getCreateDay());
                        }

                        // createTime max, 현재 비교
                        // -> 같으면 어제 23:00
                        // -> 다르면 max CreateTime
                        log.info("*[createTime max, 현재 비교] : {}", StringUtils.equals(deleteCreateTime, statisticsDtoTodayTime.getCreateTime()));
                        if (StringUtils.equals(deleteCreateTime, statisticsDtoTodayTime.getCreateTime())) {

                            dto2.setCreateTime(deleteCreateTime);
                        } else {

                            dto2.setCreateTime(statisticsDtoTodayTime.getCreateTime());
                        }

                        log.info("*[deleteInsertAuditStatistics] dto2 : {}", dto2);

                        auditStatisticsMapper.deleteInsertAuditStatisticsMig(dto2);
                        auditStatisticsMapper.deleteInsertAuditStatisticsMigDetail(dto2);
                    }
                }
            } // end if
        } // end if
    } // end method

    /**
     * 'sg_supprotmanager 프로젝트 - 스케줄러'
     * 인트로 데이터 생성 스케줄러 - 00:15
     * @param createDay
     * */
    public void secCategoryBatch(String createDay) {
        try {
            //분류 항목별 보안지수
            auditStatisticsMapper.deleteIntroSecCategory();

            for (int i = 0; i <= 6; i++) {
                SnetAssetSecCategoryDto resultDto = SnetAssetSecCategoryDto.builder()
                    .createDay(createDay)
                    .auditStdCd(i)
                    .build();

                //Linux
                resultDto.setSwType(null);
                resultDto.setSwNm(null);
                resultDto.setLnotflag("Y");
                resultDto.setNotflag(null);
                auditStatisticsMapper.insertIntroSecCategory(resultDto);


                //Windows
                resultDto.setSwType("OS");
                resultDto.setSwNm("Windows");
                resultDto.setLnotflag(null);
                auditStatisticsMapper.insertIntroSecCategory(resultDto);

                //DB"
                resultDto.setSwType("DB");
                resultDto.setSwNm(null);
                auditStatisticsMapper.insertIntroSecCategory(resultDto);

                //WEB
                resultDto.setSwType("WEB");
                auditStatisticsMapper.insertIntroSecCategory(resultDto);

                //WAS
                resultDto.setSwType("WAS");
                auditStatisticsMapper.insertIntroSecCategory(resultDto);

                //NW
                resultDto.setSwType("NW");
                auditStatisticsMapper.insertIntroSecCategory(resultDto);

                //URL
                resultDto.setSwType("URL");
                auditStatisticsMapper.insertIntroSecCategory(resultDto);

                //ETC
                resultDto.setSwType(null);
                resultDto.setNotflag("Y");
                auditStatisticsMapper.insertIntroSecCategory(resultDto);
            } // end for


            //진단결과 변경 신청 현황
            auditStatisticsMapper.deleteIntroCokStatus();
            auditStatisticsMapper.insertIntroCokStatus();

            //장비현황
            auditStatisticsMapper.deleteIntroEquipStatus();
            auditStatisticsMapper.insertIntroEquipStatus();

            //자산현황
            auditStatisticsMapper.deleteIntroAssetStatus();
            auditStatisticsMapper.insertIntroAssetStatus();

            //자산현황 DETAIL
            auditStatisticsMapper.deleteIntroAssetStatusDetail();
            auditStatisticsMapper.insertIntroAssetStatusDetail();

            //자산별 보안준수율 하위 TOP10
            auditStatisticsMapper.deleteIntroAssetAuditRateTop10();
            auditStatisticsMapper.insertIntroAssetAuditRateTop10();

            //진단항목별 취약점 TOP10
            auditStatisticsMapper.deleteIntroValnerabilityTop10();
            auditStatisticsMapper.insertIntroValnerabilityTop10();

            //중요도별 취약점 현황
            auditStatisticsMapper.deleteIntroValnerabilityStatus();
            auditStatisticsMapper.insertIntroValnerabilityStatus();

            //중요도별 진단 결과 승인/반려 현황
            auditStatisticsMapper.deleteIntroResultStatus();
            auditStatisticsMapper.insertIntroResultStatus();

            //에이전트 상태
            auditStatisticsMapper.deleteIntroAgentStatus();
            auditStatisticsMapper.insertIntroAgentStatus();


        } catch (Exception e) {
            log.error(CommonUtils.printError(e));
        }

    } // end method

    /**
     * swType, swNm 을 swNmKey 로 변환
     */
    private String getSwNmKey(String swType, String swNm) {

        String swNmKey = null;

        if (StringUtils.equalsIgnoreCase("OS", swType)) {

            if (!StringUtils.equalsIgnoreCase("Windows", swNm)) {

                swNmKey = "Linux";
            } else {

                swNmKey = "Windows";
            }
        } else if (StringUtils.equalsIgnoreCase("DB", swType)){

            swNmKey = "DB";
        } else if (StringUtils.equalsIgnoreCase("WEB", swType)){

            swNmKey = "WEB";
        } else if (StringUtils.equalsIgnoreCase("WAS", swType)){

            swNmKey = "WAS";
        } else if (StringUtils.equalsIgnoreCase("NW", swType)){

            swNmKey = "NW";
        } else if (StringUtils.equalsIgnoreCase("URL", swType)){

            swNmKey = "Web App";
        } else {

            swNmKey = "ETC";
        }

        return swNmKey;
    }
}
