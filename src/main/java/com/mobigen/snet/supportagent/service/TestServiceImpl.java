package com.mobigen.snet.supportagent.service;

import com.google.common.collect.Lists;
import com.mobigen.snet.supportagent.dao.TestMapper;
import com.mobigen.snet.supportagent.models.*;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 'sg_supprotmanager 프로젝트 - 종합보고서'
 */
@Service
@Slf4j
public class TestServiceImpl extends AbstractService implements TestService {

    private static final String AES_KEY = "snet^igloo!sec";

    @Autowired (required = false)
    private TestMapper testMapper;

    @Override
    public List<TotalOverallResultDto> getCheckTargetsList(List<SnetAssetSwAuditExcelDto> strList, Map gAssetSwJob) {

        String startAuditDay = gAssetSwJob.get("startAuditDay").toString(); // 진단 시작일
        String endAuditDay = gAssetSwJob.get("endAuditDay").toString(); // 진단 종료일

        String fileType = null;
        if (gAssetSwJob.get("FILE_TYPE") != null) {
            fileType = gAssetSwJob.get("FILE_TYPE").toString();
        }

        List<TotalOverallResultDto> list = Lists.newArrayList();

        String asset_cd = null;
        String auditDay = null;
        boolean listAddFlag = false;
        boolean etcAddFlag = false;

        String[] listArr2 = {"OS", "DB", "WEB", "WAS", "NW", "URL"};
        String etcSwTypeStr = null;

        for (SnetAssetSwAuditExcelDto strVo : strList) {

            etcAddFlag = Arrays.stream(listArr2).noneMatch(x -> StringUtils.equalsIgnoreCase(x, strVo.getSwType()));

            if (etcAddFlag) {

                etcSwTypeStr = strVo.getSwType();
                break;
            }
        } // end for

        for (SnetAssetSwAuditExcelDto swTypeStr : strList) {

            TotalOverallResultDto totalOverallResultDto = new TotalOverallResultDto();

            // 점검수량
            if (StringUtils.equalsIgnoreCase("OS", swTypeStr.getSwType())) {

                if (StringUtils.equalsIgnoreCase("Windows", swTypeStr.getSwNm())) {

                    gAssetSwJob.put("SW_TYPE", "OS");
                    gAssetSwJob.put("SW_NM", "Windows");
                } else {

                    gAssetSwJob.put("LNOT_FLAG", "Y");
                }
            } else {

                gAssetSwJob.put("SW_TYPE", swTypeStr.getSwType());
                gAssetSwJob.remove("SW_NM");
            }

            int checkTargets = 0;

            gAssetSwJob.put("HOST_GRD_FLAG", "Y");
            List<SnetAssetSwAuditExcelDto> list7Re = testMapper.selectAssetSwExcelListNew(gAssetSwJob);
            gAssetSwJob.remove("LNOT_FLAG");
            gAssetSwJob.remove("HOST_GRD_FLAG");
            gAssetSwJob.remove("SW_TYPE");

            List<SnetAssetSwAuditReportDto> reportDtoList = Lists.newArrayList();
            List<SnetAssetSwAuditReportDto> tempReportDtoList = Lists.newArrayList();
            String tempAssetCd = null;
            String tempAuditDay = null;
            String swTypeNm = null;
            int list7ReCnt = 1;
            int overAllMultiple = 0;

            for (SnetAssetSwAuditExcelDto excelDto : list7Re) {

                tempAssetCd = excelDto.getAssetCd();
                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);
                paramMap.put("SW_INFO", excelDto.getSwInfo());
                paramMap.put("SW_DIR", excelDto.getSwDir());
                paramMap.put("SW_USER", excelDto.getSwUser());
                paramMap.put("SW_ETC", excelDto.getSwEtc());

                if (StringUtils.equalsIgnoreCase("OS", swTypeStr.getSwType())) {

                    if (StringUtils.equalsIgnoreCase("Windows", swTypeStr.getSwNm())) {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", "Windows");
                    } else {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", excelDto.getSwNm());
                    }
                } else {

                    paramMap.put("SW_TYPE", swTypeStr.getSwType());
                    paramMap.remove("SW_NM");
                }

                paramMap.put("ASSET_CD", tempAssetCd);
                paramMap.put("FILE_TYPE", gAssetSwJob.get("FILE_TYPE"));

                SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetSwAuditReportNewGetMinAuditDay(paramMap);

                if (reportDto1 != null) {
                    tempAuditDay = reportDto1.getAuditDay();

                    paramMap.put("AUDIT_DAY", tempAuditDay);
                    SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                    overAllMultiple += reportDto2.getCount();
                    reportDtoList.add(reportDto2);
                }

                list7ReCnt++;
            } // end for


            if(reportDtoList.isEmpty()) {
                log.info("reportDtoList is empty.");
                return list;
            }

            checkTargets = reportDtoList.size();

            String[] assetCdIn = new String[list7Re.size()];
            int a = 0;
            for (SnetAssetSwAuditExcelDto assetCdDto : list7Re) {

                assetCdIn[a] = assetCdDto.getAssetCd();
                a++;
            }
            gAssetSwJob.put("assetCdIn", assetCdIn);

            // 종합보고서 보안점검을 위한 최초진단일
            SnetAssetSwAuditExcelDto snetAssetSwAuditExcelDto = testMapper.selectAssetSwExcelGroupbyAuditday(gAssetSwJob);
            SnetAssetSwAuditExcelDto snetAssetSwAuditExcelDto2 = testMapper.selectAssetSwExcelGroupbyMaxAuditday(gAssetSwJob);

            if (snetAssetSwAuditExcelDto != null && snetAssetSwAuditExcelDto2 != null) {

                auditDay = snetAssetSwAuditExcelDto.getMinAuditDay();
                asset_cd = snetAssetSwAuditExcelDto.getAssetCd();
            }

            // 점검항목수
            int itemTotal = 0;
            String create_date = null;
            if (list7Re != null && list7Re.size() > 0) {

                tempReportDtoList = reportDtoList.stream().collect(Collectors.toList());
                tempReportDtoList.sort(Comparator.comparing(SnetAssetSwAuditReportDto::getAuditDay).reversed());
                SnetAssetSwAuditReportDto itemReportDto = tempReportDtoList.stream().findFirst().get();

                Map paramMap = new HashMap();

                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ASSET_CD", itemReportDto.getAssetCd());
                paramMap.put("AUDIT_DAY", itemReportDto.getAuditDay());
                paramMap.put("CREATE_DATE", itemReportDto.getCreateDate());

                if (StringUtils.equalsIgnoreCase("OS", swTypeStr.getSwType())) {

                    if (StringUtils.equalsIgnoreCase("Windows", swTypeStr.getSwNm())) {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", "Windows");
                    } else {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", itemReportDto.getSwNm());
                    }
                } else {

                    paramMap.put("SW_TYPE", swTypeStr.getSwType());
                    paramMap.remove("SW_NM");
                }

                itemTotal = testMapper.getItemTotal(paramMap);
            } else {

                itemTotal = 0;
            }

            // 점검결과별 CNT
            int adResultOk = 0;
            int adResultNok = 0;
            int adResultNa = 0;
            int adResultPass = 0;
            int adResultReq = 0;

            //점검결과별 가중치합
            int adWeightOk = 0;
            int adWeightNok = 0;
            int adWeightNa = 0;
            int adWeightPass = 0;
            int adWeightReq = 0;

            if (assetCdIn != null && assetCdIn.length > 0) {

                for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

                    Map paramMap = new HashMap();
                    paramMap.put("startAuditDay", startAuditDay);
                    paramMap.put("endAuditDay", endAuditDay);
                    paramMap.put("SW_INFO", reportDto.getSwInfo());
                    paramMap.put("SW_DIR", reportDto.getSwDir());
                    paramMap.put("SW_USER", reportDto.getSwUser());
                    paramMap.put("SW_ETC", reportDto.getSwEtc());

                    if (StringUtils.equalsIgnoreCase("OS", swTypeStr.getSwType())) {

                        if (StringUtils.equalsIgnoreCase("Windows", swTypeStr.getSwNm())) {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", "Windows");
                        } else {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", reportDto.getSwNm());
                        }
                    } else {

                        paramMap.put("SW_TYPE", swTypeStr.getSwType());
                        paramMap.remove("SW_NM");
                    }
;
                    paramMap.put("ASSET_CD", reportDto.getAssetCd());
                    paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
                    paramMap.put("CREATE_DATE", reportDto.getCreateDate());
                    paramMap.put("FILE_TYPE", gAssetSwJob.get("FILE_TYPE"));

                    List<SnetAssetSwAuditReportDto> results = testMapper.getReportTot(paramMap);
                    for (SnetAssetSwAuditReportDto result : results) {
                        if (StringUtils.equals("T", result.getItemResult())) {
                            adResultOk += result.getResultCnt();
                            adWeightOk += result.getWeightTot();
                        } else if (StringUtils.equals("F", result.getItemResult())) {
                            adResultNok += result.getResultCnt();
                            adWeightNok += result.getWeightTot();
                        } else if (StringUtils.equals("C", result.getItemResult())) {
                            adResultPass += result.getResultCnt();
                            adWeightPass += result.getWeightTot();
                        } else if (StringUtils.equals("NA", result.getItemResult())) {
                            adResultNa += result.getResultCnt();
                            adWeightNa += result.getWeightTot();
                        } else if (StringUtils.equals("R", result.getItemResult())) {
                            adResultReq += result.getResultCnt();
                            adWeightReq += result.getWeightTot();
                        }
                    }
                }
            }

            log.debug("보안점검 T: " + adResultOk + ", F: " + adResultNok + ", Req: " + adResultReq + ", C: " + adResultPass);
            log.debug("보안점검 (ITEM_GRADE * CNT)  T:" + adWeightOk + ", F: " + adWeightNok + ", Req: " + adWeightReq + ", C: " + adWeightPass);

            //보안준수율
            double auditRate = ( (double)adWeightOk / (double)( adWeightOk + adWeightNok + adWeightReq ) ) * 100;		//  보안준수율 = T  /  T + F + R
            auditRate = Math.round( auditRate * 100d ) / 100d ;		// 소수 둘째 자리

            String keyStr = null;
            if (StringUtils.equalsIgnoreCase("OS", swTypeStr.getSwType())) {

                if (StringUtils.equalsIgnoreCase("Windows", swTypeStr.getSwNm())) {

                    keyStr = "Windows";
                } else {

                    keyStr = "Linux";
                }
            } else {

                keyStr = swTypeStr.getSwType();
            }

            totalOverallResultDto.setKey(keyStr);
            totalOverallResultDto.setValue1(checkTargets);
            totalOverallResultDto.setValue2(itemTotal);
            totalOverallResultDto.setValue3(overAllMultiple);
            totalOverallResultDto.setValue4(adResultOk);   //양호
            totalOverallResultDto.setValue5(adResultNok);     //취약
            totalOverallResultDto.setValue6(adResultNa);      //NA
            totalOverallResultDto.setValue7(adResultPass);      //불가
            totalOverallResultDto.setValue8(adResultReq);      //인터뷰필요
            totalOverallResultDto.setAuditRate(auditRate);      //보안준수율
            totalOverallResultDto.setMinAuditDay(startAuditDay);
            totalOverallResultDto.setMaxAuditDay(endAuditDay);
            totalOverallResultDto.setAssetCdIn(assetCdIn);
            totalOverallResultDto.setSwNm(gAssetSwJob.get("SW_NM") != null ? gAssetSwJob.get("SW_NM").toString() : null);

            list.add(totalOverallResultDto);

            gAssetSwJob.remove("SW_NM");
            gAssetSwJob.remove("assetCdIn");
        } // end for


        String[] listArr = {"Linux", "Windows", "DB", "WEB", "WAS", "NW", "URL"};

        List<TotalOverallResultDto> resultDtoList = Lists.newArrayList();
        for (String strArr : listArr) {

            TotalOverallResultDto resultDto = new TotalOverallResultDto();

            listAddFlag = list.stream().anyMatch(x -> StringUtils.equalsIgnoreCase(x.getKey(), strArr));

            if (StringUtils.equalsIgnoreCase("Linux", strArr) && listAddFlag) {

                resultDto = list.stream().filter(x -> x.getKey().equals("Linux")).findFirst().orElse(null);
                resultDtoList.add(resultDto);
            } else if (StringUtils.equalsIgnoreCase("Linux", strArr) && listAddFlag == false) {

                TotalOverallResultDto tempDto = new TotalOverallResultDto();
                tempDto.setKey(strArr);
                tempDto.setValue1(0);
                tempDto.setValue2(0);
                tempDto.setValue3(0);
                tempDto.setValue4(0);
                tempDto.setValue5(0);
                tempDto.setValue6(0);
                resultDtoList.add(tempDto);
            }

            if (StringUtils.equalsIgnoreCase("Windows", strArr) && listAddFlag) {

                resultDto = list.stream().filter(x -> x.getKey().equals("Windows")).findFirst().orElse(null);
                resultDtoList.add(resultDto);
            } else if (StringUtils.equalsIgnoreCase("Windows", strArr) && listAddFlag == false) {

                TotalOverallResultDto tempDto = new TotalOverallResultDto();
                tempDto.setKey(strArr);
                tempDto.setValue1(0);
                tempDto.setValue2(0);
                tempDto.setValue3(0);
                tempDto.setValue4(0);
                tempDto.setValue5(0);
                tempDto.setValue6(0);
                resultDtoList.add(tempDto);
            }

            if (StringUtils.equalsIgnoreCase("DB", strArr) && listAddFlag) {

                resultDto = list.stream().filter(x -> x.getKey().equals("DB")).findFirst().orElse(null);
                resultDtoList.add(resultDto);
            } else if (StringUtils.equalsIgnoreCase("DB", strArr) && listAddFlag == false) {

                TotalOverallResultDto tempDto = new TotalOverallResultDto();
                tempDto.setKey(strArr);
                tempDto.setValue1(0);
                tempDto.setValue2(0);
                tempDto.setValue3(0);
                tempDto.setValue4(0);
                tempDto.setValue5(0);
                tempDto.setValue6(0);
                resultDtoList.add(tempDto);
            }

            if (StringUtils.equalsIgnoreCase("WEB", strArr) && listAddFlag) {

                resultDto = list.stream().filter(x -> x.getKey().equals("WEB")).findFirst().orElse(null);
                resultDtoList.add(resultDto);
            } else if (StringUtils.equalsIgnoreCase("WEB", strArr) && listAddFlag == false) {

                TotalOverallResultDto tempDto = new TotalOverallResultDto();
                tempDto.setKey(strArr);
                tempDto.setValue1(0);
                tempDto.setValue2(0);
                tempDto.setValue3(0);
                tempDto.setValue4(0);
                tempDto.setValue5(0);
                tempDto.setValue6(0);
                resultDtoList.add(tempDto);
            }

            if (StringUtils.equalsIgnoreCase("WAS", strArr) && listAddFlag) {

                resultDto = list.stream().filter(x -> x.getKey().equals("WAS")).findFirst().orElse(null);
                resultDtoList.add(resultDto);
            } else if (StringUtils.equalsIgnoreCase("WAS", strArr) && listAddFlag == false) {

                TotalOverallResultDto tempDto = new TotalOverallResultDto();
                tempDto.setKey(strArr);
                tempDto.setValue1(0);
                tempDto.setValue2(0);
                tempDto.setValue3(0);
                tempDto.setValue4(0);
                tempDto.setValue5(0);
                tempDto.setValue6(0);
                resultDtoList.add(tempDto);
            }

            if (StringUtils.equalsIgnoreCase("NW", strArr) && listAddFlag) {

                resultDto = list.stream().filter(x -> x.getKey().equals("NW")).findFirst().orElse(null);
                resultDtoList.add(resultDto);
            } else if (StringUtils.equalsIgnoreCase("NW", strArr) && listAddFlag == false) {

                TotalOverallResultDto tempDto = new TotalOverallResultDto();
                tempDto.setKey(strArr);
                tempDto.setValue1(0);
                tempDto.setValue2(0);
                tempDto.setValue3(0);
                tempDto.setValue4(0);
                tempDto.setValue5(0);
                tempDto.setValue6(0);
                resultDtoList.add(tempDto);
            }

            if (StringUtils.equalsIgnoreCase("URL", strArr) && listAddFlag) {

                resultDto = list.stream().filter(x -> x.getKey().equals("URL")).findFirst().orElse(null);
                resultDto.setKey("Web App(URL)");
                resultDtoList.add(resultDto);
            } else if (StringUtils.equalsIgnoreCase("URL", strArr) && listAddFlag == false) {

                TotalOverallResultDto tempDto = new TotalOverallResultDto();
                tempDto.setKey("Web App(URL)");
                tempDto.setValue1(0);
                tempDto.setValue2(0);
                tempDto.setValue3(0);
                tempDto.setValue4(0);
                tempDto.setValue5(0);
                tempDto.setValue6(0);
                resultDtoList.add(tempDto);
            }
        }


        List<String> strListArr = Lists.newArrayList();
        boolean strListArrFlag = false;
        for (TotalOverallResultDto totalOverallResultDto : resultDtoList) {
            strListArr.add(totalOverallResultDto.getKey());
        }

        // 기타 추가
        if (etcAddFlag && StringUtils.isNotEmpty(etcSwTypeStr)) {

            TotalOverallResultDto totalOverallResultDto = new TotalOverallResultDto();

            // 점검수량
            if (StringUtils.equalsIgnoreCase("Linux", etcSwTypeStr) || StringUtils.equalsIgnoreCase("Windows", etcSwTypeStr)) {

                gAssetSwJob.put("SW_TYPE", "OS");
                gAssetSwJob.put("SW_NM", etcSwTypeStr);
            } else {

                gAssetSwJob.put("SW_TYPE", etcSwTypeStr);
            }


            int checkTargets = 0;
            int overAllMultiple = 0;
            gAssetSwJob.put("HOST_GRD_FLAG", "Y");
            List<SnetAssetSwAuditExcelDto> list7Re = testMapper.selectAssetSwExcelListNew(gAssetSwJob);
            gAssetSwJob.remove("HOST_GRD_FLAG");

            List<SnetAssetSwAuditReportDto> reportDtoList = Lists.newArrayList();
            String tempAssetCd = null;
            String tempAuditDay = null;
            String swTypeNm = null;
            for (SnetAssetSwAuditExcelDto excelDto : list7Re) {

                tempAssetCd = excelDto.getAssetCd();
                Map paramMap = new HashMap();
                paramMap.put("SW_INFO", excelDto.getSwInfo());
                paramMap.put("SW_DIR", excelDto.getSwDir());
                paramMap.put("SW_USER", excelDto.getSwUser());
                paramMap.put("SW_ETC", excelDto.getSwEtc());

                if (StringUtils.equalsIgnoreCase("Linux", etcSwTypeStr) || StringUtils.equalsIgnoreCase("Windows", etcSwTypeStr)) {

                    swTypeNm = etcSwTypeStr;
                    paramMap.put("SW_TYPE", "OS");
                    paramMap.put("SW_NM", swTypeNm);
                } else {

                    paramMap.put("SW_TYPE", etcSwTypeStr);
                    paramMap.remove("SW_NM");
                }
                paramMap.put("ASSET_CD", tempAssetCd);
                paramMap.put("FILE_TYPE", gAssetSwJob.get("FILE_TYPE"));

                SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetSwAuditReportNewGetMaxAuditDay(paramMap);

                tempAuditDay = reportDto1.getAuditDay();

                paramMap.put("AUDIT_DAY", tempAuditDay);
                SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                overAllMultiple += reportDto2.getCount();
                reportDtoList.add(reportDto2);
            }
            checkTargets = reportDtoList.size();

            String[] assetCdIn = new String[list7Re.size()];
            int a = 0;
            for (SnetAssetSwAuditExcelDto assetCdDto : list7Re) {

                assetCdIn[a] = assetCdDto.getAssetCd();
                a++;
            }
            gAssetSwJob.put("assetCdIn", assetCdIn);

            String[] strArr = (String[]) gAssetSwJob.get("assetCdIn");

            // 종합보고서 점검결과 조회를 위한 최초진단일, 최종진단일
            SnetAssetSwAuditExcelDto snetAssetSwAuditExcelDto = testMapper.selectAssetSwExcelGroupbyAuditday(gAssetSwJob);

            SnetAssetSwAuditExcelDto snetAssetSwAuditExcelDto2 = testMapper.selectAssetSwExcelGroupbyMaxAuditday(gAssetSwJob);

            auditDay = snetAssetSwAuditExcelDto.getMinAuditDay();
            asset_cd = snetAssetSwAuditExcelDto.getAssetCd();
            startAuditDay = snetAssetSwAuditExcelDto.getMinAuditDay();
            endAuditDay = snetAssetSwAuditExcelDto2.getMaxAuditDay();

            // 점검항목수
            int itemTotal = 0;
            if (list7Re != null && list7Re.size() > 0) {
                gAssetSwJob.put("ASSET_CD", asset_cd);
                gAssetSwJob.put("AUDIT_DAY", auditDay);
                itemTotal = testMapper.getItemTotal(gAssetSwJob);

            } else {

                itemTotal = 0;
            }

            // 점검결과별 CNT
            int adResultOk = 0;
            int adResultNok = 0;
            int adResultNa = 0;
            int adResultPass = 0;
            int adResultReq = 0;

            //점검결과별 가중치합
            int adWeightOk = 0;
            int adWeightNok = 0;
            int adWeightNa = 0;
            int adWeightPass = 0;
            int adWeightReq = 0;

            if (assetCdIn != null && assetCdIn.length > 0) {

                for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

                    Map paramMap = new HashMap();
                    if (StringUtils.equalsIgnoreCase("Linux", etcSwTypeStr) || StringUtils.equalsIgnoreCase("Windows", etcSwTypeStr)) {

                        swTypeNm = etcSwTypeStr;
                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", swTypeNm);
                    } else {

                        paramMap.put("SW_TYPE", etcSwTypeStr);
                        paramMap.remove("SW_NM");
                    }
                    paramMap.put("ASSET_CD", reportDto.getAssetCd());
                    paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
                    paramMap.put("CREATE_DATE", reportDto.getCreateDate());
                    paramMap.put("FILE_TYPE", gAssetSwJob.get("FILE_TYPE"));

                    List<SnetAssetSwAuditReportDto> results = testMapper.getReportTot(paramMap);
                    for (SnetAssetSwAuditReportDto result : results) {
                        if (StringUtils.equals("T", result.getItemResult())) {
                            adResultOk += result.getResultCnt();
                            adWeightOk += result.getWeightTot();
                        } else if (StringUtils.equals("F", result.getItemResult())) {
                            adResultNok += result.getResultCnt();
                            adWeightNok += result.getWeightTot();
                        } else if (StringUtils.equals("C", result.getItemResult())) {
                            adResultPass += result.getResultCnt();
                            adWeightPass += result.getWeightTot();
                        } else if (StringUtils.equals("NA", result.getItemResult())) {
                            adResultNa += result.getResultCnt();
                            adWeightNa += result.getWeightTot();
                        } else if (StringUtils.equals("R", result.getItemResult())) {
                            adResultReq += result.getResultCnt();
                            adWeightReq += result.getWeightTot();
                        }
                    }
                }
            }

            //보안준수율
            double auditRate = ((double) adWeightOk / (double) (adWeightOk + adWeightNok + adWeightReq)) * 100;        //  보안준수율 = T  /  T + F + R
            auditRate = Math.round(auditRate * 100d) / 100d;        // 소수 둘째 자리


            totalOverallResultDto.setKey("Etc");
            totalOverallResultDto.setValue1(checkTargets);
            totalOverallResultDto.setValue2(itemTotal);
            totalOverallResultDto.setValue3(overAllMultiple);
            totalOverallResultDto.setValue4(adResultOk);
            totalOverallResultDto.setValue5(adResultNok);
            totalOverallResultDto.setValue6(adResultNa);
            totalOverallResultDto.setValue7(adResultPass);       //불가
            totalOverallResultDto.setValue8(adResultReq);       //인터뷰필요
            totalOverallResultDto.setAuditRate(auditRate);      //보안준수율
            totalOverallResultDto.setMinAuditDay(startAuditDay);
            totalOverallResultDto.setMaxAuditDay(endAuditDay);
            totalOverallResultDto.setAssetCdIn(assetCdIn);
            totalOverallResultDto.setSwNm(gAssetSwJob.get("SW_NM") != null ? gAssetSwJob.get("SW_NM").toString() : null);

            gAssetSwJob.remove("SW_NM");
            gAssetSwJob.remove("assetCdIn");

            list.add(totalOverallResultDto);
        } // end etc if

        String finalEtcSwTypeStr = etcSwTypeStr;
        strListArrFlag = resultDtoList.stream().anyMatch(x -> StringUtils.equalsIgnoreCase(x.getKey(), finalEtcSwTypeStr));

        if (!strListArrFlag && StringUtils.isNotEmpty(etcSwTypeStr)) {

            TotalOverallResultDto resultDto = new TotalOverallResultDto();
            resultDto = list.stream().filter(x -> x.getKey().equals("Etc")).findFirst().orElse(null);
            resultDto.setEtcSwType(etcSwTypeStr);
            resultDto.setKey("기타");
            resultDtoList.add(resultDto);
        } else {

            TotalOverallResultDto tempDto = new TotalOverallResultDto();
            tempDto.setKey("기타");
            tempDto.setEtcSwType(etcSwTypeStr);
            tempDto.setValue1(0);
            tempDto.setValue2(0);
            tempDto.setValue3(0);
            tempDto.setValue4(0);
            tempDto.setValue5(0);
            tempDto.setValue6(0);
            resultDtoList.add(tempDto);
        }

        return resultDtoList;
    } // end method getCheckTargetsList

    @Override
    public List<TotalOverallResultDto> getMigTargetsList(List<SnetAssetSwAuditExcelDto> strList, Map gAssetSwJob) {

        String startAuditDay = gAssetSwJob.get("startAuditDay").toString(); // 진단 시작일
        String endAuditDay = gAssetSwJob.get("endAuditDay").toString(); // 진단 종료일

        List<TotalOverallResultDto> list = Lists.newArrayList();

        String asset_cd = null;
        String auditDay = null;
        boolean listAddFlag = false;
        boolean etcAddFlag = false;

        String[] listArr2 = {"OS", "DB", "WEB", "WAS", "NW", "URL"};
        String etcSwTypeStr = null;
        for (SnetAssetSwAuditExcelDto strVo : strList) {

            etcAddFlag = Arrays.stream(listArr2).noneMatch(x -> StringUtils.equalsIgnoreCase(x, strVo.getSwType()));

            if (etcAddFlag) {

                etcSwTypeStr = strVo.getSwType();
                break;
            }
        } // end for

        for (SnetAssetSwAuditExcelDto swTypeStr : strList) {

            TotalOverallResultDto totalOverallResultDto = new TotalOverallResultDto();

            // 점검수량
            if (StringUtils.equalsIgnoreCase("OS", swTypeStr.getSwType())) {

                if (StringUtils.equalsIgnoreCase("Windows", swTypeStr.getSwNm())) {

                    gAssetSwJob.put("SW_TYPE", "OS");
                    gAssetSwJob.put("SW_NM", "Windows");
                } else {

                    gAssetSwJob.put("LNOT_FLAG", "Y");
                }
            } else {

                gAssetSwJob.put("SW_TYPE", swTypeStr.getSwType());
                gAssetSwJob.remove("SW_NM");
            }

            int checkTargets = 0;
            gAssetSwJob.put("HOST_GRD_FLAG", "Y");
            List<SnetAssetSwAuditExcelDto> list7Re = testMapper.selectAssetSwExcelListNew(gAssetSwJob);
            gAssetSwJob.remove("SW_TYPE");
            gAssetSwJob.remove("LNOT_FLAG");
            gAssetSwJob.remove("HOST_GRD_FLAG");

            List<SnetAssetSwAuditReportDto> reportDtoList = Lists.newArrayList();
            String tempAssetCd = null;
            String tempAuditDay = null;
            String swTypeNm = null;
            int overAllMultiple = 0;

            for (SnetAssetSwAuditExcelDto excelDto : list7Re) {

                tempAssetCd = excelDto.getAssetCd();
                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);
                paramMap.put("SW_INFO", excelDto.getSwInfo());
                paramMap.put("SW_DIR", excelDto.getSwDir());
                paramMap.put("SW_USER", excelDto.getSwUser());
                paramMap.put("SW_ETC", excelDto.getSwEtc());

                if (StringUtils.equalsIgnoreCase("OS", swTypeStr.getSwType())) {

                    if (StringUtils.equalsIgnoreCase("Windows", swTypeStr.getSwNm())) {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", "Windows");
                    } else {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", excelDto.getSwNm());
                    }
                } else {

                    paramMap.put("SW_TYPE", swTypeStr.getSwType());
                    paramMap.remove("SW_NM");
                }
                paramMap.put("ASSET_CD", tempAssetCd);
                paramMap.put("FILE_TYPE", gAssetSwJob.get("FILE_TYPE"));

                SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetSwAuditReportNewGetMaxAuditDay(paramMap);

                if (reportDto1 != null) {
                    tempAuditDay = reportDto1.getAuditDay();

                    paramMap.put("AUDIT_DAY", tempAuditDay);
                    SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(paramMap);

                    paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
                    SnetAssetSwAuditReportDto reportDto3 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                    overAllMultiple += reportDto3.getCount();
                    reportDtoList.add(reportDto3);
                }
            }

            checkTargets = reportDtoList.size();


            String[] assetCdIn = new String[reportDtoList.size()];
            int a = 0;
            for (SnetAssetSwAuditReportDto assetCdDto : reportDtoList) {

                assetCdIn[a] = assetCdDto.getAssetCd();
                a++;
            }

            gAssetSwJob.put("assetCdIn", assetCdIn);

            String[] strArr = (String[]) gAssetSwJob.get("assetCdIn");

            // 점검항목수
            int itemTotal = 0;
            if (list7Re != null && list7Re.size() > 0) {
                gAssetSwJob.put("ASSET_CD", asset_cd);
                gAssetSwJob.put("AUDIT_DAY", auditDay);
                itemTotal = testMapper.getItemTotal(gAssetSwJob);

            } else {

                itemTotal = 0;
            }

            // 점검결과별 CNT
            int adResultOk = 0;
            int adResultNok = 0;
            int adResultNa = 0;
            int adResultPass = 0;
            int adResultReq = 0;

            //점검결과별 가중치합
            int adWeightOk = 0;
            int adWeightNok = 0;
            int adWeightNa = 0;
            int adWeightPass = 0;
            int adWeightReq = 0;

            if (assetCdIn != null && assetCdIn.length > 0) {

                for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

                    Map paramMap = new HashMap();
                    paramMap.put("startAuditDay", startAuditDay);
                    paramMap.put("endAuditDay", endAuditDay);
                    paramMap.put("SW_INFO", reportDto.getSwInfo());
                    paramMap.put("SW_DIR", reportDto.getSwDir());
                    paramMap.put("SW_USER", reportDto.getSwUser());
                    paramMap.put("SW_ETC", reportDto.getSwEtc());

                    if (StringUtils.equalsIgnoreCase("OS", swTypeStr.getSwType())) {

                        if (StringUtils.equalsIgnoreCase("Windows", swTypeStr.getSwNm())) {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", "Windows");
                        } else {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", reportDto.getSwNm());
                        }
                    } else {

                        paramMap.put("SW_TYPE", swTypeStr.getSwType());
                        paramMap.remove("SW_NM");
                    }
                    paramMap.put("ASSET_CD", reportDto.getAssetCd());
                    paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
                    paramMap.put("CREATE_DATE", reportDto.getCreateDate());
                    paramMap.put("FILE_TYPE", gAssetSwJob.get("FILE_TYPE"));

                    List<SnetAssetSwAuditReportDto> results = testMapper.getReportTot(paramMap);
                    for (SnetAssetSwAuditReportDto result : results) {
                        if (StringUtils.equals("T", result.getItemResult())) {
                            adResultOk += result.getResultCnt();
                            adWeightOk += result.getWeightTot();
                        } else if (StringUtils.equals("F", result.getItemResult())) {
                            adResultNok += result.getResultCnt();
                            adWeightNok += result.getWeightTot();
                        } else if (StringUtils.equals("C", result.getItemResult())) {
                            adResultPass += result.getResultCnt();
                            adWeightPass += result.getWeightTot();
                        } else if (StringUtils.equals("NA", result.getItemResult())) {
                            adResultNa += result.getResultCnt();
                            adWeightNa += result.getWeightTot();
                        } else if (StringUtils.equals("R", result.getItemResult())) {
                            adResultReq += result.getResultCnt();
                            adWeightReq += result.getWeightTot();
                        }
                    }
                }

            }

            log.debug("이행점검 T: " + adResultOk + ", F: " + adResultNok + ", Req: " + adResultReq + ", C: " + adResultPass);
            log.debug("이행점검 (ITEM_GRADE * CNT)  T:" + adWeightOk + ", F: " + adWeightNok + ", Req: " + adWeightReq + ", C: " + adWeightPass);

            //보안준수율
            double auditRate = ((double) adWeightOk / (double) (adWeightOk + adWeightNok + adWeightReq)) * 100;        //  보안준수율 = T  /  T + F + R
            auditRate = Math.round(auditRate * 100d) / 100d;        // 소수 둘째 자리


            String keyStr = null;
            if (StringUtils.equalsIgnoreCase("OS", swTypeStr.getSwType())) {

                if (StringUtils.equalsIgnoreCase("Windows", swTypeStr.getSwNm())) {

                    keyStr = "Windows";
                } else {

                    keyStr = "Linux";
                }
            } else {

                keyStr = swTypeStr.getSwType();
            }

            totalOverallResultDto.setKey(keyStr);
            totalOverallResultDto.setValue1(checkTargets);
            totalOverallResultDto.setValue2(itemTotal);
            totalOverallResultDto.setValue3(overAllMultiple);
            totalOverallResultDto.setValue4(adResultOk);       //양호
            totalOverallResultDto.setValue5(adResultNok);     //취약
            totalOverallResultDto.setValue6(adResultNa);      //NA
            totalOverallResultDto.setValue7(adResultPass);  //불가
            totalOverallResultDto.setValue8(adResultReq);  //인터뷰필요
            totalOverallResultDto.setAuditRate(auditRate); //보안준수율
            totalOverallResultDto.setMinAuditDay(startAuditDay);
            totalOverallResultDto.setMaxAuditDay(endAuditDay);
            totalOverallResultDto.setAssetCdIn(assetCdIn);
            totalOverallResultDto.setSwNm(gAssetSwJob.get("SW_NM") != null ? gAssetSwJob.get("SW_NM").toString() : null);

            gAssetSwJob.remove("SW_NM");
            gAssetSwJob.remove("assetCdIn");

            list.add(totalOverallResultDto);
        }


        String[] listArr = {"Linux", "Windows", "DB", "WEB", "WAS", "NW", "URL"};

        //        List<Map> strListMap = Lists.newArrayList();
        List<TotalOverallResultDto> resultDtoList = Lists.newArrayList();
        for (String strArr : listArr) {

//            Map strMap = new HashMap();
            TotalOverallResultDto resultDto = new TotalOverallResultDto();

            listAddFlag = list.stream().anyMatch(x -> StringUtils.equalsIgnoreCase(x.getKey(), strArr));

            if (StringUtils.equalsIgnoreCase("Linux", strArr) && listAddFlag) {

//                strMap.put("Linux", list.stream().filter(x -> x.getKey().equals("Linux")).findFirst().orElse(null));
                resultDto = list.stream().filter(x -> x.getKey().equals("Linux")).findFirst().orElse(null);
                resultDtoList.add(resultDto);
            } else if (StringUtils.equalsIgnoreCase("Linux", strArr) && listAddFlag == false) {

                TotalOverallResultDto tempDto = new TotalOverallResultDto();
                tempDto.setKey(strArr);
                tempDto.setValue1(0);
                tempDto.setValue2(0);
                tempDto.setValue3(0);
                tempDto.setValue4(0);
                tempDto.setValue5(0);
                tempDto.setValue6(0);
//                strMap.put("Linux", tempDto);
                resultDtoList.add(tempDto);
            }

            if (StringUtils.equalsIgnoreCase("Windows", strArr) && listAddFlag) {

                resultDto = list.stream().filter(x -> x.getKey().equals("Windows")).findFirst().orElse(null);
                resultDtoList.add(resultDto);
            } else if (StringUtils.equalsIgnoreCase("Windows", strArr) && listAddFlag == false) {

                TotalOverallResultDto tempDto = new TotalOverallResultDto();
                tempDto.setKey(strArr);
                tempDto.setValue1(0);
                tempDto.setValue2(0);
                tempDto.setValue3(0);
                tempDto.setValue4(0);
                tempDto.setValue5(0);
                tempDto.setValue6(0);
                resultDtoList.add(tempDto);
            }

            if (StringUtils.equalsIgnoreCase("DB", strArr) && listAddFlag) {

                resultDto = list.stream().filter(x -> x.getKey().equals("DB")).findFirst().orElse(null);
                resultDtoList.add(resultDto);
            } else if (StringUtils.equalsIgnoreCase("DB", strArr) && listAddFlag == false) {

                TotalOverallResultDto tempDto = new TotalOverallResultDto();
                tempDto.setKey(strArr);
                tempDto.setValue1(0);
                tempDto.setValue2(0);
                tempDto.setValue3(0);
                tempDto.setValue4(0);
                tempDto.setValue5(0);
                tempDto.setValue6(0);
                resultDtoList.add(tempDto);
            }

            if (StringUtils.equalsIgnoreCase("WEB", strArr) && listAddFlag) {

                resultDto = list.stream().filter(x -> x.getKey().equals("WEB")).findFirst().orElse(null);
                resultDtoList.add(resultDto);
            } else if (StringUtils.equalsIgnoreCase("WEB", strArr) && listAddFlag == false) {

                TotalOverallResultDto tempDto = new TotalOverallResultDto();
                tempDto.setKey(strArr);
                tempDto.setValue1(0);
                tempDto.setValue2(0);
                tempDto.setValue3(0);
                tempDto.setValue4(0);
                tempDto.setValue5(0);
                tempDto.setValue6(0);
                resultDtoList.add(tempDto);
            }

            if (StringUtils.equalsIgnoreCase("WAS", strArr) && listAddFlag) {

                resultDto = list.stream().filter(x -> x.getKey().equals("WAS")).findFirst().orElse(null);
                resultDtoList.add(resultDto);
            } else if (StringUtils.equalsIgnoreCase("WAS", strArr) && listAddFlag == false) {

                TotalOverallResultDto tempDto = new TotalOverallResultDto();
                tempDto.setKey(strArr);
                tempDto.setValue1(0);
                tempDto.setValue2(0);
                tempDto.setValue3(0);
                tempDto.setValue4(0);
                tempDto.setValue5(0);
                tempDto.setValue6(0);
                resultDtoList.add(tempDto);
            }

            if (StringUtils.equalsIgnoreCase("NW", strArr) && listAddFlag) {

                resultDto = list.stream().filter(x -> x.getKey().equals("NW")).findFirst().orElse(null);
                resultDtoList.add(resultDto);
            } else if (StringUtils.equalsIgnoreCase("NW", strArr) && listAddFlag == false) {

                TotalOverallResultDto tempDto = new TotalOverallResultDto();
                tempDto.setKey(strArr);
                tempDto.setValue1(0);
                tempDto.setValue2(0);
                tempDto.setValue3(0);
                tempDto.setValue4(0);
                tempDto.setValue5(0);
                tempDto.setValue6(0);
                resultDtoList.add(tempDto);
            }

            if (StringUtils.equalsIgnoreCase("URL", strArr) && listAddFlag) {

                resultDto = list.stream().filter(x -> x.getKey().equals("URL")).findFirst().orElse(null);
                resultDto.setKey("Web App(URL)");
                resultDtoList.add(resultDto);
            } else if (StringUtils.equalsIgnoreCase("URL", strArr) && listAddFlag == false) {

                TotalOverallResultDto tempDto = new TotalOverallResultDto();
                tempDto.setKey("Web App(URL)");
                tempDto.setValue1(0);
                tempDto.setValue2(0);
                tempDto.setValue3(0);
                tempDto.setValue4(0);
                tempDto.setValue5(0);
                tempDto.setValue6(0);
                resultDtoList.add(tempDto);
            }
        }

        // 기타 추가

        List<String> strListArr = Lists.newArrayList();
        boolean strListArrFlag = false;
        for (TotalOverallResultDto totalOverallResultDto : resultDtoList) {
            strListArr.add(totalOverallResultDto.getKey());
        }


        // 기타 추가
        if (etcAddFlag && StringUtils.isNotEmpty(etcSwTypeStr)) {

            TotalOverallResultDto totalOverallResultDto = new TotalOverallResultDto();

            // 점검수량
            if (StringUtils.equalsIgnoreCase("Linux", etcSwTypeStr) || StringUtils.equalsIgnoreCase("Windows", etcSwTypeStr)) {

                gAssetSwJob.put("SW_TYPE", "OS");
                gAssetSwJob.put("SW_NM", etcSwTypeStr);
            } else {

                gAssetSwJob.put("SW_TYPE", etcSwTypeStr);
            }


            int checkTargets = 0;
            int overAllMultiple = 0;
            gAssetSwJob.put("HOST_GRD_FLAG", "Y");
            List<SnetAssetSwAuditExcelDto> list7Re = testMapper.selectAssetSwExcelListNew(gAssetSwJob);
            gAssetSwJob.remove("HOST_GRD_FLAG");
            gAssetSwJob.remove("LNOT_FLAG");

            List<SnetAssetSwAuditReportDto> reportDtoList = Lists.newArrayList();
            String tempAssetCd = null;
            String tempAuditDay = null;
            String swTypeNm = null;
            for (SnetAssetSwAuditExcelDto excelDto : list7Re) {

                tempAssetCd = excelDto.getAssetCd();
                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);
                paramMap.put("SW_INFO", excelDto.getSwInfo());
                paramMap.put("SW_DIR", excelDto.getSwDir());
                paramMap.put("SW_USER", excelDto.getSwUser());
                paramMap.put("SW_ETC", excelDto.getSwEtc());

                if (StringUtils.equalsIgnoreCase("Linux", etcSwTypeStr) || StringUtils.equalsIgnoreCase("Windows", etcSwTypeStr)) {

                    swTypeNm = etcSwTypeStr;
                    paramMap.put("SW_TYPE", "OS");
                    paramMap.put("SW_NM", swTypeNm);
                } else {

                    paramMap.put("SW_TYPE", etcSwTypeStr);
                    paramMap.remove("SW_NM");
                }
                paramMap.put("ASSET_CD", tempAssetCd);
                paramMap.put("FILE_TYPE", gAssetSwJob.get("FILE_TYPE"));
                if (gAssetSwJob.get("SW_DIR") != null) {
                    paramMap.put("SW_DIR", gAssetSwJob.get("SW_DIR"));
                }

                SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetSwAuditReportNewGetMaxAuditDay(paramMap);

                tempAuditDay = reportDto1.getAuditDay();

                paramMap.put("AUDIT_DAY", tempAuditDay);
                SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                overAllMultiple += reportDto2.getCount();
                reportDtoList.add(reportDto2);
            }
            checkTargets = reportDtoList.size();

            String[] assetCdIn = new String[list7Re.size()];
            int a = 0;
            for (SnetAssetSwAuditExcelDto assetCdDto : list7Re) {

                assetCdIn[a] = assetCdDto.getAssetCd();
                a++;
            }
            gAssetSwJob.put("assetCdIn", assetCdIn);

            // 상세보고서 보안점검을 위한 최종진단일
            SnetAssetSwAuditExcelDto snetAssetSwAuditExcelDto = testMapper.selectAssetSwExcelGroupbyAuditday(gAssetSwJob);

            SnetAssetSwAuditExcelDto snetAssetSwAuditExcelDto2 = testMapper.selectAssetSwExcelGroupbyMaxAuditday(gAssetSwJob);

            auditDay = snetAssetSwAuditExcelDto.getMinAuditDay();
            asset_cd = snetAssetSwAuditExcelDto.getAssetCd();
            startAuditDay = snetAssetSwAuditExcelDto.getMinAuditDay();
            endAuditDay = snetAssetSwAuditExcelDto2.getMaxAuditDay();

            // 점검항목수
            int itemTotal = 0;
            if (list7Re != null && list7Re.size() > 0) {
                gAssetSwJob.put("ASSET_CD", asset_cd);
                gAssetSwJob.put("AUDIT_DAY", auditDay);
                itemTotal = testMapper.getItemTotal(gAssetSwJob);

            } else {

                itemTotal = 0;
            }

            // 점검결과별 CNT
            int adResultOk = 0;
            int adResultNok = 0;
            int adResultNa = 0;
            int adResultPass = 0;
            int adResultReq = 0;

            //점검결과별 가중치합
            int adWeightOk = 0;
            int adWeightNok = 0;
            int adWeightNa = 0;
            int adWeightPass = 0;
            int adWeightReq = 0;

            if (assetCdIn != null && assetCdIn.length > 0) {

                for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

                    Map paramMap = new HashMap();
                    paramMap.put("startAuditDay", startAuditDay);
                    paramMap.put("endAuditDay", endAuditDay);

                    if (StringUtils.equalsIgnoreCase("Linux", etcSwTypeStr) || StringUtils.equalsIgnoreCase("Windows", etcSwTypeStr)) {

                        swTypeNm = etcSwTypeStr;
                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", swTypeNm);
                    } else {

                        paramMap.put("SW_TYPE", etcSwTypeStr);
                        paramMap.remove("SW_NM");
                    }
                    paramMap.put("ASSET_CD", reportDto.getAssetCd());
                    paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
                    paramMap.put("FILE_TYPE", gAssetSwJob.get("FILE_TYPE"));

                    List<SnetAssetSwAuditReportDto> results = testMapper.getReportTot(paramMap);
                    for (SnetAssetSwAuditReportDto result : results) {
                        if (StringUtils.equals("T", result.getItemResult())) {
                            adResultOk += result.getResultCnt();
                            adWeightOk += result.getWeightTot();
                        } else if (StringUtils.equals("F", result.getItemResult())) {
                            adResultNok += result.getResultCnt();
                            adWeightNok += result.getWeightTot();
                        } else if (StringUtils.equals("C", result.getItemResult())) {
                            adResultPass += result.getResultCnt();
                            adWeightPass += result.getWeightTot();
                        } else if (StringUtils.equals("NA", result.getItemResult())) {
                            adResultNa += result.getResultCnt();
                            adWeightNa += result.getWeightTot();
                        } else if (StringUtils.equals("R", result.getItemResult())) {
                            adResultReq += result.getResultCnt();
                            adWeightReq += result.getWeightTot();
                        }
                    }
                }
            }

            //보안준수율
            double auditRate = ((double) adWeightOk / (double) (adWeightOk + adWeightNok + adWeightReq)) * 100;        //  보안준수율 = T  /  T + F + R
            auditRate = Math.round(auditRate * 100d) / 100d;        // 소수 둘째 자리

            totalOverallResultDto.setKey("Etc");
            totalOverallResultDto.setValue1(checkTargets);
            totalOverallResultDto.setValue2(itemTotal);
            totalOverallResultDto.setValue3(overAllMultiple);
            totalOverallResultDto.setValue4(adResultOk);
            totalOverallResultDto.setValue5(adResultNok);
            totalOverallResultDto.setValue6(adResultNa);
            totalOverallResultDto.setValue7(adResultPass);
            totalOverallResultDto.setValue8(adResultReq);
            totalOverallResultDto.setAuditRate(auditRate); //보안준수율
            totalOverallResultDto.setMinAuditDay(startAuditDay);
            totalOverallResultDto.setMaxAuditDay(endAuditDay);
            totalOverallResultDto.setAssetCdIn(assetCdIn);
            totalOverallResultDto.setSwNm(gAssetSwJob.get("SW_NM") != null ? gAssetSwJob.get("SW_NM").toString() : null);

            gAssetSwJob.remove("SW_NM");
            gAssetSwJob.remove("assetCdIn");

            list.add(totalOverallResultDto);
        }

        String finalEtcSwTypeStr = etcSwTypeStr;
        strListArrFlag = resultDtoList.stream().anyMatch(x -> StringUtils.equalsIgnoreCase(x.getKey(), finalEtcSwTypeStr));


        if (!strListArrFlag && StringUtils.isNotEmpty(etcSwTypeStr)) {

            TotalOverallResultDto resultDto = new TotalOverallResultDto();
            resultDto = list.stream().filter(x -> x.getKey().equals("Etc")).findFirst().orElse(null);
            resultDto.setKey("기타");
            resultDtoList.add(resultDto);
        } else {

            TotalOverallResultDto tempDto = new TotalOverallResultDto();
            tempDto.setKey("기타");
            tempDto.setValue1(0);
            tempDto.setValue2(0);
            tempDto.setValue3(0);
            tempDto.setValue4(0);
            tempDto.setValue5(0);
            tempDto.setValue6(0);
            resultDtoList.add(tempDto);
        }


        return resultDtoList;
    } // end method

    @Override
    public List<DetailResultDto> getDetailEtcTargetsList(List<TotalOverallResultDto> resultDtos, Map gAssetSwJob, String swTypeStr) throws Exception {

        if (StringUtils.equalsIgnoreCase("Web App(URL)", swTypeStr)) {

            swTypeStr = "URL";
        } else {

            swTypeStr = "URL";
        }

        List<DetailResultDto> list = Lists.newArrayList();

        String swTypeStrTemp = swTypeStr;
        String startAuditDay = gAssetSwJob.get("startAuditDay").toString();
        String endAuditDay = gAssetSwJob.get("endAuditDay").toString();

        TotalOverallResultDto totalOverallResultDto = new TotalOverallResultDto();

        // 종합보고서 전체 장비 리스트
        String swTypeNm = null;

        swTypeNm = swTypeStr;
        gAssetSwJob.put("SW_TYPE", swTypeStr);
        gAssetSwJob.remove("SW_NM");

        gAssetSwJob.put("HOST_GRD_FLAG", "Y");
        List<SnetAssetSwAuditExcelDto> list7Re = testMapper.selectAssetSwExcelListNew(gAssetSwJob);
        gAssetSwJob.remove("HOST_GRD_FLAG");

        String assetCd = null;
        String auditDay = null;
        String[] assetCdIn = new String[list7Re.size()];

        List<SnetAssetSwAuditReportDto> reportDtoList = Lists.newArrayList();

        String tempAssetCd = null;
        String tempAuditDay = null;
        for (SnetAssetSwAuditExcelDto excelDto : list7Re) {

            tempAssetCd = excelDto.getAssetCd();
            Map paramMap = new HashMap();
            paramMap.put("startAuditDay", startAuditDay);
            paramMap.put("endAuditDay", endAuditDay);

            paramMap.put("SW_TYPE", swTypeStr);
            paramMap.put("ASSET_CD", tempAssetCd);
            paramMap.put("FILE_TYPE", gAssetSwJob.get("auditType"));

            SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetSwAuditReportNewGetMinAuditDay(paramMap);

            if (reportDto1 != null) {
                tempAuditDay = reportDto1.getAuditDay();
                paramMap.put("AUDIT_DAY", tempAuditDay);

                SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());

                SnetAssetSwAuditReportDto reportDto3 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                reportDtoList.add(reportDto3);
            }
        } // end for

        reportDtoList.sort(Comparator.comparing(SnetAssetSwAuditReportDto::getAuditDay));
        totalOverallResultDto.setKey(swTypeStr);

        // select SNET_ASSET_SW_AUDIT_REPORT
        int grpNmLen = 0;
        List<List<SnetAssetSwAuditReportDto>> groupbyReportGrpNmLists = Lists.newArrayList();
        for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

            Map paramMap = new HashMap();
            paramMap.put("startAuditDay", startAuditDay);
            paramMap.put("endAuditDay", endAuditDay);

            paramMap.put("SW_TYPE", swTypeStrTemp);
            paramMap.put("ASSET_CD", reportDto.getAssetCd());
            paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
            paramMap.put("CREATE_DATE", reportDto.getCreateDate());

            List<SnetAssetSwAuditReportDto> groupbyReportGrpNmList = testMapper.getGroupbyReportGrpNmListGet(paramMap);
            groupbyReportGrpNmLists.add(groupbyReportGrpNmList);
        } // end for

        // 점검수량
        int checkTargets = 0;
        checkTargets = reportDtoList.size();

        boolean itemGrpNmFlag = false;

        // 분류항목별 점검항목수 전체 기준
        int tempWeekCnt = 0;
        int dotIndexOf = 0;
        String substrGrpNm = null;
        List<SnetAssetSwAuditReportDto> hiddenList3 = Lists.newArrayList();
        List<SnetAssetSwAuditReportDto> hiddenList4 = Lists.newArrayList();

        for (List<SnetAssetSwAuditReportDto> reportDtos : groupbyReportGrpNmLists) {

            TreeMap<String, List<SnetAssetSwAuditReportDto>> groupbyReportGrpNm =
                    reportDtos.stream().collect(Collectors.groupingBy(SnetAssetSwAuditReportDto::getItemGrpNm, TreeMap::new, Collectors.toList()));

            for (Map.Entry<String, List<SnetAssetSwAuditReportDto>> grpNmVo : groupbyReportGrpNm.entrySet()) {

                SnetAssetSwAuditReportDto hiddenDto3 = new SnetAssetSwAuditReportDto();

                for (SnetAssetSwAuditReportDto dto3 : grpNmVo.getValue()) {

                    if (StringUtils.equals("F", dto3.getItemResult())) {

                        tempWeekCnt = dto3.getWeakCount();
                        dotIndexOf = StringUtils.indexOf(dto3.getItemGrpNm(), ".");

                        if (dotIndexOf > -1) {
                            substrGrpNm = StringUtils.substring(dto3.getItemGrpNm(), dotIndexOf + 1);
                        } else {
                            substrGrpNm = dto3.getItemGrpNm();
                        }

                        substrGrpNm = StringUtils.deleteWhitespace(substrGrpNm);
                    } else {

                        tempWeekCnt = 0;
                        dotIndexOf = StringUtils.indexOf(dto3.getItemGrpNm(), ".");

                        if (dotIndexOf > -1) {
                            substrGrpNm = StringUtils.substring(dto3.getItemGrpNm(), dotIndexOf + 1);
                        } else {
                            substrGrpNm = dto3.getItemGrpNm();
                            itemGrpNmFlag = true;
                        }

                        substrGrpNm = StringUtils.deleteWhitespace(substrGrpNm);
                    }
                } // end for

                hiddenDto3.setWeakCount(tempWeekCnt);
                hiddenDto3.setItemGrpNm(substrGrpNm);

                hiddenList3.add(hiddenDto3);
            } // end for
        } // end for

        TreeMap<String, List<SnetAssetSwAuditReportDto>> groupbyReportGrpNm2 =
                hiddenList3.stream().collect(Collectors.groupingBy(SnetAssetSwAuditReportDto::getItemGrpNm, TreeMap::new, Collectors.toList()));
        for (Map.Entry<String, List<SnetAssetSwAuditReportDto>> grpNmVo2 : groupbyReportGrpNm2.entrySet()) {

            int tempWeekCount = 0;
            SnetAssetSwAuditReportDto hiddenDto4 = new SnetAssetSwAuditReportDto();

            String itemGrpNm = grpNmVo2.getKey();

            for (SnetAssetSwAuditReportDto reportDto2 : hiddenList3) {

                String itemGrpNm2 = reportDto2.getItemGrpNm();
                if (StringUtils.equalsIgnoreCase(itemGrpNm, itemGrpNm2)) {

                    tempWeekCount += reportDto2.getWeakCount();
                }
            }
            hiddenDto4.setItemGrpNm(itemGrpNm);
            hiddenDto4.setWeakCount(tempWeekCount);
            hiddenList4.add(hiddenDto4);
            grpNmLen++;
        }

//        hiddenList4.sort(Comparator.comparing(SnetAssetSwAuditReportDto::getWeakCount).reversed());

        int weekHighCount = 0;
        int weekMiddleCount = 0;
        int weekLowCount = 0;
        int weekTotalCount = 0;

        List<ResultDto> detailResultList = Lists.newArrayList();
        List<ResultDto> detailResultList1 = Lists.newArrayList();
        List<ResultDto> detailResultList2 = Lists.newArrayList();
        List<ResultDto> detailResultList3 = Lists.newArrayList();
        List<ResultDto> detailResultList4 = Lists.newArrayList();
        List<ResultDto> detailResultList5 = Lists.newArrayList();
        List<ResultDto> detailResultList6 = Lists.newArrayList();
        List<List> doubleList = Lists.newArrayList();
        List<List> doubleList1 = Lists.newArrayList();
        List<List> doubleList2 = Lists.newArrayList();
        List<List> doubleList3 = Lists.newArrayList();
        List<List> doubleList4 = Lists.newArrayList();
        List<List> doubleList5 = Lists.newArrayList();
        List<List> doubleList6 = Lists.newArrayList();

        int totalHighCount = 0;
        int totalMiddleCount = 0;
        int totalLowCount = 0;

        int normalHighCount = 0;
        int normalMiddleCount = 0;
        int normalLowCount = 0;

        int disableHighCount = 0;
        int disableMiddleCount = 0;
        int disableLowCount = 0;

        int interviewHighCount = 0;
        int interviewMiddleCount = 0;
        int interviewLowCount = 0;

        int weekCountSum = 0;

        // 전체
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());

                paramMap.put("SW_TYPE", swTypeStrTemp);

                detailResultList = testMapper.getDetailResultList(paramMap);

                paramMap.remove("ITEM_GRP_NM");
                if (detailResultList != null && detailResultList.size() > 0) {

                    doubleList.add(detailResultList);
                }
            }
        } // end for 0

        // 양호
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());

                paramMap.put("SW_TYPE", swTypeStrTemp);
                paramMap.put("T_FLAG", "Y");

                detailResultList1 = testMapper.getDetailResultList(paramMap);

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("T_FLAG");
                if (detailResultList1 != null && detailResultList1.size() > 0) {

                    doubleList1.add(detailResultList1);
                }
            }
        } // end for 1

        // 취약
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());

                paramMap.put("SW_TYPE", swTypeStrTemp);
                paramMap.put("F_FLAG", "Y");

                detailResultList2 = testMapper.getDetailResultList(paramMap);

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("F_FLAG");
                if (detailResultList2 != null && detailResultList2.size() > 0) {

                    doubleList2.add(detailResultList2);
                }
            }
        } // end for 2

        // 불가
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());

                paramMap.put("SW_TYPE", swTypeStrTemp);
                paramMap.put("C_FLAG", "Y");

                detailResultList3 = testMapper.getDetailResultList(paramMap);

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("C_FLAG");
                if (detailResultList3 != null && detailResultList3.size() > 0) {

                    doubleList3.add(detailResultList3);
                }
            }
        } // end for 3

        // 인터뷰필요
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());

                paramMap.put("SW_TYPE", swTypeStrTemp);
                paramMap.put("R_FLAG", "Y");

                detailResultList4 = testMapper.getDetailResultList(paramMap);

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("R_FLAG");
                if (detailResultList4 != null && detailResultList4.size() > 0) {

                    doubleList4.add(detailResultList4);
                }
            }
        } // end for 4

        /*
           key; // 분류항목
           value1; // 점검수량
           value2; // 개별항목수
           value3; // 전체항목수
           value7; // 취약(F)
                ITEM_RESULT
                - T: 양호
                - F: 취약
                - C: 불가 (=위험수용)
                - R: 인터뷰 필요
                - NA : N/A
           value8; // 전체종합결과 보안준수율
           value9; // 항목 코드
         */

        // 웹UI 보안준수율
        double value8 = 0L;

        value8 = list7Re.stream()
                .filter(x -> x.getAuditRate() > 0)
                .mapToDouble(x -> x.getAuditRate())
                .average()
                .orElseGet(() -> 0.0);

        value8 = value8 * 100;

        for (SnetAssetSwAuditReportDto resultReportDto : hiddenList4) {

            DetailResultDto vo = new DetailResultDto();

            String str = resultReportDto.getItemGrpNm();
            vo.setKey(str);
            vo.setValue1(checkTargets);
            vo.setValue8(value8);

            for (List<ResultDto> resultDtoList : doubleList) {

                for (ResultDto resultDto : resultDtoList) {
                    if (StringUtils.equals(str, resultDto.getGrpname())) {
                        weekCountSum += resultDto.getCount();
                        vo.setValue2(weekCountSum);
                        vo.setValue3(weekCountSum * checkTargets);
                        vo.setValue9(StringUtils.mid(resultDto.getItemCode(), 4,2));
                    }
                }
            } // end for 1
            // value26 -> 보안점검 보안준수율

            // 전체 (NA제외)
            for (List<ResultDto> resultDtoList : doubleList) {

                for (ResultDto resultDto : resultDtoList) {

                    if (StringUtils.equals(str, resultDto.getGrpname())) {

                        if (StringUtils.equals("H", resultDto.getName())) {

                            totalHighCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("M", resultDto.getName())) {

                            totalMiddleCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("L", resultDto.getName())) {

                            totalLowCount += resultDto.getCount();
                        }
                    }
                }
            } // end for 1

            // 양호
            for (List<ResultDto> resultDtoList : doubleList1) {

                for (ResultDto resultDto : resultDtoList) {

                    if (StringUtils.equals(str, resultDto.getGrpname())) {

                        if (StringUtils.equals("H", resultDto.getName())) {

                            normalHighCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("M", resultDto.getName())) {

                            normalMiddleCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("L", resultDto.getName())) {

                            normalLowCount += resultDto.getCount();
                        }
                    }
                }
            } // end for 1

            // 취약
            for (List<ResultDto> resultDtoList : doubleList2) {

                for (ResultDto resultDto : resultDtoList) {

                    if (StringUtils.equals(str, resultDto.getGrpname())) {

                        if (StringUtils.equals("H", resultDto.getName())) {

                            weekHighCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("M", resultDto.getName())) {

                            weekMiddleCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("L", resultDto.getName())) {

                            weekLowCount += resultDto.getCount();
                        }

                        vo.setValue4(weekHighCount);
                        vo.setValue5(weekMiddleCount);
                        vo.setValue6(weekLowCount);

                        weekTotalCount = weekHighCount + weekMiddleCount + weekLowCount;
                        vo.setValue7(weekTotalCount);

                    } // end if else
                } // end for
            } // end for 2

            // 불가
            for (List<ResultDto> resultDtoList : doubleList3) {

                for (ResultDto resultDto : resultDtoList) {

                    if (StringUtils.equals(str, resultDto.getGrpname())) {

                        if (StringUtils.equals("H", resultDto.getName())) {

                            disableHighCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("M", resultDto.getName())) {

                            disableMiddleCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("L", resultDto.getName())) {

                            disableLowCount += resultDto.getCount();
                        }
                    }
                }
            } // end for 3

            // 인터뷰필요
            for (List<ResultDto> resultDtoList : doubleList4) {

                for (ResultDto resultDto : resultDtoList) {

                    if (StringUtils.equals(str, resultDto.getGrpname())) {

                        if (StringUtils.equals("H", resultDto.getName())) {

                            interviewHighCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("M", resultDto.getName())) {

                            interviewMiddleCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("L", resultDto.getName())) {

                            interviewLowCount += resultDto.getCount();
                        }
                    }
                }
            } // end for 4

            double sCheckAuditRateResult = 0.0;
            double sCheckAuditRate1 = 0.0;
            double sCheckAuditRate2 = 0.0;
            double sCheckAuditRate3 = 0.0;
            double sCheckAuditRate4 = 0.0;
            // 보안준수율 = (양호개수 * 중요도) / ((양호개수 * 중요도) + (취약개수 * 중요도) + (불가개수 * 중요도) + (인터뷰필요개수 * 중요도) ) * 100

            sCheckAuditRate1 = (normalHighCount * 3) + (normalMiddleCount * 2) + (normalLowCount * 1);
            sCheckAuditRate2 = (weekHighCount * 3) + (weekMiddleCount * 2) + (weekLowCount * 1);
            sCheckAuditRate3 = (disableHighCount * 3) + (disableMiddleCount * 2) + (disableLowCount * 1);
            sCheckAuditRate4 = (interviewHighCount * 3) + (interviewMiddleCount * 2) + (interviewLowCount * 1);

            sCheckAuditRateResult = sCheckAuditRate1 / (sCheckAuditRate1 + sCheckAuditRate2 + sCheckAuditRate4);
            sCheckAuditRateResult = sCheckAuditRateResult * 100;
            sCheckAuditRateResult = Math.round(sCheckAuditRateResult * 100d) / 100d; // 소수 둘째 자리

            if (Double.isNaN(sCheckAuditRateResult)) {
                sCheckAuditRateResult = 0;
            }

            // 분류항목별 보안점검 보안준수율
            vo.setValue26(sCheckAuditRateResult);

            list.add(vo);

            weekCountSum = 0;

            totalHighCount = 0;
            totalMiddleCount = 0;
            totalLowCount = 0;

            weekHighCount = 0;
            weekMiddleCount = 0;
            weekLowCount = 0;

            normalHighCount = 0;
            normalMiddleCount = 0;
            normalLowCount = 0;

            disableHighCount = 0;
            disableMiddleCount = 0;
            disableLowCount = 0;

            interviewHighCount = 0;
            interviewMiddleCount = 0;
            interviewLowCount = 0;

        } // end for

        // 항목 코드로 오름차순 정렬
        list.sort(Comparator.comparing(DetailResultDto::getValue9));

        return list;
    } // end method

    @Override
    public List<DetailResultDto> getMigDetailEtcTargetsList(List<TotalOverallResultDto> resultDtos, Map gAssetSwJob, String swTypeStr) throws Exception {

        if (StringUtils.equalsIgnoreCase("Web App(URL)", swTypeStr)) {

            swTypeStr = "URL";
        } else {
            swTypeStr = "URL";
        }

        List<DetailResultDto> list = Lists.newArrayList();

        String swTypeStrTemp = swTypeStr;
        String startAuditDay = gAssetSwJob.get("startAuditDay").toString();
        String endAuditDay = gAssetSwJob.get("endAuditDay").toString();

        TotalOverallResultDto totalOverallResultDto = new TotalOverallResultDto();

        // 종합보고서 전체 장비 리스트
        String swTypeNm = null;

        swTypeNm = swTypeStr;
        gAssetSwJob.put("SW_TYPE", swTypeStr);
        gAssetSwJob.remove("SW_NM");

        gAssetSwJob.put("HOST_GRD_FLAG", "Y");
        List<SnetAssetSwAuditExcelDto> list7Re = testMapper.selectAssetSwExcelListNew(gAssetSwJob);
        gAssetSwJob.remove("HOST_GRD_FLAG");

        String assetCd = null;
        String auditDay = null;
        String[] assetCdIn = new String[list7Re.size()];

        List<SnetAssetSwAuditReportDto> reportDtoList = Lists.newArrayList();

        String tempAssetCd = null;
        String tempAuditDay = null;
        for (SnetAssetSwAuditExcelDto excelDto : list7Re) {

            tempAssetCd = excelDto.getAssetCd();
            Map paramMap = new HashMap();
            paramMap.put("startAuditDay", startAuditDay);
            paramMap.put("endAuditDay", endAuditDay);

            paramMap.put("SW_TYPE", swTypeStr);
            paramMap.put("ASSET_CD", tempAssetCd);
            paramMap.put("FILE_TYPE", gAssetSwJob.get("auditType"));

            SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetSwAuditReportNewGetMaxAuditDay(paramMap);

            if (reportDto1 != null) {
                tempAuditDay = reportDto1.getAuditDay();
                paramMap.put("AUDIT_DAY", tempAuditDay);

                SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());

                SnetAssetSwAuditReportDto reportDto3 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                reportDtoList.add(reportDto3);
            }
        } // end for

        reportDtoList.sort(Comparator.comparing(SnetAssetSwAuditReportDto::getAuditDay));
        totalOverallResultDto.setKey(swTypeStr);

        // select SNET_ASSET_SW_AUDIT_REPORT
        int grpNmLen = 0;
        List<List<SnetAssetSwAuditReportDto>> groupbyReportGrpNmLists = Lists.newArrayList();
        for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

            Map paramMap = new HashMap();
            paramMap.put("startAuditDay", startAuditDay);
            paramMap.put("endAuditDay", endAuditDay);

            paramMap.put("SW_TYPE", swTypeStrTemp);
            paramMap.put("ASSET_CD", reportDto.getAssetCd());
            paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
            paramMap.put("CREATE_DATE", reportDto.getCreateDate());

            List<SnetAssetSwAuditReportDto> groupbyReportGrpNmList = testMapper.getGroupbyReportGrpNmListGet(paramMap);
            groupbyReportGrpNmLists.add(groupbyReportGrpNmList);
        } // end for

        // 점검수량
        int checkTargets = 0;
        checkTargets = reportDtoList.size();

        boolean itemGrpNmFlag = false;

        // 분류항목별 점검항목수 전체 기준
        int tempWeekCnt = 0;
        int dotIndexOf = 0;
        String substrGrpNm = null;
        List<SnetAssetSwAuditReportDto> hiddenList3 = Lists.newArrayList();
        List<SnetAssetSwAuditReportDto> hiddenList4 = Lists.newArrayList();

        for (List<SnetAssetSwAuditReportDto> reportDtos : groupbyReportGrpNmLists) {

            TreeMap<String, List<SnetAssetSwAuditReportDto>> groupbyReportGrpNm =
                    reportDtos.stream().collect(Collectors.groupingBy(SnetAssetSwAuditReportDto::getItemGrpNm, TreeMap::new, Collectors.toList()));

            for (Map.Entry<String, List<SnetAssetSwAuditReportDto>> grpNmVo : groupbyReportGrpNm.entrySet()) {

                SnetAssetSwAuditReportDto hiddenDto3 = new SnetAssetSwAuditReportDto();

                for (SnetAssetSwAuditReportDto dto3 : grpNmVo.getValue()) {

                    if (StringUtils.equals("F", dto3.getItemResult())) {

                        tempWeekCnt = dto3.getWeakCount();
                        dotIndexOf = StringUtils.indexOf(dto3.getItemGrpNm(), ".");

                        if (dotIndexOf > -1) {
                            substrGrpNm = StringUtils.substring(dto3.getItemGrpNm(), dotIndexOf + 1);
                        } else {
                            substrGrpNm = dto3.getItemGrpNm();
                        }

                        substrGrpNm = StringUtils.deleteWhitespace(substrGrpNm);
                    } else {

                        tempWeekCnt = 0;
                        dotIndexOf = StringUtils.indexOf(dto3.getItemGrpNm(), ".");

                        if (dotIndexOf > -1) {
                            substrGrpNm = StringUtils.substring(dto3.getItemGrpNm(), dotIndexOf + 1);
                        } else {
                            substrGrpNm = dto3.getItemGrpNm();
                            itemGrpNmFlag = true;
                        }

                        substrGrpNm = StringUtils.deleteWhitespace(substrGrpNm);
                    }
                } // end for

                hiddenDto3.setWeakCount(tempWeekCnt);
                hiddenDto3.setItemGrpNm(substrGrpNm);

                hiddenList3.add(hiddenDto3);
            } // end for
        } // end for

        TreeMap<String, List<SnetAssetSwAuditReportDto>> groupbyReportGrpNm2 =
                hiddenList3.stream().collect(Collectors.groupingBy(SnetAssetSwAuditReportDto::getItemGrpNm, TreeMap::new, Collectors.toList()));
        for (Map.Entry<String, List<SnetAssetSwAuditReportDto>> grpNmVo2 : groupbyReportGrpNm2.entrySet()) {

            int tempWeekCount = 0;
            SnetAssetSwAuditReportDto hiddenDto4 = new SnetAssetSwAuditReportDto();

            String itemGrpNm = grpNmVo2.getKey();

            for (SnetAssetSwAuditReportDto reportDto2 : hiddenList3) {

                String itemGrpNm2 = reportDto2.getItemGrpNm();
                if (StringUtils.equalsIgnoreCase(itemGrpNm, itemGrpNm2)) {

                    tempWeekCount += reportDto2.getWeakCount();
                }
            }
            hiddenDto4.setItemGrpNm(itemGrpNm);
            hiddenDto4.setWeakCount(tempWeekCount);
            hiddenList4.add(hiddenDto4);
            grpNmLen++;
        }

//        hiddenList4.sort(Comparator.comparing(SnetAssetSwAuditReportDto::getWeakCount).reversed());

        int weekHighCount = 0;
        int weekMiddleCount = 0;
        int weekLowCount = 0;
        int weekTotalCount = 0;

        List<ResultDto> detailResultList = Lists.newArrayList();
        List<ResultDto> detailResultList1 = Lists.newArrayList();
        List<ResultDto> detailResultList2 = Lists.newArrayList();
        List<ResultDto> detailResultList3 = Lists.newArrayList();
        List<ResultDto> detailResultList4 = Lists.newArrayList();
        List<ResultDto> detailResultList5 = Lists.newArrayList();
        List<ResultDto> detailResultList6 = Lists.newArrayList();
        List<List> doubleList = Lists.newArrayList();
        List<List> doubleList1 = Lists.newArrayList();
        List<List> doubleList2 = Lists.newArrayList();
        List<List> doubleList3 = Lists.newArrayList();
        List<List> doubleList4 = Lists.newArrayList();
        List<List> doubleList5 = Lists.newArrayList();
        List<List> doubleList6 = Lists.newArrayList();

        int totalHighCount = 0;
        int totalMiddleCount = 0;
        int totalLowCount = 0;

        int normalHighCount = 0;
        int normalMiddleCount = 0;
        int normalLowCount = 0;

        int disableHighCount = 0;
        int disableMiddleCount = 0;
        int disableLowCount = 0;

        int interviewHighCount = 0;
        int interviewMiddleCount = 0;
        int interviewLowCount = 0;

        int weekCountSum = 0;

        // 전체
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());

                paramMap.put("SW_TYPE", swTypeStrTemp);

                detailResultList = testMapper.getDetailResultList(paramMap);

                paramMap.remove("ITEM_GRP_NM");
                if (detailResultList != null && detailResultList.size() > 0) {

                    doubleList.add(detailResultList);
                }
            }
        } // end for 0

        // 양호
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());

                paramMap.put("SW_TYPE", swTypeStrTemp);
                paramMap.put("T_FLAG", "Y");

                detailResultList1 = testMapper.getDetailResultList(paramMap);

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("T_FLAG");
                if (detailResultList1 != null && detailResultList1.size() > 0) {

                    doubleList1.add(detailResultList1);
                }
            }
        } // end for 1

        // 취약
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());

                paramMap.put("SW_TYPE", swTypeStrTemp);
                paramMap.put("F_FLAG", "Y");

                detailResultList2 = testMapper.getDetailResultList(paramMap);

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("F_FLAG");
                if (detailResultList2 != null && detailResultList2.size() > 0) {

                    doubleList2.add(detailResultList2);
                }
            }
        } // end for 2

        // 불가
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());

                paramMap.put("SW_TYPE", swTypeStrTemp);
                paramMap.put("C_FLAG", "Y");

                detailResultList3 = testMapper.getDetailResultList(paramMap);

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("C_FLAG");
                if (detailResultList3 != null && detailResultList3.size() > 0) {

                    doubleList3.add(detailResultList3);
                }
            }
        } // end for 3

        // 인터뷰필요
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());

                paramMap.put("SW_TYPE", swTypeStrTemp);
                paramMap.put("R_FLAG", "Y");

                detailResultList4 = testMapper.getDetailResultList(paramMap);

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("R_FLAG");
                if (detailResultList4 != null && detailResultList4.size() > 0) {

                    doubleList4.add(detailResultList4);
                }
            }
        } // end for 4

        /*
           key; // 분류항목
           value1; // 점검수량
           value2; // 개별항목수
           value3; // 전체항목수
           value7; // 취약(F)
                ITEM_RESULT
                - T: 양호
                - F: 취약
                - C: 불가 (=위험수용)
                - R: 인터뷰 필요
                - NA : N/A
           value8; // 전체종합결과 보안준수율
           value9; // 항목 코드
         */

        // 웹UI 보안준수율
        double value8 = 0L;

        value8 = list7Re.stream()
                .filter(x -> x.getAuditRate() > 0)
                .mapToDouble(x -> x.getAuditRate())
                .average()
                .orElseGet(() -> 0.0);

        value8 = value8 * 100;

        for (SnetAssetSwAuditReportDto resultReportDto : hiddenList4) {

            DetailResultDto vo = new DetailResultDto();

            String str = resultReportDto.getItemGrpNm();
            vo.setKey(str);
            vo.setValue1(checkTargets);
            vo.setValue8(value8);

            for (List<ResultDto> resultDtoList : doubleList) {

                for (ResultDto resultDto : resultDtoList) {
                    if (StringUtils.equals(str, resultDto.getGrpname())) {
                        weekCountSum += resultDto.getCount();
                        vo.setValue2(weekCountSum);
                        vo.setValue3(weekCountSum * checkTargets);
                        vo.setValue9(StringUtils.mid(resultDto.getItemCode(), 4,2));
                    }
                }
            } // end for 1
            // value26 -> 보안점검 보안준수율

            // 전체 (NA제외)
            for (List<ResultDto> resultDtoList : doubleList) {

                for (ResultDto resultDto : resultDtoList) {

                    if (StringUtils.equals(str, resultDto.getGrpname())) {

                        if (StringUtils.equals("H", resultDto.getName())) {

                            totalHighCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("M", resultDto.getName())) {

                            totalMiddleCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("L", resultDto.getName())) {

                            totalLowCount += resultDto.getCount();
                        }
                    }
                }
            } // end for 1

            // 양호
            for (List<ResultDto> resultDtoList : doubleList1) {

                for (ResultDto resultDto : resultDtoList) {

                    if (StringUtils.equals(str, resultDto.getGrpname())) {

                        if (StringUtils.equals("H", resultDto.getName())) {

                            normalHighCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("M", resultDto.getName())) {

                            normalMiddleCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("L", resultDto.getName())) {

                            normalLowCount += resultDto.getCount();
                        }
                    }
                }
            } // end for 1

            // 취약
            for (List<ResultDto> resultDtoList : doubleList2) {

                for (ResultDto resultDto : resultDtoList) {

                    if (StringUtils.equals(str, resultDto.getGrpname())) {

                        if (StringUtils.equals("H", resultDto.getName())) {

                            weekHighCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("M", resultDto.getName())) {

                            weekMiddleCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("L", resultDto.getName())) {

                            weekLowCount += resultDto.getCount();
                        }

                        weekTotalCount = weekHighCount + weekMiddleCount + weekLowCount;
                        vo.setValue7(weekTotalCount);

                    } // end if else
                } // end for
            } // end for 2

            // 불가
            for (List<ResultDto> resultDtoList : doubleList3) {

                for (ResultDto resultDto : resultDtoList) {

                    if (StringUtils.equals(str, resultDto.getGrpname())) {

                        if (StringUtils.equals("H", resultDto.getName())) {

                            disableHighCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("M", resultDto.getName())) {

                            disableMiddleCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("L", resultDto.getName())) {

                            disableLowCount += resultDto.getCount();
                        }
                    }
                }
            } // end for 3

            // 인터뷰필요
            for (List<ResultDto> resultDtoList : doubleList4) {

                for (ResultDto resultDto : resultDtoList) {

                    if (StringUtils.equals(str, resultDto.getGrpname())) {

                        if (StringUtils.equals("H", resultDto.getName())) {

                            interviewHighCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("M", resultDto.getName())) {

                            interviewMiddleCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("L", resultDto.getName())) {

                            interviewLowCount += resultDto.getCount();
                        }
                    }
                }
            } // end for 4

            double sCheckAuditRateResult = 0.0;
            double sCheckAuditRate1 = 0.0;
            double sCheckAuditRate2 = 0.0;
            double sCheckAuditRate3 = 0.0;
            double sCheckAuditRate4 = 0.0;
            // 보안준수율 = (양호개수 * 중요도) / ((양호개수 * 중요도) + (취약개수 * 중요도) + (불가개수 * 중요도) + (인터뷰필요개수 * 중요도) ) * 100

            sCheckAuditRate1 = (normalHighCount * 3) + (normalMiddleCount * 2) + (normalLowCount * 1);
            sCheckAuditRate2 = (weekHighCount * 3) + (weekMiddleCount * 2) + (weekLowCount * 1);
            sCheckAuditRate3 = (disableHighCount * 3) + (disableMiddleCount * 2) + (disableLowCount * 1);
            sCheckAuditRate4 = (interviewHighCount * 3) + (interviewMiddleCount * 2) + (interviewLowCount * 1);

            sCheckAuditRateResult = sCheckAuditRate1 / (sCheckAuditRate1 + sCheckAuditRate2 + sCheckAuditRate4);
            sCheckAuditRateResult = sCheckAuditRateResult * 100;
            sCheckAuditRateResult = Math.round(sCheckAuditRateResult * 100d) / 100d; // 소수 둘째 자리

            if (Double.isNaN(sCheckAuditRateResult)) {
                sCheckAuditRateResult = 0;
            }

            // 분류항목별 이행점검 보안준수율
            vo.setValue27(sCheckAuditRateResult);

            list.add(vo);

            weekCountSum = 0;

            totalHighCount = 0;
            totalMiddleCount = 0;
            totalLowCount = 0;

            weekHighCount = 0;
            weekMiddleCount = 0;
            weekLowCount = 0;

            normalHighCount = 0;
            normalMiddleCount = 0;
            normalLowCount = 0;

            disableHighCount = 0;
            disableMiddleCount = 0;
            disableLowCount = 0;

            interviewHighCount = 0;
            interviewMiddleCount = 0;
            interviewLowCount = 0;

        } // end for

        // 항목 코드로 오름차순 정렬
        list.sort(Comparator.comparing(DetailResultDto::getValue9));

        return list;
    } // end method

    @Override
    public List<DetailResultDto> getDetailTargetsList(List<TotalOverallResultDto> resultDtos, Map gAssetSwJob, String swTypeStr, boolean notFlag) {

        List<DetailResultDto> list = Lists.newArrayList();
        String swTypeStrTemp = swTypeStr;
        String startAuditDay = gAssetSwJob.get("startAuditDay").toString();
        String endAuditDay = gAssetSwJob.get("endAuditDay").toString();

        TotalOverallResultDto totalOverallResultDto = new TotalOverallResultDto();

        // 종합보고서 전체 장비 리스트
        if (StringUtils.equalsIgnoreCase("Linux", swTypeStrTemp) || StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

            if (StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                gAssetSwJob.put("SW_TYPE", "OS");
                gAssetSwJob.put("SW_NM", "Windows");
            } else {

                gAssetSwJob.put("LNOT_FLAG", "Y");
            }
        } else {

            gAssetSwJob.put("SW_TYPE", swTypeStrTemp);
            gAssetSwJob.remove("SW_NM");
        }

        gAssetSwJob.put("HOST_GRD_FLAG", "Y");
        List<SnetAssetSwAuditExcelDto> list7Re = testMapper.selectAssetSwExcelListNew(gAssetSwJob);
        gAssetSwJob.remove("HOST_GRD_FLAG");

        String assetCd = null;
        String auditDay = null;
        String[] assetCdIn = new String[list7Re.size()];

        List<SnetAssetSwAuditReportDto> reportDtoList = Lists.newArrayList();

        if (notFlag) {

            String tempAssetCd = null;
            String tempAuditDay = null;
            for (SnetAssetSwAuditExcelDto excelDto : list7Re) {

                tempAssetCd = excelDto.getAssetCd();
                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                if (StringUtils.equalsIgnoreCase("Linux", swTypeStr) || StringUtils.equalsIgnoreCase("Windows", swTypeStr)) {

                    if (StringUtils.equalsIgnoreCase("Windows", swTypeStr)) {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", "Windows");
                    } else {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", excelDto.getSwNm());
                    }
                } else {

                    paramMap.put("SW_TYPE", swTypeStr);
                    paramMap.remove("SW_NM");
                }
                paramMap.put("ASSET_CD", tempAssetCd);
                paramMap.put("FILE_TYPE", gAssetSwJob.get("auditType"));
                paramMap.put("SW_DIR", excelDto.getSwDir());

                SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetSwAuditReportNewGetMinAuditDay(paramMap);

                if (reportDto1 != null) {
                    tempAuditDay = reportDto1.getAuditDay();

                    paramMap.put("AUDIT_DAY", tempAuditDay);

                    SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                    paramMap.put("CREATE_DATE", reportDto2.getCreateDate());

                    SnetAssetSwAuditReportDto reportDto3 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                    reportDtoList.add(reportDto3);
                }
            }
            reportDtoList.sort(Comparator.comparing(SnetAssetSwAuditReportDto::getAuditDay));
            totalOverallResultDto.setKey(swTypeStr);
        } else {

            gAssetSwJob.put("NOT_FLAG", "Y");
            gAssetSwJob.remove("SW_TYPE");
            gAssetSwJob.put("HOST_GRD_FLAG", "Y");
            List<SnetAssetSwAuditExcelDto> listEtc = testMapper.selectAssetSwExcelListNew(gAssetSwJob);
            gAssetSwJob.remove("HOST_GRD_FLAG");

            String tempAssetCd = null;
            String tempAuditDay = null;
            String tempSwType = null;
            for (SnetAssetSwAuditExcelDto excelDto : listEtc) {

                tempAssetCd = excelDto.getAssetCd();
                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("SW_TYPE", excelDto.getSwType());
                paramMap.put("ASSET_CD", tempAssetCd);
                paramMap.put("FILE_TYPE", gAssetSwJob.get("auditType"));

                SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetSwAuditReportNewGetMinAuditDay(paramMap);

                if (reportDto1 != null) {
                    tempAuditDay = reportDto1.getAuditDay();

                    paramMap.put("AUDIT_DAY", tempAuditDay);

                    SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                    paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
                    SnetAssetSwAuditReportDto reportDto3 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                    reportDtoList.add(reportDto3);
                }
            }
            reportDtoList.sort(Comparator.comparing(SnetAssetSwAuditReportDto::getAuditDay));
            totalOverallResultDto.setKey(swTypeStr);
        } // end if else

        // select SNET_ASSET_SW_AUDIT_REPORT
        int grpNmLen = 0;
        List<List<SnetAssetSwAuditReportDto>> groupbyReportGrpNmLists = Lists.newArrayList();
        for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

            Map paramMap = new HashMap();
            paramMap.put("startAuditDay", startAuditDay);
            paramMap.put("endAuditDay", endAuditDay);

            if (notFlag) {
                if (StringUtils.equalsIgnoreCase("Linux", swTypeStr) || StringUtils.equalsIgnoreCase("Windows", swTypeStr)) {

                    if (StringUtils.equalsIgnoreCase("Windows", swTypeStr)) {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", "Windows");
                    } else {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", reportDto.getSwNm());
                    }
                } else {

                    paramMap.put("SW_TYPE", reportDto.getSwType());
                    paramMap.remove("SW_NM");
                }
                paramMap.put("SW_DIR", reportDto.getSwDir());
            } else {
                paramMap.put("SW_TYPE", reportDto.getSwType());
            }
            paramMap.put("ASSET_CD", reportDto.getAssetCd());
            paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
            paramMap.put("CREATE_DATE", reportDto.getCreateDate());

            List<SnetAssetSwAuditReportDto> groupbyReportGrpNmList = testMapper.getGroupbyReportGrpNmListGet(paramMap);
            groupbyReportGrpNmLists.add(groupbyReportGrpNmList);
        } // end for

        // 점검수량
        int checkTargets = 0;
        checkTargets = reportDtoList.size();

        // search standardcount max
        List<SnetAssetSwAuditReportDto> reportGrpNmStandardLists = Lists.newArrayList();
        for (List<SnetAssetSwAuditReportDto> list2 : groupbyReportGrpNmLists) {

            int countSum = 0;
            SnetAssetSwAuditReportDto standardReporDto = new SnetAssetSwAuditReportDto();
            for (SnetAssetSwAuditReportDto list3 : list2) {

                countSum += list3.getWeakCount();

                if (notFlag) {
                    standardReporDto.setSwType(list3.getSwType());
                    standardReporDto.setSwNm(list3.getSwNm());
                    standardReporDto.setAssetCd(list3.getAssetCd());
                    standardReporDto.setAuditDay(list3.getAuditDay());
                    standardReporDto.setCreateDate(list3.getCreateDate());
                } else {
                    standardReporDto.setSwType(list3.getSwType());
                    standardReporDto.setSwNm(list3.getSwNm());
                    standardReporDto.setAssetCd(list3.getAssetCd());
                    standardReporDto.setAuditDay(list3.getAuditDay());
                    standardReporDto.setCreateDate(list3.getCreateDate());
                }
            }
            standardReporDto.setStadardCount(countSum);
            reportGrpNmStandardLists.add(standardReporDto);
        }

        // logging
        // search last one
        Comparator<SnetAssetSwAuditReportDto> comparator = Comparator.comparing(SnetAssetSwAuditReportDto::getStadardCount);
        SnetAssetSwAuditReportDto lastOne = reportGrpNmStandardLists.stream().max(comparator).get();

        // search groupbyReportGrpNmLastOneList
        Map paramStandardMap = new HashMap();

        if (notFlag) {

            paramStandardMap.put("startAuditDay", startAuditDay);
            paramStandardMap.put("endAuditDay", endAuditDay);

            if (StringUtils.equalsIgnoreCase("Linux", swTypeStrTemp) || StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                if (StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                    paramStandardMap.put("SW_TYPE", "OS");
                    paramStandardMap.put("SW_NM", "Windows");
                } else {

                    paramStandardMap.put("SW_TYPE", "OS");
                    paramStandardMap.put("SW_NM", lastOne.getSwNm());
                }
            } else {

                paramStandardMap.put("SW_TYPE", swTypeStrTemp);
                paramStandardMap.remove("SW_NM");
            }
            paramStandardMap.put("SW_DIR", lastOne.getSwDir());
        } else {

            paramStandardMap.put("SW_TYPE", swTypeStrTemp);
        }
        paramStandardMap.put("ASSET_CD", lastOne.getAssetCd());
        paramStandardMap.put("AUDIT_DAY", lastOne.getAuditDay());
        paramStandardMap.put("CREATE_DATE", lastOne.getCreateDate());
        List<SnetAssetSwAuditReportDto> groupbyReportGrpNmLastOneList = testMapper.getGroupbyReportGrpNmListGet(paramStandardMap);

        int standardTempWeekCnt = 0;
        int standardDotIndexOf = 0;
        String standardSubstrGrpNm = null;
        boolean itemGrpNmFlag = false;

        TreeMap<String, List<SnetAssetSwAuditReportDto>> groupbyReportGrpNmStandard =
                groupbyReportGrpNmLastOneList.stream().collect(Collectors.groupingBy(SnetAssetSwAuditReportDto::getItemGrpNm, TreeMap::new, Collectors.toList()));

        List<SnetAssetSwAuditReportDto> reportGrpNmStandardLastOneList = Lists.newArrayList();
        for (Map.Entry<String, List<SnetAssetSwAuditReportDto>> grpNmVo : groupbyReportGrpNmStandard.entrySet()) {

            SnetAssetSwAuditReportDto latOneDto = new SnetAssetSwAuditReportDto();

            for (SnetAssetSwAuditReportDto dto : grpNmVo.getValue()) {

                standardTempWeekCnt += dto.getWeakCount();
                standardDotIndexOf = StringUtils.indexOf(dto.getItemGrpNm(), ".");

                if (standardDotIndexOf > -1) {
                    standardSubstrGrpNm = StringUtils.substring(dto.getItemGrpNm(), standardDotIndexOf + 2);
                } else {
                    standardSubstrGrpNm = dto.getItemGrpNm();
                    itemGrpNmFlag = true;
                }
            }

            int stanardMaxCount = standardTempWeekCnt * checkTargets;
            latOneDto.setItemCount(standardTempWeekCnt);
            latOneDto.setStadardCount(stanardMaxCount);
            latOneDto.setItemGrpNm(StringUtils.deleteWhitespace(standardSubstrGrpNm));

            reportGrpNmStandardLastOneList.add(latOneDto);
            standardTempWeekCnt = 0;
        } // end for

        // 분류항목별 점검항목수 전체 기준 : reportGrpNmStandardLastOneList
        int tempWeekCnt = 0;
        int dotIndexOf = 0;
        String substrGrpNm = null;
        List<SnetAssetSwAuditReportDto> hiddenList3 = Lists.newArrayList();
        List<SnetAssetSwAuditReportDto> hiddenList4 = Lists.newArrayList();

        for (List<SnetAssetSwAuditReportDto> reportDtos : groupbyReportGrpNmLists) {

            TreeMap<String, List<SnetAssetSwAuditReportDto>> groupbyReportGrpNm =
                    reportDtos.stream().collect(Collectors.groupingBy(SnetAssetSwAuditReportDto::getItemGrpNm, TreeMap::new, Collectors.toList()));

            for (Map.Entry<String, List<SnetAssetSwAuditReportDto>> grpNmVo : groupbyReportGrpNm.entrySet()) {

                SnetAssetSwAuditReportDto hiddenDto3 = new SnetAssetSwAuditReportDto();

                for (SnetAssetSwAuditReportDto dto3 : grpNmVo.getValue()) {

                    if (StringUtils.equals("F", dto3.getItemResult())) {

                        tempWeekCnt = dto3.getWeakCount();
                        dotIndexOf = StringUtils.indexOf(dto3.getItemGrpNm(), ".");

                        if (dotIndexOf > -1) {
                            substrGrpNm = StringUtils.substring(dto3.getItemGrpNm(), dotIndexOf + 1);
                        } else {
                            substrGrpNm = dto3.getItemGrpNm();
                        }

                        substrGrpNm = StringUtils.deleteWhitespace(substrGrpNm);
                    } else {

                        tempWeekCnt = 0;
                        dotIndexOf = StringUtils.indexOf(dto3.getItemGrpNm(), ".");

                        if (dotIndexOf > -1) {
                            substrGrpNm = StringUtils.substring(dto3.getItemGrpNm(), dotIndexOf + 1);
                        } else {
                            substrGrpNm = dto3.getItemGrpNm();
                            itemGrpNmFlag = true;
                        }

                        substrGrpNm = StringUtils.deleteWhitespace(substrGrpNm);
                    }
                } // end for

                hiddenDto3.setWeakCount(tempWeekCnt);
                hiddenDto3.setItemGrpNm(substrGrpNm);

                hiddenList3.add(hiddenDto3);
            } // end for
        } // end for

        TreeMap<String, List<SnetAssetSwAuditReportDto>> groupbyReportGrpNm2 =
                hiddenList3.stream().collect(Collectors.groupingBy(SnetAssetSwAuditReportDto::getItemGrpNm, TreeMap::new, Collectors.toList()));
        for (Map.Entry<String, List<SnetAssetSwAuditReportDto>> grpNmVo2 : groupbyReportGrpNm2.entrySet()) {

            int tempWeekCount = 0;
            SnetAssetSwAuditReportDto hiddenDto4 = new SnetAssetSwAuditReportDto();

            String itemGrpNm = grpNmVo2.getKey();

            for (SnetAssetSwAuditReportDto reportDto2 : hiddenList3) {

                String itemGrpNm2 = reportDto2.getItemGrpNm();
                if (StringUtils.equalsIgnoreCase(itemGrpNm, itemGrpNm2)) {

                    tempWeekCount += reportDto2.getWeakCount();
                }
            }
            hiddenDto4.setItemGrpNm(itemGrpNm);
            hiddenDto4.setWeakCount(tempWeekCount);
            hiddenList4.add(hiddenDto4);
            grpNmLen++;
        }

//        hiddenList4.sort(Comparator.comparing(SnetAssetSwAuditReportDto::getWeakCount).reversed());

        int weekHighCount = 0;
        int weekMiddleCount = 0;
        int weekLowCount = 0;
        int weebTotalCount = 0;
        List<ResultDto> detailResultList = Lists.newArrayList();   // 전체
        List<ResultDto> detailResultList2 = Lists.newArrayList();  // 취약
        List<ResultDto> detailResultList3 = Lists.newArrayList();  // NA 제외
        List<ResultDto> detailResultList4 = Lists.newArrayList();  // 양호
        List<ResultDto> detailResultList5 = Lists.newArrayList();  // 인터뷰필요
        List<ResultDto> detailResultList6 = Lists.newArrayList();  // 불가
        List<List> doubleList = Lists.newArrayList();   // 전체
        List<List> doubleList2 = Lists.newArrayList();  // 취약
        List<List> doubleList3 = Lists.newArrayList();  // NA 제외
        List<List> doubleList4 = Lists.newArrayList();  // 양호
        List<List> doubleList5 = Lists.newArrayList();  // 인터뷰필요
        List<List> doubleList6 = Lists.newArrayList();  // 불가

        int totalHighCount = 0;
        int totalMiddleCount = 0;
        int totalLowCount = 0;

        int normalHighCount = 0;
        int normalMiddleCount = 0;
        int normalLowCount = 0;

        int interviewHighCount = 0;
        int interviewMiddleCount = 0;
        int interviewLowCount = 0;

        int disableHighCount = 0;
        int disableMiddleCount = 0;
        int disableLowCount = 0;

        int weekCountSum = 0;

        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();
            List<ResultDto> detailTargetResultList = Lists.newArrayList();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());

                if (notFlag) {
                    if (StringUtils.equalsIgnoreCase("Linux", swTypeStrTemp) || StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                        if (StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", "Windows");
                        } else {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", reportDto2.getSwNm());
                        }
                    } else {

                        paramMap.put("SW_TYPE", swTypeStrTemp);
                        paramMap.remove("SW_NM");
                    }
                    paramMap.put("SW_DIR", reportDto2.getSwDir());
                } else {
                    paramMap.put("SW_TYPE", reportDto2.getSwType());
                }

                if (itemGrpNmFlag) {

                    detailResultList = testMapper.getDetailResultListItemGrpOrigin(paramMap);
                } else {

                    detailResultList = testMapper.getDetailResultList(paramMap);
                }

//                if (detailResultList.size() == 0) {
//
//                    detailResultList = testMapper.getDetailResultList2(paramMap);
//                }


                if (detailResultList != null && detailResultList.size() > 0) {
                   if(detailTargetResultList.size() == 0) {
                       detailTargetResultList = detailResultList;
                   }else {
                       for(ResultDto resultDto : detailResultList) {
                           if(!detailTargetResultList.stream().anyMatch(o -> o.getName().equals(resultDto.getName()))) {
                               detailTargetResultList.add(resultDto);
                           }

                           for(ResultDto resultDtoTarget : detailTargetResultList) {
                               if (resultDtoTarget.getName().equalsIgnoreCase(resultDto.getName()) && resultDtoTarget.getCount() < resultDto.getCount()) {
                                   resultDtoTarget.setCount(resultDto.getCount());
                               }
                           }
                       }
                   }
               }

                paramMap.remove("ITEM_GRP_NM");
            }
            doubleList.add(detailResultList);
        } // end for
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
                paramMap.put("F_FLAG", "Y");

                if (notFlag) {

                    if (StringUtils.equalsIgnoreCase("Linux", swTypeStrTemp) || StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                        if (StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", "Windows");
                        } else {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", reportDto2.getSwNm());
                        }
                    } else {

                        paramMap.put("SW_TYPE", swTypeStrTemp);
                        paramMap.remove("SW_NM");
                    }
                    paramMap.put("SW_DIR", reportDto2.getSwDir());
                } else {
                    paramMap.put("SW_TYPE", reportDto2.getSwType());
                }

                if (itemGrpNmFlag) {

                    detailResultList2 = testMapper.getDetailResultListItemGrpOrigin(paramMap);
                } else {

                    detailResultList2 = testMapper.getDetailResultList(paramMap);
                }

//                if (detailResultList2.size() == 0) {
//
//                    detailResultList2 = testMapper.getDetailResultList2(paramMap);
//                }

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("F_FLAG");

                if (detailResultList2 != null && detailResultList2.size() > 0) {

                    doubleList2.add(detailResultList2);
                }
            }
        } // end for

        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
                paramMap.put("NA_FLAG", "Y");

                if (notFlag) {

                    if (StringUtils.equalsIgnoreCase("Linux", swTypeStrTemp) || StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                        if (StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", "Windows");
                        } else {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", reportDto2.getSwNm());
                        }
                    } else {

                        paramMap.put("SW_TYPE", swTypeStrTemp);
                        paramMap.remove("SW_NM");
                    }
                    paramMap.put("SW_DIR", reportDto2.getSwDir());
                } else {

                    paramMap.put("SW_TYPE", reportDto2.getSwType());
                }

                if (itemGrpNmFlag) {

                    detailResultList3 = testMapper.getDetailResultListItemGrpOrigin(paramMap);
                } else {

                    detailResultList3 = testMapper.getDetailResultList(paramMap);
                }

//                if (detailResultList3.size() == 0) {
//
//                    detailResultList3 = testMapper.getDetailResultList2(paramMap);
//                }

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("NA_FLAG");
                if (detailResultList3 != null && detailResultList3.size() > 0) {

                    doubleList3.add(detailResultList3);
                }
            }
        } // end for
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
                paramMap.put("T_FLAG", "Y");

                if (notFlag) {

                    if (StringUtils.equalsIgnoreCase("Linux", swTypeStrTemp) || StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                        if (StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", "Windows");
                        } else {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", reportDto2.getSwNm());
                        }
                    } else {

                        paramMap.put("SW_TYPE", swTypeStrTemp);
                        paramMap.remove("SW_NM");
                    }
                    paramMap.put("SW_DIR", reportDto2.getSwDir());
                } else {

                    paramMap.put("SW_TYPE", reportDto2.getSwType());
                }

                if (itemGrpNmFlag) {

                    detailResultList4 = testMapper.getDetailResultListItemGrpOrigin(paramMap);
                } else {

                    detailResultList4 = testMapper.getDetailResultList(paramMap);
                }

//                if (detailResultList3.size() == 0) {
//
//                    detailResultList3 = testMapper.getDetailResultList2(paramMap);
//                }

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("T_FLAG");
                if (detailResultList4 != null && detailResultList4.size() > 0) {

                    doubleList4.add(detailResultList4);
                }
            }
        } // end for
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
                paramMap.put("R_FLAG", "Y");

                if (notFlag) {

                    if (StringUtils.equalsIgnoreCase("Linux", swTypeStrTemp) || StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                        if (StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", "Windows");
                        } else {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", reportDto2.getSwNm());
                        }
                    } else {

                        paramMap.put("SW_TYPE", swTypeStrTemp);
                        paramMap.remove("SW_NM");
                    }
                    paramMap.put("SW_DIR", reportDto2.getSwDir());
                } else {

                    paramMap.put("SW_TYPE", reportDto2.getSwType());
                }

                if (itemGrpNmFlag) {

                    detailResultList5 = testMapper.getDetailResultListItemGrpOrigin(paramMap);
                } else {

                    detailResultList5 = testMapper.getDetailResultList(paramMap);
                }

//                if (detailResultList3.size() == 0) {
//
//                    detailResultList3 = testMapper.getDetailResultList2(paramMap);
//                }

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("R_FLAG");
                if (detailResultList5 != null && detailResultList5.size() > 0) {

                    doubleList5.add(detailResultList5);
                }
            }
        } // end for
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
                paramMap.put("C_FLAG", "Y");

                if (notFlag) {

                    if (StringUtils.equalsIgnoreCase("Linux", swTypeStrTemp) || StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                        if (StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", "Windows");
                        } else {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", reportDto2.getSwNm());
                        }
                    } else {

                        paramMap.put("SW_TYPE", swTypeStrTemp);
                        paramMap.remove("SW_NM");
                    }
                    paramMap.put("SW_DIR", reportDto2.getSwDir());
                } else {

                    paramMap.put("SW_TYPE", reportDto2.getSwType());
                }

                if (itemGrpNmFlag) {

                    detailResultList6 = testMapper.getDetailResultListItemGrpOrigin(paramMap);
                } else {

                    detailResultList6 = testMapper.getDetailResultList(paramMap);
                }

//                if (detailResultList3.size() == 0) {
//
//                    detailResultList3 = testMapper.getDetailResultList2(paramMap);
//                }

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("C_FLAG");
                if (detailResultList6 != null && detailResultList6.size() > 0) {

                    doubleList6.add(detailResultList6);
                }
            }
        } // end for

        // 점검수량, 점검항목수, 점검결과 dtoList 생성
        int itemGrpCountTotal = 0;

        // 웹UI 보안준수율
        double value8 = 0L;

        value8 = list7Re.stream()
                .filter(x -> x.getAuditRate() > 0)
                .mapToDouble(x -> x.getAuditRate())
                .average()
                .orElseGet(() -> 0.0);

        value8 = value8 * 100;

        for (SnetAssetSwAuditReportDto resultReportDto : hiddenList4) {

            DetailResultDto vo = new DetailResultDto();

            String str = resultReportDto.getItemGrpNm();
            vo.setKey(str);
            vo.setValue1(checkTargets);
            vo.setValue8(value8);

            for (List<ResultDto> resultDtoList : doubleList) {

                for (ResultDto resultDto : resultDtoList) {

                    if (StringUtils.equals(str, resultDto.getGrpname())) {

                        weekCountSum += resultDto.getCount();
                        vo.setValue2(weekCountSum);
                        vo.setValue3(weekCountSum * checkTargets);
                    }
                }
            } // end for 1

            int plusCount = 0;

            // 진단 기준에 맞게 만들어 지므로 plus 불필요
            int value2 = vo.getValue2() / checkTargets;
            if (notFlag) {

                for (SnetAssetSwAuditReportDto dto : reportGrpNmStandardLastOneList) {

                    if (StringUtils.equalsIgnoreCase(vo.getKey(), dto.getItemGrpNm())) {

                        if (vo.getValue2() < dto.getStadardCount()) {

                            plusCount = dto.getStadardCount() - vo.getValue2();

//                            vo.setValue2(vo.getValue2() + plusCount);
//                            vo.setValue3(vo.getValue3() + plusCount);
                        }
                    }
                }
                itemGrpCountTotal += vo.getValue2();
                vo.setValue16(itemGrpCountTotal);
            } // end if

            for (List<ResultDto> resultDtoList : doubleList2) {

                for (ResultDto resultDto : resultDtoList) {

                    if (StringUtils.equals(str, resultDto.getGrpname())) {

                        if (StringUtils.equals("H", resultDto.getName())) {

                            weekHighCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("M", resultDto.getName())) {

                            weekMiddleCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("L", resultDto.getName())) {

                            weekLowCount += resultDto.getCount();
                        }

                        vo.setValue4(weekHighCount);
                        vo.setValue5(weekMiddleCount);
                        vo.setValue6(weekLowCount);

                        weebTotalCount = weekHighCount + weekMiddleCount + weekLowCount;
                        vo.setValue7(weebTotalCount);
                    }
                }
            } // end for 2

            for (List<ResultDto> resultDtoList2 : doubleList3) {

                for (ResultDto resultDto2 : resultDtoList2) {

                    if (StringUtils.equals(str, resultDto2.getGrpname())) {

                        if (StringUtils.equals("H", resultDto2.getName())) {

                            totalHighCount += resultDto2.getCount();
                        }
                        if (StringUtils.equals("M", resultDto2.getName())) {

                            totalMiddleCount += resultDto2.getCount();
                        }
                        if (StringUtils.equals("L", resultDto2.getName())) {

                            totalLowCount += resultDto2.getCount();
                        }

                        vo.setValue10(totalHighCount);
                        vo.setValue11(totalMiddleCount);

                        if (notFlag) {
                            vo.setValue12(totalLowCount + plusCount);
                        } else {
                            vo.setValue12(totalLowCount);
                        }
                    }
                }
            } // end for 3

            for (List<ResultDto> resultDtoList4 : doubleList4) {

                for (ResultDto resultDto2 : resultDtoList4) {

                    if (StringUtils.equals(str, resultDto2.getGrpname())) {

                        if (StringUtils.equals("H", resultDto2.getName())) {

                            normalHighCount += resultDto2.getCount();
                        }
                        if (StringUtils.equals("M", resultDto2.getName())) {

                            normalMiddleCount += resultDto2.getCount();
                        }
                        if (StringUtils.equals("L", resultDto2.getName())) {

                            normalLowCount += resultDto2.getCount();
                        }

                        vo.setValue17(normalHighCount);
                        vo.setValue18(normalMiddleCount);
                        vo.setValue19(normalLowCount);
                    }
                }
            } // end for 4

            for (List<ResultDto> resultDtoList5 : doubleList5) {

                for (ResultDto resultDto2 : resultDtoList5) {

                    if (StringUtils.equals(str, resultDto2.getGrpname())) {

                        if (StringUtils.equals("H", resultDto2.getName())) {

                            interviewHighCount += resultDto2.getCount();
                        }
                        if (StringUtils.equals("M", resultDto2.getName())) {

                            interviewMiddleCount += resultDto2.getCount();
                        }
                        if (StringUtils.equals("L", resultDto2.getName())) {

                            interviewLowCount += resultDto2.getCount();
                        }

                        vo.setValue20(interviewHighCount);
                        vo.setValue21(interviewMiddleCount);
                        vo.setValue22(interviewLowCount);
                    }
                }
            } // end for 5

            for (List<ResultDto> resultDtoList6 : doubleList6) {

                for (ResultDto resultDto2 : resultDtoList6) {

                    if (StringUtils.equals(str, resultDto2.getGrpname())) {

                        if (StringUtils.equals("H", resultDto2.getName())) {

                            disableHighCount += resultDto2.getCount();
                        }
                        if (StringUtils.equals("M", resultDto2.getName())) {

                            disableMiddleCount += resultDto2.getCount();
                        }
                        if (StringUtils.equals("L", resultDto2.getName())) {

                            disableLowCount += resultDto2.getCount();
                        }

                        vo.setValue23(disableHighCount);
                        vo.setValue24(disableMiddleCount);
                        vo.setValue25(disableLowCount);
                    }
                }
            } // end for 6

            weekCountSum = 0;

            weekHighCount = 0;
            weekMiddleCount = 0;
            weekLowCount = 0;

            totalHighCount = 0;
            totalMiddleCount = 0;
            totalLowCount = 0;

            normalHighCount = 0;
            normalMiddleCount = 0;
            normalLowCount = 0;

            interviewHighCount = 0;
            interviewMiddleCount = 0;
            interviewLowCount = 0;

            disableHighCount = 0;
            disableMiddleCount = 0;
            disableLowCount = 0;

            vo.setValue9(swTypeStr);

            list.add(vo);
        } // end for

//        if (list.size() < 7) {
//
//            int minusSize = 7-list.size();
//
//            for (int z = 0; z < minusSize; z++) {
//
//                DetailResultDto vo = new DetailResultDto();
//                list.add(vo);
//            }
//        }
        if (notFlag) {
            boolean itemNmEqualFlag = false;
            int listIndex = 0;
            int lastOneMinus = 0;
            int lastOneTotal = 0;
            for (DetailResultDto detailResultDto : list) {

                itemNmEqualFlag = reportGrpNmStandardLastOneList.stream().noneMatch(x -> StringUtils.equalsIgnoreCase(x.getItemGrpNm(), detailResultDto.getKey()));
                if (itemNmEqualFlag) {

                    lastOneMinus += detailResultDto.getValue2();
                }
            }
            List<DetailResultDto> lastList = list.stream().collect(Collectors.toList());

            lastList.sort(Comparator.comparing(DetailResultDto::getValue16).reversed());
            DetailResultDto detailResultDtoLastOne = lastList.stream().findFirst().get();

            // 진단 기준에 맞게 만들어 지므로 minus 불필요
//            lastOneTotal = detailResultDtoLastOne.getValue16() - lastOneMinus;
            lastOneTotal = detailResultDtoLastOne.getValue16();

            for (DetailResultDto dto : list) {

                dto.setValue16(lastOneTotal);
            }
        }

        return list;
    } // end method

    @Override
    public List<DetailResultDto> getMigDetailTargetsList(List<TotalOverallResultDto> resultDtos, Map gAssetSwJob, String swTypeStr, boolean notFlag) {

        List<DetailResultDto> list = Lists.newArrayList();
        String swTypeStrTemp = swTypeStr;
        String startAuditDay = gAssetSwJob.get("startAuditDay").toString();
        String endAuditDay = gAssetSwJob.get("endAuditDay").toString();

        TotalOverallResultDto totalOverallResultDto = new TotalOverallResultDto();

        // 종합보고서 전체 장비 리스트
        if (StringUtils.equalsIgnoreCase("Linux", swTypeStrTemp) || StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

            if (StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                gAssetSwJob.put("SW_TYPE", "OS");
                gAssetSwJob.put("SW_NM", "Windows");
            } else {

                gAssetSwJob.put("LNOT_FLAG", "Y");
            }
        } else {

            gAssetSwJob.put("SW_TYPE", swTypeStrTemp);
            gAssetSwJob.remove("SW_NM");
        }

        gAssetSwJob.put("HOST_GRD_FLAG", "Y");
        List<SnetAssetSwAuditExcelDto> list7Re = testMapper.selectAssetSwExcelListNew(gAssetSwJob);
        gAssetSwJob.remove("HOST_GRD_FLAG");

        String assetCd = null;
        String auditDay = null;
        String[] assetCdIn = new String[list7Re.size()];

        List<SnetAssetSwAuditReportDto> reportDtoList = Lists.newArrayList();

        if (notFlag) {

            String tempAssetCd = null;
            String tempAuditDay = null;
            for (SnetAssetSwAuditExcelDto excelDto : list7Re) {

                tempAssetCd = excelDto.getAssetCd();
                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                if (StringUtils.equalsIgnoreCase("Linux", swTypeStr) || StringUtils.equalsIgnoreCase("Windows", swTypeStr)) {

                    if (StringUtils.equalsIgnoreCase("Windows", swTypeStr)) {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", "Windows");
                    } else {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", excelDto.getSwNm());
                    }
                } else {

                    paramMap.put("SW_TYPE", swTypeStr);
                    paramMap.remove("SW_NM");
                }
                paramMap.put("ASSET_CD", tempAssetCd);
                paramMap.put("FILE_TYPE", gAssetSwJob.get("auditType"));
                paramMap.put("SW_DIR", excelDto.getSwDir());

                SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetSwAuditReportNewGetMaxAuditDay(paramMap);

                if (reportDto1 != null) {
                    tempAuditDay = reportDto1.getAuditDay();

                    paramMap.put("AUDIT_DAY", tempAuditDay);
                    SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                    paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
                    SnetAssetSwAuditReportDto reportDto3 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                    reportDtoList.add(reportDto3);
                }
            }

            reportDtoList.sort(Comparator.comparing(SnetAssetSwAuditReportDto::getAuditDay));
            totalOverallResultDto.setKey(swTypeStr);
        } else {

            gAssetSwJob.put("NOT_FLAG", "Y");
            gAssetSwJob.remove("SW_TYPE");
            gAssetSwJob.put("HOST_GRD_FLAG", "Y");
            List<SnetAssetSwAuditExcelDto> listEtc = testMapper.selectAssetSwExcelListNew(gAssetSwJob);
            gAssetSwJob.remove("HOST_GRD_FLAG");

            String tempAssetCd = null;
            String tempAuditDay = null;
            String tempSwType = null;
            for (SnetAssetSwAuditExcelDto excelDto : listEtc) {

                tempAssetCd = excelDto.getAssetCd();
                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("SW_TYPE", excelDto.getSwType());
                paramMap.put("ASSET_CD", tempAssetCd);
                paramMap.put("FILE_TYPE", gAssetSwJob.get("auditType"));

                SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetSwAuditReportNewGetMinAuditDay(paramMap);

                if (reportDto1 != null) {
                    tempAuditDay = reportDto1.getAuditDay();

                    paramMap.put("AUDIT_DAY", tempAuditDay);

                    SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                    paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
                    SnetAssetSwAuditReportDto reportDto3 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                    reportDtoList.add(reportDto3);
                }
            }
            reportDtoList.sort(Comparator.comparing(SnetAssetSwAuditReportDto::getAuditDay));
            totalOverallResultDto.setKey(swTypeStr);
        } // end if else

        // select SNET_ASSET_SW_AUDIT_REPORT
        int grpNmLen = 0;
        List<List<SnetAssetSwAuditReportDto>> groupbyReportGrpNmLists = Lists.newArrayList();
        for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

            Map paramMap = new HashMap();
            paramMap.put("startAuditDay", startAuditDay);
            paramMap.put("endAuditDay", endAuditDay);

            if (notFlag) {
                if (StringUtils.equalsIgnoreCase("Linux", swTypeStr) || StringUtils.equalsIgnoreCase("Windows", swTypeStr)) {

                    if (StringUtils.equalsIgnoreCase("Windows", swTypeStr)) {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", "Windows");
                    } else {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", reportDto.getSwNm());
                    }
                } else {

                    paramMap.put("SW_TYPE", reportDto.getSwType());
                    paramMap.remove("SW_NM");
                }
            } else {
                paramMap.put("SW_TYPE", reportDto.getSwType());
            }
            paramMap.put("ASSET_CD", reportDto.getAssetCd());
            paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
            paramMap.put("CREATE_DATE", reportDto.getCreateDate());
            paramMap.put("SW_DIR", reportDto.getSwDir());
            List<SnetAssetSwAuditReportDto> groupbyReportGrpNmList = testMapper.getGroupbyReportGrpNmListGet(paramMap);
            groupbyReportGrpNmLists.add(groupbyReportGrpNmList);
        }

        // 점검수량
        int checkTargets = 0;
        checkTargets = reportDtoList.size();

        // search standardcount max
        // -> 이행점검은 없음

        int tempWeekCnt = 0;
        int dotIndexOf = 0;
        String substrGrpNm = null;
        boolean itemGrpNmFlag = false;

        List<SnetAssetSwAuditReportDto> hiddenList3 = Lists.newArrayList();
        List<SnetAssetSwAuditReportDto> hiddenList4 = Lists.newArrayList();
        for (List<SnetAssetSwAuditReportDto> reportDtos : groupbyReportGrpNmLists) {

            TreeMap<String, List<SnetAssetSwAuditReportDto>> groupbyReportGrpNm =
                    reportDtos.stream().collect(Collectors.groupingBy(SnetAssetSwAuditReportDto::getItemGrpNm, TreeMap::new, Collectors.toList()));

            for (Map.Entry<String, List<SnetAssetSwAuditReportDto>> grpNmVo : groupbyReportGrpNm.entrySet()) {

                SnetAssetSwAuditReportDto hiddenDto3 = new SnetAssetSwAuditReportDto();

                for (SnetAssetSwAuditReportDto dto3 : grpNmVo.getValue()) {

                    if (StringUtils.equals("F", dto3.getItemResult())) {

                        tempWeekCnt = dto3.getWeakCount();
                        dotIndexOf = StringUtils.indexOf(dto3.getItemGrpNm(), ".");

                        if (dotIndexOf > -1) {
                            substrGrpNm = StringUtils.substring(dto3.getItemGrpNm(), dotIndexOf + 1);
                        } else {
                            substrGrpNm = dto3.getItemGrpNm();
                        }

                        substrGrpNm = StringUtils.deleteWhitespace(substrGrpNm);
                    } else {

                        tempWeekCnt = 0;
                        dotIndexOf = StringUtils.indexOf(dto3.getItemGrpNm(), ".");

                        if (dotIndexOf > -1) {
                            substrGrpNm = StringUtils.substring(dto3.getItemGrpNm(), dotIndexOf + 1);
                        } else {
                            substrGrpNm = dto3.getItemGrpNm();
                            itemGrpNmFlag = true;
                        }

                        substrGrpNm = StringUtils.deleteWhitespace(substrGrpNm);
                    }
                }

                hiddenDto3.setWeakCount(tempWeekCnt);
                hiddenDto3.setItemGrpNm(substrGrpNm);

                hiddenList3.add(hiddenDto3);
            } // end for
        } // end for

        TreeMap<String, List<SnetAssetSwAuditReportDto>> groupbyReportGrpNm2 =
                hiddenList3.stream().collect(Collectors.groupingBy(SnetAssetSwAuditReportDto::getItemGrpNm, TreeMap::new, Collectors.toList()));
        for (Map.Entry<String, List<SnetAssetSwAuditReportDto>> grpNmVo2 : groupbyReportGrpNm2.entrySet()) {

            int tempWeekCount = 0;
            SnetAssetSwAuditReportDto hiddenDto4 = new SnetAssetSwAuditReportDto();

            String itemGrpNm = grpNmVo2.getKey();

            for (SnetAssetSwAuditReportDto reportDto2 : hiddenList3) {

                String itemGrpNm2 = reportDto2.getItemGrpNm();
                if (StringUtils.equalsIgnoreCase(itemGrpNm, itemGrpNm2)) {

                    tempWeekCount += reportDto2.getWeakCount();
                }
            }

            hiddenDto4.setItemGrpNm(itemGrpNm);
            hiddenDto4.setWeakCount(tempWeekCount);
            hiddenList4.add(hiddenDto4);
            grpNmLen++;
        }

//        hiddenList4.sort(Comparator.comparing(SnetAssetSwAuditReportDto::getWeakCount).reversed());

        int weekHighCount = 0;
        int weekMiddleCount = 0;
        int weekLowCount = 0;
        int weebTotalCount = 0;
        List<ResultDto> detailResultList = Lists.newArrayList();   // 전체
        List<ResultDto> detailResultList2 = Lists.newArrayList();  // 취약
        List<ResultDto> detailResultList3 = Lists.newArrayList();  // NA 제외
        List<ResultDto> detailResultList4 = Lists.newArrayList();  // 양호
        List<ResultDto> detailResultList5 = Lists.newArrayList();  // 인터뷰필요
        List<ResultDto> detailResultList6 = Lists.newArrayList();  // 불가
        List<List> doubleList = Lists.newArrayList();   // 전체
        List<List> doubleList2 = Lists.newArrayList();  // 취약
        List<List> doubleList3 = Lists.newArrayList();  // NA 제외
        List<List> doubleList4 = Lists.newArrayList();  // 양호
        List<List> doubleList5 = Lists.newArrayList();  // 인터뷰필요
        List<List> doubleList6 = Lists.newArrayList();  // 불가

        int totalHighCount = 0;
        int totalMiddleCount = 0;
        int totalLowCount = 0;

        int normalHighCount = 0;
        int normalMiddleCount = 0;
        int normalLowCount = 0;

        int interviewHighCount = 0;
        int interviewMiddleCount = 0;
        int interviewLowCount = 0;

        int disableHighCount = 0;
        int disableMiddleCount = 0;
        int disableLowCount = 0;

        int weekCountSum = 0;

        // -> 점검군별 점검결과 - 이행점검 은 MAX 전체 조회 필요 없음
//        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {
//
//            String itemGrpNmStr = reportDto1.getItemGrpNm();
//
//            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {
//
//                Map paramMap = new HashMap();
//                paramMap.put("startAuditDay", startAuditDay);
//                paramMap.put("endAuditDay", endAuditDay);
//
//                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
//                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
//                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
//                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
//
//                if (notFlag) {
//                    if (StringUtils.equalsIgnoreCase("Linux", swTypeStrTemp) || StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {
//
//                        if (StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {
//
//                            paramMap.put("SW_TYPE", "OS");
//                            paramMap.put("SW_NM", "Windows");
//                        } else {
//
//                            paramMap.put("SW_TYPE", "OS");
//                            paramMap.put("SW_NM", reportDto2.getSwNm());
//                        }
//                    } else {
//
//                        paramMap.put("SW_TYPE", swTypeStrTemp);
//                        paramMap.remove("SW_NM");
//                    }
//                } else {
//                    paramMap.put("SW_TYPE", reportDto2.getSwType());
//                }
//                detailResultList = testMapper.getDetailResultList(paramMap);
//
////                if (detailResultList.size() == 0) {
////
////                    detailResultList = testMapper.getDetailResultList2(paramMap);
////                }
//
//                paramMap.remove("ITEM_GRP_NM");
//                if (detailResultList != null && detailResultList.size() > 0) {
//
//                    doubleList.add(detailResultList);
//                }
//            }
//        } // end for

        // 취약
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
                paramMap.put("F_FLAG", "Y");

                if (notFlag) {

                    if (StringUtils.equalsIgnoreCase("Linux", swTypeStrTemp) || StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                        if (StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", "Windows");
                        } else {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", reportDto2.getSwNm());
                        }
                    } else {

                        paramMap.put("SW_TYPE", swTypeStrTemp);
                        paramMap.remove("SW_NM");
                    }
                    paramMap.put("SW_DIR", reportDto2.getSwDir());
                } else {
                    paramMap.put("SW_TYPE", reportDto2.getSwType());
                }

                if (itemGrpNmFlag) {

                    detailResultList2 = testMapper.getDetailResultListItemGrpOrigin(paramMap);
                } else {

                    detailResultList2 = testMapper.getDetailResultList(paramMap);
                }

//                if (detailResultList2.size() == 0) {
//
//                    detailResultList2 = testMapper.getDetailResultList2(paramMap);
//                }

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("F_FLAG");

                if (detailResultList2 != null && detailResultList2.size() > 0) {

                    doubleList2.add(detailResultList2);
                }
            }
        } // end for

        // -> 점검군별 점검결과 - 이행점검 은 NA 제외 전체 조회 필요 없음
//        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {
//
//            String itemGrpNmStr = reportDto1.getItemGrpNm();
//
//            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {
//
//                Map paramMap = new HashMap();
//                paramMap.put("startAuditDay", startAuditDay);
//                paramMap.put("endAuditDay", endAuditDay);
//
//                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
//                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
//                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
//                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
//                paramMap.put("NA_FLAG", "Y");
//
//                if (notFlag) {
//
//                    if (StringUtils.equalsIgnoreCase("Linux", swTypeStrTemp) || StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {
//
//                        if (StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {
//
//                            paramMap.put("SW_TYPE", "OS");
//                            paramMap.put("SW_NM", "Windows");
//                        } else {
//
//                            paramMap.put("SW_TYPE", "OS");
//                            paramMap.put("SW_NM", reportDto2.getSwNm());
//                        }
//                    } else {
//
//                        paramMap.put("SW_TYPE", swTypeStrTemp);
//                        paramMap.remove("SW_NM");
//                    }
//                } else {
//
//                    paramMap.put("SW_TYPE", reportDto2.getSwType());
//                }
//
//                detailResultList3 = testMapper.getDetailResultList(paramMap);
//
////                if (detailResultList3.size() == 0) {
////
////                    detailResultList3 = testMapper.getDetailResultList2(paramMap);
////                }
//
//                paramMap.remove("ITEM_GRP_NM");
//                paramMap.remove("NA_FLAG");
//                if (detailResultList3 != null && detailResultList3.size() > 0) {
//
//                    doubleList3.add(detailResultList3);
//                }
//            }
//        } // end for

        // 양호
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
                paramMap.put("T_FLAG", "Y");

                if (notFlag) {

                    if (StringUtils.equalsIgnoreCase("Linux", swTypeStrTemp) || StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                        if (StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", "Windows");
                        } else {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", reportDto2.getSwNm());
                        }
                    } else {

                        paramMap.put("SW_TYPE", swTypeStrTemp);
                        paramMap.remove("SW_NM");
                    }
                } else {

                    paramMap.put("SW_TYPE", reportDto2.getSwType());
                }

                if (itemGrpNmFlag) {

                    detailResultList4 = testMapper.getDetailResultListItemGrpOrigin(paramMap);
                } else {

                    detailResultList4 = testMapper.getDetailResultList(paramMap);
                }

//                if (detailResultList3.size() == 0) {
//
//                    detailResultList3 = testMapper.getDetailResultList2(paramMap);
//                }

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("T_FLAG");
                if (detailResultList4 != null && detailResultList4.size() > 0) {

                    doubleList4.add(detailResultList4);
                }
            }
        } // end for

        // 인터뷰 필요
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
                paramMap.put("R_FLAG", "Y");

                if (notFlag) {

                    if (StringUtils.equalsIgnoreCase("Linux", swTypeStrTemp) || StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                        if (StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", "Windows");
                        } else {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", reportDto2.getSwNm());
                        }
                    } else {

                        paramMap.put("SW_TYPE", swTypeStrTemp);
                        paramMap.remove("SW_NM");
                    }
                } else {

                    paramMap.put("SW_TYPE", reportDto2.getSwType());
                }

                if (itemGrpNmFlag) {

                    detailResultList5 = testMapper.getDetailResultListItemGrpOrigin(paramMap);
                } else {

                    detailResultList5 = testMapper.getDetailResultList(paramMap);
                }

//                if (detailResultList3.size() == 0) {
//
//                    detailResultList3 = testMapper.getDetailResultList2(paramMap);
//                }

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("R_FLAG");
                if (detailResultList5 != null && detailResultList5.size() > 0) {

                    doubleList5.add(detailResultList5);
                }
            }
        } // end for

        // 불가 (=위험수용)
        for (SnetAssetSwAuditReportDto reportDto1 : hiddenList4) {

            String itemGrpNmStr = reportDto1.getItemGrpNm();

            for (SnetAssetSwAuditReportDto reportDto2 : reportDtoList) {

                Map paramMap = new HashMap();
                paramMap.put("startAuditDay", startAuditDay);
                paramMap.put("endAuditDay", endAuditDay);

                paramMap.put("ITEM_GRP_NM", itemGrpNmStr);
                paramMap.put("ASSET_CD", reportDto2.getAssetCd());
                paramMap.put("AUDIT_DAY", reportDto2.getAuditDay());
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
                paramMap.put("C_FLAG", "Y");

                if (notFlag) {

                    if (StringUtils.equalsIgnoreCase("Linux", swTypeStrTemp) || StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                        if (StringUtils.equalsIgnoreCase("Windows", swTypeStrTemp)) {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", "Windows");
                        } else {

                            paramMap.put("SW_TYPE", "OS");
                            paramMap.put("SW_NM", reportDto2.getSwNm());
                        }
                    } else {

                        paramMap.put("SW_TYPE", swTypeStrTemp);
                        paramMap.remove("SW_NM");
                    }
                } else {

                    paramMap.put("SW_TYPE", reportDto2.getSwType());
                }

                if (itemGrpNmFlag) {

                    detailResultList6 = testMapper.getDetailResultListItemGrpOrigin(paramMap);
                } else {

                    detailResultList6 = testMapper.getDetailResultList(paramMap);
                }

//                if (detailResultList3.size() == 0) {
//
//                    detailResultList3 = testMapper.getDetailResultList2(paramMap);
//                }

                paramMap.remove("ITEM_GRP_NM");
                paramMap.remove("C_FLAG");
                if (detailResultList6 != null && detailResultList6.size() > 0) {

                    doubleList6.add(detailResultList6);
                }
            }
        } // end for

        // 점검수량, 점검항목수, 점검결과 dtoList 생성
        int itemGrpCountTotal = 0;

        // 웹UI 보안준수율
        double value8 = 0L;

        value8 = list7Re.stream()
                .filter(x -> x.getAuditRate() > 0)
                .mapToDouble(x -> x.getAuditRate())
                .average()
                .orElseGet(() -> 0.0);

        value8 = value8 * 100;

        for (SnetAssetSwAuditReportDto resultReportDto : hiddenList4) {

            DetailResultDto vo = new DetailResultDto();

            String str = resultReportDto.getItemGrpNm();
            vo.setKey(str);
            vo.setValue1(checkTargets); // 점검수량
            vo.setValue8(value8); // 보안지수

            // 취약
            for (List<ResultDto> resultDtoList : doubleList2) {

                for (ResultDto resultDto : resultDtoList) {

                    if (StringUtils.equals(str, resultDto.getGrpname())) {

                        if (StringUtils.equals("H", resultDto.getName())) {

                            weekHighCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("M", resultDto.getName())) {

                            weekMiddleCount += resultDto.getCount();
                        }
                        if (StringUtils.equals("L", resultDto.getName())) {

                            weekLowCount += resultDto.getCount();
                        }

                        vo.setValue4(weekHighCount);
                        vo.setValue5(weekMiddleCount);
                        vo.setValue6(weekLowCount);

                        weebTotalCount = weekHighCount + weekMiddleCount + weekLowCount;
                        vo.setValue7(weebTotalCount);
                    }
                }
            } // end for 2

            // 양호
            for (List<ResultDto> resultDtoList4 : doubleList4) {

                for (ResultDto resultDto2 : resultDtoList4) {

                    if (StringUtils.equals(str, resultDto2.getGrpname())) {

                        if (StringUtils.equals("H", resultDto2.getName())) {

                            normalHighCount += resultDto2.getCount();
                        }
                        if (StringUtils.equals("M", resultDto2.getName())) {

                            normalMiddleCount += resultDto2.getCount();
                        }
                        if (StringUtils.equals("L", resultDto2.getName())) {

                            normalLowCount += resultDto2.getCount();
                        }

                        vo.setValue17(normalHighCount);
                        vo.setValue18(normalMiddleCount);
                        vo.setValue19(normalLowCount);
                    }
                }
            } // end for 4

            // 인터뷰 필요
            for (List<ResultDto> resultDtoList5 : doubleList5) {

                for (ResultDto resultDto2 : resultDtoList5) {

                    if (StringUtils.equals(str, resultDto2.getGrpname())) {

                        if (StringUtils.equals("H", resultDto2.getName())) {

                            interviewHighCount += resultDto2.getCount();
                        }
                        if (StringUtils.equals("M", resultDto2.getName())) {

                            interviewMiddleCount += resultDto2.getCount();
                        }
                        if (StringUtils.equals("L", resultDto2.getName())) {

                            interviewLowCount += resultDto2.getCount();
                        }

                        vo.setValue20(interviewHighCount);
                        vo.setValue21(interviewMiddleCount);
                        vo.setValue22(interviewLowCount);
                    }
                }
            } // end for 5

            // 불가 (=위험수용)
            for (List<ResultDto> resultDtoList6 : doubleList6) {

                for (ResultDto resultDto2 : resultDtoList6) {

                    if (StringUtils.equals(str, resultDto2.getGrpname())) {

                        if (StringUtils.equals("H", resultDto2.getName())) {

                            disableHighCount += resultDto2.getCount();
                        }
                        if (StringUtils.equals("M", resultDto2.getName())) {

                            disableMiddleCount += resultDto2.getCount();
                        }
                        if (StringUtils.equals("L", resultDto2.getName())) {

                            disableLowCount += resultDto2.getCount();
                        }

                        vo.setValue23(disableHighCount);
                        vo.setValue24(disableMiddleCount);
                        vo.setValue25(disableLowCount);
                    }
                }
            } // end for 6

            weekHighCount = 0;
            weekMiddleCount = 0;
            weekLowCount = 0;

            normalHighCount = 0;
            normalMiddleCount = 0;
            normalLowCount = 0;

            interviewHighCount = 0;
            interviewMiddleCount = 0;
            interviewLowCount = 0;

            disableHighCount = 0;
            disableMiddleCount = 0;
            disableLowCount = 0;

            vo.setValue9(swTypeStr);

            list.add(vo);

        } // end for

        return list;
    } // end method


    /**
     *  즉시 조치 필요 대상시스템 (TOP 10) - 보안점검/이행점검
     */
    @Override
    public List<SnetAssetSwAuditExcelDto> getReportResultProcessTargets(Map gAssetSwJob, boolean flag) {

        List<SnetAssetSwAuditExcelDto> list = Lists.newArrayList();
        boolean notFlag = false;
        String swTypeStr = null;
        String startAuditDay = gAssetSwJob.get("startAuditDay").toString();
        String endAuditDay = gAssetSwJob.get("endAuditDay").toString();


        String reqCd = gAssetSwJob.get("REQ_CD").toString();

        if (gAssetSwJob.get("NOT_FLAG") != null) {
            notFlag = false;
        } else if (gAssetSwJob.get("LNOT_FLAG") != null) {
            notFlag = true;
            gAssetSwJob.remove("SW_NM");
        } else if (gAssetSwJob.get("SW_TYPE") != null) {
            swTypeStr = gAssetSwJob.get("SW_TYPE").toString();
            notFlag = true;
        }

        String swTypeNm = null;
        if (gAssetSwJob.get("SW_NM") != null) {
            swTypeNm = gAssetSwJob.get("SW_NM").toString();
        }

        if (gAssetSwJob.get("auditType") != null) {
            gAssetSwJob.put("FILE_TYPE", gAssetSwJob.get("auditType").toString());
        }

        gAssetSwJob.put("HOST_GRD_FLAG", "Y");

        List<SnetAssetSwAuditExcelDto> list7Re = testMapper.selectAssetSwExcelListNew(gAssetSwJob);
        gAssetSwJob.remove("HOST_GRD_FLAG");

        List<SnetAssetSwAuditReportDto> reportDtoList = Lists.newArrayList();
        String tempAssetCd = null;
        String tempAuditDay = null;
        for (SnetAssetSwAuditExcelDto excelDto : list7Re) {

            tempAssetCd = excelDto.getAssetCd();

            Map paramMap = new HashMap();
            paramMap.put("startAuditDay", startAuditDay);
            paramMap.put("endAuditDay", endAuditDay);

            paramMap.put("ASSET_CD", tempAssetCd);
            paramMap.put("FILE_TYPE", gAssetSwJob.get("FILE_TYPE"));

            if (notFlag) {

                if (StringUtils.equalsIgnoreCase("OS", excelDto.getSwType())) {

                    paramMap.put("SW_TYPE", swTypeStr);

                    if (StringUtils.equalsIgnoreCase("Windows", excelDto.getSwNm())) {

                        swTypeNm = "Windows";
                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", swTypeNm);
                    } else {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", excelDto.getSwNm());
                    }
                } else {

                    swTypeNm = excelDto.getSwNm();
                    paramMap.put("SW_TYPE", excelDto.getSwType());
                    paramMap.put("SW_NM", swTypeNm);
                }
                paramMap.put("SW_DIR", excelDto.getSwDir());
            } else {

                paramMap.put("SW_TYPE", swTypeStr);
                paramMap.remove("SW_NM");
            }
            SnetAssetSwAuditReportDto reportDto1 = null;

            if (flag) {
                reportDto1 = testMapper.selectAssetSwAuditReportNewGetMinAuditDay(paramMap);
            } else {
                reportDto1 = testMapper.selectAssetSwAuditReportNewGetMaxAuditDay(paramMap);
            }

            if (reportDto1 != null) {
                tempAuditDay = reportDto1.getAuditDay();

                paramMap.put("AUDIT_DAY", tempAuditDay);
                SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(paramMap);

                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
                SnetAssetSwAuditReportDto reportDto3 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                reportDtoList.add(reportDto3);
            }
        } // end for

        // excel list 조회
        gAssetSwJob.put("D_FLAG", "Y");
        List<SnetAssetSwAuditExcelDto> getWeakLevelsParams2 = Lists.newArrayList();
        for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

            Map paramMap = new HashMap();
            paramMap.put("startAuditDay", startAuditDay);
            paramMap.put("endAuditDay", endAuditDay);


            if (notFlag) {

                if (StringUtils.equalsIgnoreCase("OS", reportDto.getSwType())) {

                    paramMap.put("SW_TYPE", swTypeStr);

                    if (StringUtils.equalsIgnoreCase("Windows", reportDto.getSwNm())) {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", "Windows");
                    } else {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", reportDto.getSwNm());
                    }
                } else {

                    paramMap.put("SW_TYPE", swTypeStr);
                    paramMap.remove("SW_NM");
                }
                paramMap.put("SW_DIR", reportDto.getSwDir());
            } else {
                paramMap.put("SW_TYPE", reportDto.getSwType());
            }
            paramMap.put("ASSET_CD", reportDto.getAssetCd());
            paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
            paramMap.put("REQ_CD", reqCd);

            SnetAssetSwAuditExcelDto getWeakLevelsParams = testMapper.selectAssetSwExcelListGroupbyWeak(paramMap);

            if (getWeakLevelsParams != null) {
                getWeakLevelsParams2.add(getWeakLevelsParams);
            } else {

                SnetAssetSwAuditExcelDto getWeakLevelsParamsNot = testMapper.selectAssetSwExcelListGroupbyNotWeak(paramMap);

                if (getWeakLevelsParams == null && getWeakLevelsParamsNot != null) {
                    getWeakLevelsParams2.add(getWeakLevelsParamsNot);
                }
            }
        } // end for

        for (int i = 0; i < getWeakLevelsParams2.size(); i++) {
            getWeakLevelsParams2.get(i).setAuditRateByTotalMinus(1-getWeakLevelsParams2.get(i).getAuditRate());
            getWeakLevelsParams2.set(i, getWeakLevelsParams2.get(i));
        }
        for (SnetAssetSwAuditExcelDto dto : getWeakLevelsParams2) {

            double weaktotal = 0.0;
            int weakLevelHigh = 0;
            int weakLevelMiddle = 0;
            int weakLevelLow = 0;

            Map paramMap = new HashMap();
            paramMap.put("startAuditDay", startAuditDay);
            paramMap.put("endAuditDay", endAuditDay);

            paramMap.put("F_FLAG", "Y");
            paramMap.put("ASSET_CD", dto.getAssetCd());
            paramMap.put("AUDIT_DAY", dto.getAuditDay());

            if (notFlag) {

                if (StringUtils.equalsIgnoreCase("OS", dto.getSwType())) {

                    paramMap.put("SW_TYPE", dto.getSwType());

                    if (StringUtils.equalsIgnoreCase("Windows", dto.getSwNm())) {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", "Windows");
                    } else {

                        paramMap.put("SW_TYPE", "OS");
                        paramMap.put("SW_NM", dto.getSwNm());
                    }
                } else {

                    paramMap.put("SW_TYPE", dto.getSwType());
                    paramMap.remove("SW_NM");
                }
                paramMap.put("SW_DIR", dto.getSwDir());
            } else {
                paramMap.put("SW_TYPE", dto.getSwType());
            }
            // asset_cd 별 상,중,하 개수 조회
            List<SnetAssetSwAuditReportDto> weakLevels = testMapper.getWeakLevels(paramMap);
            paramMap.remove("F_FLAG");

            for (SnetAssetSwAuditReportDto weekReportDto : weakLevels) {

                if (StringUtils.equals("H", weekReportDto.getItemGrade())) {

                    weakLevelHigh = weekReportDto.getCount();
                }
                if (StringUtils.equals("M", weekReportDto.getItemGrade())) {

                    weakLevelMiddle = weekReportDto.getCount();
                }
                if (StringUtils.equals("L", weekReportDto.getItemGrade())) {

                    weakLevelLow = weekReportDto.getCount();
                }
            }
            weaktotal = dto.getWeakCount() + 0.0;

            // 리턴 dto 생성 후 세팅
            SnetAssetSwAuditExcelDto resultDto = new SnetAssetSwAuditExcelDto();
            resultDto.setSwType(dto.getSwType());

            String hostNmStr = null;
            double auditRateDbl = 0.0;
            if (StringUtils.isEmpty(dto.getHostNm())) {

                hostNmStr = "";
            } else {

                if (dto.getHostNm().length() <= 10) {
                    hostNmStr = StringUtils.substring(dto.getHostNm(), 0, 10);
                } else {
                    hostNmStr = StringUtils.substring(dto.getHostNm(), 0, 10) + "...";
                }
            }

            resultDto.setHostNm(hostNmStr);
            resultDto.setAuditRate(dto.getAuditRate());

            double weekHighRateDouble = 0.0;
            if (Double.isNaN(resultDto.getWeekHighRate())) {

                weekHighRateDouble = 0.0;
            } else {

                if (weaktotal == 0.0) {

                    weekHighRateDouble = 0.0;
                } else {
                    weekHighRateDouble = ((dto.getAuditRateByTotalMinus() * 100) * (weakLevelHigh / weaktotal)) / 100;
                }
            }

            double weekMiddleRateDouble = 0.0;
            if (Double.isNaN(resultDto.getWeekMiddleLowRate())) {

                weekMiddleRateDouble = 0.0;
            } else {

                if (weaktotal == 0.0) {

                    weekMiddleRateDouble = 0.0;
                } else {
                    weekMiddleRateDouble = ((dto.getAuditRateByTotalMinus() * 100) * ((weakLevelMiddle + weakLevelLow) / weaktotal)) / 100;
                }
            }

            resultDto.setWeekHighRate(weekHighRateDouble);
            resultDto.setWeekMiddleLowRate(weekMiddleRateDouble);

            list.add(resultDto);
        } // end for

        return list;
    } // end method

    /**
     *  취약점 상세 항목
     */
    @Override
    public List<SnetAssetSwAuditReportDto> selectAssetSwAuditReportNew(Map gAssetSwJob, boolean flag) throws Exception {

        List<SnetAssetSwAuditReportDto> list = Lists.newArrayList();
        boolean notFlag = false;
        String swTypeStr = null;
        String startAuditDay = gAssetSwJob.get("startAuditDay").toString();
        String endAuditDay = gAssetSwJob.get("endAuditDay").toString();

        String reqCd = gAssetSwJob.get("REQ_CD").toString();

        if (gAssetSwJob.get("NOT_FLAG") != null) {
            notFlag = false;
        } else if (gAssetSwJob.get("LNOT_FLAG") != null) {
            notFlag = true;
            gAssetSwJob.remove("SW_NM");
        }

        String swTypeNm = null;
        if (gAssetSwJob.get("SW_NM") != null) {
            swTypeNm = gAssetSwJob.get("SW_NM").toString();
        }

        if (gAssetSwJob.get("auditType") != null) {
            gAssetSwJob.put("FILE_TYPE", gAssetSwJob.get("auditType").toString());
        }

        gAssetSwJob.put("HOST_GRD_FLAG", "Y");
        List<SnetAssetSwAuditExcelDto> list7Re = testMapper.selectAssetSwExcelListNew(gAssetSwJob);
        gAssetSwJob.remove("HOST_GRD_FLAG");

        List<SnetAssetSwAuditReportDto> reportDtoList = Lists.newArrayList();
        String tempAssetCd = null;
        String tempAuditDay = null;

        for (SnetAssetSwAuditExcelDto excelDto : list7Re) {

            tempAssetCd = excelDto.getAssetCd();
            Map paramMap = new HashMap();
            paramMap.put("startAuditDay", startAuditDay);
            paramMap.put("endAuditDay", endAuditDay);

            paramMap.put("REQ_CD", reqCd);
            paramMap.put("FILE_TYPE", gAssetSwJob.get("FILE_TYPE"));

            if (notFlag) {
                if (!StringUtils.equalsIgnoreCase("Windows", swTypeNm)) {
                    paramMap.put("SW_TYPE", "OS");
                    paramMap.put("SW_NM", excelDto.getSwNm());
                }
            } else {
                paramMap.put("SW_TYPE", excelDto.getSwType());
            }
            paramMap.put("ASSET_CD", tempAssetCd);

            SnetAssetSwAuditReportDto reportDto1 = null;
            if (flag) {
                reportDto1 = testMapper.selectAssetSwAuditReportNewGetMinAuditDay(paramMap);
            } else {
                reportDto1 = testMapper.selectAssetSwAuditReportNewGetMaxAuditDay(paramMap);
            }


            if (reportDto1 != null) {

                tempAuditDay = reportDto1.getAuditDay();

                paramMap.put("AUDIT_DAY", tempAuditDay);
                SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                paramMap.put("CREATE_DATE", reportDto2.getCreateDate());
                SnetAssetSwAuditReportDto reportDto3 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
                reportDtoList.add(reportDto3);
            }
        } // end for

        List<List<SnetAssetSwAuditReportDto>> groupbyReportGrpNmLists = Lists.newArrayList();
        for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

            Map paramMap3 = new HashMap();
            paramMap3.put("startAuditDay", startAuditDay);
            paramMap3.put("endAuditDay", endAuditDay);

            if (notFlag) {
                if (!StringUtils.equalsIgnoreCase("Windows", swTypeNm)) {
                    paramMap3.put("SW_TYPE", "OS");
                    paramMap3.put("SW_NM", reportDto.getSwNm());
                }
            } else {
                paramMap3.put("SW_TYPE", reportDto.getSwType());
            }
            paramMap3.put("ASSET_CD", reportDto.getAssetCd());
            paramMap3.put("AUDIT_DAY", reportDto.getAuditDay());
            paramMap3.put("CREATE_DATE", reportDto.getCreateDate());
            paramMap3.put("ITEM_RESULT", "F");
            List<SnetAssetSwAuditReportDto> groupbyReportGrpNmList = testMapper.selectAssetSwAuditReportNew(paramMap3);

            paramMap3.remove("ITEM_RESULT");
            groupbyReportGrpNmLists.add(groupbyReportGrpNmList);
        }

        int listSize = 0;
        for (List<SnetAssetSwAuditReportDto> list2 : groupbyReportGrpNmLists) {
            listSize += list2.size();
        }

        for (List<SnetAssetSwAuditReportDto> list2 : groupbyReportGrpNmLists) {

            for (SnetAssetSwAuditReportDto dto : list2) {

                Map gAssetSwReportMap = new HashMap();
                gAssetSwReportMap.put("startAuditDay", startAuditDay);
                gAssetSwReportMap.put("endAuditDay", endAuditDay);

                gAssetSwReportMap.put("ASSET_CD", dto.getAssetCd());
                gAssetSwReportMap.put("AUDIT_DAY", dto.getAuditDay());
                gAssetSwReportMap.put("SW_TYPE", dto.getSwType());
                gAssetSwReportMap.put("ITEM_RESULT", dto.getItemResult());
                gAssetSwReportMap.put("DIAGNOSIS_CD", dto.getDiagnosisCd());

                if (StringUtils.isNotEmpty(dto.getSwNm())) {
                    gAssetSwReportMap.put("SW_NM", dto.getSwNm());
                }
                gAssetSwReportMap.put("CREATE_DATE", dto.getCreateDate());
                List<SnetAssetSwAuditReportDto> assetSwAuditReportDtoList2 = testMapper.selectAssetSwAuditReportNew(gAssetSwReportMap);

                SnetAssetSwAuditReportDto vo = new SnetAssetSwAuditReportDto();
                vo.setItemGrpNm(dto.getItemGrpNm());
                vo.setDiagnosisCd(dto.getDiagnosisCd());
                vo.setDivideNum(StringUtils.right(dto.getDiagnosisCd(),4));
                // -> to do test : 취약점 항목 분류항목 그룹핑 수정
                vo.setItemNmDesc(makeCellString(dto.getItemNmDesc()));
                vo.setItemGradeView(dto.getItemGradeView());
                vo.setHostCount(assetSwAuditReportDtoList2.size());

                SnetConfigAuditItem snetConfigAuditItem = testMapper.selectConfigAuditItem(gAssetSwReportMap);
                if (snetConfigAuditItem != null && StringUtils.isNotEmpty(snetConfigAuditItem.getItemStandard())) {

                    vo.setItemStandard(snetConfigAuditItem.getItemStandard()); // 판단기준
                } else {

                    vo.setItemStandard("NONE"); // 판단기준
                }
                if (snetConfigAuditItem != null && StringUtils.isNotEmpty(snetConfigAuditItem.getItemCountermeasure())) {

                    vo.setItemCountermeasure(snetConfigAuditItem.getItemCountermeasure()); // 조치방법
                } else {
                    vo.setItemCountermeasure("NONE"); // 조치방법
                }

                vo.setEtc(dto.getEtc());

                list.add(vo);
            } // end for
        } // end for


        // 분류항목     : itemGrpNm=1. 계정 관리
        // 분류번호     : 점검항목의 공백구분으로 앞 몇자리 사용 <- 임시로 diagnosis_cd 사용
        // 점검항목     : itemNm -> aes_decryptCustomize
        // 위험도      : itemGradeView=상
        // HOST수        : 1
        // 결과를 param 으로 (asset_cd, audit_day, sw_type, sw_nm, diagnosis_cd) 로 REPORT 테이블 조회해서 count
        // 비고     : etc 조회

        TreeMap<String, List<SnetAssetSwAuditReportDto>> groupingList =
                list.stream().collect(Collectors.groupingBy(SnetAssetSwAuditReportDto::getItemNmDesc, TreeMap::new, Collectors.toList()));
        List<SnetAssetSwAuditReportDto> resultList = Lists.newArrayList();
        for (Map.Entry<String, List<SnetAssetSwAuditReportDto>> grpNmVo : groupingList.entrySet()) {

            int hostNmCount = 0;
            String itemNmDescKey = grpNmVo.getKey();
            SnetAssetSwAuditReportDto vo = new SnetAssetSwAuditReportDto();

            for (SnetAssetSwAuditReportDto dto3 : grpNmVo.getValue()) {

                if (StringUtils.equalsIgnoreCase(itemNmDescKey, dto3.getItemNmDesc())) {

                    hostNmCount += dto3.getHostCount();
                }
                vo.setItemGrpNm(dto3.getItemGrpNm());
                vo.setItemNmDesc(dto3.getItemNmDesc());
                vo.setItemGradeView(dto3.getItemGradeView());
                vo.setItemStandard(dto3.getItemStandard());
                vo.setItemCountermeasure(dto3.getItemCountermeasure());
                vo.setEtc(dto3.getEtc());
                vo.setDivideNum("[".concat(StringUtils.right(dto3.getDiagnosisCd(),4)).concat("]"));
                vo.setDiagnosisCd(dto3.getDiagnosisCd());
            }
            vo.setHostCount(hostNmCount);
            resultList.add(vo);
        } // end for

        resultList.sort(Comparator.comparing(SnetAssetSwAuditReportDto::getItemGrpNm).thenComparing(SnetAssetSwAuditReportDto::getDiagnosisCd));

        return resultList;
    } // end method

    private String makeCellString(String cellData){

        String result = "";
        int dotIndexOf = 0;
        String substrGrpNm = "";

        dotIndexOf = cellData.indexOf(".", 3);
        if (dotIndexOf == -1 || dotIndexOf > 4)
            substrGrpNm = StringUtils.substring(cellData, StringUtils.indexOf(cellData, " ") + 1);
        else
            substrGrpNm = StringUtils.substring(cellData, dotIndexOf + 1);

        cellData = substrGrpNm;

        if (StringUtils.isNotEmpty(cellData)) {
            if (cellData.length() > 100)
                result = StringUtil.substring(cellData, 0, 100) + " ......";
            else
                result = StringUtil.substring(cellData, 0, 100);
        } else {
            result = "";
        }
        return result;
    }

}
