package com.mobigen.snet.supportagent.component;

import com.google.common.collect.Lists;
import com.mobigen.snet.supportagent.dao.ExcelExportMapper;
import com.mobigen.snet.supportagent.dao.TestMapper;
import com.mobigen.snet.supportagent.exception.ErrorException;
import com.mobigen.snet.supportagent.models.*;
import com.mobigen.snet.supportagent.models.api.*;
import com.mobigen.snet.supportagent.service.TestService;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 'sg_supprotmanager 프로젝트 - 종합보고서'
 */
@Component
public class ExcelExportTotalComponent extends AbstractComponent {

    @Autowired
    private ExcelExportMapper excelExportMapper;

    @Autowired(required = false)
    private TestMapper testMapper;

    @Autowired(required = false)
    TestService testService;

    @Value("${snet.support.zip.path}")
    private String excelPath;

    // to do test : 서버에 올릴때 주석 변경할 것
//	private static final String tempReportFilesPath = "D:/report/";
    private static final String tempReportFilesPath = "/usr/local/snetManager/data/excel/tempReportFiles/";

    // -> to do test : 보고서 API
    /**
     * 점검군별 전체 종합 결과 (보안점검) API URL
     */
    static final String TOTAL_SECU_URL = "https://localhost:10443/api/statistics/interwork/overallResultOfSecurityChkByInspectGrp";
    /**
     * 점검군별 전체 종합 결과 (이행점검) API URL
     */
    static final String TOTAL_IMPL_URL = "https://localhost:10443/api/statistics/interwork/overallResultOfImplChkByInspectGrp";
    /**
     * 점검군별 점검결과 (보안점검) API URL
     */
    static final String DETAIL_SECU_URL = "https://localhost:10443/api/statistics/interwork/resultOfSecurityChkByInspectGrp";
    /**
     * 즉시 조치 필요 대상 시스템 (보안점검) API URL
     */
    static final String TOTAL_SECU_ACT_URL = "https://localhost:10443/api/statistics/interwork/securityChkOfTargetRequireImdAction";
    /**
     * 점검군별 점검결과 (이행점검) API URL
     */
    static final String DETAIL_IMPL_URL = "https://localhost:10443/api/statistics/interwork/resultOfImplChkByInspectGrp";
    /**
     * 즉시 조치 필요 대상 시스템 (이행점검) API URL
     */
    static final String TOTAL_IMPL_ACT_URL = "https://localhost:10443/api/statistics/interwork/impkChkOfTargetSystemRequireImdAction";
    /**
     * 점검군별 점검결과 API restApiKey
     */
    static final String REST_API_KEY = "726188DEBA4846E8B2AE475EE09317CA";

    /**
     * SSL ignore
     */
    public void initSSL() {
        TrustManager[] trustAllCerts = {new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception localException) {
        }
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    /**
     * 종합보고서 생성
     */
    public int createTotalReport(Map obj, ReportRequestDto reportRequestDto) throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();

        // -> to do test : 보고서 API
        boolean reportApiCheckFlag = false;
        initSSL();
        RestTemplate rest = new RestTemplate();

        int resultCnt = 0;
        Map gAssetSwJob = new HashMap();
        boolean checkFlag = false;

        // .xlsx 파일 생성을 위한 XSSFWorkbook
//        XSSFWorkbook sxssfWorkbook = null;
        XSSFWorkbook gWorkbook = null;

        String reqCd = obj.get("REQ_CD").toString();
        String fileType = reportRequestDto.getFileType();

        gAssetSwJob.put("REQ_CD", reqCd);
        gAssetSwJob.put("FILE_TYPE", fileType);

        // SNET_ASSET_SW_AUDIT_EXCEL_LIST 의 min, max 구하기
        SnetAssetSwAuditExcelDto list7MinMaxAuditdayDto = testMapper.getExcelListGroupbyNewMinMaxAuditDay(gAssetSwJob);

        String startAuditDay = list7MinMaxAuditdayDto.getMinAuditDay();
        String endAuditDay = list7MinMaxAuditdayDto.getMaxAuditDay();

        // 보고서 API 호출 위한 flag
        SimpleDateFormat formatDay = new SimpleDateFormat("yyyyMMdd");
        Calendar timeDay = Calendar.getInstance();
        String createDay = formatDay.format(timeDay.getTime()); // createDay 는 오늘 날짜

        /*if (StringUtils.equals(createDay, startAuditDay) && StringUtils.equals(createDay, endAuditDay)) {
            reportApiCheckFlag = false;
        } else {
            reportApiCheckFlag = true;
        } // end if*/


        gAssetSwJob.put("startAuditDay", startAuditDay);
        gAssetSwJob.put("endAuditDay", endAuditDay);

        // 종합보고서 전체 장비 리스트
        List<SnetAssetSwAuditExcelDto> list7 = testMapper.selectAssetSwExcelListGroupbyNew(gAssetSwJob);

        List<SnetAssetSwAuditExcelDto> swTypeList = Lists.newArrayList();
        List<SnetAssetSwAuditExcelDto> swTypeListNew = Lists.newArrayList();
        List<SnetAssetSwAuditExcelDto> swTypeListNeo = Lists.newArrayList();
        String swTypeOs1 = null;
        String swTypeOs2 = null;
        boolean linuxFlag = true;

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

        swTypeList = swTypeList.stream().distinct().collect(Collectors.toList());

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
                        break;
                    }
                }
            } // end if
        } // end for

        TreeMap<String, List<SnetAssetSwAuditExcelDto>> groupbySwtype =
                swTypeList.stream().collect(Collectors.groupingBy(SnetAssetSwAuditExcelDto::getSwType, TreeMap::new, Collectors.toList()));

        for (Map.Entry<String, List<SnetAssetSwAuditExcelDto>> grpNmVo : groupbySwtype.entrySet()) {

            SnetAssetSwAuditExcelDto resultExcelDto = new SnetAssetSwAuditExcelDto();

            String key = grpNmVo.getKey();
            List<SnetAssetSwAuditExcelDto> values = grpNmVo.getValue();

            String swNmKey1 = null;
            String swNmKey2 = null;

            if (StringUtils.equalsIgnoreCase("OS", key)) {

                for (SnetAssetSwAuditExcelDto excelDto : values) {

                    if (StringUtils.equalsIgnoreCase("Windows", excelDto.getSwNm())) {
                        swNmKey2 = "Windows";
                        break;
                    }
                }

                for (SnetAssetSwAuditExcelDto excelDto : values) {

                    if (!StringUtils.equalsIgnoreCase("Windows", excelDto.getSwNm())) {
                        swNmKey1 = "Linux";
                        break;
                    }
                }
            } // end if

            resultExcelDto.setSwType(key);
            resultExcelDto.setSwNmLinux(swNmKey1);
            resultExcelDto.setSwNmWindows(swNmKey2);
            swTypeListNew.add(resultExcelDto);
        } // end for

        boolean linuxAddFlag = false;
        boolean windowsAddFlag = false;
        for (SnetAssetSwAuditExcelDto swTypeStr : swTypeListNew) {

            SnetAssetSwAuditExcelDto excelDto = new SnetAssetSwAuditExcelDto();
            excelDto.setSwType(swTypeStr.getSwType());

            if (StringUtils.equalsIgnoreCase("Windows", swTypeStr.getSwNmWindows())) {
                windowsAddFlag = true;
            }

            if (StringUtils.equalsIgnoreCase("Linux", swTypeStr.getSwNmLinux())) {
                linuxAddFlag = true;
            }

            if (!StringUtils.equalsIgnoreCase("OS", swTypeStr.getSwType())) {

                int z = 0;
                String key = swTypeStr.getSwType();

                if (StringUtils.equals("DB", key)) {
                    z = 3;
                } else if (StringUtils.equals("WEB", key)) {
                    z = 4;
                } else if (StringUtils.equals("WAS", key)) {
                    z = 5;
                } else if (StringUtils.equals("NW", key)) {
                    z = 6;
                } else if (StringUtils.equals("URL", key)) {
                    z = 7;
                }
                excelDto.setSeq(z);
                swTypeListNeo.add(excelDto);
            }
        } // end for

        if (windowsAddFlag) {

            SnetAssetSwAuditExcelDto excelDto = new SnetAssetSwAuditExcelDto();
            excelDto.setSeq(2);
            excelDto.setSwType("OS");
            excelDto.setSwNm("Windows");
            swTypeListNeo.add(excelDto);
        }

        if (linuxAddFlag) {
            SnetAssetSwAuditExcelDto excelDto = new SnetAssetSwAuditExcelDto();
            excelDto.setSeq(1);
            excelDto.setSwType("OS");
            excelDto.setSwNm("Linux");
            swTypeListNeo.add(excelDto);
        }

        // 점검군 sw_type, sheet name 비교해서 sheet 삭제 처리하기 위한 변수
        String swTypeStr = null;
        List<String> swTypeStrList = Lists.newArrayList();
        String[] sheetArr = {
                "Linux"
                , "Windows"
                , "DB"
                , "WEB"
                , "WAS"
                , "NW"
                , "Web App(URL)"
                , "기타"
        };
        List<String> sheetNotList = Arrays.asList(sheetArr);

        // read file
        // to do test : (서버에 올릴때에는 주석 제거)
//        String tempReportFilesPath = "D:/report";
//        String excelTempPath = "D:/tempFiles";

//        FileInputStream is = new FileInputStream(new File(excelTempPath + "/total_report_mapping3.xlsx"));
        FileInputStream is = new FileInputStream(new File("/usr/local/snetManager/data/excel/tempFiles" + "/total_report_mapping3.xlsx"));

        /*-------------------- [ sheet 값 매핑 처리 ] --------------------*/
        // (1) jXLS setting (너무느림)

//        XLSTransformer transformer = new XLSTransformer();
//        Map<String, Object> beans = new HashMap<>();
//        Map sheetMaps1 = new HashMap();
//        beans.put("data1", sheetMaps1);
//        gWorkbook = (XSSFWorkbook) transformer.transformXLS(is, beans);

        // (2) apache poi (값 내려서 확인)
        gWorkbook = new XSSFWorkbook(is);
//        sxssfWorkbook = new SXSSFWorkbook(gWorkbook, -1);

        XSSFSheet sheet4 = gWorkbook.getSheet("Ⅲ. 수행내역");

        String str1 = "스마트가드";
        String str2 = "보안점검 상세 결과보고서";

        int listSize = swTypeListNeo.size();

        String[] listArr = {"OS", "DB", "WEB", "WAS", "NW", "URL"};
        boolean listFlag = false;
        String listStr = null;

        int ii2 = 6;
        int j = 0;


        // 점검 산출물 정렬
        swTypeListNeo.sort((SnetAssetSwAuditExcelDto s1, SnetAssetSwAuditExcelDto s2) -> s1.getSeq() - s2.getSeq());

        for (int i = 6; i < ii2 + listSize; i++) {

            boolean b = false;

            for (String x : listArr) {
                if (StringUtils.equalsIgnoreCase(swTypeListNeo.get(j).getSwType(), x)) {
                    b = true;
                    break;
                } // end if
            } // end for

            listFlag = b;

            if (listFlag) {

                if (StringUtils.equalsIgnoreCase("URL", swTypeListNeo.get(j).getSwType())) {


                    getCell(sheet4, i, 4).setCellValue("Web App(URL)");
                    getCell(sheet4, i, 7).setCellValue(str1 + " " + "Web App(URL)" + " " + str2);
                    listStr = "Web App(URL)";
                } else {

                    if (StringUtils.equalsIgnoreCase("OS", swTypeListNeo.get(j).getSwType())) {

                        if (StringUtils.equalsIgnoreCase("Windows", swTypeListNeo.get(j).getSwNm())) {

                            listStr = "Windows";
                        } else {

                            listStr = "Linux";
                        }
                    } else {

                        listStr = swTypeListNeo.get(j).getSwType();
                    }

                    getCell(sheet4, i, 4).setCellValue(listStr);
                    getCell(sheet4, i, 7).setCellValue(str1 + " " + listStr + " " + str2);
                }
            } else {


                getCell(sheet4, i, 4).setCellValue("기타");
                getCell(sheet4, i, 7).setCellValue(str1 + " " + "기타" + " " + str2);
            } // end if else
            j++;
            swTypeStrList.add(listStr);
        } // end for

        // 종합보고서 보안점검을 위한 최초진단일, 최종진단일
        Map paramMap = new HashMap();
        paramMap.put("REQ_CD", reqCd);
        paramMap.put("startAuditDay", startAuditDay);
        paramMap.put("endAuditDay", endAuditDay);

        XSSFSheet sheet1 = gWorkbook.getSheet("표지");
        getCell(sheet1, 10, 8).setCellValue(String.format("%s/%s/%s ~ %s/%s/%s"
                , StringUtils.substring(startAuditDay, 0, 4)
                , StringUtils.substring(startAuditDay, 4, 6)
                , StringUtils.substring(startAuditDay, 6, 8)
                , StringUtils.substring(endAuditDay, 0, 4)
                , StringUtils.substring(endAuditDay, 4, 6)
                , StringUtils.substring(endAuditDay, 6, 8)));

        getCell(sheet1, 10, 8).setCellStyle(cellStyle(gWorkbook, 12));

        // 종합보고서 표지 - 점검군별 점검 결과 목차 생성 추가
        int k = 5;
        j = 0;
        ii2 = 92 + (listSize * 2);
        for (int i = 92; i < ii2; i+=2) {

            if (StringUtils.equalsIgnoreCase("OS", swTypeListNeo.get(j).getSwType())) {
                getCell(sheet1, i, 1).setCellValue("\t          - " + swTypeListNeo.get(j).getSwNm()+ "   ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙");
            }else if (StringUtils.equalsIgnoreCase("URL", swTypeListNeo.get(j).getSwType())) {
                getCell(sheet1, i, 1).setCellValue("\t          - Web App(URL)   ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙");
            }else {
                getCell(sheet1, i, 1).setCellValue("\t          - " + swTypeListNeo.get(j).getSwType()+ "   ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙");
            }

            getCell(sheet1, i, 11).setCellValue("∙ ∙ ∙ ∙ ∙ ∙ ∙ ∙ " + k);
            j++;
            k+=6;
        }
        /*----------------------------------------------------------------------------------------------------*/
        XSSFSheet sheet5 = gWorkbook.getSheet("Ⅳ. 전체종합결과");

        List<SnetAssetSwAuditExcelDto> swTypeListNeo2 = Lists.newArrayList();


        for (SnetAssetSwAuditExcelDto dto : swTypeListNeo) {

            SnetAssetSwAuditExcelDto resultDto = new SnetAssetSwAuditExcelDto();

            if (StringUtils.equalsIgnoreCase("Linux", dto.getSwNm())) {
                resultDto.setSwType("OS");
                resultDto.setSwNm("Linux");
                swTypeListNeo2.add(resultDto);
                break;
            }
        }
        for (SnetAssetSwAuditExcelDto dto : swTypeListNeo) {

            SnetAssetSwAuditExcelDto resultDto = new SnetAssetSwAuditExcelDto();

            if (StringUtils.equalsIgnoreCase("Windows", dto.getSwNm())) {
                resultDto.setSwType("OS");
                resultDto.setSwNm("Windows");
                swTypeListNeo2.add(resultDto);
                break;
            }
        }
        for (SnetAssetSwAuditExcelDto dto : swTypeListNeo) {

            SnetAssetSwAuditExcelDto resultDto = new SnetAssetSwAuditExcelDto();

            if (StringUtils.equalsIgnoreCase("DB", dto.getSwType())) {
                resultDto.setSwType("DB");
                swTypeListNeo2.add(resultDto);
                break;
            }
        }
        for (SnetAssetSwAuditExcelDto dto : swTypeListNeo) {

            SnetAssetSwAuditExcelDto resultDto = new SnetAssetSwAuditExcelDto();

            if (StringUtils.equalsIgnoreCase("WEB", dto.getSwType())) {
                resultDto.setSwType("WEB");
                swTypeListNeo2.add(resultDto);
                break;
            }
        }
        for (SnetAssetSwAuditExcelDto dto : swTypeListNeo) {

            SnetAssetSwAuditExcelDto resultDto = new SnetAssetSwAuditExcelDto();

            if (StringUtils.equalsIgnoreCase("WAS", dto.getSwType())) {
                resultDto.setSwType("WAS");
                swTypeListNeo2.add(resultDto);
                break;
            }
        }
        for (SnetAssetSwAuditExcelDto dto : swTypeListNeo) {

            SnetAssetSwAuditExcelDto resultDto = new SnetAssetSwAuditExcelDto();

            if (StringUtils.equalsIgnoreCase("NW", dto.getSwType())) {
                resultDto.setSwType("NW");
                swTypeListNeo2.add(resultDto);
                break;
            }
        }
        for (SnetAssetSwAuditExcelDto dto : swTypeListNeo) {

            SnetAssetSwAuditExcelDto resultDto = new SnetAssetSwAuditExcelDto();

            if (StringUtils.equalsIgnoreCase("URL", dto.getSwType())) {
                resultDto.setSwType("URL");
                swTypeListNeo2.add(resultDto);
                break;
            }
        }

        boolean etcAddFlag = false;
        String[] listArrEtc = {"OS", "DB", "WEB", "WAS", "NW", "URL"};
        for (SnetAssetSwAuditExcelDto strVo : swTypeListNeo) {

            SnetAssetSwAuditExcelDto resultDto = new SnetAssetSwAuditExcelDto();

            etcAddFlag = Arrays.stream(listArrEtc).noneMatch(x -> StringUtils.equalsIgnoreCase(x, strVo.getSwType()));

            if (etcAddFlag) {

                resultDto.setSwType(strVo.getSwType());
                swTypeListNeo2.add(resultDto);
                break;
            }
        } // end for

        logger.info("*[종합보고서] Ⅳ. 전체종합결과 sheet Start");

        // 보안점검 종합 결과
        List<TotalOverallResultDto> checkTargetsList = testService.getCheckTargetsList(swTypeListNeo2, gAssetSwJob);

        if(checkTargetsList.isEmpty()) {
            logger.info("checkTargetsList is empty.");
            return 0;
        }

        // -> to do test : 보고서 API
        List<TotalOverallDto> checkTargetsListReportApi = Lists.newArrayList();

        if (reportApiCheckFlag) {

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            String userId = reportRequestDto.getReqUser();
            ApiRequestDto apiRequestDto = ApiRequestDto.builder()
                    .userId(userId)
                    .startDay(startAuditDay)
                    .endDay(endAuditDay)
                    .auditType(fileType)
                    .build();

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(TOTAL_SECU_URL)
                    .queryParam("restApiKey", REST_API_KEY);
            HttpEntity<ApiRequestDto> httpEntity = new HttpEntity<>(apiRequestDto, httpHeaders);
            ApiResult apiResult = null;
            ApiResponseDto tempDto = null;
            try {

                apiResult = rest.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, ApiResult.class).getBody();

                if (apiResult != null) {

                    ModelMapper modelMapper = new ModelMapper();

                    tempDto = modelMapper.map(apiResult.getResultData(), ApiResponseDto.class);
                    checkTargetsListReportApi = tempDto.getTotalOverallDtoList().stream().collect(Collectors.toList());
                } // end if
            } catch (Exception e) {

                logger.error(e.toString());
            }

            logger.info("*[점검군별 전체 종합 결과 (보안점검)] 보고서 생성");
            logger.info("*[점검군별 전체 종합 결과 (보안점검)] 보고서 API 호출");
        } // end if

        if (checkTargetsList != null && checkTargetsList.size() > 0 && checkTargetsListReportApi != null && checkTargetsListReportApi.size() > 0)

            for (int i = 0; i < checkTargetsList.size(); i++) {

                TotalOverallResultDto tempDto = checkTargetsList.get(i);
                TotalOverallDto targetDto = checkTargetsListReportApi.get(i);

                if (tempDto.getValue1() == targetDto.getAssetCntTotal()) {

                    tempDto.setValue1(targetDto.getAssetCntTotal());
                }
                if (tempDto.getValue2() == targetDto.getItemCntTotal()) {

                    tempDto.setValue2(targetDto.getItemCntTotal());
                }
                if (tempDto.getValue3() == targetDto.getAllItemCntTotal()) {

                    tempDto.setValue3(targetDto.getAllItemCntTotal());
                }
                if (tempDto.getValue4() == targetDto.getSchkGoodCntTotal()) {

                    tempDto.setValue4(targetDto.getSchkGoodCntTotal());
                }
                if (tempDto.getValue5() == targetDto.getSchkWeakCntTotal()) {

                    tempDto.setValue5(targetDto.getSchkWeakCntTotal());
                }
                if (tempDto.getValue6() == targetDto.getSchkNaCntTotal()) {

                    tempDto.setValue6(targetDto.getSchkNaCntTotal());
                }
            } // end for
        String etcSwTypeStr = null;
        for (int a2 = 0; a2 < checkTargetsList.size(); a2++) {

            if (StringUtils.equalsIgnoreCase("기타", checkTargetsList.get(a2).getKey())) {

                etcSwTypeStr = checkTargetsList.get(a2).getEtcSwType();
                break;
            }
        }

        int b3 = 0;
        for (int a3 = 8; a3 < 16; a3++) {

            if (StringUtils.equalsIgnoreCase("기타", checkTargetsList.get(b3).getKey())) {

                etcSwTypeStr = checkTargetsList.get(b3).getEtcSwType();
            }
            getCell(sheet5, a3, 1) .setCellValue (checkTargetsList.get(b3).getKey());   // 점검군
            getCell(sheet5, a3, 2) .setCellValue (checkTargetsList.get(b3).getValue1());// 점검수량
            //getCell(sheet5, a3, 3) .setCellValue (checkTargetsList.get(b3).getValue2());// 점검항목수
            getCell(sheet5, a3, 3) .setCellValue (checkTargetsList.get(b3).getValue3());// 전체점검항목수

            //getCell(sheet5, a3, 5) .setCellFormula (String.format("IF(C%d=\"-\",\"-\",E%d-L%d-R%d)",a3+1,a3+1,a3+1,a3+1));

            getCell(sheet5, a3, 4) .setCellValue(checkTargetsList.get(b3).getValue4());//양호
            getCell(sheet5, a3, 7).setCellValue(checkTargetsList.get(b3).getValue5()); //취약
            getCell(sheet5, a3, 11).setCellValue(checkTargetsList.get(b3).getValue6()); //NA
            getCell(sheet5, a3, 15).setCellValue(checkTargetsList.get(b3).getValue7()); //불가
            getCell(sheet5, a3, 19).setCellValue(checkTargetsList.get(b3).getValue8()); //인터뷰필요
            getCell(sheet5, a3, 22).setCellValue (checkTargetsList.get(b3).getAuditRate()); //보안준수율
            b3++;
        }
        Long grpNmTopWeekCountAvg = 0L;
        double grpNmTopWeekCountDbl = 0L;
        OptionalDouble asDouble = checkTargetsList.stream()
                .mapToInt(x -> x.getValue7())
                .average();
        grpNmTopWeekCountDbl = asDouble.getAsDouble();
        grpNmTopWeekCountAvg = Math.round(grpNmTopWeekCountDbl);

        for (int c = 8; c < 16 ; c++) {

            getCell(sheet5, c, 0).setCellFormula(String.format("IF(W%d=0, 0, 100-W%d)", c+1, c+1));
            getCell(sheet5, c, 28).setCellFormula(String.format("IF(AY%d = 0, 0, 100-AY%d)", c+1, c+1));
            getCell(sheet5, c, 29).setCellFormula("W17");
            getCell(sheet5, c, 56).setCellFormula("AY17");
        }

        // 보안준수율
//        getCell(sheet5, 8, 22)  .setCellFormula ("IFERROR(IF(C9=\"-\",\"-\",ROUND(Linux!AB56,2)),0)");
//        getCell(sheet5, 9, 22)  .setCellFormula ("IFERROR(IF(C10=\"-\",\"-\",ROUND(Windows!AB56,2)),0)");
//        getCell(sheet5, 10, 22) .setCellFormula ("IFERROR(IF(C11=\"-\",\"-\",ROUND(DB!AB56,2)),0)");
//        getCell(sheet5, 11, 22) .setCellFormula ("IFERROR(IF(C12=\"-\",\"-\",ROUND(WEB!AB56,2)),0)");
//        getCell(sheet5, 12, 22) .setCellFormula ("IFERROR(IF(C13=\"-\",\"-\",ROUND(WAS!AB56,2)),0)");
//        getCell(sheet5, 13, 22) .setCellFormula ("IFERROR(IF(C14=\"-\",\"-\",ROUND(NW!AB56,2)),0)");
//        getCell(sheet5, 14, 22) .setCellFormula ("IFERROR(IF(C15=\"-\",\"-\",ROUND('Web App(URL)'!AF56,2)),0)");
//        getCell(sheet5, 15, 22) .setCellFormula ("IFERROR(IF(C16=\"-\",\"-\",ROUND(기타!AB55,2)),0)");

        // 보안점검.보안수준
        getCell(sheet5, 8,  25) .setCellFormula ("IF(C9=0,\"해당사항없음\",IF(W9>=90,\"우수\",IF(AND(W9<90,W9>=80),\"양호\",IF(AND(W9<80,W9>=70),\"보통\",\"미흡\"))))");
        getCell(sheet5, 9,  25) .setCellFormula ("IF(C10=0,\"해당사항없음\",IF(W10>=90,\"우수\",IF(AND(W10<90,W10>=80),\"양호\",IF(AND(W10<80,W10>=70),\"보통\",\"미흡\"))))");
        getCell(sheet5, 10, 25) .setCellFormula ("IF(C11=0,\"해당사항없음\",IF(W11>=90,\"우수\",IF(AND(W11<90,W11>=80),\"양호\",IF(AND(W11<80,W11>=70),\"보통\",\"미흡\"))))");
        getCell(sheet5, 11, 25) .setCellFormula ("IF(C12=0,\"해당사항없음\",IF(W12>=90,\"우수\",IF(AND(W12<90,W12>=80),\"양호\",IF(AND(W12<80,W12>=70),\"보통\",\"미흡\"))))");
        getCell(sheet5, 12, 25) .setCellFormula ("IF(C13=0,\"해당사항없음\",IF(W13>=90,\"우수\",IF(AND(W13<90,W13>=80),\"양호\",IF(AND(W13<80,W13>=70),\"보통\",\"미흡\"))))");
        getCell(sheet5, 13, 25) .setCellFormula ("IF(C14=0,\"해당사항없음\",IF(W14>=90,\"우수\",IF(AND(W14<90,W14>=80),\"양호\",IF(AND(W14<80,W14>=70),\"보통\",\"미흡\"))))");
        getCell(sheet5, 14, 25) .setCellFormula ("IF(C15=0,\"해당사항없음\",IF(W15>=90,\"우수\",IF(AND(W15<90,W15>=80),\"양호\",IF(AND(W15<80,W15>=70),\"보통\",\"미흡\"))))");
        getCell(sheet5, 15, 25) .setCellFormula ("IF(C16=0,\"해당사항없음\",IF(W16>=90,\"우수\",IF(AND(W16<90,W16>=80),\"양호\",IF(AND(W16<80,W16>=70),\"보통\",\"미흡\"))))");

        // 보안점검.Total
        getCell(sheet5, 16, 2)  .setCellFormula ("IF(COUNTIF(C9:C16,\"-\")=7,\"-\",SUM(C9:C16))"); //점검수량
        getCell(sheet5, 16, 3)  .setCellFormula ("IF(C17=\"-\",\"-\",SUM(D9:D16))");    //전체 점검 항목 수
        getCell(sheet5, 16, 4)  .setCellFormula ("IF(C17=\"-\",\"-\",SUM(E9:G16))");    //양호
        getCell(sheet5, 16, 7)  .setCellFormula ("IF(C17=\"-\",\"-\",SUM(H9:K16))");    //취약
        getCell(sheet5, 16, 11) .setCellFormula ("IF(C17=\"-\",\"-\",SUM(L9:L16))");    //NA
        getCell(sheet5, 16, 15) .setCellFormula ("IF(C17=\"-\",\"-\",SUM(P9:S16))");    //불가
        getCell(sheet5, 16, 19) .setCellFormula ("IF(C17=\"-\",\"-\",SUM(T9:V16))");    //인터뷰필요
        getCell(sheet5, 16, 22) .setCellFormula ("IF(C17=\"-\",\"-\",ROUND(IFERROR(SUM(W9:Y16)/COUNTIF(C9:C16,\">0\"),0),2))");
        getCell(sheet5, 16, 25) .setCellFormula ("IF(C17=\"-\",\"-\",IF(W17>=90,\"우수\",IF(AND(W17<90,W17>=80),\"양호\",IF(AND(W17<80,W17>=70),\"보통\",\"미흡\"))))");

        // 이행점검 종합 결과
        List<TotalOverallResultDto> migTargetsList = testService.getMigTargetsList(swTypeListNeo, gAssetSwJob);

        // -> to do test : 보고서 API
        List<TotalOverallDto> migTargetsListReportApi = Lists.newArrayList();

        if (reportApiCheckFlag) {

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            String userId = reportRequestDto.getReqUser();
            ApiRequestDto apiRequestDto = ApiRequestDto.builder()
                    .userId(userId)
                    .startDay(startAuditDay)
                    .endDay(endAuditDay)
                    .auditType(fileType)
                    .build();

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(TOTAL_IMPL_URL)
                    .queryParam("restApiKey", REST_API_KEY);
            HttpEntity<ApiRequestDto> httpEntity = new HttpEntity<>(apiRequestDto, httpHeaders);
            ApiResult apiResult = null;
            ApiResponseDto tempDto = null;
            try {

                apiResult = rest.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, ApiResult.class).getBody();

                if (apiResult != null) {

                    ModelMapper modelMapper = new ModelMapper();

                    tempDto = modelMapper.map(apiResult.getResultData(), ApiResponseDto.class);
                    migTargetsListReportApi = tempDto.getTotalOverallDtoList().stream().collect(Collectors.toList());
                } // end if
            } catch (Exception e) {

                logger.error(e.toString());
            }

            logger.info("*[점검군별 전체 종합 결과 (이행점검)] 보고서 생성");
            logger.info("*[점검군별 전체 종합 결과 (이행점검)] 보고서 API 호출");
        } // end if

        if (migTargetsList != null && migTargetsList.size() > 0 && migTargetsListReportApi != null && migTargetsListReportApi.size() > 0)

            for (int i = 0; i < migTargetsList.size(); i++) {

                TotalOverallResultDto tempDto = migTargetsList.get(i);
                TotalOverallDto targetDto = migTargetsListReportApi.get(i);

                if (tempDto.getValue1() == targetDto.getAssetCntTotal()) {

                    tempDto.setValue1(targetDto.getAssetCntTotal());
                }
                if (tempDto.getValue1() == targetDto.getAssetCntTotal()) {

                    tempDto.setValue1(targetDto.getAssetCntTotal());
                }
                if (tempDto.getValue2() == targetDto.getItemCntTotal()) {

                    tempDto.setValue2(targetDto.getItemCntTotal());
                }
                if (tempDto.getValue3() == targetDto.getAllItemCntTotal()) {

                    tempDto.setValue3(targetDto.getAllItemCntTotal());
                }
                if (tempDto.getValue4() == targetDto.getSchkGoodCntTotal()) {

                    tempDto.setValue4(targetDto.getSchkGoodCntTotal());
                }
                if (tempDto.getValue5() == targetDto.getSchkWeakCntTotal()) {

                    tempDto.setValue5(targetDto.getSchkWeakCntTotal());
                }
                if (tempDto.getValue6() == targetDto.getSchkNaCntTotal()) {

                    tempDto.setValue6(targetDto.getSchkNaCntTotal());
                }
            } // end for

        int e = 0;
        for (int d = 8; d < 16; d++) {

            getCell(sheet5, d, 30) .setCellValue (checkTargetsList.get(e).getKey());
            getCell(sheet5, d, 31) .setCellValue (migTargetsList.get(e).getValue1());  //점검수량
            //getCell(sheet5, d, 32) .setCellValue (checkTargetsList.get(e).getValue2());
            getCell(sheet5, d, 32).setCellValue(migTargetsList.get(e).getValue3());  //전체 점검 항목수

            getCell(sheet5, d, 33).setCellValue(migTargetsList.get(e).getValue4());    //양호
            getCell(sheet5, d, 36).setCellValue(migTargetsList.get(e).getValue5());    //취약

            //getCell(sheet5, d, 40).setCellFormula(String.format("H%d-AK%d", d + 1, d + 1)); // 조치개수

            getCell(sheet5, d, 40).setCellValue(migTargetsList.get(e).getValue6());     //NA
            getCell(sheet5, d, 43).setCellValue(migTargetsList.get(e).getValue7());     //불가
            getCell(sheet5, d, 47).setCellValue(migTargetsList.get(e).getValue8());     //인터뷰필요
            getCell(sheet5, d, 50).setCellValue(migTargetsList.get(e).getAuditRate());  //보안준수율
            e++;
        }

        // 보안준수율
//        getCell(sheet5, 8, 51)  .setCellFormula  ("IFERROR(IF(AF9=\"-\",\"-\",ROUND(Linux!BO56,2)),0)");
//        getCell(sheet5, 9, 51)  .setCellFormula  ("IFERROR(IF(AF10=\"-\",\"-\",ROUND(Windows!BO56,2)),0)");
//        getCell(sheet5, 10, 51)  .setCellFormula ("IFERROR(IF(AF11=\"-\",\"-\",ROUND(DB!BO56,2)),0)");
//        getCell(sheet5, 11, 51)  .setCellFormula ("IFERROR(IF(AF12=\"-\",\"-\",ROUND(WEB!BO56,2)),0)");
//        getCell(sheet5, 12, 51)  .setCellFormula ("IFERROR(IF(AF13=\"-\",\"-\",ROUND(WAS!BO56,2)),0)");
//        getCell(sheet5, 13, 51)  .setCellFormula ("IFERROR(IF(AF14=\"-\",\"-\",ROUND(NW!BO56,2)),0)");
//        getCell(sheet5, 14, 51)  .setCellFormula ("IFERROR(IF(AF15=\"-\",\"-\",ROUND('Web App(URL)'!BM56,2)),0)");
//        getCell(sheet5, 15, 51)  .setCellFormula ("IFERROR(IF(AF16=\"-\",\"-\",ROUND(기타!BO55,2)),0)");

        // 이행점검.보안수준 : IF(AF9="-","해당사항없음",IF(AZ9>=90,"우수",IF(AND(AZ9<90,AZ9>=80),"양호",IF(AND(AZ9<80,AZ9>=70),"보통","미흡")))) 54
        getCell(sheet5, 8,  52).setCellFormula("IF(AF9=0, \"해당사항없음\",IF(AY9>=90, \"우수\",IF(AND(AY9<90,AY9>=80),  \"양호\",IF(AND(AY9<80,AY9>=70),  \"보통\",\"미흡\"))))");
        getCell(sheet5, 9, 52).setCellFormula("IF(AF10=0,\"해당사항없음\",IF(AY10>=90,\"우수\",IF(AND(AY10<90,AY10>=80),\"양호\",IF(AND(AY10<80,AY10>=70),\"보통\",\"미흡\"))))");
        getCell(sheet5, 10, 52).setCellFormula("IF(AF11=0,\"해당사항없음\",IF(AY11>=90,\"우수\",IF(AND(AY11<90,AY11>=80),\"양호\",IF(AND(AY11<80,AY11>=70),\"보통\",\"미흡\"))))");
        getCell(sheet5, 11, 52).setCellFormula("IF(AF12=0,\"해당사항없음\",IF(AY12>=90,\"우수\",IF(AND(AY12<90,AY12>=80),\"양호\",IF(AND(AY12<80,AY12>=70),\"보통\",\"미흡\"))))");
        getCell(sheet5, 12, 52).setCellFormula("IF(AF13=0,\"해당사항없음\",IF(AY13>=90,\"우수\",IF(AND(AY13<90,AY13>=80),\"양호\",IF(AND(AY13<80,AY13>=70),\"보통\",\"미흡\"))))");
        getCell(sheet5, 13, 52).setCellFormula("IF(AF14=0,\"해당사항없음\",IF(AY14>=90,\"우수\",IF(AND(AY14<90,AY14>=80),\"양호\",IF(AND(AY14<80,AY14>=70),\"보통\",\"미흡\"))))");
        getCell(sheet5, 14, 52).setCellFormula("IF(AF15=0,\"해당사항없음\",IF(AY15>=90,\"우수\",IF(AND(AY15<90,AY15>=80),\"양호\",IF(AND(AY15<80,AY15>=70),\"보통\",\"미흡\"))))");
        getCell(sheet5, 15, 52).setCellFormula("IF(AF16=0,\"해당사항없음\",IF(AY16>=90,\"우수\",IF(AND(AY16<90,AY16>=80),\"양호\",IF(AND(AY16<80,AY16>=70),\"보통\",\"미흡\"))))");

        // 이행점검.Total
        getCell(sheet5, 16, 31).setCellFormula("IF(COUNTIF(AF9:AF16,\"-\")=7,\"-\",SUM(AF9:AF16))");  //점검수량
        //getCell(sheet5, 16, 32).setCellFormula ("IF(AF17=\"-\",\"-\",SUM(AG9:AG16))");  //점검항목수
        getCell(sheet5, 16, 32).setCellFormula("IF(AF17=\"-\",\"-\",SUM(AG9:AG16))");     //전체점검항목수
        getCell(sheet5, 16, 33).setCellFormula("IF(AF17=\"-\",\"-\",SUM(AH9:AJ16))");      //양호
        getCell(sheet5, 16, 36).setCellFormula("IF(AF17=\"-\",\"-\",SUM(AK9:AN16))");      //취약
        getCell(sheet5, 16, 40).setCellFormula("IF(AF17=\"-\",\"-\",SUM(AO9:AQ16))");      //NA
        getCell(sheet5, 16, 43).setCellFormula("IF(AF17=\"-\",\"-\",SUM(AR9:AU16))");      //불가
        getCell(sheet5, 16, 47).setCellFormula("IF(AF17=\"-\",\"-\",SUM(AV9:AX16))");      //인터뷰필요
        getCell(sheet5, 16, 50).setCellFormula("IF(AF17=\"-\",\"-\",ROUND(IFERROR(SUM(AY9:AY16)/COUNTIF(C9:C16,\">0\"),0),2))"); //보안준수율
        getCell(sheet5, 16, 52).setCellFormula("IF(C17=\"-\",\"-\",IF(AY17>=90,\"우수\",IF(AND(AY17<90,AZ17>=80),\"양호\",IF(AND(AY17<80,AY17>=70),\"보통\",\"미흡\"))))");

        logger.info("*[종합보고서] Ⅳ. 전체종합결과 sheet End");
        /*---------- #- Ⅳ. 전체종합결과 End ----------*/



        /*---------- #- Ⅵ. 보안점검 결과 전체 sheet Start ----------*/
        try {
            logger.info("*[종합보고서] Ⅵ. 보안점검 결과 전체 sheet Start");
            for (TotalOverallResultDto vo : checkTargetsList) {

                if (vo.getValue1() != 0 && vo.getAssetCdIn() != null) {

                    Map gAssetSwJob1 = new HashMap<>();
                    gAssetSwJob1.put("REQ_CD", reqCd);
                    gAssetSwJob1.put("REQ_USER", reportRequestDto.getReqUser());
                    gAssetSwJob1.put("startAuditDay", startAuditDay);
                    gAssetSwJob1.put("endAuditDay", endAuditDay);
                    gAssetSwJob1.put("auditType", fileType);
                    if (StringUtils.equalsIgnoreCase("Linux", vo.getKey())) {
                        createTotalReportTargets1(gWorkbook, checkTargetsList, gAssetSwJob1, rest);
                    }
                    /* end Linux*/

                    Map gAssetSwJob2 = new HashMap<>();
                    gAssetSwJob2.put("REQ_CD", reqCd);
                    gAssetSwJob2.put("REQ_USER", reportRequestDto.getReqUser());
                    gAssetSwJob2.put("startAuditDay", startAuditDay);
                    gAssetSwJob2.put("endAuditDay", endAuditDay);
                    gAssetSwJob2.put("auditType", fileType);
                    if (StringUtils.equalsIgnoreCase("Windows", vo.getKey())) {
                        createTotalReportTargets2(gWorkbook, checkTargetsList, gAssetSwJob2, rest);
                    }
                    /* end Window*/
                    Map gAssetSwJob3 = new HashMap<>();
                    gAssetSwJob3.put("REQ_CD", reqCd);
                    gAssetSwJob3.put("REQ_USER", reportRequestDto.getReqUser());
                    gAssetSwJob3.put("startAuditDay", startAuditDay);
                    gAssetSwJob3.put("endAuditDay", endAuditDay);
                    gAssetSwJob3.put("auditType", fileType);
                    if (StringUtils.equalsIgnoreCase("DB", vo.getKey())) {
                        createTotalReportTargets3(gWorkbook, checkTargetsList, gAssetSwJob3, rest);
                    }
                    /* end DB*/

                    Map gAssetSwJob4 = new HashMap<>();
                    gAssetSwJob4.put("REQ_CD", reqCd);
                    gAssetSwJob4.put("REQ_USER", reportRequestDto.getReqUser());
                    gAssetSwJob4.put("startAuditDay", startAuditDay);
                    gAssetSwJob4.put("endAuditDay", endAuditDay);
                    gAssetSwJob4.put("auditType", fileType);
                    if (StringUtils.equalsIgnoreCase("WEB", vo.getKey())) {
                        createTotalReportTargets4(gWorkbook, checkTargetsList, gAssetSwJob4, rest);
                    }
                    /* end WEB*/

                    Map gAssetSwJob5 = new HashMap<>();
                    gAssetSwJob5.put("REQ_CD", reqCd);
                    gAssetSwJob5.put("REQ_USER", reportRequestDto.getReqUser());
                    gAssetSwJob5.put("startAuditDay", startAuditDay);
                    gAssetSwJob5.put("endAuditDay", endAuditDay);
                    gAssetSwJob5.put("auditType", fileType);
                    if (StringUtils.equalsIgnoreCase("WAS", vo.getKey())) {
                        createTotalReportTargets5(gWorkbook, checkTargetsList, gAssetSwJob5, rest);
                    }
                    /* end WAS*/

                    Map gAssetSwJob6 = new HashMap<>();
                    gAssetSwJob6.put("REQ_CD", reqCd);
                    gAssetSwJob6.put("REQ_USER", reportRequestDto.getReqUser());
                    gAssetSwJob6.put("startAuditDay", startAuditDay);
                    gAssetSwJob6.put("endAuditDay", endAuditDay);
                    gAssetSwJob6.put("auditType", fileType);
                    if (StringUtils.equalsIgnoreCase("NW", vo.getKey())) {
                        createTotalReportTargets6(gWorkbook, checkTargetsList, gAssetSwJob6, rest);
                    }
                    /* end NW*/

                    Map gAssetSwJob7 = new HashMap<>();
                    gAssetSwJob7.put("REQ_CD", reqCd);
                    gAssetSwJob7.put("REQ_USER", reportRequestDto.getReqUser());
                    gAssetSwJob7.put("startAuditDay", startAuditDay);
                    gAssetSwJob7.put("endAuditDay", endAuditDay);
                    gAssetSwJob7.put("auditType", fileType);
                    if (StringUtils.equalsIgnoreCase("Web App(URL)", vo.getKey())) {
                        createTotalReportTargets7(gWorkbook, checkTargetsList, migTargetsList, gAssetSwJob7, rest);
                    }
                    /* end Web App(URL) */

                    Map gAssetSwJob8 = new HashMap<>();
                    gAssetSwJob8.put("REQ_CD", reqCd);
                    gAssetSwJob8.put("REQ_USER", reportRequestDto.getReqUser());
                    gAssetSwJob8.put("SW_TYPE", etcSwTypeStr);
                    gAssetSwJob8.put("startAuditDay", startAuditDay);
                    gAssetSwJob8.put("endAuditDay", endAuditDay);
                    gAssetSwJob8.put("auditType", fileType);
                    if (StringUtils.equalsIgnoreCase("기타", vo.getKey())) {
                        createTotalReportTargets8(gWorkbook, checkTargetsList, gAssetSwJob8, rest);
                    }
                    /* end 기타*/

                } // end if
            } // end for
            logger.info("*[종합보고서] Ⅵ. 보안점검 결과 전체 sheet End");
        } catch (ErrorException ee) {

            ee.printStackTrace();
        }

        // 별첨1. 점검 대상
        Map gAssetSwJob9 = new HashMap<>();
        gAssetSwJob9.put("REQ_CD", reqCd);
        gAssetSwJob9.put("startAuditDay", startAuditDay);
        gAssetSwJob9.put("endAuditDay", endAuditDay);
        createTotalReportTargets9(gWorkbook, checkTargetsList, gAssetSwJob9);

        // 별첨2. 점검 항목(MSIT)
        Map gAssetSwJob10 = new HashMap<>();
        gAssetSwJob10.put("REQ_CD", reqCd);

        // -> to do test
        if (StringUtils.isEmpty(fileType)) {
            gAssetSwJob10.put("FILE_TYPE", "1");
        } else {
            gAssetSwJob10.put("FILE_TYPE", fileType);
        }
        createTotalReportTargets10(gWorkbook, checkTargetsList, gAssetSwJob10);

        /*-------------------- [ 기타 처리 ] --------------------*/

        // delete sheet
        boolean tempFlag = false;
        for (String str : sheetNotList) {

            tempFlag = swTypeStrList.stream().anyMatch(x -> StringUtils.contains(x, str));

            if (!tempFlag) {

                int deleteSheetIndex = gWorkbook.getSheetIndex(str);
                gWorkbook.removeSheetAt(deleteSheetIndex);
            }
        } // end if

        // write file
        String separator = "/";
        String jobFileNm = "스마트가드 보안점검 종합보고서";

        File tempReportFolder = new File(tempReportFilesPath + "/" + reportRequestDto.getReqCd());
        if (!tempReportFolder.exists()) {

            tempReportFolder.mkdirs();
        }

        File file = new File(tempReportFilesPath + "/" + reportRequestDto.getReqCd() + separator + reqCd + "_" + jobFileNm + ".xlsx");
        FileOutputStream out = new FileOutputStream(file);
        gWorkbook.write(out);
        out.flush();
        out.close();
        is.close();

        watch.stop();

        long millis = watch.getTotalTimeMillis();

        String pattern = "mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String date = format.format(new Timestamp(millis));
        logger.info(jobFileNm + " 생성 완료, Total time : {} sec", date);
        return 1;
    } // end method test

    private void createTotalReportTargets10(XSSFWorkbook gWorkbook, List<TotalOverallResultDto> checkTargetsList, Map gAssetSwJob10) {

        logger.info("*[종합보고서] 별첨2. 점검 항목 sheet Start");
        XSSFSheet sheet10 = gWorkbook.getSheet("별첨2. 점검 항목");
        int rownum = 6;
        // [별첨 2] 점검 항목
        // [1,0]

        // ■ Unix
        // [3,0]
        String title1 = "Linux";
        getCell(sheet10, 3, 0).setCellValue("■ " +title1);

        // 구분,순번,점검항목,중요도
        // [5,1] ~ [5,4]
        String titleList[] = { "구분","순번","점검항목", "중요도" };
        int j = 0;
        for (int i = 1; i <= titleList.length; i++) {
            getCell(sheet10, 5, i).setCellStyle(mainStyle(gWorkbook, 8)); // style
            getCell(sheet10, 5, i).setCellValue(titleList[j]);
            j++;
        } // end for loop report title

        List<SnetAssetSwAuditReportDto> list1 = testMapper.getCheckAnalysisStandardListByFiletype1(gAssetSwJob10);

        if (list1 == null || list1.size() == 0) {
            list1 = testMapper.getCheckAnalysisStandardListByFiletype1Linux(gAssetSwJob10);
        }
        String diagnosisCdLeft = null;
        if (list1 != null && list1.size() > 0) {

            diagnosisCdLeft = StringUtils.left(list1.stream().findFirst().get().getDiagnosisCd(),3);
        }

        int j1 = 0;
        for (int i1 = 6; i1 < 6 + list1.size(); i1++) {

            if (StringUtils.equalsIgnoreCase(diagnosisCdLeft, StringUtils.left(list1.get(j1).getDiagnosisCd(), 3))) {

                // [6,1]
                getCell(sheet10, i1, 1).setCellValue(list1.get(j1).getItemGrpNm());
                getCell(sheet10, i1, 2).setCellValue(StringUtils.right(list1.get(j1).getDiagnosisCd(), 4));
                getCell(sheet10, i1, 3).setCellValue(list1.get(j1).getItemNm());
                getCell(sheet10, i1, 4).setCellValue(list1.get(j1).getItemGradeView());
                j1++;
            } else {

                break;
            }
            rownum++;
        }

        rownum++;

        String title2 = "Windows";
        getCell(sheet10, rownum, 0).setCellValue("■ " +title2);

        List<SnetAssetSwAuditReportDto> list2 = testMapper.getCheckAnalysisStandardListByFiletype2(gAssetSwJob10);

        String diagnosisCdLeft2 = null;
        if (list2 != null && list2.size() > 0) {

            diagnosisCdLeft2 = StringUtils.left(list2.stream().findFirst().get().getDiagnosisCd(),3);
        }

        int windowsRownum = rownum+1;
        int j2 = 0;
        for (int i2 = windowsRownum; i2 < windowsRownum + list2.size(); i2++) {

            if (StringUtils.equalsIgnoreCase(diagnosisCdLeft2, StringUtils.left(list2.get(j2).getDiagnosisCd(), 3))) {

                // [6,1]
                getCell(sheet10, i2, 1).setCellValue(list2.get(j2).getItemGrpNm());
                getCell(sheet10, i2, 2).setCellValue(StringUtils.right(list2.get(j2).getDiagnosisCd(), 4));
                getCell(sheet10, i2, 3).setCellValue(list2.get(j2).getItemNm());
                getCell(sheet10, i2, 4).setCellValue(list2.get(j2).getItemGradeView());
                j2++;
            } else {

                break;
            }
            rownum++;
        }

        rownum++;

        rownum++;
        String title3 = "DB";
        getCell(sheet10, rownum, 0).setCellValue("■ " +title3);

        List<SnetAssetSwAuditReportDto> list3 = testMapper.getCheckAnalysisStandardListByFiletype3(gAssetSwJob10);

        String diagnosisCdLeft3 = null;
        if (list3 != null && list3.size() > 0) {

            diagnosisCdLeft3 = StringUtils.left(list3.stream().findFirst().get().getDiagnosisCd(),3);
        }

        int dbRownum = rownum+1;
        int j3 = 0;
        for (int i3 = dbRownum; i3 < dbRownum + list3.size(); i3++) {

            if (StringUtils.equalsIgnoreCase(diagnosisCdLeft3, StringUtils.left(list3.get(j3).getDiagnosisCd(), 3))) {

                // [6,1]
                getCell(sheet10, i3, 1).setCellValue(list3.get(j3).getItemGrpNm());
                getCell(sheet10, i3, 2).setCellValue(StringUtils.right(list3.get(j3).getDiagnosisCd(), 4));
                getCell(sheet10, i3, 3).setCellValue(list3.get(j3).getItemNm());
                getCell(sheet10, i3, 4).setCellValue(list3.get(j3).getItemGradeView());
                j3++;
            } else {

                break;
            }
            rownum++;
        }

        rownum++;

        rownum++;
        String title4 = "WEB";
        getCell(sheet10, rownum, 0).setCellValue("■ " +title4);

        List<SnetAssetSwAuditReportDto> list4 = testMapper.getCheckAnalysisStandardListByFiletype4(gAssetSwJob10);

        String diagnosisCdLeft4 = null;
        if (list4 != null && list4.size() > 0) {

            diagnosisCdLeft4 = StringUtils.left(list4.stream().findFirst().get().getDiagnosisCd(),3);
        }

        int webRownum = rownum+1;
        int j4 = 0;
        for (int i4 = webRownum; i4 < webRownum + list4.size(); i4++) {

            if (StringUtils.equalsIgnoreCase(diagnosisCdLeft4, StringUtils.left(list4.get(j4).getDiagnosisCd(), 3))) {

                // [6,1]
                getCell(sheet10, i4, 1).setCellValue(list4.get(j4).getItemGrpNm());
                getCell(sheet10, i4, 2).setCellValue(StringUtils.right(list4.get(j4).getDiagnosisCd(), 4));
                getCell(sheet10, i4, 3).setCellValue(list4.get(j4).getItemNm());
                getCell(sheet10, i4, 4).setCellValue(list4.get(j4).getItemGradeView());
                j4++;
            } else {

                break;
            }
            rownum++;
        }

        rownum++;

        rownum++;
        String title5 = "WAS";
        getCell(sheet10, rownum, 0).setCellValue("■ " +title5);

        List<SnetAssetSwAuditReportDto> list5 = testMapper.getCheckAnalysisStandardListByFiletype5(gAssetSwJob10);

        String diagnosisCdLeft5 = null;
        if (list5 != null && list5.size() > 0) {

            diagnosisCdLeft5 = StringUtils.left(list5.stream().findFirst().get().getDiagnosisCd(),3);
        }

        int wasRownum = rownum+1;
        int j5 = 0;
        for (int i5 = wasRownum; i5 < wasRownum + list5.size(); i5++) {

            if (StringUtils.equalsIgnoreCase(diagnosisCdLeft5, StringUtils.left(list5.get(j5).getDiagnosisCd(), 3))) {

                // [6,1]
                getCell(sheet10, i5, 1).setCellValue(list5.get(j5).getItemGrpNm());
                getCell(sheet10, i5, 2).setCellValue(StringUtils.right(list5.get(j5).getDiagnosisCd(), 4));
                getCell(sheet10, i5, 3).setCellValue(list5.get(j5).getItemNm());
                getCell(sheet10, i5, 4).setCellValue(list5.get(j5).getItemGradeView());
                j5++;
            } else {

                break;
            }
            rownum++;
        }

        rownum++;

        rownum++;
        String title6 = "NW";
        getCell(sheet10, rownum, 0).setCellValue("■ " +title6);

        List<SnetAssetSwAuditReportDto> list6 = testMapper.getCheckAnalysisStandardListByFiletype6(gAssetSwJob10);

        String diagnosisCdLeft6 = null;
        if (list6 != null && list6.size() > 0) {

            diagnosisCdLeft6 = StringUtils.left(list6.stream().findFirst().get().getDiagnosisCd(),3);
        }

        int nwRownum = rownum+1;
        int j6 = 0;
        for (int i6 = nwRownum; i6 < nwRownum + list6.size(); i6++) {

            if (StringUtils.equalsIgnoreCase(diagnosisCdLeft6, StringUtils.left(list6.get(j6).getDiagnosisCd(), 3))) {

                // [6,1]
                getCell(sheet10, i6, 1).setCellValue(list6.get(j6).getItemGrpNm());
                getCell(sheet10, i6, 2).setCellValue(StringUtils.right(list6.get(j6).getDiagnosisCd(), 4));
                getCell(sheet10, i6, 3).setCellValue(list6.get(j6).getItemNm());
                getCell(sheet10, i6, 4).setCellValue(list6.get(j6).getItemGradeView());
                j6++;
            } else {

                break;
            }
            rownum++;
        }

        rownum++;

        /*-------------------------------- Web Applicaton --------------------------------*/
        if (gAssetSwJob10.get("FILE_TYPE").equals("1")) {
            rownum++;
            String title7 = "Web App(URL)";
            getCell(sheet10, rownum, 0).setCellValue("■ " + title7);

            List<SnetAssetSwAuditReportDto> list7 = testMapper.getCheckAnalysisStandardListByFiletype7(gAssetSwJob10);

            String diagnosisCdLeft7 = null;
            if (list7 != null && list7.size() > 0) {

                diagnosisCdLeft7 = StringUtils.left(list7.stream().findFirst().get().getDiagnosisCd(), 3);
            }

            int urlRownum = rownum + 1;
            int j7 = 0;
            for (int i7 = urlRownum; i7 < urlRownum + list7.size(); i7++) {

                if (StringUtils.equalsIgnoreCase(diagnosisCdLeft7, StringUtils.left(list7.get(j7).getDiagnosisCd(), 3))) {

                    // [6,1]
                    getCell(sheet10, i7, 1).setCellValue(list7.get(j7).getItemGrpNm());
                    getCell(sheet10, i7, 2).setCellValue(StringUtils.right(list7.get(j7).getDiagnosisCd(), 4));
                    getCell(sheet10, i7, 3).setCellValue(list7.get(j7).getItemNm());
                    getCell(sheet10, i7, 4).setCellValue(list7.get(j7).getItemGradeView());
                    j7++;
                } else {

                    break;
                }
                rownum++;
            }

            rownum++;
        }
        /*-------------------------------- 기타 --------------------------------*/
        rownum++;
        String title8 = "기타";

        List<SnetAssetSwAuditReportDto> list8 = testMapper.getCheckAnalysisStandardListByFiletype8(gAssetSwJob10);

        String diagnosisCdLeft8 = null;
        if (list8 != null && list8.size() > 0) {
            getCell(sheet10, rownum, 0).setCellValue("■ " +title8);
            diagnosisCdLeft8 = StringUtils.left(list8.stream().findFirst().get().getDiagnosisCd(),3);
        }

        int etcRownum = rownum+1;
        int j8 = 0;
        for (int i8 = etcRownum; i8 < etcRownum + list8.size(); i8++) {

            if (StringUtils.equalsIgnoreCase(diagnosisCdLeft8, StringUtils.left(list8.get(j8).getDiagnosisCd(), 3))) {

                // [6,1]
                getCell(sheet10, i8, 1).setCellValue(list8.get(j8).getItemGrpNm());
                getCell(sheet10, i8, 2).setCellValue(StringUtils.right(list8.get(j8).getDiagnosisCd(), 4));
                getCell(sheet10, i8, 3).setCellValue(list8.get(j8).getItemNm());
                getCell(sheet10, i8, 4).setCellValue(list8.get(j8).getItemGradeView());
                j8++;
            } else {

                break;
            }
            rownum++;
        } // end for
        logger.info("*[종합보고서] 별첨2. 점검 항목 sheet End");
    } // end method

    /**
     * 종합보고서 별첨1. 중요도
     */
    private void createTotalReportTargets9(XSSFWorkbook gWorkbook, List<TotalOverallResultDto> checkTargetsList, Map gAssetSwJob9) {

        logger.info("*[종합보고서] 별첨1. 점검 대상 sheet Start");
        XSSFSheet sheet9 = gWorkbook.getSheet("별첨1. 점검 대상");

        List<SnetAssetSwAuditExcelDto> list = Lists.newArrayList();

        int[] listSizeArr = new int[checkTargetsList.size()];

        int k = 0;

        gAssetSwJob9.put("HOST_GRD_FLAG", "Y");
        List<SnetAssetSwAuditExcelDto> excelDtoListTemp = testMapper.selectAssetSwExcelListNew(gAssetSwJob9);
        gAssetSwJob9.remove("HOST_GRD_FLAG");

        listSizeArr[k] = excelDtoListTemp.size();

        for (SnetAssetSwAuditExcelDto excelDto : excelDtoListTemp) {

            SnetAssetSwAuditExcelDto vo = new SnetAssetSwAuditExcelDto();
            vo.setHostNm(excelDto.getHostNm());
            vo.setIpAddress(excelDto.getIpAddress());
            vo.setSwType(excelDto.getSwType());
            vo.setSwNm(excelDto.getSwNm());
            vo.setSwInfo(excelDto.getSwInfo());
            vo.setHostGrade("");
            vo.setSwEtc(excelDto.getAuditDay());

            list.add(vo);
        } // end for SnetAssetSwAuditExcelDto


        // [별첨 1] 점검 대상 : 장비 리스트
        int j = 0;
        int l = 1;
        for (int i = 4; i < 4 + list.size(); i++) {

            // [4,1]
            k++;
            getCell(sheet9, i, 1).setCellValue(k);
            getCell(sheet9, i, 2).setCellValue(list.get(j).getHostNm());
            getCell(sheet9, i, 3).setCellValue(list.get(j).getIpAddress());
            getCell(sheet9, i, 4).setCellValue(list.get(j).getSwType());
            getCell(sheet9, i, 5).setCellValue(list.get(j).getSwNm());
            getCell(sheet9, i, 6).setCellValue(list.get(j).getSwInfo());
            getCell(sheet9, i, 7).setCellValue(list.get(j).getHostGrade());
            getCell(sheet9, i, 8).setCellValue(list.get(j).getAuditDay());
            j++;

        }
        logger.info("*[종합보고서] 별첨1. 점검 대상 sheet End");
        /* end 별첨1. 점검 대상 */
    }

    // 기타
    private void createTotalReportTargets8(XSSFWorkbook gWorkbook, List<TotalOverallResultDto> checkTargetsList, Map gAssetSwJob8, RestTemplate rest) throws Exception {

        if (gAssetSwJob8.get("SW_TYPE") == null) {
            return;
        }
        String etcSwType = gAssetSwJob8.get("SW_TYPE").toString();
        XSSFSheet sheet6 = gWorkbook.getSheet("기타");
        // 점검군별 (Linux, Window, DB, WEB, WAS, NW, Web App(URL), 기타)
        // -> 기타 : not in (Linux, Window, DB, WEB, WAS, NW, Web App(URL))
        // -> 기타 : not in (OS, DB, WEB, WAS, NW, URL)

        // (1) 보안점검 기타

        // 현황
        String statusStr = "CONCATENATE(\" 서버 보안점검 결과, 전체 점검대상(\",D32,\"EA)의 평균 보안수준은 \",AB56,\"입니다. 분류 항목 중 보안 설정이 가장 양호하게 설정된 분류 항목은 \",\"\"\"\",INDEX(B38:B55,MATCH(MAX(AB38:AB55),AB38:AB55,0),1),\"\"\"\",\"로 보안준수율은 \", MAX(AB38:AB55),\"점이며 \",\"\"\"\",INDEX(B38:B55,MATCH(MIN(AB38:AB55),AB38:AB55,0),1),\"\"\"\",\" 항목이 \",MIN(AB38:AB55),\"점으로 보안 설정이 가장 미흡하게 설정되어 있습니다.\")";
        getCell(sheet6, 11-1-1-1-2, 5).setCellFormula(statusStr);

        String statusMigStr = "CONCATENATE(\" 서버 이행점검 결과, 전체 점검대상(\",AM32,\"EA)의 평균 보안수준은 \",BD56,\"에서 \",BO56,\"으로 변경되었습니다. 보안설정이 가장 미흡하게 설정되어 있던 \",\"\"\"\",INDEX(AK38:AK55,MATCH(MIN(BD38:BD55),BD38:BD55,0),1),\"\"\"\",\" 분류 항목이 \", MIN(BD38:BD55),\"점에서 \",INDEX(BO38:BO55,MATCH(MIN(BD38:BD55),BD38:BD55,0),1),\"점으로 보안준수율이 변경되었으며, 현재 보안준수율이 가장 낮은 분류 항목으로는 \",\"\"\"\",INDEX(AK38:AK55,MATCH(MIN(BO38:BO55),BO38:BO55,0),1),\"\"\"\",\"으로 보안준수율은 \",MIN(BO38:BO55),\"점 입니다.\")";
        getCell(sheet6, 11-1-1-1-2, 40).setCellFormula(statusMigStr);



        /*----------------------------------------------------------------------------------------------------*/
        List<DetailResultDto> linuxTargetsList = testService.getDetailTargetsList(checkTargetsList, gAssetSwJob8, "ETC", false);

        if (linuxTargetsList == null || linuxTargetsList.size() < 1) {

            throw new ErrorException("NoSuchElementException: No value");
        }

        String etcSwtypeStr = null;
        etcSwtypeStr = linuxTargetsList.stream().findFirst().get().getValue9(); // swType

        // 점검수량 (EA)
        for (DetailResultDto dto : linuxTargetsList) {

            getCell(sheet6, 40-1-1-1-2-4, 3).setCellValue(dto.getValue1());
//            getCell(sheet6, 64-1-1-1-2-4, 5).setCellValue(dto.getValue16());
//            getCell(sheet6, 64-1-1-1-2-4, 8).setCellValue(dto.getValue16());
//        -> 현재는 개별항목수 x 점검수량 개수로 맞춤
            break;
        }

        // 양호 상중하Total.테이블
        int t1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 20).setCellValue(linuxTargetsList.get(t1).getValue17());
            getCell(sheet6, k2, 21).setCellValue(linuxTargetsList.get(t1).getValue18());
            getCell(sheet6, k2, 22).setCellValue(linuxTargetsList.get(t1).getValue19());
            t1++;
        }

        // 인터뷰필요 상중하Total.테이블
        int i1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 24).setCellValue(linuxTargetsList.get(i1).getValue20());
            getCell(sheet6, k2, 25).setCellValue(linuxTargetsList.get(i1).getValue21());
            getCell(sheet6, k2, 26).setCellValue(linuxTargetsList.get(i1).getValue22());
            i1++;
        }
        // 불가 상중하Total.테이블
        int d1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 28).setCellValue(linuxTargetsList.get(d1).getValue23());
            getCell(sheet6, k2, 29).setCellValue(linuxTargetsList.get(d1).getValue24());
            getCell(sheet6, k2, 30).setCellValue(linuxTargetsList.get(d1).getValue25());
            d1++;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h = 0;
        int h2 = 23-1-1-1-2-4;
        for (int g = 46-1-1-1-2-4; g < 46-1-1-1-2-4 + (linuxTargetsList.size()); g+=1) {

            getCell(sheet6, g, 1).setCellValue(linuxTargetsList.get(h).getKey());
            getCell(sheet6, g, 5).setCellValue(linuxTargetsList.get(h).getValue2());
            getCell(sheet6, g, 8).setCellValue(linuxTargetsList.get(h).getValue3());
            getCell(sheet6, g, 11).setCellValue(linuxTargetsList.get(h).getValue4());
            getCell(sheet6, g, 15).setCellValue(linuxTargetsList.get(h).getValue5());
            getCell(sheet6, g, 19).setCellValue(linuxTargetsList.get(h).getValue6());
            getCell(sheet6, g, 23).setCellValue(linuxTargetsList.get(h).getValue7());

            // 보안준수율 (웹UI 의 audit_rate 산정식을 기반으로 다시 계산함)
            getCell(sheet6, g, 27).setCellFormula(String.format(
                    "IF(F%d=0,100,ROUND(IFERROR((U%d*3+V%d*2+W%d*1) / ((U%d*3+V%d*2+W%d*1)+(L%d*3+P%d*2+T%d*1)+(Y%d*3+Z%d*2+AA%d*1)+(AC%d*3+AD%d*2+AE%d*1))*100,0),2))"
                    ,g+1
                    ,h2+1,h2+1,h2+1
                    ,h2+1,h2+1,h2+1
                    ,g+1,g+1,g+1
                    ,h2+1,h2+1,h2+1
                    ,h2+1,h2+1,h2+1)
            );
            // 보안수준
            getCell(sheet6, g, 31).setCellFormula(String.format("IF(AB%d>=90,\"우수\",IF(AND(AB%d>=80,AB%d<90),\"양호\",IF(AND(AB%d>=70,AB%d<80),\"보통\",\"미흡\")))"
                    , g + 1, g + 1, g + 1, g + 1, g + 1));
            h++;
            h2++;
        }

        // 전체 점검 개수(N/A 제외) Total
        getCell(sheet6, 33-1-1-2, 5).setCellFormula("SUM(F15:F29)");  // 25
        getCell(sheet6, 33-1-1-2, 8).setCellFormula("SUM(I15:I29)");  // 16
        getCell(sheet6, 33-1-1-2, 11).setCellFormula("SUM(L15:L29)"); // 5
        getCell(sheet6, 33-1-1-2, 14).setCellFormula("SUM(O15:O29)"); // 0

        getCell(sheet6, 33-1-1-2, 20).setCellFormula("SUM(U15:U20)"); // 0
        getCell(sheet6, 33-1-1-2, 21).setCellFormula("SUM(V15:V20)"); // 0
        getCell(sheet6, 33-1-1-2, 22).setCellFormula("SUM(W15:W20)"); // 0

        getCell(sheet6, 33-1-1-2, 24).setCellFormula("SUM(Y15:Y20)"); // 0
        getCell(sheet6, 33-1-1-2, 25).setCellFormula("SUM(Z15:Z20)"); // 0
        getCell(sheet6, 33-1-1-2, 26).setCellFormula("SUM(AA15:AA20)"); // 0

        getCell(sheet6, 33-1-1-2, 28).setCellFormula("SUM(AC15:AC20)"); // 0
        getCell(sheet6, 33-1-1-2, 29).setCellFormula("SUM(AD15:AD20)"); // 0
        getCell(sheet6, 33-1-1-2, 30).setCellFormula("SUM(AE15:AE20)"); // 0

        /*
            Total. 합계. 점검항목수
         */
        getCell(sheet6, 64-1-1-1-2-4, 5).setCellFormula("SUM(F38:H55)");
        getCell(sheet6, 64-1-1-1-2-4, 8).setCellFormula("SUM(I38:K55)");
//        getCell(sheet6, 64, 8).setCellFormula("SUM(I38:K55)");
//        -> value16 으로 대체
//        -> 현재는 개별항목수 x 점검수량 개수로 맞춤

        getCell(sheet6, 64-1-1-1-2-4, 11).setCellFormula("IFERROR(SUM(L38:O55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 15).setCellFormula("IFERROR(SUM(P38:S55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 19).setCellFormula("IFERROR(SUM(T38:W55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 23).setCellFormula("SUM(L56:W57)");

        //보안준수율 Total
        getCell(sheet6, 64-1-1-1-2-4, 34).setCellFormula("ROUND(AVERAGEA(AB38:AE55),2)");

        double sCheckValue = 0.0;
        double tempSCheckValue1 = 0.0;
        double tempSCheckValue2 = 0.0;

        // 전체 점검 개수(N/A 제외) 상중하Total.테이블
        int value10 = 0;
        int value11 = 0;
        int value12 = 0;
        for (DetailResultDto detailResultDto : linuxTargetsList) {

            value10 += detailResultDto.getValue10();  // 상
            value11 += detailResultDto.getValue11();  // 중
            value12 += detailResultDto.getValue12();  // 하
        }
        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int value4 = 0;
        int value5 = 0;
        int value6 = 0;
        for (DetailResultDto detailResultDto : linuxTargetsList) {

            value4 += detailResultDto.getValue4();   // 상
            value5 += detailResultDto.getValue5();   // 중
            value6 += detailResultDto.getValue6();   // 하
        }

        // 보안준수율 에서 보안준수율 로 변경
        tempSCheckValue1 = ((value10*3+value11*2+value12*1)-(value4*3+value5*2+value6*1));
        tempSCheckValue2 = (value10*3+value11*2+value12*1);
        sCheckValue = (tempSCheckValue1/tempSCheckValue2) * 100;
        sCheckValue = Math.round( sCheckValue * 100d ) / 100d;

        XSSFSheet sheet5 = gWorkbook.getSheet("Ⅳ. 전체종합결과");
        getCell(sheet6, 64-1-1-1-2-4, 27).setCellFormula("IF(AI56>=90,\"우수\",IF(AND(AI56>=80,AI56<90),\"양호\",IF(AND(AI56>=70,AI56<80),\"보통\",\"미흡\")))");

        /*
            Total. 평균. 점검결과
         */
        getCell(sheet6, 66-1-1-1-2-4, 11).setCellFormula("ROUND(IFERROR((U30*3)/((U30*3)+(L56*3)+(Y30*3)+(AC30*3))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 15).setCellFormula("ROUND(IFERROR((V30*2)/((V30*2)+(P56*2)+(Z30*2)+(AD30*2))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 19).setCellFormula("ROUND(IFERROR((W30*1)/((W30*1)+(T56*1)+(AA30*1)+(AE30*1))*100,0),2)");

        // 발견된 취약점 기간별 조치 방안
        // 현황 [74, 5]
        String statusStr2 = "CONCATENATE(\" 발견된 취약한 항목 중 위험도 \",\"\"\"\",\"상\",\"\"\"\",\"에 해당하는 즉시 조치가 필요한 항목의 비율은 평균 \",ROUND(Q112*100,1),\"%를 차지하고 있으며, 위험도 \",\"\"\"\",\"중\",\"\"\"\",\",\",\"\"\"\",\"하\",\"\"\"\",\"에 해당하는 중장기적으로 조치가 필요한 항목의 비율은 \",ROUND(AC112*100,1),\"%로 분석 되었습니다. 또한 즉시 조치가 필요한 항목 중, 가장 높은 비율을 차지하고 있는 상위 10개의 점검 대상은 아래와 같습니다.\")";
        getCell(sheet6, 74-1-1-1-2-4, 5).setCellFormula(statusStr2);
        // 현황 [74, 40]
        String statusMigStr2 = "CONCATENATE(\" 발견된 취약한 항목 중 평균 조치율은 \",ROUND(BR112*100,1),\"%로써 위험도 \",\"\"\"\",\"상\",\"\"\"\",\"에 해당하는 즉시 조치가 필요한 항목의 비율은 평균 \",ROUND(IFERROR(AR112/AO112*100,0),1),\"%에서 \",ROUND(BD112*100,1),\"%로 감소하였으며 위험도 \",\"\"\"\",\"중\",\"\"\"\",\",\",\"\"\"\",\"하\",\"\"\"\",\"에 해당하는 중장기적으로 조치가 필요한 항목의 비율은 \",ROUND(IFERROR(AV112/AO112*100,0),1),\"%에서 \",ROUND(BK112*100,1),\"%로 감소한 것으로 분석 되었습니다. 또한 잔여 취약점이 존재하는 대상 중, 즉시 조치가 필요한 항목이 높은 비율을 차지하고 있는 상위 10개의 점검 대상은 아래와 같습니다.\")";
        getCell(sheet6, 74-1-1-1-2-4, 40).setCellFormula(statusMigStr2);

        // 구분, 발견된취약점 수, 기간별 조치 방안 표
        int jj = 0;
        int kk = 47-1-1-1-2-4;
        int ll = 103-1-1-1-2-4;
        for (int ii = 102-1-1-1-2-4; ii < 102-1-1-1-2-4 + (linuxTargetsList.size()); ii+=1) {

            getCell(sheet6, ii, 1).setCellValue(linuxTargetsList.get(jj).getKey());

            getCell(sheet6, ii, 5).setCellFormula(String.format("X%d", kk));

            getCell(sheet6, ii, 10).setCellFormula(String.format("L%d", kk));

            getCell(sheet6, ii, 16).setCellFormula(String.format("IFERROR(K%d/F%d,0)", ll, ll));

            getCell(sheet6, ii, 22).setCellFormula(String.format("P%d+T%d", kk, kk));

            getCell(sheet6, ii, 28).setCellFormula(String.format("IFERROR(W%d/F%d,0)", ll, ll));
            jj++;
            kk += 1;
            ll += 1;
        }

        // 구분, 발견된취약점 수, 기간별 조치 방안 표 Total
        getCell(sheet6, 120-1-1-1-2-4, 5).setCellFormula("SUM(F94:J111)");
        getCell(sheet6, 120-1-1-1-2-4, 10).setCellFormula("SUM(K94:P111)");
        getCell(sheet6, 120-1-1-1-2-4, 16).setCellFormula("IFERROR(K112/F112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 22).setCellFormula("SUM(W94:AB111)");
        getCell(sheet6, 120-1-1-1-2-4, 28).setCellFormula("IFERROR(W112/F112,0)");

        // 즉시 조치 필요 대상시스템 (TOP 10). 테이블
        // -> to do test : swType Null 에러 확인
        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList = testService.getReportResultProcessTargets(gAssetSwJob8, true);

        snetAssetSwAuditExcelDtoList.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate).reversed());

        snetAssetSwAuditExcelDtoList = snetAssetSwAuditExcelDtoList.stream().limit(10).collect(Collectors.toList());

        int bb = 0;
        if (snetAssetSwAuditExcelDtoList != null && snetAssetSwAuditExcelDtoList.size() > 0) {

            for (int aa = 126-1-1-1-2-4; aa < 126-1-1-1-2-4 + snetAssetSwAuditExcelDtoList.size(); aa++) {

                getCell(sheet6, aa, 182).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getHostNm());
                getCell(sheet6, aa, 183).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getAuditRate());

                if (snetAssetSwAuditExcelDtoList.get(bb).getWeekHighRate() == 0.0) {

                    getCell(sheet6, aa, 184).setCellValue("");
                } else {

                    getCell(sheet6, aa, 184).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getWeekHighRate());
                }

                if (snetAssetSwAuditExcelDtoList.get(bb).getWeekMiddleLowRate() == 0.0) {

                    getCell(sheet6, aa, 185).setCellValue("");
                } else {

                    getCell(sheet6, aa, 185).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getWeekMiddleLowRate());
                }
                bb++;
            } // end for
        } // end if

        // 취약점 상세 항목. 테이블. 보안점검
        gAssetSwJob8.put("SW_TYPE", etcSwtypeStr);
        List<SnetAssetSwAuditReportDto> assetSwAuditReportDtoList = testService.selectAssetSwAuditReportNew(gAssetSwJob8, true);

        if (assetSwAuditReportDtoList != null && assetSwAuditReportDtoList.size() > 0) {
            int dd = 0;
            for (int cc = 157-1-1-1-2-4; cc < 157-1-1-1-2-4 + assetSwAuditReportDtoList.size(); cc++) {

                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemNmDesc());
                getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGradeView());
                getCell(sheet6, cc, 26).setCellValue(assetSwAuditReportDtoList.get(dd).getHostCount());
                getCell(sheet6, cc, 28).setCellValue(assetSwAuditReportDtoList.get(dd).getEtc());
                dd++;
            } // end for assetSwAuditReportDtoList
//            for (int cc = 157; cc < 157 + assetSwAuditReportDtoList.size(); cc++) {
//
//                for (int ee = 1; ee <= 33; ee++) {
//                    getCell(sheet6, cc, ee).setCellStyle(cellStyle(gWorkbook, 10)); // style
//                }
//            } // end for style
        } // end if

        // 취약점 상세 항목 가이드. 테이블. 보안점검
        int colWithinchars = (sheet6.getColumnWidth(2) + sheet6.getColumnWidth(3));
        if (assetSwAuditReportDtoList != null && assetSwAuditReportDtoList.size() > 0) {
            int dd = 0;
            for (int cc = 211-1-1-1-2-4; cc < 211-1-1-1-2-4 + assetSwAuditReportDtoList.size(); cc++) {

//                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
//                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
//                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemStandard());
//                getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());


                String itemCMstr = assetSwAuditReportDtoList.get(dd).getItemCountermeasure();
                String[] chars = null;
                boolean autoRowHeightFlag = false;
                short charsLength = 1;
                try {
                    chars = itemCMstr.split("\n");

                    charsLength = (short) (chars.length);
                    autoRowHeightFlag = true;
                } catch (Exception e) {

                    autoRowHeightFlag = false;
                }


                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemStandard());

                if (autoRowHeightFlag) {
                    getCellAutoRowHeight(sheet6, cc, 24, charsLength).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());
                } else {
                    getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());
                }
                dd++;

            } // end for
        } // end if


        // (2) 이행점검 Linux
        List<DetailResultDto> migTargetsList = testService.getMigDetailTargetsList(checkTargetsList, gAssetSwJob8, etcSwtypeStr, true);

        // 점검수량 (EA)
        getCell(sheet6, 40-1-1-1-2-4, 38).setCellFormula("D32");

        // 양호 상중하Total.테이블
        int t2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 36).setCellValue(migTargetsList.get(t2).getValue17());
            getCell(sheet6, k2, 37).setCellValue(migTargetsList.get(t2).getValue18());
            getCell(sheet6, k2, 38).setCellValue(migTargetsList.get(t2).getValue19());
            t2++;
        }

        // 인터뷰필요 상중하Total.테이블
        int i2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 40).setCellValue(migTargetsList.get(i2).getValue20());
            getCell(sheet6, k2, 41).setCellValue(migTargetsList.get(i2).getValue21());
            getCell(sheet6, k2, 42).setCellValue(migTargetsList.get(i2).getValue22());
            i2++;
        }
        // 불가 상중하Total.테이블
        int d2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 44).setCellValue(migTargetsList.get(d2).getValue23());
            getCell(sheet6, k2, 45).setCellValue(migTargetsList.get(d2).getValue24());
            getCell(sheet6, k2, 46).setCellValue(migTargetsList.get(d2).getValue25());
            d2++;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h3 = 0;
        int h4 = 47-1-1-1-2-4;
        for (int g2 = 46-1-1-1-2-4; g2 < 46-1-1-1-2-4 + (linuxTargetsList.size()); g2+=1) {

            getCell(sheet6, g2, 36).setCellValue(linuxTargetsList.get(h3).getKey());
            getCell(sheet6, g2, 40).setCellFormula(String.format("F%d", g2 + 1));
            getCell(sheet6, g2, 43).setCellFormula(String.format("I%d", g2 + 1));
            getCell(sheet6, g2, 46).setCellFormula(String.format("L%d", g2 + 1));
            getCell(sheet6, g2, 48).setCellFormula(String.format("P%d", g2 + 1));
            getCell(sheet6, g2, 50).setCellFormula(String.format("T%d", g2 + 1));
            getCell(sheet6, g2, 53).setCellFormula(String.format("SUM(AU%d:BA%d)", g2 + 1, g2 + 1));

            // 보안준수율
            getCell(sheet6, g2, 55).setCellFormula(String.format("AB%d", g2 + 1));
            h3++;
            h4++;
        }

        int h5 = 0;
        int h6 = 23-1-1-1-2-4;
        for (int g2 = 46-1-1-1-2-4; g2 < 46-1-1-1-2-4 + (migTargetsList.size()); g2+=1) {

            // 이행점검
            // 상
            getCell(sheet6, g2, 58).setCellValue(migTargetsList.get(h5).getValue4());
            // 중
            getCell(sheet6, g2, 60).setCellValue(migTargetsList.get(h5).getValue5());
            // 하
            getCell(sheet6, g2, 62).setCellValue(migTargetsList.get(h5).getValue6());
            // Total
            getCell(sheet6, g2, 64).setCellFormula(String.format("SUM(BG%d:BL%d)", g2 + 1, g2 + 1));

            // 전체 점검 개수(N/A 제외) 상중하Total.테이블 옆에 위치
            //      보안준수율   :
            getCell(sheet6, g2, 66).setCellFormula(String.format(
                    "IF(AO%d=0,100,ROUND(IFERROR((AK%d*3+AL%d*2+AM%d*1) / ((AK%d*3+AL%d*2+AM%d*1)+(BG%d*3+BI%d*2+BK%d*1)+(AO%d*3+AP%d*2+AQ%d*1)+(AS%d*3+AT%d*2+AU%d*1))*100,0),2))"
                    ,g2+1
                    ,h6+1,h6+1,h6+1
                    ,h6+1,h6+1,h6+1
                    ,g2+1,g2+1,g2+1
                    ,h6+1,h6+1,h6+1
                    ,h6+1,h6+1,h6+1)
            );

            //      보안수준   :
            getCell(sheet6, g2, 68).setCellFormula(String.format("IF(BO%d>=90,\"우수\",IF(AND(BO%d>=80,BO%d<90),\"양호\",IF(AND(BO%d>=70,BO%d<80),\"보통\",\"미흡\")))"
                    , g2 + 1, g2 + 1, g2 + 1, g2 + 1, g2 + 1));
            h5++;
            h6++;
        }

        getCell(sheet6, 33-1-1-2, 36).setCellFormula("SUM(AK15:AK20)"); // 20
        getCell(sheet6, 33-1-1-2, 37).setCellFormula("SUM(AL15:AL20)"); // 12
        getCell(sheet6, 33-1-1-2, 38).setCellFormula("SUM(AM15:AM20)"); // 2

        getCell(sheet6, 33-1-1-2, 40).setCellFormula("SUM(AO15:AO20)"); // 0
        getCell(sheet6, 33-1-1-2, 41).setCellFormula("SUM(AP15:AP20)"); // 1
        getCell(sheet6, 33-1-1-2, 42).setCellFormula("SUM(AQ15:AQ20)"); // 0

        getCell(sheet6, 33-1-1-2, 44).setCellFormula("SUM(AS15:AS20)"); // 0
        getCell(sheet6, 33-1-1-2, 45).setCellFormula("SUM(AT15:AT20)"); // 0
        getCell(sheet6, 33-1-1-2, 46).setCellFormula("SUM(AU15:AU20)"); // 0
        // Total
        //      합계 :

        getCell(sheet6, 64-1-1-1-2-4, 40).setCellFormula("F56");
        getCell(sheet6, 64-1-1-1-2-4, 43).setCellFormula("I56");
        getCell(sheet6, 64-1-1-1-2-4, 46).setCellFormula("SUM(AU38:AV55)");
        getCell(sheet6, 64-1-1-1-2-4, 48).setCellFormula("SUM(AW38:AX55)");
        getCell(sheet6, 64-1-1-1-2-4, 50).setCellFormula("SUM(AY38:BA55)");
        getCell(sheet6, 64-1-1-1-2-4, 53).setCellFormula("SUM(BB38:BC55)");
        getCell(sheet6, 64-1-1-1-2-4, 55).setCellFormula("AB56");

        //          - 이행점검
        getCell(sheet6, 64-1-1-1-2-4, 58).setCellFormula("IFERROR(SUM(BG38:BH55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 60).setCellFormula("IFERROR(SUM(BI38:BJ55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 62).setCellFormula("IFERROR(SUM(BK38:BL55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 64).setCellFormula("SUM(BM38:BN55)");

        //보안준수율 Total
        getCell(sheet6, 64-1-1-1-2-4, 71).setCellFormula("ROUND(AVERAGEA(BO38:BP55),2)");

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int pvalue4 = 0;
        int pvalue5 = 0;
        int pvalue6 = 0;
        int pvalue13 = 0;
        int pvalue14 = 0;
        int pvalue15 = 0;
        for (DetailResultDto detailResultDto : migTargetsList) {

            pvalue4 += detailResultDto.getValue4();   // 상.취약점
            pvalue5 += detailResultDto.getValue5();   // 중.취약점
            pvalue6 += detailResultDto.getValue6();   // 하.취약점
        }

        getCell(sheet6, 64-1-1-1-2-4, 66).setCellFormula("IF(BT56>=90,\"우수\",IF(AND(BT56>=80,BT56<90),\"양호\",IF(AND(BT56>=70,BT56<80),\"보통\",\"미흡\")))");

        //      평균 :
        getCell(sheet6, 66-1-1-1-2-4, 46).setCellFormula("L58");
        getCell(sheet6, 66-1-1-1-2-4, 48).setCellFormula("P58");
        getCell(sheet6, 66-1-1-1-2-4, 50).setCellFormula("T58");

        //     - 이행점검 Total. 평균. 점검결과
        getCell(sheet6, 66-1-1-1-2-4, 58).setCellFormula("ROUND(IFERROR((AK30*3)/((AK30*3)+(BG56*3)+(AO30*3)+(AS30*3))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 60).setCellFormula("ROUND(IFERROR((AL30*2)/((AL30*2)+(BI56*2)+(AP30*2)+(AT30*2))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 62).setCellFormula("ROUND(IFERROR((AM30*1)/((AM30*1)+(BK56*1)+(AQ30*1)+(AU30*1))*100,0),2)");

        // 발견된 취약점 기간별 조치 방안
        // 구분.기간별 조치 방안 표
        int n1 = 47-1-1-1-2-4;
        for (int m1 = 102-1-1-1-2-4; m1 < 102-1-1-1-2-4 + (linuxTargetsList.size()); m1+=1) {
            getCell(sheet6, m1, 36).setCellFormula(String.format("B%d", m1+1));
            getCell(sheet6, m1, 40).setCellFormula(String.format("BB%d", n1));
            getCell(sheet6, m1, 43).setCellFormula(String.format("AU%d", n1));
            getCell(sheet6, m1, 47).setCellFormula(String.format("AW%d+AY%d", n1, n1));
            n1+=1;
        }

        int n2 = 47-1-1-1-2-4;
        for (int m2 = 102-1-1-1-2-4; m2 < 102-1-1-1-2-4 + (migTargetsList.size()); m2+=1) {
            getCell(sheet6, m2, 52).setCellFormula(String.format("BG%d", n2));
            getCell(sheet6, m2, 55).setCellFormula(String.format("IFERROR(BA%d/AO%d,0)", m2+1, m2+1));
            getCell(sheet6, m2, 59).setCellFormula(String.format("BI%d+BK%d", n2, n2));
            getCell(sheet6, m2, 62).setCellFormula(String.format("IFERROR(BH%d/AO%d,0)", m2+1, m2+1));
            getCell(sheet6, m2, 65).setCellFormula(String.format("BA%d+BH%d", m2+1, m2+1));
            getCell(sheet6, m2, 67).setCellFormula(String.format("AO%d-BN%d", m2+1, m2+1));
            getCell(sheet6, m2, 69).setCellFormula(String.format("IFERROR(BP%d/AO%d,0)", m2+1, m2+1));
            n2+=1;
        }

        // 구분.기간별 조치 방안 표 Total
        getCell(sheet6, 120-1-1-1-2-4, 40).setCellFormula("SUM(AO94:AQ111)");
        getCell(sheet6, 120-1-1-1-2-4, 43).setCellFormula("SUM(AR94:AU111)");
        getCell(sheet6, 120-1-1-1-2-4, 47).setCellFormula("SUM(AV94:AY111)");
        getCell(sheet6, 120-1-1-1-2-4, 52).setCellFormula("SUM(BA94:BC111)");
        getCell(sheet6, 120-1-1-1-2-4, 55).setCellFormula("IFERROR(BA112/AO112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 59).setCellFormula("SUM(BH94:BJ111)");
        getCell(sheet6, 120-1-1-1-2-4, 62).setCellFormula("IFERROR(BH112/AO112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 65).setCellFormula("SUM(BN94:BO111)");
        getCell(sheet6, 120-1-1-1-2-4, 67).setCellFormula("SUM(BP94:BQ111)");
        getCell(sheet6, 120-1-1-1-2-4, 69).setCellFormula("IFERROR(BP112/AO112,0)");


        // 즉시 조치 필요 대상시스템 (TOP 10). 테이블
        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList2 = testService.getReportResultProcessTargets(gAssetSwJob8, false);

        snetAssetSwAuditExcelDtoList2.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate).reversed());

        snetAssetSwAuditExcelDtoList2 = snetAssetSwAuditExcelDtoList2.stream().limit(10).collect(Collectors.toList());

        int bb2 = 0;
        if (snetAssetSwAuditExcelDtoList2 != null && snetAssetSwAuditExcelDtoList2.size() > 0) {
            for (int aa2 = 126-1-1-1-2-4; aa2 < 126-1-1-1-2-4 + snetAssetSwAuditExcelDtoList2.size(); aa2++) {

                getCell(sheet6, aa2, 186).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getHostNm());
                getCell(sheet6, aa2, 187).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getAuditRate());

                if (snetAssetSwAuditExcelDtoList.get(bb2).getWeekHighRate() == 0.0) {

                    getCell(sheet6, aa2, 188).setCellValue("");
                } else {

                    getCell(sheet6, aa2, 188).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getWeekHighRate());
                }

                if (snetAssetSwAuditExcelDtoList.get(bb2).getWeekMiddleLowRate() == 0.0) {

                    getCell(sheet6, aa2, 189).setCellValue("");
                } else {

                    getCell(sheet6, aa2, 189).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getWeekMiddleLowRate());
                }
                bb2++;
            }
        }

        // 취약점 상세 항목. 테이블. 이행점검
        List<SnetAssetSwAuditReportDto> assetSwAuditReportDtoList2 = testService.selectAssetSwAuditReportNew(gAssetSwJob8, false);

        if (assetSwAuditReportDtoList2 != null && assetSwAuditReportDtoList2.size() > 0) {

            int dd2 = 0;
            for (int cc2 = 157-1-1-1-2-4; cc2 < 157-1-1-1-2-4 + assetSwAuditReportDtoList2.size(); cc2++) {

                getCell(sheet6, cc2, 36).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemGrpNm());
                getCell(sheet6, cc2, 41).setCellValue(assetSwAuditReportDtoList2.get(dd2).getDivideNum());
                getCell(sheet6, cc2, 44).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemNm());
                getCell(sheet6, cc2, 61).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemGradeView());
                getCell(sheet6, cc2, 63).setCellValue(assetSwAuditReportDtoList2.get(dd2).getHostCount());
                getCell(sheet6, cc2, 64).setCellValue(assetSwAuditReportDtoList2.get(dd2).getHostCount());
                getCell(sheet6, cc2, 65).setCellValue(assetSwAuditReportDtoList2.get(dd2).getEtc());
                dd2++;
            } // end for assetSwAuditReportDtoList2
//            for (int cc = 157; cc < 157 + assetSwAuditReportDtoList2.size(); cc++) {
//
//                for (int ff = 36; ff <= 65; ff++) {
//                    getCell(sheet6, cc, ff).setCellStyle(cellStyle(gWorkbook, 10)); // style
//                }
//            } // end for style
        }

        // 취약점 상세 항목 가이드. 테이블. 이행점검
        if (assetSwAuditReportDtoList2 != null && assetSwAuditReportDtoList2.size() > 0) {
            int dd = 0;
            for (int cc = 211-1-1-1-2-4; cc < 211-1-1-1-2-4 + assetSwAuditReportDtoList2.size(); cc++) {

//                getCell(sheet6, cc, 36).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemGrpNm());
//                getCell(sheet6, cc, 41).setCellValue(assetSwAuditReportDtoList2.get(dd).getDivideNum());
//                getCell(sheet6, cc, 44).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemStandard());
//                getCell(sheet6, cc, 61).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());

                String itemCMstr = assetSwAuditReportDtoList2.get(dd).getItemCountermeasure();
                String[] chars = null;
                boolean autoRowHeightFlag = false;
                short charsLength = 1;
                try {
                    chars = itemCMstr.split("\n");

                    charsLength = (short) (chars.length);
                    autoRowHeightFlag = true;
                } catch (Exception e) {

                    autoRowHeightFlag = false;
                }

                getCell(sheet6, cc, 36).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 41).setCellValue(assetSwAuditReportDtoList2.get(dd).getDivideNum());
                getCell(sheet6, cc, 44).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemStandard());
                if (autoRowHeightFlag) {

                    getCellAutoRowHeight(sheet6, cc, 61, charsLength).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());
                } else {
                    getCell(sheet6, cc, 61).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());
                }

                dd++;
            } // end for
        } // end if


    } // end method createTotalReportTargets8

    private void createTotalReportTargets7(XSSFWorkbook gWorkbook, List<TotalOverallResultDto> checkTargetsList, List<TotalOverallResultDto> migTargetsList, Map gAssetSwJob7, RestTemplate rest) throws Exception {

        logger.info("*[Web App(URL)]");
        XSSFSheet sheet9 = gWorkbook.getSheet("Web App(URL)");
        // 점검군별 (Linux, Window, DB, WEB, WAS, NW, Web App(URL), 기타)

        // (1) 보안점검 Web App(URL)
        // -> sw_nm in ('2018_Web Application', '2019_Web Application' , 'ASP' , 'ASP.NET' , 'JSP' , 'PHP')

        // 현황 [14,1]
        String statusStr = "CONCATENATE(\" 웹 어플리케이션 보안점검 결과, 전체 점검대상(\",D20,\"EA)의 평균 보안수준은 \",AB56,\"입니다. 점검 항목 중 보안 설정이 가장 양호하게 설정된 점검 항목은 \",\"\"\"\",INDEX(B26:B55,MATCH(MAX(AB26:AB55),AB26:AB55,0),1),\"\"\"\",\"로 보안준수율은 \", MAX(AB26:AB55),\"점이며 \",\"\"\"\",INDEX(B26:B55,MATCH(MIN(AB26:AB55),AB26:AB55,0),1),\"\"\"\",\" 항목이 \",MIN(AB26:AB55),\"점으로 보안 설정이 가장 미흡하게 설정되어 있습니다.\")";
        getCell(sheet9, 11, 5).setCellFormula(statusStr);
        // [14,40]
        String statusMigStr = "CONCATENATE(\" 웹 어플리케이션 이행점검 결과, 전체 점검대상(\",AM20,\"EA)의 평균 보안수준은 \",AB56,\"에서 \",BK56,\"으로 변경되었습니다. 보안설정이 가장 미흡하게 설정되어 있던 \",\"\"\"\",INDEX(B26:B55,MATCH(MAX(AB26:AB55),AB26:AB55,0),1),\"\"\"\",\" 점검 항목이 \", MIN(AB26:AB55),\"점에서 \",INDEX(BK26:BK55,MATCH(MIN(AB26:AB55),AB26:AB55,0),1),\"점으로 보안준수율이 변경되었으며, 현재 보안준수율이 가장 낮은 점검 항목으로는 \",\"\"\"\",INDEX(AK26:AK55,MATCH(MIN(BK26:BK55),BK26:BK55,0),1),\"\"\"\",\"으로 보안준수율은 \",MIN(BK26:BK55),\"점 입니다.\")";
        getCell(sheet9, 11, 40).setCellFormula(statusMigStr);


        List<DetailResultDto> urlTargetsList = testService.getDetailEtcTargetsList(checkTargetsList, gAssetSwJob7, "Web App(URL)");

        if (urlTargetsList == null || urlTargetsList.size() < 1) {

            throw new ErrorException("NoSuchElementException: No value");
        }

        // 점검수량 (EA)
        for (DetailResultDto dto : urlTargetsList) {

            // [19,3]
            getCell(sheet9, 19, 3).setCellValue(dto.getValue1());
            break;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h = 0;
        for (int g = 25; g < 25 + urlTargetsList.size(); g++) {

            getCell(sheet9, g, 1).setCellValue(urlTargetsList.get(h).getKey());
            getCell(sheet9, g, 5).setCellValue(urlTargetsList.get(h).getValue2());
            getCell(sheet9, g, 8).setCellValue(urlTargetsList.get(h).getValue3());
            getCell(sheet9, g, 11).setCellValue(urlTargetsList.get(h).getValue4());
            getCell(sheet9, g, 15).setCellValue(urlTargetsList.get(h).getValue5());
            getCell(sheet9, g, 19).setCellValue(urlTargetsList.get(h).getValue6());
            getCell(sheet9, g, 23).setCellValue(urlTargetsList.get(h).getValue7());

            double value26AuditRate = urlTargetsList.get(h).getValue26();
            value26AuditRate = Math.round(value26AuditRate * 100d) / 100d; // 소수 둘째 자리
            getCell(sheet9, g, 27).setCellValue(value26AuditRate);

            // 보안수준
            getCell(sheet9, g, 31).setCellFormula(String.format("IF(AB%d>=90,\"우수\",IF(AND(AB%d>=80,AB%d<90),\"양호\",IF(AND(AB%d>=70,AB%d<80),\"보통\",\"미흡\")))"
                    , g + 1, g + 1, g + 1, g + 1, g + 1));
            h++;
        }

        XSSFSheet sheet5 = gWorkbook.getSheet("Ⅳ. 전체종합결과");

        /*
            Total. 합계. 점검항목수
         */
        //
        getCell(sheet9, 55, 5).setCellFormula("SUM(F26:H55)");
        getCell(sheet9, 55, 8).setCellFormula("SUM(I26:K55)");
        getCell(sheet9, 55, 11).setCellFormula("SUM(L26:L55)");
        getCell(sheet9, 55, 15).setCellFormula("SUM(P26:S55)");
        getCell(sheet9, 55, 19).setCellFormula("SUM(T26:W55)");
        getCell(sheet9, 55, 23).setCellFormula("SUM(L56:W56)");
        getCell(sheet9, 55, 27).setCellFormula("IF(AI56>=90,\"우수\",IF(AND(AI56>=80,AI56<90),\"양호\",IF(AND(AI56>=70,AI56<80),\"보통\",\"미흡\")))");


        // (2) 이행점검 URL
        List<DetailResultDto> migDeatailTargetsList = testService.getMigDetailEtcTargetsList(migTargetsList, gAssetSwJob7, "Web App(URL)");

        /*
            점검군별 이행점검 결과
              value10; // 이행점검 취약점 개수
         */
        // 점검수량 (EA)

        for (DetailResultDto dto : migDeatailTargetsList) {

            // [19,38]
            getCell(sheet9, 19, 38).setCellValue(dto.getValue1());
            break;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h2 = 0;
        for (int g2 = 25; g2 < 25 + migDeatailTargetsList.size(); g2++) {

            getCell(sheet9, g2, 36).setCellValue(migDeatailTargetsList.get(h2).getKey());
            getCell(sheet9, g2, 40).setCellValue(migDeatailTargetsList.get(h2).getValue2());
            getCell(sheet9, g2, 43).setCellValue(migDeatailTargetsList.get(h2).getValue3());
            getCell(sheet9, g2, 46).setCellValue(migDeatailTargetsList.get(h2).getValue4());
            getCell(sheet9, g2, 50).setCellValue(migDeatailTargetsList.get(h2).getValue5());
            getCell(sheet9, g2, 54).setCellValue(migDeatailTargetsList.get(h2).getValue6());
            getCell(sheet9, g2, 58).setCellValue(migDeatailTargetsList.get(h2).getValue7());

            double value26AuditRate = migDeatailTargetsList.get(h2).getValue27();
            value26AuditRate = Math.round(value26AuditRate * 100d) / 100d; // 소수 둘째 자리
            getCell(sheet9, g2, 62).setCellValue(value26AuditRate);

            // 보안수준
            getCell(sheet9, g2, 66).setCellFormula(String.format("IF(BK%d>=90,\"우수\",IF(AND(BK%d>=80,BK%d<90),\"양호\",IF(AND(BK%d>=70,BK%d<80),\"보통\",\"미흡\")))"
                    , g2 + 1, g2 + 1, g2 + 1, g2 + 1, g2 + 1));

            h2++;
        } // end for

        /*
            Total. 합계. 점검항목수
         */
        getCell(sheet9, 55, 40).setCellFormula("SUM(AO26:AQ55)");
        getCell(sheet9, 55, 43).setCellFormula("SUM(AR26:AT55)");
        getCell(sheet9, 55, 46).setCellFormula("SUM(AU26:AU55)");
        getCell(sheet9, 55, 50).setCellFormula("SUM(AY26:BB55)");
        getCell(sheet9, 55, 54).setCellFormula("SUM(BC26:BF55)");
        getCell(sheet9, 55, 58).setCellFormula("SUM(BG26:BJ55)");
        getCell(sheet9, 55, 62).setCellFormula("IF(BT56>=90,\"우수\",IF(AND(BT56>=80,BT56<90),\"양호\",IF(AND(BT56>=70,BT56<80),\"보통\",\"미흡\")))");

        /* end Web App(URL) */

    } // end method createTotalReportTargets7

    // NW
    private void createTotalReportTargets6(XSSFWorkbook gWorkbook, List<TotalOverallResultDto> checkTargetsList, Map gAssetSwJob1, RestTemplate rest) throws Exception {

        logger.info("*[NW]");
        XSSFSheet sheet6 = gWorkbook.getSheet("NW");
        // 점검군별 (Linux, Window, DB, WEB, WAS, NW, Web App(URL), 기타)

        // (1) 보안점검 NW

        // 현황
        String statusStr = "CONCATENATE(\" 서버 보안점검 결과, 전체 점검대상(\",D32,\"EA)의 평균 보안수준은 \",AB56,\"입니다. 분류 항목 중 보안 설정이 가장 양호하게 설정된 분류 항목은 \",\"\"\"\",INDEX(B38:B55,MATCH(MAX(AB38:AB55),AB38:AB55,0),1),\"\"\"\",\"로 보안준수율은 \", MAX(AB38:AB55),\"점이며 \",\"\"\"\",INDEX(B38:B55,MATCH(MIN(AB38:AB55),AB38:AB55,0),1),\"\"\"\",\" 항목이 \",MIN(AB38:AB55),\"점으로 보안 설정이 가장 미흡하게 설정되어 있습니다.\")";
        getCell(sheet6, 11-1-1-1-2, 5).setCellFormula(statusStr);

        String statusMigStr = "CONCATENATE(\" 서버 이행점검 결과, 전체 점검대상(\",AM32,\"EA)의 평균 보안수준은 \",BD56,\"에서 \",BO56,\"으로 변경되었습니다. 보안설정이 가장 미흡하게 설정되어 있던 \",\"\"\"\",INDEX(AK38:AK55,MATCH(MIN(BD38:BD55),BD38:BD55,0),1),\"\"\"\",\" 분류 항목이 \", MIN(BD38:BD55),\"점에서 \",INDEX(BO38:BO55,MATCH(MIN(BD38:BD55),BD38:BD55,0),1),\"점으로 보안준수율이  변경되었으며, 현재 보안준수율이 가장 낮은 분류 항목으로는 \",\"\"\"\",INDEX(AK38:AK55,MATCH(MIN(BO38:BO55),BO38:BO55,0),1),\"\"\"\",\"으로 보안준수율은 \",MIN(BO38:BO55),\"점 입니다.\")";
        getCell(sheet6, 11-1-1-1-2, 40).setCellFormula(statusMigStr);


        /*----------------------------------------------------------------------------------------------------*/
        List<DetailResultDto> linuxTargetsList = testService.getDetailTargetsList(checkTargetsList, gAssetSwJob1, "NW", true);

        if (linuxTargetsList == null || linuxTargetsList.size() < 1) {

            throw new ErrorException("NoSuchElementException: No value");
        }

        // 점검수량 (EA)
        for (DetailResultDto dto : linuxTargetsList) {

            getCell(sheet6, 40-1-1-1-2-4, 3).setCellValue(dto.getValue1());
//            getCell(sheet6, 64-1-1-1-2-4, 5).setCellValue(dto.getValue16());
//            getCell(sheet6, 64-1-1-1-2-4, 8).setCellValue(dto.getValue16());
//        -> 현재는 개별항목수 x 점검수량 개수로 맞춤
            break;
        }

        // 양호 상중하Total.테이블
        int t1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 20).setCellValue(linuxTargetsList.get(t1).getValue17());
            getCell(sheet6, k2, 21).setCellValue(linuxTargetsList.get(t1).getValue18());
            getCell(sheet6, k2, 22).setCellValue(linuxTargetsList.get(t1).getValue19());
            t1++;
        }

        // 인터뷰필요 상중하Total.테이블
        int i1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 24).setCellValue(linuxTargetsList.get(i1).getValue20());
            getCell(sheet6, k2, 25).setCellValue(linuxTargetsList.get(i1).getValue21());
            getCell(sheet6, k2, 26).setCellValue(linuxTargetsList.get(i1).getValue22());
            i1++;
        }
        // 불가 상중하Total.테이블
        int d1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 28).setCellValue(linuxTargetsList.get(d1).getValue23());
            getCell(sheet6, k2, 29).setCellValue(linuxTargetsList.get(d1).getValue24());
            getCell(sheet6, k2, 30).setCellValue(linuxTargetsList.get(d1).getValue25());
            d1++;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h = 0;
        int h2 = 23-1-1-1-2-4;
        for (int g = 46-1-1-1-2-4; g < 46-1-1-1-2-4 + (linuxTargetsList.size()); g+=1) {

            getCell(sheet6, g, 1).setCellValue(linuxTargetsList.get(h).getKey());
            getCell(sheet6, g, 5).setCellValue(linuxTargetsList.get(h).getValue2());
            getCell(sheet6, g, 8).setCellValue(linuxTargetsList.get(h).getValue3());
            getCell(sheet6, g, 11).setCellValue(linuxTargetsList.get(h).getValue4());
            getCell(sheet6, g, 15).setCellValue(linuxTargetsList.get(h).getValue5());
            getCell(sheet6, g, 19).setCellValue(linuxTargetsList.get(h).getValue6());
            getCell(sheet6, g, 23).setCellValue(linuxTargetsList.get(h).getValue7());

            // 보안점검 보안준수율 (웹UI 의 audit_rate 산정식을 기반으로 다시 계산함)
            getCell(sheet6, g, 27).setCellFormula(String.format(
                    "IF(F%d=0,100,ROUND(IFERROR((U%d*3+V%d*2+W%d*1) / ((U%d*3+V%d*2+W%d*1)+(L%d*3+P%d*2+T%d*1)+(Y%d*3+Z%d*2+AA%d*1)+(AC%d*3+AD%d*2+AE%d*1))*100,0),2))"
                    ,g+1
                    ,h2+1,h2+1,h2+1
                    ,h2+1,h2+1,h2+1
                    ,g+1,g+1,g+1
                    ,h2+1,h2+1,h2+1
                    ,h2+1,h2+1,h2+1)
            );
            // 보안수준
            getCell(sheet6, g, 31).setCellFormula(String.format("IF(AB%d>=90,\"우수\",IF(AND(AB%d>=80,AB%d<90),\"양호\",IF(AND(AB%d>=70,AB%d<80),\"보통\",\"미흡\")))"
                    , g + 1, g + 1, g + 1, g + 1, g + 1));
            h++;
            h2++;
        }

        // 전체 점검 개수(N/A 제외) Total
        getCell(sheet6, 33-1-1-2, 5).setCellFormula("SUM(F15:F29)");  // 25
        getCell(sheet6, 33-1-1-2, 8).setCellFormula("SUM(I15:I29)");  // 16
        getCell(sheet6, 33-1-1-2, 11).setCellFormula("SUM(L15:L29)"); // 5
        getCell(sheet6, 33-1-1-2, 14).setCellFormula("SUM(O15:O29)"); // 0

        getCell(sheet6, 33-1-1-2, 20).setCellFormula("SUM(U15:U20)"); // 0
        getCell(sheet6, 33-1-1-2, 21).setCellFormula("SUM(V15:V20)"); // 0
        getCell(sheet6, 33-1-1-2, 22).setCellFormula("SUM(W15:W20)"); // 0

        getCell(sheet6, 33-1-1-2, 24).setCellFormula("SUM(Y15:Y20)"); // 0
        getCell(sheet6, 33-1-1-2, 25).setCellFormula("SUM(Z15:Z20)"); // 0
        getCell(sheet6, 33-1-1-2, 26).setCellFormula("SUM(AA15:AA20)"); // 0

        getCell(sheet6, 33-1-1-2, 28).setCellFormula("SUM(AC15:AC20)"); // 0
        getCell(sheet6, 33-1-1-2, 29).setCellFormula("SUM(AD15:AD20)"); // 0
        getCell(sheet6, 33-1-1-2, 30).setCellFormula("SUM(AE15:AE20)"); // 0

        /*
            Total. 합계. 점검항목수
         */
        getCell(sheet6, 64-1-1-1-2-4, 5).setCellFormula("SUM(F38:H55)");
        getCell(sheet6, 64-1-1-1-2-4, 8).setCellFormula("SUM(I38:K55)");
//        getCell(sheet6, 64, 8).setCellFormula("SUM(I38:K55)");
//        -> value16 으로 대체
//        -> 현재는 개별항목수 x 점검수량 개수로 맞춤

        getCell(sheet6, 64-1-1-1-2-4, 11).setCellFormula("IFERROR(SUM(L38:O55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 15).setCellFormula("IFERROR(SUM(P38:S55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 19).setCellFormula("IFERROR(SUM(T38:W55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 23).setCellFormula("SUM(L56:W57)");

        //보안준수율 Total
        getCell(sheet6, 64-1-1-1-2-4, 34).setCellFormula("ROUND(AVERAGEA(AB38:AE55),2)");

        double sCheckValue = 0.0;
        double tempSCheckValue1 = 0.0;
        double tempSCheckValue2 = 0.0;

        // 전체 점검 개수(N/A 제외) 상중하Total.테이블
        int value10 = 0;
        int value11 = 0;
        int value12 = 0;
        for (DetailResultDto detailResultDto : linuxTargetsList) {

            value10 += detailResultDto.getValue10();  // 상
            value11 += detailResultDto.getValue11();  // 중
            value12 += detailResultDto.getValue12();  // 하
        }
        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int value4 = 0;
        int value5 = 0;
        int value6 = 0;
        for (DetailResultDto detailResultDto : linuxTargetsList) {

            value4 += detailResultDto.getValue4();   // 상
            value5 += detailResultDto.getValue5();   // 중
            value6 += detailResultDto.getValue6();   // 하
        }

        // 보안준수율 에서 보안준수율 로 변경
        tempSCheckValue1 = ((value10*3+value11*2+value12*1)-(value4*3+value5*2+value6*1));
        tempSCheckValue2 = (value10*3+value11*2+value12*1);
        sCheckValue = (tempSCheckValue1/tempSCheckValue2) * 100;
        sCheckValue = Math.round( sCheckValue * 100d ) / 100d;

        XSSFSheet sheet5 = gWorkbook.getSheet("Ⅳ. 전체종합결과");
        getCell(sheet6, 64-1-1-1-2-4, 27).setCellFormula("IF(AI56>=90,\"우수\",IF(AND(AI56>=80,AI56<90),\"양호\",IF(AND(AI56>=70,AI56<80),\"보통\",\"미흡\")))");

        /*
            Total. 평균. 점검결과
         */
        getCell(sheet6, 66-1-1-1-2-4, 11).setCellFormula("ROUND(IFERROR((U30*3)/((U30*3)+(L56*3)+(Y30*3)+(AC30*3))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 15).setCellFormula("ROUND(IFERROR((V30*2)/((V30*2)+(P56*2)+(Z30*2)+(AD30*2))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 19).setCellFormula("ROUND(IFERROR((W30*1)/((W30*1)+(T56*1)+(AA30*1)+(AE30*1))*100,0),2)");

        // 발견된 취약점 기간별 조치 방안
        // 현황 [74, 5]
        String statusStr2 = "CONCATENATE(\" 발견된 취약한 항목 중 위험도 \",\"\"\"\",\"상\",\"\"\"\",\"에 해당하는 즉시 조치가 필요한 항목의 비율은 평균 \",ROUND(Q112*100,1),\"%를 차지하고 있으며, 위험도 \",\"\"\"\",\"중\",\"\"\"\",\",\",\"\"\"\",\"하\",\"\"\"\",\"에 해당하는 중장기적으로 조치가 필요한 항목의 비율은 \",ROUND(AC112*100,1),\"%로 분석 되었습니다. 또한 즉시 조치가 필요한 항목 중, 가장 높은 비율을 차지하고 있는 상위 10개의 점검 대상은 아래와 같습니다.\")";
        getCell(sheet6, 74-1-1-1-2-4, 5).setCellFormula(statusStr2);
        // 현황 [74, 40]
        String statusMigStr2 = "CONCATENATE(\" 발견된 취약한 항목 중 평균 조치율은 \",ROUND(BR112*100,1),\"%로써 위험도 \",\"\"\"\",\"상\",\"\"\"\",\"에 해당하는 즉시 조치가 필요한 항목의 비율은 평균 \",ROUND(IFERROR(AR112/AO112*100,0),1),\"%에서 \",ROUND(BD112*100,1),\"%로 감소하였으며 위험도 \",\"\"\"\",\"중\",\"\"\"\",\",\",\"\"\"\",\"하\",\"\"\"\",\"에 해당하는 중장기적으로 조치가 필요한 항목의 비율은 \",ROUND(IFERROR(AV112/AO112*100,0),1),\"%에서 \",ROUND(BK112*100,1),\"%로 감소한 것으로 분석 되었습니다. 또한 잔여 취약점이 존재하는 대상 중, 즉시 조치가 필요한 항목이 높은 비율을 차지하고 있는 상위 10개의 점검 대상은 아래와 같습니다.\")";
        getCell(sheet6, 74-1-1-1-2-4, 40).setCellFormula(statusMigStr2);

        // 구분, 발견된취약점 수, 기간별 조치 방안 표
        int jj = 0;
        int kk = 47-1-1-1-2-4;
        int ll = 103-1-1-1-2-4;
        for (int ii = 102-1-1-1-2-4; ii < 102-1-1-1-2-4 + (linuxTargetsList.size()); ii+=1) {

            getCell(sheet6, ii, 1).setCellValue(linuxTargetsList.get(jj).getKey());

            getCell(sheet6, ii, 5).setCellFormula(String.format("X%d", kk));

            getCell(sheet6, ii, 10).setCellFormula(String.format("L%d", kk));

            getCell(sheet6, ii, 16).setCellFormula(String.format("IFERROR(K%d/F%d,0)", ll, ll));

            getCell(sheet6, ii, 22).setCellFormula(String.format("P%d+T%d", kk, kk));

            getCell(sheet6, ii, 28).setCellFormula(String.format("IFERROR(W%d/F%d,0)", ll, ll));
            jj++;
            kk += 1;
            ll += 1;
        }

        // 구분, 발견된취약점 수, 기간별 조치 방안 표 Total
        getCell(sheet6, 120-1-1-1-2-4, 5).setCellFormula("SUM(F94:J111)");
        getCell(sheet6, 120-1-1-1-2-4, 10).setCellFormula("SUM(K94:P111)");
        getCell(sheet6, 120-1-1-1-2-4, 16).setCellFormula("IFERROR(K112/F112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 22).setCellFormula("SUM(W94:AB111)");
        getCell(sheet6, 120-1-1-1-2-4, 28).setCellFormula("IFERROR(W112/F112,0)");

        // 즉시 조치 필요 대상시스템 (TOP 10). 테이블
        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList = testService.getReportResultProcessTargets(gAssetSwJob1, true);

        snetAssetSwAuditExcelDtoList.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate).reversed());

        snetAssetSwAuditExcelDtoList = snetAssetSwAuditExcelDtoList.stream().limit(10).collect(Collectors.toList());

        int bb = 0;
        if (snetAssetSwAuditExcelDtoList != null && snetAssetSwAuditExcelDtoList.size() > 0) {
            for (int aa = 126-1-1-1-2-4; aa < 126-1-1-1-2-4 + snetAssetSwAuditExcelDtoList.size(); aa++) {

                getCell(sheet6, aa, 182).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getHostNm());
                getCell(sheet6, aa, 183).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getAuditRate());

                if (snetAssetSwAuditExcelDtoList.get(bb).getWeekHighRate() == 0.0) {

                    getCell(sheet6, aa, 184).setCellValue("");
                } else {

                    getCell(sheet6, aa, 184).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getWeekHighRate());
                }

                if (snetAssetSwAuditExcelDtoList.get(bb).getWeekMiddleLowRate() == 0.0) {

                    getCell(sheet6, aa, 185).setCellValue("");
                } else {

                    getCell(sheet6, aa, 185).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getWeekMiddleLowRate());
                }
                bb++;
            }
        }

        // 취약점 상세 항목. 테이블. 보안점검
        List<SnetAssetSwAuditReportDto> assetSwAuditReportDtoList = testService.selectAssetSwAuditReportNew(gAssetSwJob1, true);

        if (assetSwAuditReportDtoList != null && assetSwAuditReportDtoList.size() > 0) {
            int dd = 0;
            for (int cc = 157-1-1-1-2-4; cc < 157-1-1-1-2-4 + assetSwAuditReportDtoList.size(); cc++) {

                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemNmDesc());
                getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGradeView());
                getCell(sheet6, cc, 26).setCellValue(assetSwAuditReportDtoList.get(dd).getHostCount());
                getCell(sheet6, cc, 28).setCellValue(assetSwAuditReportDtoList.get(dd).getEtc());
                dd++;
            } // end for assetSwAuditReportDtoList
//            for (int cc = 157; cc < 157 + assetSwAuditReportDtoList.size(); cc++) {
//
//                for (int ee = 1; ee <= 33; ee++) {
//                    getCell(sheet6, cc, ee).setCellStyle(cellStyle(gWorkbook, 10)); // style
//                }
//            } // end for style
        } // end if

        // 취약점 상세 항목 가이드. 테이블. 보안점검
        int colWithinchars = (sheet6.getColumnWidth(2) + sheet6.getColumnWidth(3));
        if (assetSwAuditReportDtoList != null && assetSwAuditReportDtoList.size() > 0) {
            int dd = 0;
            for (int cc = 211-1-1-1-2-4; cc < 211-1-1-1-2-4 + assetSwAuditReportDtoList.size(); cc++) {

//                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
//                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
//                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemStandard());
//                getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());


                String itemCMstr = assetSwAuditReportDtoList.get(dd).getItemCountermeasure();
                String[] chars = null;
                boolean autoRowHeightFlag = false;
                short charsLength = 1;
                try {
                    chars = itemCMstr.split("\n");

                    charsLength = (short) (chars.length);
                    autoRowHeightFlag = true;
                } catch (Exception e) {

                    autoRowHeightFlag = false;
                }


                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemStandard());
                if (autoRowHeightFlag) {
                    getCellAutoRowHeight(sheet6, cc, 24, charsLength).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());
                } else {
                    getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());
                }
                dd++;

            } // end for
        } // end if


        // (2) 이행점검 Linux
        List<DetailResultDto> migTargetsList = testService.getMigDetailTargetsList(checkTargetsList, gAssetSwJob1, "NW", true);

        // 점검수량 (EA)
        getCell(sheet6, 40-1-1-1-2-4, 38).setCellFormula("D32");

        // 양호 상중하Total.테이블
        int t2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 36).setCellValue(migTargetsList.get(t2).getValue17());
            getCell(sheet6, k2, 37).setCellValue(migTargetsList.get(t2).getValue18());
            getCell(sheet6, k2, 38).setCellValue(migTargetsList.get(t2).getValue19());
            t2++;
        }

        // 인터뷰필요 상중하Total.테이블
        int i2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 40).setCellValue(migTargetsList.get(i2).getValue20());
            getCell(sheet6, k2, 41).setCellValue(migTargetsList.get(i2).getValue21());
            getCell(sheet6, k2, 42).setCellValue(migTargetsList.get(i2).getValue22());
            i2++;
        }
        // 불가 상중하Total.테이블
        int d2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 44).setCellValue(migTargetsList.get(d2).getValue23());
            getCell(sheet6, k2, 45).setCellValue(migTargetsList.get(d2).getValue24());
            getCell(sheet6, k2, 46).setCellValue(migTargetsList.get(d2).getValue25());
            d2++;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h3 = 0;
        int h4 = 47-1-1-1-2-4;
        for (int g2 = 46-1-1-1-2-4; g2 < 46-1-1-1-2-4 + (linuxTargetsList.size()); g2+=1) {

            getCell(sheet6, g2, 36).setCellValue(linuxTargetsList.get(h3).getKey());
            getCell(sheet6, g2, 40).setCellFormula(String.format("F%d", g2 + 1));
            getCell(sheet6, g2, 43).setCellFormula(String.format("I%d", g2 + 1));
            getCell(sheet6, g2, 46).setCellFormula(String.format("L%d", g2 + 1));
            getCell(sheet6, g2, 48).setCellFormula(String.format("P%d", g2 + 1));
            getCell(sheet6, g2, 50).setCellFormula(String.format("T%d", g2 + 1));
            getCell(sheet6, g2, 53).setCellFormula(String.format("SUM(AU%d:BA%d)", g2 + 1, g2 + 1));

            // 보안준수율
            getCell(sheet6, g2, 55).setCellFormula(String.format("AB%d", g2 + 1));
            h3++;
            h4++;
        }

        int h5 = 0;
        int h6 = 23-1-1-1-2-4;
        for (int g2 = 46-1-1-1-2-4; g2 < 46-1-1-1-2-4 + (migTargetsList.size()); g2+=1) {

            // 이행점검
            // 상
            getCell(sheet6, g2, 58).setCellValue(migTargetsList.get(h5).getValue4());
            // 중
            getCell(sheet6, g2, 60).setCellValue(migTargetsList.get(h5).getValue5());
            // 하
            getCell(sheet6, g2, 62).setCellValue(migTargetsList.get(h5).getValue6());
            // Total
            getCell(sheet6, g2, 64).setCellFormula(String.format("SUM(BG%d:BL%d)", g2 + 1, g2 + 1));

            // 전체 점검 개수(N/A 제외) 상중하Total.테이블 옆에 위치
            //      보안준수율   :
            getCell(sheet6, g2, 66).setCellFormula(String.format(
                    "IF(AO%d=0,100,ROUND(IFERROR((AK%d*3+AL%d*2+AM%d*1) / ((AK%d*3+AL%d*2+AM%d*1)+(BG%d*3+BI%d*2+BK%d*1)+(AO%d*3+AP%d*2+AQ%d*1)+(AS%d*3+AT%d*2+AU%d*1))*100,0),2))"
                    ,g2+1
                    ,h6+1,h6+1,h6+1
                    ,h6+1,h6+1,h6+1
                    ,g2+1,g2+1,g2+1
                    ,h6+1,h6+1,h6+1
                    ,h6+1,h6+1,h6+1)
            );

            //      보안수준   :
            getCell(sheet6, g2, 68).setCellFormula(String.format("IF(BO%d>=90,\"우수\",IF(AND(BO%d>=80,BO%d<90),\"양호\",IF(AND(BO%d>=70,BO%d<80),\"보통\",\"미흡\")))"
                    , g2 + 1, g2 + 1, g2 + 1, g2 + 1, g2 + 1));
            h5++;
            h6++;
        }

        getCell(sheet6, 33-1-1-2, 36).setCellFormula("SUM(AK15:AK20)"); // 20
        getCell(sheet6, 33-1-1-2, 37).setCellFormula("SUM(AL15:AL20)"); // 12
        getCell(sheet6, 33-1-1-2, 38).setCellFormula("SUM(AM15:AM20)"); // 2

        getCell(sheet6, 33-1-1-2, 40).setCellFormula("SUM(AO15:AO20)"); // 0
        getCell(sheet6, 33-1-1-2, 41).setCellFormula("SUM(AP15:AP20)"); // 1
        getCell(sheet6, 33-1-1-2, 42).setCellFormula("SUM(AQ15:AQ20)"); // 0

        getCell(sheet6, 33-1-1-2, 44).setCellFormula("SUM(AS15:AS20)"); // 0
        getCell(sheet6, 33-1-1-2, 45).setCellFormula("SUM(AT15:AT20)"); // 0
        getCell(sheet6, 33-1-1-2, 46).setCellFormula("SUM(AU15:AU20)"); // 0
        // Total
        //      합계 :

        getCell(sheet6, 64-1-1-1-2-4, 40).setCellFormula("F56");
        getCell(sheet6, 64-1-1-1-2-4, 43).setCellFormula("I56");
        getCell(sheet6, 64-1-1-1-2-4, 46).setCellFormula("SUM(AU38:AV55)");
        getCell(sheet6, 64-1-1-1-2-4, 48).setCellFormula("SUM(AW38:AX55)");
        getCell(sheet6, 64-1-1-1-2-4, 50).setCellFormula("SUM(AY38:BA55)");
        getCell(sheet6, 64-1-1-1-2-4, 53).setCellFormula("SUM(BB38:BC55)");
        getCell(sheet6, 64-1-1-1-2-4, 55).setCellFormula("AB56");

        //          - 이행점검
        getCell(sheet6, 64-1-1-1-2-4, 58).setCellFormula("IFERROR(SUM(BG38:BH55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 60).setCellFormula("IFERROR(SUM(BI38:BJ55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 62).setCellFormula("IFERROR(SUM(BK38:BL55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 64).setCellFormula("SUM(BM38:BN55)");

        //보안준수율 Total
        getCell(sheet6, 64-1-1-1-2-4, 71).setCellFormula("ROUND(AVERAGEA(BO38:BP55),2)");

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int pvalue4 = 0;
        int pvalue5 = 0;
        int pvalue6 = 0;
        int pvalue13 = 0;
        int pvalue14 = 0;
        int pvalue15 = 0;
        for (DetailResultDto detailResultDto : migTargetsList) {

            pvalue4 += detailResultDto.getValue4();   // 상.취약점
            pvalue5 += detailResultDto.getValue5();   // 중.취약점
            pvalue6 += detailResultDto.getValue6();   // 하.취약점
        }

        getCell(sheet6, 64-1-1-1-2-4, 66).setCellFormula("IF(BT56>=90,\"우수\",IF(AND(BT56>=80,BT56<90),\"양호\",IF(AND(BT56>=70,BT56<80),\"보통\",\"미흡\")))");

        //      평균 :
        getCell(sheet6, 66-1-1-1-2-4, 46).setCellFormula("L58");
        getCell(sheet6, 66-1-1-1-2-4, 48).setCellFormula("P58");
        getCell(sheet6, 66-1-1-1-2-4, 50).setCellFormula("T58");

        //     - 이행점검 Total. 평균. 점검결과
        getCell(sheet6, 66-1-1-1-2-4, 58).setCellFormula("ROUND(IFERROR((AK30*3)/((AK30*3)+(BG56*3)+(AO30*3)+(AS30*3))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 60).setCellFormula("ROUND(IFERROR((AL30*2)/((AL30*2)+(BI56*2)+(AP30*2)+(AT30*2))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 62).setCellFormula("ROUND(IFERROR((AM30*1)/((AM30*1)+(BK56*1)+(AQ30*1)+(AU30*1))*100,0),2)");

        // 발견된 취약점 기간별 조치 방안
        // 구분.기간별 조치 방안 표
        int n1 = 47-1-1-1-2-4;
        for (int m1 = 102-1-1-1-2-4; m1 < 102-1-1-1-2-4 + (linuxTargetsList.size()); m1+=1) {
            getCell(sheet6, m1, 36).setCellFormula(String.format("B%d", m1+1));
            getCell(sheet6, m1, 40).setCellFormula(String.format("BB%d", n1));
            getCell(sheet6, m1, 43).setCellFormula(String.format("AU%d", n1));
            getCell(sheet6, m1, 47).setCellFormula(String.format("AW%d+AY%d", n1, n1));
            n1+=1;
        }

        int n2 = 47-1-1-1-2-4;
        for (int m2 = 102-1-1-1-2-4; m2 < 102-1-1-1-2-4 + (migTargetsList.size()); m2+=1) {
            getCell(sheet6, m2, 52).setCellFormula(String.format("BG%d", n2));
            getCell(sheet6, m2, 55).setCellFormula(String.format("IFERROR(BA%d/AO%d,0)", m2+1, m2+1));
            getCell(sheet6, m2, 59).setCellFormula(String.format("BI%d+BK%d", n2, n2));
            getCell(sheet6, m2, 62).setCellFormula(String.format("IFERROR(BH%d/AO%d,0)", m2+1, m2+1));
            getCell(sheet6, m2, 65).setCellFormula(String.format("BA%d+BH%d", m2+1, m2+1));
            getCell(sheet6, m2, 67).setCellFormula(String.format("AO%d-BN%d", m2+1, m2+1));
            getCell(sheet6, m2, 69).setCellFormula(String.format("IFERROR(BP%d/AO%d,0)", m2+1, m2+1));
            n2+=1;
        }

        // 구분.기간별 조치 방안 표 Total
        getCell(sheet6, 120-1-1-1-2-4, 40).setCellFormula("SUM(AO94:AQ111)");
        getCell(sheet6, 120-1-1-1-2-4, 43).setCellFormula("SUM(AR94:AU111)");
        getCell(sheet6, 120-1-1-1-2-4, 47).setCellFormula("SUM(AV94:AY111)");
        getCell(sheet6, 120-1-1-1-2-4, 52).setCellFormula("SUM(BA94:BC111)");
        getCell(sheet6, 120-1-1-1-2-4, 55).setCellFormula("IFERROR(BA112/AO112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 59).setCellFormula("SUM(BH94:BJ111)");
        getCell(sheet6, 120-1-1-1-2-4, 62).setCellFormula("IFERROR(BH112/AO112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 65).setCellFormula("SUM(BN94:BO111)");
        getCell(sheet6, 120-1-1-1-2-4, 67).setCellFormula("SUM(BP94:BQ111)");
        getCell(sheet6, 120-1-1-1-2-4, 69).setCellFormula("IFERROR(BP112/AO112,0)");

        // 즉시 조치 필요 대상시스템 (TOP 10). 테이블
        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList2 = testService.getReportResultProcessTargets(gAssetSwJob1, false);

        snetAssetSwAuditExcelDtoList2.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate).reversed());

        snetAssetSwAuditExcelDtoList2 = snetAssetSwAuditExcelDtoList2.stream().limit(10).collect(Collectors.toList());

        int bb2 = 0;
        if (snetAssetSwAuditExcelDtoList2 != null && snetAssetSwAuditExcelDtoList2.size() > 0) {
            for (int aa2 = 126-1-1-1-2-4; aa2 < 126-1-1-1-2-4 + snetAssetSwAuditExcelDtoList2.size(); aa2++) {

                getCell(sheet6, aa2, 186).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getHostNm());
                getCell(sheet6, aa2, 187).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getAuditRate());

                if (snetAssetSwAuditExcelDtoList.get(bb2).getWeekHighRate() == 0.0) {

                    getCell(sheet6, aa2, 188).setCellValue("");
                } else {

                    getCell(sheet6, aa2, 188).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getWeekHighRate());
                }

                if (snetAssetSwAuditExcelDtoList.get(bb2).getWeekMiddleLowRate() == 0.0) {

                    getCell(sheet6, aa2, 189).setCellValue("");
                } else {

                    getCell(sheet6, aa2, 189).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getWeekMiddleLowRate());
                }
                bb2++;
            }
        }

        // 취약점 상세 항목. 테이블. 이행점검
        List<SnetAssetSwAuditReportDto> assetSwAuditReportDtoList2 = testService.selectAssetSwAuditReportNew(gAssetSwJob1, false);

        if (assetSwAuditReportDtoList2 != null && assetSwAuditReportDtoList2.size() > 0) {

            int dd2 = 0;
            for (int cc2 = 157-1-1-1-2-4; cc2 < 157-1-1-1-2-4 + assetSwAuditReportDtoList2.size(); cc2++) {

                getCell(sheet6, cc2, 36).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemGrpNm());
                getCell(sheet6, cc2, 41).setCellValue(assetSwAuditReportDtoList2.get(dd2).getDivideNum());
                getCell(sheet6, cc2, 44).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemNmDesc());
                getCell(sheet6, cc2, 61).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemGradeView());
                getCell(sheet6, cc2, 63).setCellValue(assetSwAuditReportDtoList2.get(dd2).getHostCount());
                getCell(sheet6, cc2, 64).setCellValue(assetSwAuditReportDtoList2.get(dd2).getHostCount());
                getCell(sheet6, cc2, 65).setCellValue(assetSwAuditReportDtoList2.get(dd2).getEtc());
                dd2++;
            } // end for assetSwAuditReportDtoList2
//            for (int cc = 157; cc < 157 + assetSwAuditReportDtoList2.size(); cc++) {
//
//                for (int ff = 36; ff <= 65; ff++) {
//                    getCell(sheet6, cc, ff).setCellStyle(cellStyle(gWorkbook, 10)); // style
//                }
//            } // end for style
        }

        // 취약점 상세 항목 가이드. 테이블. 이행점검
        if (assetSwAuditReportDtoList2 != null && assetSwAuditReportDtoList2.size() > 0) {
            int dd = 0;
            for (int cc = 211-1-1-1-2-4; cc < 211-1-1-1-2-4 + assetSwAuditReportDtoList2.size(); cc++) {

//                getCell(sheet6, cc, 36).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemGrpNm());
//                getCell(sheet6, cc, 41).setCellValue(assetSwAuditReportDtoList2.get(dd).getDivideNum());
//                getCell(sheet6, cc, 44).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemStandard());
//                getCell(sheet6, cc, 61).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());

                String itemCMstr = assetSwAuditReportDtoList2.get(dd).getItemCountermeasure();
                String[] chars = null;
                boolean autoRowHeightFlag = false;
                short charsLength = 1;
                try {
                    chars = itemCMstr.split("\n");

                    charsLength = (short) (chars.length);
                    autoRowHeightFlag = true;
                } catch (Exception e) {

                    autoRowHeightFlag = false;
                }

                getCell(sheet6, cc, 36).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 41).setCellValue(assetSwAuditReportDtoList2.get(dd).getDivideNum());
                getCell(sheet6, cc, 44).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemStandard());
                if (autoRowHeightFlag) {
                    getCellAutoRowHeight(sheet6, cc, 61, charsLength).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());
                } else {
                    getCell(sheet6, cc, 61).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());
                }

                dd++;
            } // end for
        } // end if

        /* end NW */
    } // end method createTotalReportTargets6


    // WAS
    private void createTotalReportTargets5(XSSFWorkbook gWorkbook, List<TotalOverallResultDto> checkTargetsList, Map gAssetSwJob1, RestTemplate rest) throws Exception {

        logger.info("*[WAS]");
        XSSFSheet sheet6 = gWorkbook.getSheet("WAS");
        // 점검군별 (Linux, Window, DB, WEB, WAS, NW, Web App(URL), 기타)

        // (1) 보안점검 WAS

        // 현황
        String statusStr = "CONCATENATE(\" 서버 보안점검 결과, 전체 점검대상(\",D32,\"EA)의 평균 보안수준은 \",AB56,\"입니다. 분류 항목 중 보안 설정이 가장 양호하게 설정된 분류 항목은 \",\"\"\"\",INDEX(B38:B55,MATCH(MAX(AB38:AB55),AB38:AB55,0),1),\"\"\"\",\"로 보안준수율은 \", MAX(AB38:AB55),\"점이며 \",\"\"\"\",INDEX(B38:B55,MATCH(MIN(AB38:AB55),AB38:AB55,0),1),\"\"\"\",\" 항목이 \",MIN(AB38:AB55),\"점으로 보안 설정이 가장 미흡하게 설정되어 있습니다.\")";
        getCell(sheet6, 11-1-1-1-2, 5).setCellFormula(statusStr);

        String statusMigStr = "CONCATENATE(\" 서버 이행점검 결과, 전체 점검대상(\",AM32,\"EA)의 평균 보안수준은 \",BD56,\"에서 \",BO56,\"으로 변경되었습니다. 보안설정이 가장 미흡하게 설정되어 있던 \",\"\"\"\",INDEX(AK38:AK55,MATCH(MIN(BD38:BD55),BD38:BD55,0),1),\"\"\"\",\" 분류 항목이 \", MIN(BD38:BD55),\"점에서 \",INDEX(BO38:BO55,MATCH(MIN(BD38:BD55),BD38:BD55,0),1),\"점으로 보안준수율이 변경되었으며, 현재 보안준수율이 가장 낮은 분류 항목으로는 \",\"\"\"\",INDEX(AK38:AK55,MATCH(MIN(BO38:BO55),BO38:BO55,0),1),\"\"\"\",\"으로 보안준수율은 \",MIN(BO38:BO55),\"점 입니다.\")";
        getCell(sheet6, 11-1-1-1-2, 40).setCellFormula(statusMigStr);


        /*----------------------------------------------------------------------------------------------------*/
        List<DetailResultDto> linuxTargetsList = testService.getDetailTargetsList(checkTargetsList, gAssetSwJob1, "WAS", true);

        if (linuxTargetsList == null || linuxTargetsList.size() < 1) {

            throw new ErrorException("NoSuchElementException: No value");
        }

        // 점검수량 (EA)
        for (DetailResultDto dto : linuxTargetsList) {

            getCell(sheet6, 40-1-1-1-2-4, 3).setCellValue(dto.getValue1());
//            getCell(sheet6, 64-1-1-1-2-4, 5).setCellValue(dto.getValue16());
//            getCell(sheet6, 64-1-1-1-2-4, 8).setCellValue(dto.getValue16());
//        -> 현재는 개별항목수 x 점검수량 개수로 맞춤
            break;
        }

        // 양호 상중하Total.테이블
        int t1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 20).setCellValue(linuxTargetsList.get(t1).getValue17());
            getCell(sheet6, k2, 21).setCellValue(linuxTargetsList.get(t1).getValue18());
            getCell(sheet6, k2, 22).setCellValue(linuxTargetsList.get(t1).getValue19());
            t1++;
        }

        // 인터뷰필요 상중하Total.테이블
        int i1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 24).setCellValue(linuxTargetsList.get(i1).getValue20());
            getCell(sheet6, k2, 25).setCellValue(linuxTargetsList.get(i1).getValue21());
            getCell(sheet6, k2, 26).setCellValue(linuxTargetsList.get(i1).getValue22());
            i1++;
        }
        // 불가 상중하Total.테이블
        int d1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 28).setCellValue(linuxTargetsList.get(d1).getValue23());
            getCell(sheet6, k2, 29).setCellValue(linuxTargetsList.get(d1).getValue24());
            getCell(sheet6, k2, 30).setCellValue(linuxTargetsList.get(d1).getValue25());
            d1++;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h = 0;
        int h2 = 23-1-1-1-2-4;
        for (int g = 46-1-1-1-2-4; g < 46-1-1-1-2-4 + (linuxTargetsList.size()); g+=1) {

            getCell(sheet6, g, 1).setCellValue(linuxTargetsList.get(h).getKey());
            getCell(sheet6, g, 5).setCellValue(linuxTargetsList.get(h).getValue2());
            getCell(sheet6, g, 8).setCellValue(linuxTargetsList.get(h).getValue3());
            getCell(sheet6, g, 11).setCellValue(linuxTargetsList.get(h).getValue4());
            getCell(sheet6, g, 15).setCellValue(linuxTargetsList.get(h).getValue5());
            getCell(sheet6, g, 19).setCellValue(linuxTargetsList.get(h).getValue6());
            getCell(sheet6, g, 23).setCellValue(linuxTargetsList.get(h).getValue7());

            // 보안준수율 (웹UI 의 audit_rate 산정식을 기반으로 다시 계산함)
            getCell(sheet6, g, 27).setCellFormula(String.format(
                    "IF(F%d=0,100,ROUND(IFERROR((U%d*3+V%d*2+W%d*1) / ((U%d*3+V%d*2+W%d*1)+(L%d*3+P%d*2+T%d*1)+(Y%d*3+Z%d*2+AA%d*1)+(AC%d*3+AD%d*2+AE%d*1))*100,0),2))"
                    ,g+1
                    ,h2+1,h2+1,h2+1
                    ,h2+1,h2+1,h2+1
                    ,g+1,g+1,g+1
                    ,h2+1,h2+1,h2+1
                    ,h2+1,h2+1,h2+1)
            );
            // 보안수준
            getCell(sheet6, g, 31).setCellFormula(String.format("IF(AB%d>=90,\"우수\",IF(AND(AB%d>=80,AB%d<90),\"양호\",IF(AND(AB%d>=70,AB%d<80),\"보통\",\"미흡\")))"
                    , g + 1, g + 1, g + 1, g + 1, g + 1));
            h++;
            h2++;
        }

        // 전체 점검 개수(N/A 제외) Total
        getCell(sheet6, 33-1-1-2, 5).setCellFormula("SUM(F15:F29)");  // 25
        getCell(sheet6, 33-1-1-2, 8).setCellFormula("SUM(I15:I29)");  // 16
        getCell(sheet6, 33-1-1-2, 11).setCellFormula("SUM(L15:L29)"); // 5
        getCell(sheet6, 33-1-1-2, 14).setCellFormula("SUM(O15:O29)"); // 0

        getCell(sheet6, 33-1-1-2, 20).setCellFormula("SUM(U15:U20)"); // 0
        getCell(sheet6, 33-1-1-2, 21).setCellFormula("SUM(V15:V20)"); // 0
        getCell(sheet6, 33-1-1-2, 22).setCellFormula("SUM(W15:W20)"); // 0

        getCell(sheet6, 33-1-1-2, 24).setCellFormula("SUM(Y15:Y20)"); // 0
        getCell(sheet6, 33-1-1-2, 25).setCellFormula("SUM(Z15:Z20)"); // 0
        getCell(sheet6, 33-1-1-2, 26).setCellFormula("SUM(AA15:AA20)"); // 0

        getCell(sheet6, 33-1-1-2, 28).setCellFormula("SUM(AC15:AC20)"); // 0
        getCell(sheet6, 33-1-1-2, 29).setCellFormula("SUM(AD15:AD20)"); // 0
        getCell(sheet6, 33-1-1-2, 30).setCellFormula("SUM(AE15:AE20)"); // 0

        /*
            Total. 합계. 점검항목수
         */
        getCell(sheet6, 64-1-1-1-2-4, 5).setCellFormula("SUM(F38:H55)");
        getCell(sheet6, 64-1-1-1-2-4, 8).setCellFormula("SUM(I38:K55)");
//        getCell(sheet6, 64, 8).setCellFormula("SUM(I38:K55)");
//        -> value16 으로 대체
//        -> 현재는 개별항목수 x 점검수량 개수로 맞춤

        getCell(sheet6, 64-1-1-1-2-4, 11).setCellFormula("IFERROR(SUM(L38:O55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 15).setCellFormula("IFERROR(SUM(P38:S55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 19).setCellFormula("IFERROR(SUM(T38:W55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 23).setCellFormula("SUM(L56:W57)");

        //보안준수율 Total
        getCell(sheet6, 64-1-1-1-2-4, 34).setCellFormula("ROUND(AVERAGEA(AB38:AE55),2)");

        double sCheckValue = 0.0;
        double tempSCheckValue1 = 0.0;
        double tempSCheckValue2 = 0.0;

        // 전체 점검 개수(N/A 제외) 상중하Total.테이블
        int value10 = 0;
        int value11 = 0;
        int value12 = 0;
        for (DetailResultDto detailResultDto : linuxTargetsList) {

            value10 += detailResultDto.getValue10();  // 상
            value11 += detailResultDto.getValue11();  // 중
            value12 += detailResultDto.getValue12();  // 하
        }
        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int value4 = 0;
        int value5 = 0;
        int value6 = 0;
        for (DetailResultDto detailResultDto : linuxTargetsList) {

            value4 += detailResultDto.getValue4();   // 상
            value5 += detailResultDto.getValue5();   // 중
            value6 += detailResultDto.getValue6();   // 하
        }

        // 보안준수율 에서 보안준수율 로 변경
        tempSCheckValue1 = ((value10*3+value11*2+value12*1)-(value4*3+value5*2+value6*1));
        tempSCheckValue2 = (value10*3+value11*2+value12*1);
        sCheckValue = (tempSCheckValue1/tempSCheckValue2) * 100;
        sCheckValue = Math.round( sCheckValue * 100d ) / 100d;

        XSSFSheet sheet5 = gWorkbook.getSheet("Ⅳ. 전체종합결과");
        // Total 보안수준
        getCell(sheet6, 64-1-1-1-2-4, 27).setCellFormula("IF(AI56>=90,\"우수\",IF(AND(AI56>=80,AI56<90),\"양호\",IF(AND(AI56>=70,AI56<80),\"보통\",\"미흡\")))");

        /*
            Total. 평균. 점검결과
         */
        getCell(sheet6, 66-1-1-1-2-4, 11).setCellFormula("ROUND(IFERROR((U30*3)/((U30*3)+(L56*3)+(Y30*3)+(AC30*3))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 15).setCellFormula("ROUND(IFERROR((V30*2)/((V30*2)+(P56*2)+(Z30*2)+(AD30*2))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 19).setCellFormula("ROUND(IFERROR((W30*1)/((W30*1)+(T56*1)+(AA30*1)+(AE30*1))*100,0),2)");

        // 발견된 취약점 기간별 조치 방안
        // 현황 [74, 5]
        String statusStr2 = "CONCATENATE(\" 발견된 취약한 항목 중 위험도 \",\"\"\"\",\"상\",\"\"\"\",\"에 해당하는 즉시 조치가 필요한 항목의 비율은 평균 \",ROUND(Q112*100,1),\"%를 차지하고 있으며, 위험도 \",\"\"\"\",\"중\",\"\"\"\",\",\",\"\"\"\",\"하\",\"\"\"\",\"에 해당하는 중장기적으로 조치가 필요한 항목의 비율은 \",ROUND(AC112*100,1),\"%로 분석 되었습니다. 또한 즉시 조치가 필요한 항목 중, 가장 높은 비율을 차지하고 있는 상위 10개의 점검 대상은 아래와 같습니다.\")";
        getCell(sheet6, 74-1-1-1-2-4, 5).setCellFormula(statusStr2);
        // 현황 [74, 40]
        String statusMigStr2 = "CONCATENATE(\" 발견된 취약한 항목 중 평균 조치율은 \",ROUND(BR112*100,1),\"%로써 위험도 \",\"\"\"\",\"상\",\"\"\"\",\"에 해당하는 즉시 조치가 필요한 항목의 비율은 평균 \",ROUND(IFERROR(AR112/AO112*100,0),1),\"%에서 \",ROUND(BD112*100,1),\"%로 감소하였으며 위험도 \",\"\"\"\",\"중\",\"\"\"\",\",\",\"\"\"\",\"하\",\"\"\"\",\"에 해당하는 중장기적으로 조치가 필요한 항목의 비율은 \",ROUND(IFERROR(AV112/AO112*100,0),1),\"%에서 \",ROUND(BK112*100,1),\"%로 감소한 것으로 분석 되었습니다. 또한 잔여 취약점이 존재하는 대상 중, 즉시 조치가 필요한 항목이 높은 비율을 차지하고 있는 상위 10개의 점검 대상은 아래와 같습니다.\")";
        getCell(sheet6, 74-1-1-1-2-4, 40).setCellFormula(statusMigStr2);

        // 구분, 발견된취약점 수, 기간별 조치 방안 표
        int jj = 0;
        int kk = 47-1-1-1-2-4;
        int ll = 103-1-1-1-2-4;
        for (int ii = 102-1-1-1-2-4; ii < 102-1-1-1-2-4 + (linuxTargetsList.size()); ii+=1) {

            getCell(sheet6, ii, 1).setCellValue(linuxTargetsList.get(jj).getKey());

            getCell(sheet6, ii, 5).setCellFormula(String.format("X%d", kk));

            getCell(sheet6, ii, 10).setCellFormula(String.format("L%d", kk));

            getCell(sheet6, ii, 16).setCellFormula(String.format("IFERROR(K%d/F%d,0)", ll, ll));

            getCell(sheet6, ii, 22).setCellFormula(String.format("P%d+T%d", kk, kk));

            getCell(sheet6, ii, 28).setCellFormula(String.format("IFERROR(W%d/F%d,0)", ll, ll));
            jj++;
            kk += 1;
            ll += 1;
        }

        // 구분, 발견된취약점 수, 기간별 조치 방안 표 Total
        getCell(sheet6, 120-1-1-1-2-4, 5).setCellFormula("SUM(F94:J111)");
        getCell(sheet6, 120-1-1-1-2-4, 10).setCellFormula("SUM(K94:P111)");
        getCell(sheet6, 120-1-1-1-2-4, 16).setCellFormula("IFERROR(K112/F112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 22).setCellFormula("SUM(W94:AB111)");
        getCell(sheet6, 120-1-1-1-2-4, 28).setCellFormula("IFERROR(W112/F112,0)");

        // 즉시 조치 필요 대상시스템 (TOP 10). 테이블
        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList = testService.getReportResultProcessTargets(gAssetSwJob1, true);

        snetAssetSwAuditExcelDtoList.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate).reversed());

        snetAssetSwAuditExcelDtoList = snetAssetSwAuditExcelDtoList.stream().limit(10).collect(Collectors.toList());

        int bb = 0;
        if (snetAssetSwAuditExcelDtoList != null && snetAssetSwAuditExcelDtoList.size() > 0) {
            for (int aa = 126-1-1-1-2-4; aa < 126-1-1-1-2-4 + snetAssetSwAuditExcelDtoList.size(); aa++) {

                getCell(sheet6, aa, 182).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getHostNm());
                getCell(sheet6, aa, 183).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getAuditRate());

                if (snetAssetSwAuditExcelDtoList.get(bb).getWeekHighRate() == 0.0) {

                    getCell(sheet6, aa, 184).setCellValue("");
                } else {

                    getCell(sheet6, aa, 184).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getWeekHighRate());
                }

                if (snetAssetSwAuditExcelDtoList.get(bb).getWeekMiddleLowRate() == 0.0) {

                    getCell(sheet6, aa, 185).setCellValue("");
                } else {

                    getCell(sheet6, aa, 185).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getWeekMiddleLowRate());
                }
                bb++;
            }
        }

        // 취약점 상세 항목. 테이블. 보안점검
        List<SnetAssetSwAuditReportDto> assetSwAuditReportDtoList = testService.selectAssetSwAuditReportNew(gAssetSwJob1, true);

        if (assetSwAuditReportDtoList != null && assetSwAuditReportDtoList.size() > 0) {
            int dd = 0;
            for (int cc = 157-1-1-1-2-4; cc < 157-1-1-1-2-4 + assetSwAuditReportDtoList.size(); cc++) {

                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemNmDesc());
                getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGradeView());
                getCell(sheet6, cc, 26).setCellValue(assetSwAuditReportDtoList.get(dd).getHostCount());
                getCell(sheet6, cc, 28).setCellValue(assetSwAuditReportDtoList.get(dd).getEtc());
                dd++;
            } // end for assetSwAuditReportDtoList
//            for (int cc = 157; cc < 157 + assetSwAuditReportDtoList.size(); cc++) {
//
//                for (int ee = 1; ee <= 33; ee++) {
//                    getCell(sheet6, cc, ee).setCellStyle(cellStyle(gWorkbook, 10)); // style
//                }
//            } // end for style
        } // end if

        // 취약점 상세 항목 가이드. 테이블. 보안점검
        int colWithinchars = (sheet6.getColumnWidth(2) + sheet6.getColumnWidth(3));
        if (assetSwAuditReportDtoList != null && assetSwAuditReportDtoList.size() > 0) {
            int dd = 0;
            for (int cc = 211-1-1-1-2-4; cc < 211-1-1-1-2-4 + assetSwAuditReportDtoList.size(); cc++) {

//                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
//                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
//                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemStandard());
//                getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());


                String itemCMstr = assetSwAuditReportDtoList.get(dd).getItemCountermeasure();
                String[] chars = null;
                boolean autoRowHeightFlag = false;
                short charsLength = 1;
                try {
                    chars = itemCMstr.split("\n");

                    charsLength = (short) (chars.length);
                    autoRowHeightFlag = true;
                } catch (Exception e) {

                    autoRowHeightFlag = false;
                }


                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemStandard());
                if (autoRowHeightFlag) {
                    getCellAutoRowHeight(sheet6, cc, 24, charsLength).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());
                } else {
                    getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());
                }
                dd++;

            } // end for
        } // end if


        // (2) 이행점검 Linux
        List<DetailResultDto> migTargetsList = testService.getMigDetailTargetsList(checkTargetsList, gAssetSwJob1, "WAS", true);

        // 점검수량 (EA)
        getCell(sheet6, 40-1-1-1-2-4, 38).setCellFormula("D32");

        // 양호 상중하Total.테이블
        int t2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 36).setCellValue(migTargetsList.get(t2).getValue17());
            getCell(sheet6, k2, 37).setCellValue(migTargetsList.get(t2).getValue18());
            getCell(sheet6, k2, 38).setCellValue(migTargetsList.get(t2).getValue19());
            t2++;
        }

        // 인터뷰필요 상중하Total.테이블
        int i2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 40).setCellValue(migTargetsList.get(i2).getValue20());
            getCell(sheet6, k2, 41).setCellValue(migTargetsList.get(i2).getValue21());
            getCell(sheet6, k2, 42).setCellValue(migTargetsList.get(i2).getValue22());
            i2++;
        }
        // 불가 상중하Total.테이블
        int d2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 44).setCellValue(migTargetsList.get(d2).getValue23());
            getCell(sheet6, k2, 45).setCellValue(migTargetsList.get(d2).getValue24());
            getCell(sheet6, k2, 46).setCellValue(migTargetsList.get(d2).getValue25());
            d2++;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h3 = 0;
        int h4 = 47-1-1-1-2-4;
        for (int g2 = 46-1-1-1-2-4; g2 < 46-1-1-1-2-4 + (linuxTargetsList.size()); g2+=1) {

            getCell(sheet6, g2, 36).setCellValue(linuxTargetsList.get(h3).getKey());
            getCell(sheet6, g2, 40).setCellFormula(String.format("F%d", g2 + 1));
            getCell(sheet6, g2, 43).setCellFormula(String.format("I%d", g2 + 1));
            getCell(sheet6, g2, 46).setCellFormula(String.format("L%d", g2 + 1));
            getCell(sheet6, g2, 48).setCellFormula(String.format("P%d", g2 + 1));
            getCell(sheet6, g2, 50).setCellFormula(String.format("T%d", g2 + 1));
            getCell(sheet6, g2, 53).setCellFormula(String.format("SUM(AU%d:BA%d)", g2 + 1, g2 + 1));

            // 보안준수율
            getCell(sheet6, g2, 55).setCellFormula(String.format("AB%d", g2 + 1));
            h3++;
            h4++;
        }

        int h5 = 0;
        int h6 = 23-1-1-1-2-4;
        for (int g2 = 46-1-1-1-2-4; g2 < 46-1-1-1-2-4 + (migTargetsList.size()); g2+=1) {
            // 이행점검
            // 상
            getCell(sheet6, g2, 58).setCellValue(migTargetsList.get(h5).getValue4());
            // 중
            getCell(sheet6, g2, 60).setCellValue(migTargetsList.get(h5).getValue5());
            // 하
            getCell(sheet6, g2, 62).setCellValue(migTargetsList.get(h5).getValue6());
            // Total
            getCell(sheet6, g2, 64).setCellFormula(String.format("SUM(BG%d:BL%d)",g2+1, g2+1));

            // 전체 점검 개수(N/A 제외) 상중하Total.테이블 옆에 위치
            //      보안준수율   :
            getCell(sheet6, g2, 66).setCellFormula(String.format(
                    "IF(AO%d=0,100,ROUND(IFERROR((AK%d*3+AL%d*2+AM%d*1) / ((AK%d*3+AL%d*2+AM%d*1)+(BG%d*3+BI%d*2+BK%d*1)+(AO%d*3+AP%d*2+AQ%d*1)+(AS%d*3+AT%d*2+AU%d*1))*100,0),2))"
                    ,g2+1
                    ,h6+1,h6+1,h6+1
                    ,h6+1,h6+1,h6+1
                    ,g2+1,g2+1,g2+1
                    ,h6+1,h6+1,h6+1
                    ,h6+1,h6+1,h6+1)
            );

            //      보안수준   :
            getCell(sheet6, g2, 68).setCellFormula(String.format("IF(BO%d>=90,\"우수\",IF(AND(BO%d>=80,BO%d<90),\"양호\",IF(AND(BO%d>=70,BO%d<80),\"보통\",\"미흡\")))"
                    ,g2+1,g2+1,g2+1,g2+1,g2+1));
            h5++;
            h6++;
        }

        getCell(sheet6, 33-1-1-2, 36).setCellFormula("SUM(AK15:AK20)"); // 20
        getCell(sheet6, 33-1-1-2, 37).setCellFormula("SUM(AL15:AL20)"); // 12
        getCell(sheet6, 33-1-1-2, 38).setCellFormula("SUM(AM15:AM20)"); // 2

        getCell(sheet6, 33-1-1-2, 40).setCellFormula("SUM(AO15:AO20)"); // 0
        getCell(sheet6, 33-1-1-2, 41).setCellFormula("SUM(AP15:AP20)"); // 1
        getCell(sheet6, 33-1-1-2, 42).setCellFormula("SUM(AQ15:AQ20)"); // 0

        getCell(sheet6, 33-1-1-2, 44).setCellFormula("SUM(AS15:AS20)"); // 0
        getCell(sheet6, 33-1-1-2, 45).setCellFormula("SUM(AT15:AT20)"); // 0
        getCell(sheet6, 33-1-1-2, 46).setCellFormula("SUM(AU15:AU20)"); // 0
        // Total
        //      합계 :

        getCell(sheet6, 64-1-1-1-2-4, 40).setCellFormula("F56");
        getCell(sheet6, 64-1-1-1-2-4, 43).setCellFormula("I56");
        getCell(sheet6, 64-1-1-1-2-4, 46).setCellFormula("SUM(AU38:AV55)");
        getCell(sheet6, 64-1-1-1-2-4, 48).setCellFormula("SUM(AW38:AX55)");
        getCell(sheet6, 64-1-1-1-2-4, 50).setCellFormula("SUM(AY38:BA55)");
        getCell(sheet6, 64-1-1-1-2-4, 53).setCellFormula("SUM(BB38:BC55)");
        getCell(sheet6, 64-1-1-1-2-4, 55).setCellFormula("AB56");

        //          - 이행점검
        getCell(sheet6, 64-1-1-1-2-4, 58).setCellFormula("IFERROR(SUM(BG38:BH55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 60).setCellFormula("IFERROR(SUM(BI38:BJ55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 62).setCellFormula("IFERROR(SUM(BK38:BL55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 64).setCellFormula("SUM(BM38:BN55)");

        //보안준수율 Total
        getCell(sheet6, 64-1-1-1-2-4, 71).setCellFormula("ROUND(AVERAGEA(BO38:BP55),2)");

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int pvalue4 = 0;
        int pvalue5 = 0;
        int pvalue6 = 0;
        int pvalue13 = 0;
        int pvalue14 = 0;
        int pvalue15 = 0;
        for (DetailResultDto detailResultDto : migTargetsList) {

            pvalue4 += detailResultDto.getValue4();   // 상.취약점
            pvalue5 += detailResultDto.getValue5();   // 중.취약점
            pvalue6 += detailResultDto.getValue6();   // 하.취약점
        }

        // TOTAL. 보안수준
        getCell(sheet6, 64-1-1-1-2-4, 66).setCellFormula("IF(BT56>=90,\"우수\",IF(AND(BT56>=80,BT56<90),\"양호\",IF(AND(BT56>=70,BT56<80),\"보통\",\"미흡\")))");

        //      평균 :
        getCell(sheet6, 66-1-1-1-2-4, 46).setCellFormula("L58");
        getCell(sheet6, 66-1-1-1-2-4, 48).setCellFormula("P58");
        getCell(sheet6, 66-1-1-1-2-4, 50).setCellFormula("T58");

        //     - 이행점검 Total. 평균. 점검결과
        getCell(sheet6, 66-1-1-1-2-4, 58).setCellFormula("ROUND(IFERROR((AK30*3)/((AK30*3)+(BG56*3)+(AO30*3)+(AS30*3))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 60).setCellFormula("ROUND(IFERROR((AL30*2)/((AL30*2)+(BI56*2)+(AP30*2)+(AT30*2))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 62).setCellFormula("ROUND(IFERROR((AM30*1)/((AM30*1)+(BK56*1)+(AQ30*1)+(AU30*1))*100,0),2)");

        // 발견된 취약점 기간별 조치 방안
        // 구분.기간별 조치 방안 표
        int n1 = 47-1-1-1-2-4;
        for (int m1 = 102-1-1-1-2-4; m1 < 102-1-1-1-2-4 + (linuxTargetsList.size()); m1+=1) {
            getCell(sheet6, m1, 36).setCellFormula(String.format("B%d", m1+1));
            getCell(sheet6, m1, 40).setCellFormula(String.format("BB%d", n1));
            getCell(sheet6, m1, 43).setCellFormula(String.format("AU%d", n1));
            getCell(sheet6, m1, 47).setCellFormula(String.format("AW%d+AY%d", n1, n1));
            n1+=1;
        }

        int n2 = 47-1-1-1-2-4;
        for (int m2 = 102-1-1-1-2-4; m2 < 102-1-1-1-2-4 + (migTargetsList.size()); m2+=1) {
            getCell(sheet6, m2, 52).setCellFormula(String.format("BG%d", n2));
            getCell(sheet6, m2, 55).setCellFormula(String.format("IFERROR(BA%d/AO%d,0)", m2+1, m2+1));
            getCell(sheet6, m2, 59).setCellFormula(String.format("BI%d+BK%d", n2, n2));
            getCell(sheet6, m2, 62).setCellFormula(String.format("IFERROR(BH%d/AO%d,0)", m2+1, m2+1));
            getCell(sheet6, m2, 65).setCellFormula(String.format("BA%d+BH%d", m2+1, m2+1));
            getCell(sheet6, m2, 67).setCellFormula(String.format("AO%d-BN%d", m2+1, m2+1));
            getCell(sheet6, m2, 69).setCellFormula(String.format("IFERROR(BP%d/AO%d,0)", m2+1, m2+1));
            n2+=1;
        }

        // 구분.기간별 조치 방안 표 Total
        getCell(sheet6, 120-1-1-1-2-4, 40).setCellFormula("SUM(AO94:AQ111)");
        getCell(sheet6, 120-1-1-1-2-4, 43).setCellFormula("SUM(AR94:AU111)");
        getCell(sheet6, 120-1-1-1-2-4, 47).setCellFormula("SUM(AV94:AY111)");
        getCell(sheet6, 120-1-1-1-2-4, 52).setCellFormula("SUM(BA94:BC111)");
        getCell(sheet6, 120-1-1-1-2-4, 55).setCellFormula("IFERROR(BA112/AO112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 59).setCellFormula("SUM(BH94:BJ111)");
        getCell(sheet6, 120-1-1-1-2-4, 62).setCellFormula("IFERROR(BH112/AO112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 65).setCellFormula("SUM(BN94:BO111)");
        getCell(sheet6, 120-1-1-1-2-4, 67).setCellFormula("SUM(BP94:BQ111)");
        getCell(sheet6, 120-1-1-1-2-4, 69).setCellFormula("IFERROR(BP112/AO112,0)");

        // 즉시 조치 필요 대상시스템 (TOP 10). 테이블
        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList2 = testService.getReportResultProcessTargets(gAssetSwJob1, false);

        snetAssetSwAuditExcelDtoList2.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate).reversed());

        snetAssetSwAuditExcelDtoList2 = snetAssetSwAuditExcelDtoList2.stream().limit(10).collect(Collectors.toList());

        int bb2 = 0;
        if (snetAssetSwAuditExcelDtoList2 != null && snetAssetSwAuditExcelDtoList2.size() > 0) {
            for (int aa2 = 126-1-1-1-2-4; aa2 < 126-1-1-1-2-4 + snetAssetSwAuditExcelDtoList2.size(); aa2++) {

                getCell(sheet6, aa2, 186).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getHostNm());
                getCell(sheet6, aa2, 187).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getAuditRate());

                if (snetAssetSwAuditExcelDtoList.get(bb2).getWeekHighRate() == 0.0) {

                    getCell(sheet6, aa2, 188).setCellValue("");
                } else {

                    getCell(sheet6, aa2, 188).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getWeekHighRate());
                }

                if (snetAssetSwAuditExcelDtoList.get(bb2).getWeekMiddleLowRate() == 0.0) {

                    getCell(sheet6, aa2, 189).setCellValue("");
                } else {

                    getCell(sheet6, aa2, 189).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getWeekMiddleLowRate());
                }
                bb2++;
            }
        }

        // 취약점 상세 항목. 테이블. 이행점검
        List<SnetAssetSwAuditReportDto> assetSwAuditReportDtoList2 = testService.selectAssetSwAuditReportNew(gAssetSwJob1, false);

        if (assetSwAuditReportDtoList2 != null && assetSwAuditReportDtoList2.size() > 0) {

            int dd2 = 0;
            for (int cc2 = 157-1-1-1-2-4; cc2 < 157-1-1-1-2-4 + assetSwAuditReportDtoList2.size(); cc2++) {

                getCell(sheet6, cc2, 36).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemGrpNm());
                getCell(sheet6, cc2, 41).setCellValue(assetSwAuditReportDtoList2.get(dd2).getDivideNum());
                getCell(sheet6, cc2, 44).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemNmDesc());
                getCell(sheet6, cc2, 61).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemGradeView());
                getCell(sheet6, cc2, 63).setCellValue(assetSwAuditReportDtoList2.get(dd2).getHostCount());
                getCell(sheet6, cc2, 64).setCellValue(assetSwAuditReportDtoList2.get(dd2).getHostCount());
                getCell(sheet6, cc2, 65).setCellValue(assetSwAuditReportDtoList2.get(dd2).getEtc());
                dd2++;
            } // end for assetSwAuditReportDtoList2
//            for (int cc = 157; cc < 157 + assetSwAuditReportDtoList2.size(); cc++) {
//
//                for (int ff = 36; ff <= 65; ff++) {
//                    getCell(sheet6, cc, ff).setCellStyle(cellStyle(gWorkbook, 10)); // style
//                }
//            } // end for style
        }

        // 취약점 상세 항목 가이드. 테이블. 이행점검
        if (assetSwAuditReportDtoList2 != null && assetSwAuditReportDtoList2.size() > 0) {
            int dd = 0;
            for (int cc = 211-1-1-1-2-4; cc < 211-1-1-1-2-4 + assetSwAuditReportDtoList2.size(); cc++) {

//                getCell(sheet6, cc, 36).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemGrpNm());
//                getCell(sheet6, cc, 41).setCellValue(assetSwAuditReportDtoList2.get(dd).getDivideNum());
//                getCell(sheet6, cc, 44).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemStandard());
//                getCell(sheet6, cc, 61).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());

                String itemCMstr = assetSwAuditReportDtoList2.get(dd).getItemCountermeasure();
                String[] chars = null;
                boolean autoRowHeightFlag = false;
                short charsLength = 1;
                try {
                    chars = itemCMstr.split("\n");

                    charsLength = (short) (chars.length);
                    autoRowHeightFlag = true;
                } catch (Exception e) {

                    autoRowHeightFlag = false;
                }

                getCell(sheet6, cc, 36).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 41).setCellValue(assetSwAuditReportDtoList2.get(dd).getDivideNum());
                getCell(sheet6, cc, 44).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemStandard());
                if (autoRowHeightFlag) {
                    getCellAutoRowHeight(sheet6, cc, 61, charsLength).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());
                } else {
                    getCell(sheet6, cc, 61).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());
                }

                dd++;
            } // end for
        } // end if

        /* end WAS */
    } // end method createTotalReportTargets5

    // WEB
    private void createTotalReportTargets4(XSSFWorkbook gWorkbook, List<TotalOverallResultDto> checkTargetsList, Map gAssetSwJob1, RestTemplate rest) throws Exception {

        logger.info("*[WEB]");
        XSSFSheet sheet6 = gWorkbook.getSheet("WEB");
        // 점검군별 (Linux, Window, DB, WEB, WAS, NW, Web App(URL), 기타)

        // (1) 보안점검 WEB

        // 현황
        String statusStr = "CONCATENATE(\" 서버 보안점검 결과, 전체 점검대상(\",D32,\"EA)의 평균 보안수준은 \",AB56,\"입니다. 분류 항목 중 보안 설정이 가장 양호하게 설정된 분류 항목은 \",\"\"\"\",INDEX(B38:B55,MATCH(MAX(AB38:AB55),AB38:AB55,0),1),\"\"\"\",\"로 보안준수율은 \", MAX(AB38:AB55),\"점이며 \",\"\"\"\",INDEX(B38:B55,MATCH(MIN(AB38:AB55),AB38:AB55,0),1),\"\"\"\",\" 항목이 \",MIN(AB38:AB55),\"점으로 보안 설정이 가장 미흡하게 설정되어 있습니다.\")";
        getCell(sheet6, 11-1-1-1-2, 5).setCellFormula(statusStr);

        String statusMigStr = "CONCATENATE(\" 서버 이행점검 결과, 전체 점검대상(\",AM32,\"EA)의 평균 보안수준은 \",BD56,\"에서 \",BO56,\"으로 변경되었습니다. 보안설정이 가장 미흡하게 설정되어 있던 \",\"\"\"\",INDEX(AK38:AK55,MATCH(MIN(BD38:BD55),BD38:BD55,0),1),\"\"\"\",\" 분류 항목이 \", MIN(BD38:BD55),\"점에서 \",INDEX(BO38:BO55,MATCH(MIN(BD38:BD55),BD38:BD55,0),1),\"점으로 보안준수율이 변경되었으며, 현재 보안준수율이 가장 낮은 분류 항목으로는 \",\"\"\"\",INDEX(AK38:AK55,MATCH(MIN(BO38:BO55),BO38:BO55,0),1),\"\"\"\",\"으로 보안준수율은 \",MIN(BO38:BO55),\"점 입니다.\")";
        getCell(sheet6, 11-1-1-1-2, 40).setCellFormula(statusMigStr);


        /*----------------------------------------------------------------------------------------------------*/
        List<DetailResultDto> linuxTargetsList = testService.getDetailTargetsList(checkTargetsList, gAssetSwJob1, "WEB", true);

        if (linuxTargetsList == null || linuxTargetsList.size() < 1) {

            throw new ErrorException("NoSuchElementException: No value");
        }

        // 점검수량 (EA)
        for (DetailResultDto dto : linuxTargetsList) {

            getCell(sheet6, 40-1-1-1-2-4, 3).setCellValue(dto.getValue1());
//            getCell(sheet6, 64-1-1-1-2-4, 5).setCellValue(dto.getValue16());
//            getCell(sheet6, 64-1-1-1-2-4, 8).setCellValue(dto.getValue16());
//        -> 현재는 개별항목수 x 점검수량 개수로 맞춤
            break;
        }

        // 양호 상중하Total.테이블
        int t1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 20).setCellValue(linuxTargetsList.get(t1).getValue17());
            getCell(sheet6, k2, 21).setCellValue(linuxTargetsList.get(t1).getValue18());
            getCell(sheet6, k2, 22).setCellValue(linuxTargetsList.get(t1).getValue19());
            t1++;
        }

        // 인터뷰필요 상중하Total.테이블
        int i1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 24).setCellValue(linuxTargetsList.get(i1).getValue20());
            getCell(sheet6, k2, 25).setCellValue(linuxTargetsList.get(i1).getValue21());
            getCell(sheet6, k2, 26).setCellValue(linuxTargetsList.get(i1).getValue22());
            i1++;
        }
        // 불가 상중하Total.테이블
        int d1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 28).setCellValue(linuxTargetsList.get(d1).getValue23());
            getCell(sheet6, k2, 29).setCellValue(linuxTargetsList.get(d1).getValue24());
            getCell(sheet6, k2, 30).setCellValue(linuxTargetsList.get(d1).getValue25());
            d1++;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h = 0;
        int h2 = 23-1-1-1-2-4;
        for (int g = 46-1-1-1-2-4; g < 46-1-1-1-2-4 + (linuxTargetsList.size()); g+=1) {

            getCell(sheet6, g, 1).setCellValue(linuxTargetsList.get(h).getKey());
            getCell(sheet6, g, 5).setCellValue(linuxTargetsList.get(h).getValue2());
            getCell(sheet6, g, 8).setCellValue(linuxTargetsList.get(h).getValue3());
            getCell(sheet6, g, 11).setCellValue(linuxTargetsList.get(h).getValue4());
            getCell(sheet6, g, 15).setCellValue(linuxTargetsList.get(h).getValue5());
            getCell(sheet6, g, 19).setCellValue(linuxTargetsList.get(h).getValue6());
            getCell(sheet6, g, 23).setCellValue(linuxTargetsList.get(h).getValue7());

            // 보안준수율 (웹UI 의 audit_rate 산정식을 기반으로 다시 계산함)
            getCell(sheet6, g, 27).setCellFormula(String.format(
                    "IF(F%d=0,100,ROUND(IFERROR((U%d*3+V%d*2+W%d*1) / ((U%d*3+V%d*2+W%d*1)+(L%d*3+P%d*2+T%d*1)+(Y%d*3+Z%d*2+AA%d*1)+(AC%d*3+AD%d*2+AE%d*1))*100,0),2))"
                    ,g+1
                    ,h2+1,h2+1,h2+1
                    ,h2+1,h2+1,h2+1
                    ,g+1,g+1,g+1
                    ,h2+1,h2+1,h2+1
                    ,h2+1,h2+1,h2+1)
            );
            // 보안수준
            getCell(sheet6, g, 31).setCellFormula(String.format("IF(AB%d>=90,\"우수\",IF(AND(AB%d>=80,AB%d<90),\"양호\",IF(AND(AB%d>=70,AB%d<80),\"보통\",\"미흡\")))"
                    , g + 1, g + 1, g + 1, g + 1, g + 1));
            h++;
            h2++;
        }

        // 전체 점검 개수(N/A 제외) Total
        getCell(sheet6, 33-1-1-2, 5).setCellFormula("SUM(F15:F29)");  // 25
        getCell(sheet6, 33-1-1-2, 8).setCellFormula("SUM(I15:I29)");  // 16
        getCell(sheet6, 33-1-1-2, 11).setCellFormula("SUM(L15:L29)"); // 5
        getCell(sheet6, 33-1-1-2, 14).setCellFormula("SUM(O15:O29)"); // 0

        getCell(sheet6, 33-1-1-2, 20).setCellFormula("SUM(U15:U20)"); // 0
        getCell(sheet6, 33-1-1-2, 21).setCellFormula("SUM(V15:V20)"); // 0
        getCell(sheet6, 33-1-1-2, 22).setCellFormula("SUM(W15:W20)"); // 0

        getCell(sheet6, 33-1-1-2, 24).setCellFormula("SUM(Y15:Y20)"); // 0
        getCell(sheet6, 33-1-1-2, 25).setCellFormula("SUM(Z15:Z20)"); // 0
        getCell(sheet6, 33-1-1-2, 26).setCellFormula("SUM(AA15:AA20)"); // 0

        getCell(sheet6, 33-1-1-2, 28).setCellFormula("SUM(AC15:AC20)"); // 0
        getCell(sheet6, 33-1-1-2, 29).setCellFormula("SUM(AD15:AD20)"); // 0
        getCell(sheet6, 33-1-1-2, 30).setCellFormula("SUM(AE15:AE20)"); // 0

        /*
            Total. 합계. 점검항목수
         */
        getCell(sheet6, 64-1-1-1-2-4, 5).setCellFormula("SUM(F38:H55)");
        getCell(sheet6, 64-1-1-1-2-4, 8).setCellFormula("SUM(I38:K55)");
//        getCell(sheet6, 64, 8).setCellFormula("SUM(I38:K55)");
//        -> value16 으로 대체
//        -> 현재는 개별항목수 x 점검수량 개수로 맞춤

        getCell(sheet6, 64-1-1-1-2-4, 11).setCellFormula("IFERROR(SUM(L38:O55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 15).setCellFormula("IFERROR(SUM(P38:S55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 19).setCellFormula("IFERROR(SUM(T38:W55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 23).setCellFormula("SUM(L56:W57)");

        //보안준수율 Total
        getCell(sheet6, 64-1-1-1-2-4, 34).setCellFormula("ROUND(AVERAGEA(AB38:AE55),2)");

        double sCheckValue = 0.0;
        double tempSCheckValue1 = 0.0;
        double tempSCheckValue2 = 0.0;

        // 전체 점검 개수(N/A 제외) 상중하Total.테이블
        int value10 = 0;
        int value11 = 0;
        int value12 = 0;
        for (DetailResultDto detailResultDto : linuxTargetsList) {

            value10 += detailResultDto.getValue10();  // 상
            value11 += detailResultDto.getValue11();  // 중
            value12 += detailResultDto.getValue12();  // 하
        }
        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int value4 = 0;
        int value5 = 0;
        int value6 = 0;
        for (DetailResultDto detailResultDto : linuxTargetsList) {

            value4 += detailResultDto.getValue4();   // 상
            value5 += detailResultDto.getValue5();   // 중
            value6 += detailResultDto.getValue6();   // 하
        }

        // 보안준수율 에서 보안준수율 로 변경
        tempSCheckValue1 = ((value10*3+value11*2+value12*1)-(value4*3+value5*2+value6*1));
        tempSCheckValue2 = (value10*3+value11*2+value12*1);
        sCheckValue = (tempSCheckValue1/tempSCheckValue2) * 100;
        sCheckValue = Math.round( sCheckValue * 100d ) / 100d;

        XSSFSheet sheet5 = gWorkbook.getSheet("Ⅳ. 전체종합결과");

        getCell(sheet6, 64-1-1-1-2-4, 27).setCellFormula("IF(AI56>=90,\"우수\",IF(AND(AI56>=80,AI56<90),\"양호\",IF(AND(AI56>=70,AI56<80),\"보통\",\"미흡\")))");

        /*
            Total. 평균. 점검결과
         */
        getCell(sheet6, 66-1-1-1-2-4, 11).setCellFormula("ROUND(IFERROR((U30*3)/((U30*3)+(L56*3)+(Y30*3)+(AC30*3))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 15).setCellFormula("ROUND(IFERROR((V30*2)/((V30*2)+(P56*2)+(Z30*2)+(AD30*2))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 19).setCellFormula("ROUND(IFERROR((W30*1)/((W30*1)+(T56*1)+(AA30*1)+(AE30*1))*100,0),2)");

        // 발견된 취약점 기간별 조치 방안
        // 현황 [74, 5]
        String statusStr2 = "CONCATENATE(\" 발견된 취약한 항목 중 위험도 \",\"\"\"\",\"상\",\"\"\"\",\"에 해당하는 즉시 조치가 필요한 항목의 비율은 평균 \",ROUND(Q112*100,1),\"%를 차지하고 있으며, 위험도 \",\"\"\"\",\"중\",\"\"\"\",\",\",\"\"\"\",\"하\",\"\"\"\",\"에 해당하는 중장기적으로 조치가 필요한 항목의 비율은 \",ROUND(AC112*100,1),\"%로 분석 되었습니다. 또한 즉시 조치가 필요한 항목 중, 가장 높은 비율을 차지하고 있는 상위 10개의 점검 대상은 아래와 같습니다.\")";
        getCell(sheet6, 74-1-1-1-2-4, 5).setCellFormula(statusStr2);
        // 현황 [74, 40]
        String statusMigStr2 = "CONCATENATE(\" 발견된 취약한 항목 중 평균 조치율은 \",ROUND(BR112*100,1),\"%로써 위험도 \",\"\"\"\",\"상\",\"\"\"\",\"에 해당하는 즉시 조치가 필요한 항목의 비율은 평균 \",ROUND(IFERROR(AR112/AO112*100,0),1),\"%에서 \",ROUND(BD112*100,1),\"%로 감소하였으며 위험도 \",\"\"\"\",\"중\",\"\"\"\",\",\",\"\"\"\",\"하\",\"\"\"\",\"에 해당하는 중장기적으로 조치가 필요한 항목의 비율은 \",ROUND(IFERROR(AV112/AO112*100,0),1),\"%에서 \",ROUND(BK112*100,1),\"%로 감소한 것으로 분석 되었습니다. 또한 잔여 취약점이 존재하는 대상 중, 즉시 조치가 필요한 항목이 높은 비율을 차지하고 있는 상위 10개의 점검 대상은 아래와 같습니다.\")";
        getCell(sheet6, 74-1-1-1-2-4, 40).setCellFormula(statusMigStr2);

        // 구분, 발견된취약점 수, 기간별 조치 방안 표
        int jj = 0;
        int kk = 47-1-1-1-2-4;
        int ll = 103-1-1-1-2-4;
        for (int ii = 102-1-1-1-2-4; ii < 102-1-1-1-2-4 + (linuxTargetsList.size()); ii+=1) {

            getCell(sheet6, ii, 1).setCellValue(linuxTargetsList.get(jj).getKey());

            getCell(sheet6, ii, 5).setCellFormula(String.format("X%d", kk));

            getCell(sheet6, ii, 10).setCellFormula(String.format("L%d", kk));

            getCell(sheet6, ii, 16).setCellFormula(String.format("IFERROR(K%d/F%d,0)", ll, ll));

            getCell(sheet6, ii, 22).setCellFormula(String.format("P%d+T%d", kk, kk));

            getCell(sheet6, ii, 28).setCellFormula(String.format("IFERROR(W%d/F%d,0)", ll, ll));
            jj++;
            kk += 1;
            ll += 1;
        }

        // 구분, 발견된취약점 수, 기간별 조치 방안 표 Total
        getCell(sheet6, 120-1-1-1-2-4, 5).setCellFormula("SUM(F94:J111)");
        getCell(sheet6, 120-1-1-1-2-4, 10).setCellFormula("SUM(K94:P111)");
        getCell(sheet6, 120-1-1-1-2-4, 16).setCellFormula("IFERROR(K112/F112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 22).setCellFormula("SUM(W94:AB111)");
        getCell(sheet6, 120-1-1-1-2-4, 28).setCellFormula("IFERROR(W112/F112,0)");

        // 즉시 조치 필요 대상시스템 (TOP 10). 테이블
        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList = testService.getReportResultProcessTargets(gAssetSwJob1, true);

        snetAssetSwAuditExcelDtoList.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate).reversed());

        snetAssetSwAuditExcelDtoList = snetAssetSwAuditExcelDtoList.stream().limit(10).collect(Collectors.toList());

        int bb = 0;
        if (snetAssetSwAuditExcelDtoList != null && snetAssetSwAuditExcelDtoList.size() > 0) {
            for (int aa = 126-1-1-1-2-4; aa < 126-1-1-1-2-4 + snetAssetSwAuditExcelDtoList.size(); aa++) {

                getCell(sheet6, aa, 182).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getHostNm());
                getCell(sheet6, aa, 183).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getAuditRate());

                if (snetAssetSwAuditExcelDtoList.get(bb).getWeekHighRate() == 0.0) {

                    getCell(sheet6, aa, 184).setCellValue("");
                } else {

                    getCell(sheet6, aa, 184).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getWeekHighRate());
                }

                if (snetAssetSwAuditExcelDtoList.get(bb).getWeekMiddleLowRate() == 0.0) {

                    getCell(sheet6, aa, 185).setCellValue("");
                } else {

                    getCell(sheet6, aa, 185).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getWeekMiddleLowRate());
                }
                bb++;
            }
        }

        // 취약점 상세 항목. 테이블. 보안점검
        List<SnetAssetSwAuditReportDto> assetSwAuditReportDtoList = testService.selectAssetSwAuditReportNew(gAssetSwJob1, true);

        if (assetSwAuditReportDtoList != null && assetSwAuditReportDtoList.size() > 0) {
            int dd = 0;
            for (int cc = 157-1-1-1-2-4; cc < 157-1-1-1-2-4 + assetSwAuditReportDtoList.size(); cc++) {

                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemNmDesc());
                getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGradeView());
                getCell(sheet6, cc, 26).setCellValue(assetSwAuditReportDtoList.get(dd).getHostCount());
                getCell(sheet6, cc, 28).setCellValue(assetSwAuditReportDtoList.get(dd).getEtc());
                dd++;
            } // end for assetSwAuditReportDtoList
//            for (int cc = 157; cc < 157 + assetSwAuditReportDtoList.size(); cc++) {
//
//                for (int ee = 1; ee <= 33; ee++) {
//                    getCell(sheet6, cc, ee).setCellStyle(cellStyle(gWorkbook, 10)); // style
//                }
//            } // end for style
        } // end if

        // 취약점 상세 항목 가이드. 테이블. 보안점검
        int colWithinchars = (sheet6.getColumnWidth(2) + sheet6.getColumnWidth(3));
        if (assetSwAuditReportDtoList != null && assetSwAuditReportDtoList.size() > 0) {
            int dd = 0;
            for (int cc = 211-1-1-1-2-4; cc < 211-1-1-1-2-4 + assetSwAuditReportDtoList.size(); cc++) {

//                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
//                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
//                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemStandard());
//                getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());


                String itemCMstr = assetSwAuditReportDtoList.get(dd).getItemCountermeasure();
                String[] chars = null;
                boolean autoRowHeightFlag = false;
                short charsLength = 1;
                try {
                    chars = itemCMstr.split("\n");

                    charsLength = (short) (chars.length);
                    autoRowHeightFlag = true;
                } catch (Exception e) {

                    autoRowHeightFlag = false;
                }


                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemStandard());
                if (autoRowHeightFlag) {
                    getCellAutoRowHeight(sheet6, cc, 24, charsLength).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());
                } else {
                    getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());
                }
                dd++;

            } // end for
        } // end if


        // (2) 이행점검 WEB
        List<DetailResultDto> migTargetsList = testService.getMigDetailTargetsList(checkTargetsList, gAssetSwJob1, "WEB", true);

        // 점검수량 (EA)
        getCell(sheet6, 40-1-1-1-2-4, 38).setCellFormula("D32");

        // 양호 상중하Total.테이블
        int t2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 36).setCellValue(migTargetsList.get(t2).getValue17());
            getCell(sheet6, k2, 37).setCellValue(migTargetsList.get(t2).getValue18());
            getCell(sheet6, k2, 38).setCellValue(migTargetsList.get(t2).getValue19());
            t2++;
        }

        // 인터뷰필요 상중하Total.테이블
        int i2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 40).setCellValue(migTargetsList.get(i2).getValue20());
            getCell(sheet6, k2, 41).setCellValue(migTargetsList.get(i2).getValue21());
            getCell(sheet6, k2, 42).setCellValue(migTargetsList.get(i2).getValue22());
            i2++;
        }
        // 불가 상중하Total.테이블
        int d2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 44).setCellValue(migTargetsList.get(d2).getValue23());
            getCell(sheet6, k2, 45).setCellValue(migTargetsList.get(d2).getValue24());
            getCell(sheet6, k2, 46).setCellValue(migTargetsList.get(d2).getValue25());
            d2++;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h3 = 0;
        int h4 = 47-1-1-1-2-4;
        for (int g2 = 46-1-1-1-2-4; g2 < 46-1-1-1-2-4 + (linuxTargetsList.size()); g2+=1) {

            getCell(sheet6, g2, 36).setCellValue(linuxTargetsList.get(h3).getKey());
            getCell(sheet6, g2, 40).setCellFormula(String.format("F%d", g2 + 1));
            getCell(sheet6, g2, 43).setCellFormula(String.format("I%d", g2 + 1));
            getCell(sheet6, g2, 46).setCellFormula(String.format("L%d", g2 + 1));
            getCell(sheet6, g2, 48).setCellFormula(String.format("P%d", g2 + 1));
            getCell(sheet6, g2, 50).setCellFormula(String.format("T%d", g2 + 1));
            getCell(sheet6, g2, 53).setCellFormula(String.format("SUM(AU%d:BA%d)", g2 + 1, g2 + 1));

            // 보안준수율
            getCell(sheet6, g2, 55).setCellFormula(String.format("AB%d", g2 + 1));
            h3++;
            h4++;
        }

        int h5 = 0;
        int h6 = 23-1-1-1-2-4;
        for (int g2 = 46-1-1-1-2-4; g2 < 46-1-1-1-2-4 + (migTargetsList.size()); g2+=1) {
            // 이행점검
            // 상
            getCell(sheet6, g2, 58).setCellValue(migTargetsList.get(h5).getValue4());
            // 중
            getCell(sheet6, g2, 60).setCellValue(migTargetsList.get(h5).getValue5());
            // 하
            getCell(sheet6, g2, 62).setCellValue(migTargetsList.get(h5).getValue6());
            // Total
            getCell(sheet6, g2, 64).setCellFormula(String.format("SUM(BG%d:BL%d)",g2+1, g2+1));

            // 전체 점검 개수(N/A 제외) 상중하Total.테이블 옆에 위치
            //      보안준수율   :
            getCell(sheet6, g2, 66).setCellFormula(String.format(
                    "IF(AO%d=0,100,ROUND(IFERROR((AK%d*3+AL%d*2+AM%d*1) / ((AK%d*3+AL%d*2+AM%d*1)+(BG%d*3+BI%d*2+BK%d*1)+(AO%d*3+AP%d*2+AQ%d*1)+(AS%d*3+AT%d*2+AU%d*1))*100,0),2))"
                    ,g2+1
                    ,h6+1,h6+1,h6+1
                    ,h6+1,h6+1,h6+1
                    ,g2+1,g2+1,g2+1
                    ,h6+1,h6+1,h6+1
                    ,h6+1,h6+1,h6+1)
            );

            //      보안수준   :
            getCell(sheet6, g2, 68).setCellFormula(String.format("IF(BO%d>=90,\"우수\",IF(AND(BO%d>=80,BO%d<90),\"양호\",IF(AND(BO%d>=70,BO%d<80),\"보통\",\"미흡\")))"
                    ,g2+1,g2+1,g2+1,g2+1,g2+1));
            h5++;
            h6++;
        }

        getCell(sheet6, 33-1-1-2, 36).setCellFormula("SUM(AK15:AK20)"); // 20
        getCell(sheet6, 33-1-1-2, 37).setCellFormula("SUM(AL15:AL20)"); // 12
        getCell(sheet6, 33-1-1-2, 38).setCellFormula("SUM(AM15:AM20)"); // 2

        getCell(sheet6, 33-1-1-2, 40).setCellFormula("SUM(AO15:AO20)"); // 0
        getCell(sheet6, 33-1-1-2, 41).setCellFormula("SUM(AP15:AP20)"); // 1
        getCell(sheet6, 33-1-1-2, 42).setCellFormula("SUM(AQ15:AQ20)"); // 0

        getCell(sheet6, 33-1-1-2, 44).setCellFormula("SUM(AS15:AS20)"); // 0
        getCell(sheet6, 33-1-1-2, 45).setCellFormula("SUM(AT15:AT20)"); // 0
        getCell(sheet6, 33-1-1-2, 46).setCellFormula("SUM(AU15:AU20)"); // 0
        // Total
        //      합계 :

        getCell(sheet6, 64-1-1-1-2-4, 40).setCellFormula("F56");
        getCell(sheet6, 64-1-1-1-2-4, 43).setCellFormula("I56");
        getCell(sheet6, 64-1-1-1-2-4, 46).setCellFormula("SUM(AU38:AV55)");
        getCell(sheet6, 64-1-1-1-2-4, 48).setCellFormula("SUM(AW38:AX55)");
        getCell(sheet6, 64-1-1-1-2-4, 50).setCellFormula("SUM(AY38:BA55)");
        getCell(sheet6, 64-1-1-1-2-4, 53).setCellFormula("SUM(BB38:BC55)");
        getCell(sheet6, 64-1-1-1-2-4, 55).setCellFormula("AB56");

        //          - 이행점검
        getCell(sheet6, 64-1-1-1-2-4, 58).setCellFormula("IFERROR(SUM(BG38:BH55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 60).setCellFormula("IFERROR(SUM(BI38:BJ55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 62).setCellFormula("IFERROR(SUM(BK38:BL55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 64).setCellFormula("SUM(BM38:BN55)");

        //보안준수율 Total
        getCell(sheet6, 64-1-1-1-2-4, 71).setCellFormula("ROUND(AVERAGEA(BO38:BP55),2)");

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int pvalue4 = 0;
        int pvalue5 = 0;
        int pvalue6 = 0;
        int pvalue13 = 0;
        int pvalue14 = 0;
        int pvalue15 = 0;
        for (DetailResultDto detailResultDto : migTargetsList) {

            pvalue4 += detailResultDto.getValue4();   // 상.취약점
            pvalue5 += detailResultDto.getValue5();   // 중.취약점
            pvalue6 += detailResultDto.getValue6();   // 하.취약점
        }

        getCell(sheet6, 64-1-1-1-2-4, 66).setCellFormula("IF(BT56>=90,\"우수\",IF(AND(BT56>=80,BT56<90),\"양호\",IF(AND(BT56>=70,BT56<80),\"보통\",\"미흡\")))");

        //      평균 :
        getCell(sheet6, 66-1-1-1-2-4, 46).setCellFormula("L58");
        getCell(sheet6, 66-1-1-1-2-4, 48).setCellFormula("P58");
        getCell(sheet6, 66-1-1-1-2-4, 50).setCellFormula("T58");

        //     - 이행점검 Total. 평균. 점검결과
        getCell(sheet6, 66-1-1-1-2-4, 58).setCellFormula("ROUND(IFERROR((AK30*3)/((AK30*3)+(BG56*3)+(AO30*3)+(AS30*3))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 60).setCellFormula("ROUND(IFERROR((AL30*2)/((AL30*2)+(BI56*2)+(AP30*2)+(AT30*2))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 62).setCellFormula("ROUND(IFERROR((AM30*1)/((AM30*1)+(BK56*1)+(AQ30*1)+(AU30*1))*100,0),2)");

        // 발견된 취약점 기간별 조치 방안
        // 구분.기간별 조치 방안 표
        int n1 = 47-1-1-1-2-4;
        for (int m1 = 102-1-1-1-2-4; m1 < 102-1-1-1-2-4 + (linuxTargetsList.size()); m1+=1) {
            getCell(sheet6, m1, 36).setCellFormula(String.format("B%d", m1+1));
            getCell(sheet6, m1, 40).setCellFormula(String.format("BB%d", n1));
            getCell(sheet6, m1, 43).setCellFormula(String.format("AU%d", n1));
            getCell(sheet6, m1, 47).setCellFormula(String.format("AW%d+AY%d", n1, n1));
            n1+=1;
        }

        int n2 = 47-1-1-1-2-4;
        for (int m2 = 102-1-1-1-2-4; m2 < 102-1-1-1-2-4 + (migTargetsList.size()); m2+=1) {
            getCell(sheet6, m2, 52).setCellFormula(String.format("BG%d", n2));
            getCell(sheet6, m2, 55).setCellFormula(String.format("IFERROR(BA%d/AO%d,0)", m2+1, m2+1));
            getCell(sheet6, m2, 59).setCellFormula(String.format("BI%d+BK%d", n2, n2));
            getCell(sheet6, m2, 62).setCellFormula(String.format("IFERROR(BH%d/AO%d,0)", m2+1, m2+1));
            getCell(sheet6, m2, 65).setCellFormula(String.format("BA%d+BH%d", m2+1, m2+1));
            getCell(sheet6, m2, 67).setCellFormula(String.format("AO%d-BN%d", m2+1, m2+1));
            getCell(sheet6, m2, 69).setCellFormula(String.format("IFERROR(BP%d/AO%d,0)", m2+1, m2+1));
            n2+=1;
        }

        // 구분.기간별 조치 방안 표 Total
        getCell(sheet6, 120-1-1-1-2-4, 40).setCellFormula("SUM(AO94:AQ111)");
        getCell(sheet6, 120-1-1-1-2-4, 43).setCellFormula("SUM(AR94:AU111)");
        getCell(sheet6, 120-1-1-1-2-4, 47).setCellFormula("SUM(AV94:AY111)");
        getCell(sheet6, 120-1-1-1-2-4, 52).setCellFormula("SUM(BA94:BC111)");
        getCell(sheet6, 120-1-1-1-2-4, 55).setCellFormula("IFERROR(BA112/AO112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 59).setCellFormula("SUM(BH94:BJ111)");
        getCell(sheet6, 120-1-1-1-2-4, 62).setCellFormula("IFERROR(BH112/AO112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 65).setCellFormula("SUM(BN94:BO111)");
        getCell(sheet6, 120-1-1-1-2-4, 67).setCellFormula("SUM(BP94:BQ111)");
        getCell(sheet6, 120-1-1-1-2-4, 69).setCellFormula("IFERROR(BP112/AO112,0)");

        // 즉시 조치 필요 대상시스템 (TOP 10). 테이블
        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList2 = testService.getReportResultProcessTargets(gAssetSwJob1, false);

        snetAssetSwAuditExcelDtoList2.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate).reversed());

        snetAssetSwAuditExcelDtoList2 = snetAssetSwAuditExcelDtoList2.stream().limit(10).collect(Collectors.toList());

        int bb2 = 0;
        if (snetAssetSwAuditExcelDtoList2 != null && snetAssetSwAuditExcelDtoList2.size() > 0) {
            for (int aa2 = 126-1-1-1-2-4; aa2 < 126-1-1-1-2-4 + snetAssetSwAuditExcelDtoList2.size(); aa2++) {

                getCell(sheet6, aa2, 186).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getHostNm());
                getCell(sheet6, aa2, 187).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getAuditRate());

                if (snetAssetSwAuditExcelDtoList.get(bb2).getWeekHighRate() == 0.0) {

                    getCell(sheet6, aa2, 188).setCellValue("");
                } else {

                    getCell(sheet6, aa2, 188).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getWeekHighRate());
                }

                if (snetAssetSwAuditExcelDtoList.get(bb2).getWeekMiddleLowRate() == 0.0) {

                    getCell(sheet6, aa2, 189).setCellValue("");
                } else {

                    getCell(sheet6, aa2, 189).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getWeekMiddleLowRate());
                }
                bb2++;
            }
        }

        // 취약점 상세 항목. 테이블. 이행점검
        List<SnetAssetSwAuditReportDto> assetSwAuditReportDtoList2 = testService.selectAssetSwAuditReportNew(gAssetSwJob1, false);

        if (assetSwAuditReportDtoList2 != null && assetSwAuditReportDtoList2.size() > 0) {

            int dd2 = 0;
            for (int cc2 = 157-1-1-1-2-4; cc2 < 157-1-1-1-2-4 + assetSwAuditReportDtoList2.size(); cc2++) {

                getCell(sheet6, cc2, 36).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemGrpNm());
                getCell(sheet6, cc2, 41).setCellValue(assetSwAuditReportDtoList2.get(dd2).getDivideNum());
                getCell(sheet6, cc2, 44).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemNmDesc());
                getCell(sheet6, cc2, 61).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemGradeView());
                getCell(sheet6, cc2, 63).setCellValue(assetSwAuditReportDtoList2.get(dd2).getHostCount());
                getCell(sheet6, cc2, 64).setCellValue(assetSwAuditReportDtoList2.get(dd2).getHostCount());
                getCell(sheet6, cc2, 65).setCellValue(assetSwAuditReportDtoList2.get(dd2).getEtc());
                dd2++;
            } // end for assetSwAuditReportDtoList2
//            for (int cc = 157; cc < 157 + assetSwAuditReportDtoList2.size(); cc++) {
//
//                for (int ff = 36; ff <= 65; ff++) {
//                    getCell(sheet6, cc, ff).setCellStyle(cellStyle(gWorkbook, 10)); // style
//                }
//            } // end for style
        }

        // 취약점 상세 항목 가이드. 테이블. 이행점검
        if (assetSwAuditReportDtoList2 != null && assetSwAuditReportDtoList2.size() > 0) {
            int dd = 0;
            for (int cc = 211-1-1-1-2-4; cc < 211-1-1-1-2-4 + assetSwAuditReportDtoList2.size(); cc++) {

//                getCell(sheet6, cc, 36).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemGrpNm());
//                getCell(sheet6, cc, 41).setCellValue(assetSwAuditReportDtoList2.get(dd).getDivideNum());
//                getCell(sheet6, cc, 44).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemStandard());
//                getCell(sheet6, cc, 61).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());

                String itemCMstr = assetSwAuditReportDtoList2.get(dd).getItemCountermeasure();
                String[] chars = null;
                boolean autoRowHeightFlag = false;
                short charsLength = 1;
                try {
                    chars = itemCMstr.split("\n");

                    charsLength = (short) (chars.length);
                    autoRowHeightFlag = true;
                } catch (Exception e) {

                    autoRowHeightFlag = false;
                }

                getCell(sheet6, cc, 36).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 41).setCellValue(assetSwAuditReportDtoList2.get(dd).getDivideNum());
                getCell(sheet6, cc, 44).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemStandard());
                if (autoRowHeightFlag) {
                    getCellAutoRowHeight(sheet6, cc, 61, charsLength).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());
                } else {
                    getCell(sheet6, cc, 61).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());
                }

                dd++;
            } // end for
        } // end if


        /* end WEB */
    } // end method createTotalReportTargets4

    // DB
    private void createTotalReportTargets3(XSSFWorkbook gWorkbook, List<TotalOverallResultDto> checkTargetsList, Map gAssetSwJob1, RestTemplate rest) throws Exception {

        logger.info("*[DB]");
        XSSFSheet sheet6 = gWorkbook.getSheet("DB");
        // 점검군별 (Linux, Window, DB, WEB, WAS, NW, Web App(URL), 기타)

        // (1) 보안점검 DB

        // 현황
        String statusStr = "CONCATENATE(\" 서버 보안점검 결과, 전체 점검대상(\",D32,\"EA)의 평균 보안수준은 \",AB56,\"입니다. 분류 항목 중 보안 설정이 가장 양호하게 설정된 분류 항목은 \",\"\"\"\",INDEX(B38:B55,MATCH(MAX(AB38:AB55),AB38:AB55,0),1),\"\"\"\",\"로 보안준수율은 \", MAX(AB38:AB55),\"점이며 \",\"\"\"\",INDEX(B38:B55,MATCH(MIN(AB38:AB55),AB38:AB55,0),1),\"\"\"\",\" 항목이 \",MIN(AB38:AB55),\"점으로 보안 설정이 가장 미흡하게 설정되어 있습니다.\")";
        getCell(sheet6, 11-1-1-1-2, 5).setCellFormula(statusStr);

        String statusMigStr = "CONCATENATE(\" 서버 이행점검 결과, 전체 점검대상(\",AM32,\"EA)의 평균 보안수준은 \",BD56,\"에서 \",BO56,\"으로 변경되었습니다. 보안설정이 가장 미흡하게 설정되어 있던 \",\"\"\"\",INDEX(AK38:AK55,MATCH(MIN(BD38:BD55),BD38:BD55,0),1),\"\"\"\",\" 분류 항목이 \", MIN(BD38:BD55),\"점에서 \",INDEX(BO38:BO55,MATCH(MIN(BD38:BD55),BD38:BD55,0),1),\"점으로 보안준수율이 변경되었으며, 현재 보안준수율이 가장 낮은 분류 항목으로는 \",\"\"\"\",INDEX(AK38:AK55,MATCH(MIN(BO38:BO55),BO38:BO55,0),1),\"\"\"\",\"으로 보안준수율은 \",MIN(BO38:BO55),\"점 입니다.\")";
        getCell(sheet6, 11-1-1-1-2, 40).setCellFormula(statusMigStr);


        /*----------------------------------------------------------------------------------------------------*/
        List<DetailResultDto> linuxTargetsList = testService.getDetailTargetsList(checkTargetsList, gAssetSwJob1, "DB", true);

        if (linuxTargetsList == null || linuxTargetsList.size() < 1) {

            throw new ErrorException("NoSuchElementException: No value");
        }

        // 점검수량 (EA)
        for (DetailResultDto dto : linuxTargetsList) {

            getCell(sheet6, 40-1-1-1-2-4, 3).setCellValue(dto.getValue1());
//            getCell(sheet6, 64-1-1-1-2-4, 5).setCellValue(dto.getValue16());
//            getCell(sheet6, 64-1-1-1-2-4, 8).setCellValue(dto.getValue16());
//        -> 현재는 개별항목수 x 점검수량 개수로 맞춤
            break;
        }

        // 양호 상중하Total.테이블
        int t1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 20).setCellValue(linuxTargetsList.get(t1).getValue17());
            getCell(sheet6, k2, 21).setCellValue(linuxTargetsList.get(t1).getValue18());
            getCell(sheet6, k2, 22).setCellValue(linuxTargetsList.get(t1).getValue19());
            t1++;
        }

        // 인터뷰필요 상중하Total.테이블
        int i1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 24).setCellValue(linuxTargetsList.get(i1).getValue20());
            getCell(sheet6, k2, 25).setCellValue(linuxTargetsList.get(i1).getValue21());
            getCell(sheet6, k2, 26).setCellValue(linuxTargetsList.get(i1).getValue22());
            i1++;
        }
        // 불가 상중하Total.테이블
        int d1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 28).setCellValue(linuxTargetsList.get(d1).getValue23());
            getCell(sheet6, k2, 29).setCellValue(linuxTargetsList.get(d1).getValue24());
            getCell(sheet6, k2, 30).setCellValue(linuxTargetsList.get(d1).getValue25());
            d1++;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h = 0;
        int h2 = 23-1-1-1-2-4;
        for (int g = 46-1-1-1-2-4; g < 46-1-1-1-2-4 + (linuxTargetsList.size()); g+=1) {

            getCell(sheet6, g, 1).setCellValue(linuxTargetsList.get(h).getKey());
            getCell(sheet6, g, 5).setCellValue(linuxTargetsList.get(h).getValue2());
            getCell(sheet6, g, 8).setCellValue(linuxTargetsList.get(h).getValue3());
            getCell(sheet6, g, 11).setCellValue(linuxTargetsList.get(h).getValue4());
            getCell(sheet6, g, 15).setCellValue(linuxTargetsList.get(h).getValue5());
            getCell(sheet6, g, 19).setCellValue(linuxTargetsList.get(h).getValue6());
            getCell(sheet6, g, 23).setCellValue(linuxTargetsList.get(h).getValue7());

            // 보안준수율 (웹UI 의 audit_rate 산정식을 기반으로 다시 계산함)
            getCell(sheet6, g, 27).setCellFormula(String.format(
                    "IF(F%d=0,100,ROUND(IFERROR((U%d*3+V%d*2+W%d*1) / ((U%d*3+V%d*2+W%d*1)+(L%d*3+P%d*2+T%d*1)+(Y%d*3+Z%d*2+AA%d*1)+(AC%d*3+AD%d*2+AE%d*1))*100,0),2))"
                    ,g+1
                    ,h2+1,h2+1,h2+1
                    ,h2+1,h2+1,h2+1
                    ,g+1,g+1,g+1
                    ,h2+1,h2+1,h2+1
                    ,h2+1,h2+1,h2+1)
            );
            // 보안수준
            getCell(sheet6, g, 31).setCellFormula(String.format("IF(AB%d>=90,\"우수\",IF(AND(AB%d>=80,AB%d<90),\"양호\",IF(AND(AB%d>=70,AB%d<80),\"보통\",\"미흡\")))"
                    , g + 1, g + 1, g + 1, g + 1, g + 1));
            h++;
            h2++;
        }

        // 전체 점검 개수(N/A 제외) Total
        getCell(sheet6, 33-1-1-2, 5).setCellFormula("SUM(F15:F29)");  // 25
        getCell(sheet6, 33-1-1-2, 8).setCellFormula("SUM(I15:I29)");  // 16
        getCell(sheet6, 33-1-1-2, 11).setCellFormula("SUM(L15:L29)"); // 5
        getCell(sheet6, 33-1-1-2, 14).setCellFormula("SUM(O15:O29)"); // 0

        getCell(sheet6, 33-1-1-2, 20).setCellFormula("SUM(U15:U20)"); // 0
        getCell(sheet6, 33-1-1-2, 21).setCellFormula("SUM(V15:V20)"); // 0
        getCell(sheet6, 33-1-1-2, 22).setCellFormula("SUM(W15:W20)"); // 0

        getCell(sheet6, 33-1-1-2, 24).setCellFormula("SUM(Y15:Y20)"); // 0
        getCell(sheet6, 33-1-1-2, 25).setCellFormula("SUM(Z15:Z20)"); // 0
        getCell(sheet6, 33-1-1-2, 26).setCellFormula("SUM(AA15:AA20)"); // 0

        getCell(sheet6, 33-1-1-2, 28).setCellFormula("SUM(AC15:AC20)"); // 0
        getCell(sheet6, 33-1-1-2, 29).setCellFormula("SUM(AD15:AD20)"); // 0
        getCell(sheet6, 33-1-1-2, 30).setCellFormula("SUM(AE15:AE20)"); // 0

        /*
            Total. 합계. 점검항목수
         */
        getCell(sheet6, 64-1-1-1-2-4, 5).setCellFormula("SUM(F38:H55)");
        getCell(sheet6, 64-1-1-1-2-4, 8).setCellFormula("SUM(I38:K55)");
//        getCell(sheet6, 64, 8).setCellFormula("SUM(I38:K55)");
//        -> value16 으로 대체
//        -> 현재는 개별항목수 x 점검수량 개수로 맞춤

        getCell(sheet6, 64-1-1-1-2-4, 11).setCellFormula("IFERROR(SUM(L38:O55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 15).setCellFormula("IFERROR(SUM(P38:S55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 19).setCellFormula("IFERROR(SUM(T38:W55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 23).setCellFormula("SUM(L56:W57)");

        //보안준수율 Total
        getCell(sheet6, 64-1-1-1-2-4, 34).setCellFormula("ROUND(AVERAGEA(AB38:AE55),2)");

        double sCheckValue = 0.0;
        double tempSCheckValue1 = 0.0;
        double tempSCheckValue2 = 0.0;

        // 전체 점검 개수(N/A 제외) 상중하Total.테이블
        int value10 = 0;
        int value11 = 0;
        int value12 = 0;
        for (DetailResultDto detailResultDto : linuxTargetsList) {

            value10 += detailResultDto.getValue10();  // 상
            value11 += detailResultDto.getValue11();  // 중
            value12 += detailResultDto.getValue12();  // 하
        }
        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int value4 = 0;
        int value5 = 0;
        int value6 = 0;
        for (DetailResultDto detailResultDto : linuxTargetsList) {

            value4 += detailResultDto.getValue4();   // 상
            value5 += detailResultDto.getValue5();   // 중
            value6 += detailResultDto.getValue6();   // 하
        }

        // 보안준수율 에서 보안준수율 로 변경
        tempSCheckValue1 = ((value10*3+value11*2+value12*1)-(value4*3+value5*2+value6*1));
        tempSCheckValue2 = (value10*3+value11*2+value12*1);
        sCheckValue = (tempSCheckValue1/tempSCheckValue2) * 100;
        sCheckValue = Math.round( sCheckValue * 100d ) / 100d;

        getCell(sheet6, 64-1-1-1-2-4, 27).setCellFormula("IF(AI56>=90,\"우수\",IF(AND(AI56>=80,AI56<90),\"양호\",IF(AND(AI56>=70,AI56<80),\"보통\",\"미흡\")))");

        /*
            Total. 평균. 점검결과
         */
        getCell(sheet6, 66-1-1-1-2-4, 11).setCellFormula("ROUND(IFERROR((U30*3)/((U30*3)+(L56*3)+(Y30*3)+(AC30*3))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 15).setCellFormula("ROUND(IFERROR((V30*2)/((V30*2)+(P56*2)+(Z30*2)+(AD30*2))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 19).setCellFormula("ROUND(IFERROR((W30*1)/((W30*1)+(T56*1)+(AA30*1)+(AE30*1))*100,0),2)");

        // 발견된 취약점 기간별 조치 방안
        // 현황 [74, 5]
        String statusStr2 = "CONCATENATE(\" 발견된 취약한 항목 중 위험도 \",\"\"\"\",\"상\",\"\"\"\",\"에 해당하는 즉시 조치가 필요한 항목의 비율은 평균 \",ROUND(Q112*100,1),\"%를 차지하고 있으며, 위험도 \",\"\"\"\",\"중\",\"\"\"\",\",\",\"\"\"\",\"하\",\"\"\"\",\"에 해당하는 중장기적으로 조치가 필요한 항목의 비율은 \",ROUND(AC112*100,1),\"%로 분석 되었습니다. 또한 즉시 조치가 필요한 항목 중, 가장 높은 비율을 차지하고 있는 상위 10개의 점검 대상은 아래와 같습니다.\")";
        getCell(sheet6, 74-1-1-1-2-4, 5).setCellFormula(statusStr2);
        // 현황 [74, 40]
        String statusMigStr2 = "CONCATENATE(\" 발견된 취약한 항목 중 평균 조치율은 \",ROUND(BR112*100,1),\"%로써 위험도 \",\"\"\"\",\"상\",\"\"\"\",\"에 해당하는 즉시 조치가 필요한 항목의 비율은 평균 \",ROUND(IFERROR(AR112/AO112*100,0),1),\"%에서 \",ROUND(BD112*100,1),\"%로 감소하였으며 위험도 \",\"\"\"\",\"중\",\"\"\"\",\",\",\"\"\"\",\"하\",\"\"\"\",\"에 해당하는 중장기적으로 조치가 필요한 항목의 비율은 \",ROUND(IFERROR(AV112/AO112*100,0),1),\"%에서 \",ROUND(BK112*100,1),\"%로 감소한 것으로 분석 되었습니다. 또한 잔여 취약점이 존재하는 대상 중, 즉시 조치가 필요한 항목이 높은 비율을 차지하고 있는 상위 10개의 점검 대상은 아래와 같습니다.\")";
        getCell(sheet6, 74-1-1-1-2-4, 40).setCellFormula(statusMigStr2);

        // 구분, 발견된취약점 수, 기간별 조치 방안 표
        int jj = 0;
        int kk = 47-1-1-1-2-4;
        int ll = 103-1-1-1-2-4;
        for (int ii = 102-1-1-1-2-4; ii < 102-1-1-1-2-4 + (linuxTargetsList.size()); ii+=1) {

            getCell(sheet6, ii, 1).setCellValue(linuxTargetsList.get(jj).getKey());

            getCell(sheet6, ii, 5).setCellFormula(String.format("X%d", kk));

            getCell(sheet6, ii, 10).setCellFormula(String.format("L%d", kk));

            getCell(sheet6, ii, 16).setCellFormula(String.format("IFERROR(K%d/F%d,0)",ll,ll));

            getCell(sheet6, ii, 22).setCellFormula(String.format("P%d+T%d",kk,kk));

            getCell(sheet6, ii, 28).setCellFormula(String.format("IFERROR(W%d/F%d,0)",ll,ll));
            jj++;
            kk+=1;
            ll+=1;
        }

        // 구분, 발견된취약점 수, 기간별 조치 방안 표 Total
        getCell(sheet6, 120-1-1-1-2-4, 5).setCellFormula("SUM(F94:J111)");
        getCell(sheet6, 120-1-1-1-2-4, 10).setCellFormula("SUM(K94:P111)");
        getCell(sheet6, 120-1-1-1-2-4, 16).setCellFormula("IFERROR(K112/F112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 22).setCellFormula("SUM(W94:AB111)");
        getCell(sheet6, 120-1-1-1-2-4, 28).setCellFormula("IFERROR(W112/F112,0)");

        // 즉시 조치 필요 대상시스템 (TOP 10). 테이블
        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList = testService.getReportResultProcessTargets(gAssetSwJob1, true);

        snetAssetSwAuditExcelDtoList.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate).reversed());

        snetAssetSwAuditExcelDtoList = snetAssetSwAuditExcelDtoList.stream().limit(10).collect(Collectors.toList());

        int bb = 0;
        if (snetAssetSwAuditExcelDtoList != null && snetAssetSwAuditExcelDtoList.size() > 0) {
            for (int aa = 126-1-1-1-2-4; aa < 126-1-1-1-2-4 + snetAssetSwAuditExcelDtoList.size(); aa++) {

                getCell(sheet6, aa, 182).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getHostNm());
                getCell(sheet6, aa, 183).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getAuditRate());

                if (snetAssetSwAuditExcelDtoList.get(bb).getWeekHighRate() == 0.0) {

                    getCell(sheet6, aa, 184).setCellValue("");
                } else {

                    getCell(sheet6, aa, 184).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getWeekHighRate());
                }

                if (snetAssetSwAuditExcelDtoList.get(bb).getWeekMiddleLowRate() == 0.0) {

                    getCell(sheet6, aa, 185).setCellValue("");
                } else {

                    getCell(sheet6, aa, 185).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getWeekMiddleLowRate());
                }
                bb++;
            }
        }

        // 취약점 상세 항목. 테이블. 보안점검
        List<SnetAssetSwAuditReportDto> assetSwAuditReportDtoList = testService.selectAssetSwAuditReportNew(gAssetSwJob1, true);

        if (assetSwAuditReportDtoList != null && assetSwAuditReportDtoList.size() > 0) {
            int dd = 0;
            for (int cc = 157-1-1-1-2-4; cc < 157-1-1-1-2-4 + assetSwAuditReportDtoList.size(); cc++) {

                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemNmDesc());
                getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGradeView());
                getCell(sheet6, cc, 26).setCellValue(assetSwAuditReportDtoList.get(dd).getHostCount());
                getCell(sheet6, cc, 28).setCellValue(assetSwAuditReportDtoList.get(dd).getEtc());
                dd++;
            } // end for assetSwAuditReportDtoList
//            for (int cc = 157; cc < 157 + assetSwAuditReportDtoList.size(); cc++) {
//
//                for (int ee = 1; ee <= 33; ee++) {
//                    getCell(sheet6, cc, ee).setCellStyle(cellStyle(gWorkbook, 10)); // style
//                }
//            } // end for style
        } // end if

        // 취약점 상세 항목 가이드. 테이블. 보안점검
        int colWithinchars = (sheet6.getColumnWidth(2) + sheet6.getColumnWidth(3));
        if (assetSwAuditReportDtoList != null && assetSwAuditReportDtoList.size() > 0) {
            int dd = 0;
            for (int cc = 211-1-1-1-2-4; cc < 211-1-1-1-2-4 + assetSwAuditReportDtoList.size(); cc++) {

//                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
//                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
//                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemStandard());
//                getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());


                String itemCMstr = assetSwAuditReportDtoList.get(dd).getItemCountermeasure();
                String[] chars = null;
                boolean autoRowHeightFlag = false;
                short charsLength = 1;
                try {
                    chars = itemCMstr.split("\n");

                    charsLength = (short) (chars.length);
                    autoRowHeightFlag = true;
                } catch (Exception e) {

                    autoRowHeightFlag = false;
                }


                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemStandard());
                if (autoRowHeightFlag) {
                    getCellAutoRowHeight(sheet6, cc, 24, charsLength).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());
                } else {
                    getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());
                }
                dd++;

            } // end for
        } // end if


        // (2) 이행점검 Linux
        List<DetailResultDto> migTargetsList = testService.getMigDetailTargetsList(checkTargetsList, gAssetSwJob1, "DB", true);

        // 점검수량 (EA)
        getCell(sheet6, 40-1-1-1-2-4, 38).setCellFormula("D32");

        // 양호 상중하Total.테이블
        int t2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 36).setCellValue(migTargetsList.get(t2).getValue17());
            getCell(sheet6, k2, 37).setCellValue(migTargetsList.get(t2).getValue18());
            getCell(sheet6, k2, 38).setCellValue(migTargetsList.get(t2).getValue19());
            t2++;
        }

        // 인터뷰필요 상중하Total.테이블
        int i2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 40).setCellValue(migTargetsList.get(i2).getValue20());
            getCell(sheet6, k2, 41).setCellValue(migTargetsList.get(i2).getValue21());
            getCell(sheet6, k2, 42).setCellValue(migTargetsList.get(i2).getValue22());
            i2++;
        }
        // 불가 상중하Total.테이블
        int d2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 44).setCellValue(migTargetsList.get(d2).getValue23());
            getCell(sheet6, k2, 45).setCellValue(migTargetsList.get(d2).getValue24());
            getCell(sheet6, k2, 46).setCellValue(migTargetsList.get(d2).getValue25());
            d2++;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h3 = 0;
        int h4 = 47-1-1-1-2-4;
        for (int g2 = 46-1-1-1-2-4; g2 < 46-1-1-1-2-4 + (linuxTargetsList.size()); g2+=1) {

            getCell(sheet6, g2, 36).setCellValue(linuxTargetsList.get(h3).getKey());
            getCell(sheet6, g2, 40).setCellFormula(String.format("F%d", g2 + 1));
            getCell(sheet6, g2, 43).setCellFormula(String.format("I%d", g2 + 1));
            getCell(sheet6, g2, 46).setCellFormula(String.format("L%d", g2 + 1));
            getCell(sheet6, g2, 48).setCellFormula(String.format("P%d", g2 + 1));
            getCell(sheet6, g2, 50).setCellFormula(String.format("T%d", g2 + 1));
            getCell(sheet6, g2, 53).setCellFormula(String.format("SUM(AU%d:BA%d)", g2 + 1, g2 + 1));

            // 보안준수율
            getCell(sheet6, g2, 55).setCellFormula(String.format("AB%d", g2 + 1));
            h3++;
            h4++;
        }

        int h5 = 0;
        int h6 = 23-1-1-1-2-4;
        for (int g2 = 46-1-1-1-2-4; g2 < 46-1-1-1-2-4 + (migTargetsList.size()); g2+=1) {
            // 이행점검
            // 상
            getCell(sheet6, g2, 58).setCellValue(migTargetsList.get(h5).getValue4());
            // 중
            getCell(sheet6, g2, 60).setCellValue(migTargetsList.get(h5).getValue5());
            // 하
            getCell(sheet6, g2, 62).setCellValue(migTargetsList.get(h5).getValue6());
            // Total
            getCell(sheet6, g2, 64).setCellFormula(String.format("SUM(BG%d:BL%d)",g2+1, g2+1));

            // 전체 점검 개수(N/A 제외) 상중하Total.테이블 옆에 위치
            //      보안준수율   :
            getCell(sheet6, g2, 66).setCellFormula(String.format(
                    "IF(AO%d=0,100,ROUND(IFERROR((AK%d*3+AL%d*2+AM%d*1) / ((AK%d*3+AL%d*2+AM%d*1)+(BG%d*3+BI%d*2+BK%d*1)+(AO%d*3+AP%d*2+AQ%d*1)+(AS%d*3+AT%d*2+AU%d*1))*100,0),2))"
                    ,g2+1
                    ,h6+1,h6+1,h6+1
                    ,h6+1,h6+1,h6+1
                    ,g2+1,g2+1,g2+1
                    ,h6+1,h6+1,h6+1
                    ,h6+1,h6+1,h6+1)
            );

            //      보안수준   :
            getCell(sheet6, g2, 68).setCellFormula(String.format("IF(BO%d>=90,\"우수\",IF(AND(BO%d>=80,BO%d<90),\"양호\",IF(AND(BO%d>=70,BO%d<80),\"보통\",\"미흡\")))"
                    ,g2+1,g2+1,g2+1,g2+1,g2+1));
            h5++;
            h6++;
        }

        getCell(sheet6, 33-1-1-2, 36).setCellFormula("SUM(AK15:AK20)"); // 20
        getCell(sheet6, 33-1-1-2, 37).setCellFormula("SUM(AL15:AL20)"); // 12
        getCell(sheet6, 33-1-1-2, 38).setCellFormula("SUM(AM15:AM20)"); // 2

        getCell(sheet6, 33-1-1-2, 40).setCellFormula("SUM(AO15:AO20)"); // 0
        getCell(sheet6, 33-1-1-2, 41).setCellFormula("SUM(AP15:AP20)"); // 1
        getCell(sheet6, 33-1-1-2, 42).setCellFormula("SUM(AQ15:AQ20)"); // 0

        getCell(sheet6, 33-1-1-2, 44).setCellFormula("SUM(AS15:AS20)"); // 0
        getCell(sheet6, 33-1-1-2, 45).setCellFormula("SUM(AT15:AT20)"); // 0
        getCell(sheet6, 33-1-1-2, 46).setCellFormula("SUM(AU15:AU20)"); // 0
        // Total
        //      합계 :

        getCell(sheet6, 64-1-1-1-2-4, 40).setCellFormula("F56");
        getCell(sheet6, 64-1-1-1-2-4, 43).setCellFormula("I56");
        getCell(sheet6, 64-1-1-1-2-4, 46).setCellFormula("SUM(AU38:AV55)");
        getCell(sheet6, 64-1-1-1-2-4, 48).setCellFormula("SUM(AW38:AX55)");
        getCell(sheet6, 64-1-1-1-2-4, 50).setCellFormula("SUM(AY38:BA55)");
        getCell(sheet6, 64-1-1-1-2-4, 53).setCellFormula("SUM(BB38:BC55)");
        getCell(sheet6, 64-1-1-1-2-4, 55).setCellFormula("AB56");

        //          - 이행점검
        getCell(sheet6, 64-1-1-1-2-4, 58).setCellFormula("IFERROR(SUM(BG38:BH55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 60).setCellFormula("IFERROR(SUM(BI38:BJ55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 62).setCellFormula("IFERROR(SUM(BK38:BL55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 64).setCellFormula("SUM(BM38:BN55)");

        //보안준수율 Total
        getCell(sheet6, 64-1-1-1-2-4, 71).setCellFormula("ROUND(AVERAGEA(BO38:BP55),2)");

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int pvalue4 = 0;
        int pvalue5 = 0;
        int pvalue6 = 0;
        int pvalue13 = 0;
        int pvalue14 = 0;
        int pvalue15 = 0;
        for (DetailResultDto detailResultDto : migTargetsList) {

            pvalue4 += detailResultDto.getValue4();   // 상.취약점
            pvalue5 += detailResultDto.getValue5();   // 중.취약점
            pvalue6 += detailResultDto.getValue6();   // 하.취약점
        }

        getCell(sheet6, 64-1-1-1-2-4, 66).setCellFormula("IF(BT56>=90,\"우수\",IF(AND(BT56>=80,BT56<90),\"양호\",IF(AND(BT56>=70,BT56<80),\"보통\",\"미흡\")))");

        //      평균 :
        getCell(sheet6, 66-1-1-1-2-4, 46).setCellFormula("L58");
        getCell(sheet6, 66-1-1-1-2-4, 48).setCellFormula("P58");
        getCell(sheet6, 66-1-1-1-2-4, 50).setCellFormula("T58");

        //     - 이행점검 Total. 평균. 점검결과
        getCell(sheet6, 66-1-1-1-2-4, 58).setCellFormula("ROUND(IFERROR((AK30*3)/((AK30*3)+(BG56*3)+(AO30*3)+(AS30*3))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 60).setCellFormula("ROUND(IFERROR((AL30*2)/((AL30*2)+(BI56*2)+(AP30*2)+(AT30*2))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 62).setCellFormula("ROUND(IFERROR((AM30*1)/((AM30*1)+(BK56*1)+(AQ30*1)+(AU30*1))*100,0),2)");

        // 발견된 취약점 기간별 조치 방안
        // 구분.기간별 조치 방안 표
        int n1 = 47-1-1-1-2-4;
        for (int m1 = 102-1-1-1-2-4; m1 < 102-1-1-1-2-4 + (linuxTargetsList.size()); m1+=1) {
            getCell(sheet6, m1, 36).setCellFormula(String.format("B%d", m1+1));
            getCell(sheet6, m1, 40).setCellFormula(String.format("BB%d", n1));
            getCell(sheet6, m1, 43).setCellFormula(String.format("AU%d", n1));
            getCell(sheet6, m1, 47).setCellFormula(String.format("AW%d+AY%d", n1, n1));
            n1+=1;
        }

        int n2 = 47-1-1-1-2-4;
        for (int m2 = 102-1-1-1-2-4; m2 < 102-1-1-1-2-4 + (migTargetsList.size()); m2+=1) {
            getCell(sheet6, m2, 52).setCellFormula(String.format("BG%d", n2));
            getCell(sheet6, m2, 55).setCellFormula(String.format("IFERROR(BA%d/AO%d,0)", m2+1, m2+1));
            getCell(sheet6, m2, 59).setCellFormula(String.format("BI%d+BK%d", n2, n2));
            getCell(sheet6, m2, 62).setCellFormula(String.format("IFERROR(BH%d/AO%d,0)", m2+1, m2+1));
            getCell(sheet6, m2, 65).setCellFormula(String.format("BA%d+BH%d", m2+1, m2+1));
            getCell(sheet6, m2, 67).setCellFormula(String.format("AO%d-BN%d", m2+1, m2+1));
            getCell(sheet6, m2, 69).setCellFormula(String.format("IFERROR(BP%d/AO%d,0)", m2+1, m2+1));
            n2+=1;
        }

        // 구분.기간별 조치 방안 표 Total
        getCell(sheet6, 120-1-1-1-2-4, 40).setCellFormula("SUM(AO94:AQ111)");
        getCell(sheet6, 120-1-1-1-2-4, 43).setCellFormula("SUM(AR94:AU111)");
        getCell(sheet6, 120-1-1-1-2-4, 47).setCellFormula("SUM(AV94:AY111)");
        getCell(sheet6, 120-1-1-1-2-4, 52).setCellFormula("SUM(BA94:BC111)");
        getCell(sheet6, 120-1-1-1-2-4, 55).setCellFormula("IFERROR(BA112/AO112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 59).setCellFormula("SUM(BH94:BJ111)");
        getCell(sheet6, 120-1-1-1-2-4, 62).setCellFormula("IFERROR(BH112/AO112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 65).setCellFormula("SUM(BN94:BO111)");
        getCell(sheet6, 120-1-1-1-2-4, 67).setCellFormula("SUM(BP94:BQ111)");
        getCell(sheet6, 120-1-1-1-2-4, 69).setCellFormula("IFERROR(BP112/AO112,0)");

        // 즉시 조치 필요 대상시스템 (TOP 10). 테이블
        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList2 = testService.getReportResultProcessTargets(gAssetSwJob1, false);

        snetAssetSwAuditExcelDtoList2.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate).reversed());

        snetAssetSwAuditExcelDtoList2 = snetAssetSwAuditExcelDtoList2.stream().limit(10).collect(Collectors.toList());

        int bb2 = 0;
        if (snetAssetSwAuditExcelDtoList2 != null && snetAssetSwAuditExcelDtoList2.size() > 0) {

            for (int aa2 = 126-1-1-1-2-4; aa2 < 126-1-1-1-2-4 + snetAssetSwAuditExcelDtoList2.size(); aa2++) {


                getCell(sheet6, aa2, 186).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getHostNm());
                getCell(sheet6, aa2, 187).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getAuditRate());

                if (snetAssetSwAuditExcelDtoList.get(bb2).getWeekHighRate() == 0.0) {

                    getCell(sheet6, aa2, 188).setCellValue("");
                } else {

                    getCell(sheet6, aa2, 188).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getWeekHighRate());
                }

                if (snetAssetSwAuditExcelDtoList.get(bb2).getWeekMiddleLowRate() == 0.0) {

                    getCell(sheet6, aa2, 189).setCellValue("");
                } else {

                    getCell(sheet6, aa2, 189).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getWeekMiddleLowRate());
                }
                bb2++;
            }
        }

        // 취약점 상세 항목. 테이블. 이행점검
        List<SnetAssetSwAuditReportDto> assetSwAuditReportDtoList2 = testService.selectAssetSwAuditReportNew(gAssetSwJob1, false);

        if (assetSwAuditReportDtoList2 != null && assetSwAuditReportDtoList2.size() > 0) {

            int dd2 = 0;
            for (int cc2 = 157-1-1-1-2-4; cc2 < 157-1-1-1-2-4 + assetSwAuditReportDtoList2.size(); cc2++) {

                getCell(sheet6, cc2, 36).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemGrpNm());
                getCell(sheet6, cc2, 41).setCellValue(assetSwAuditReportDtoList2.get(dd2).getDivideNum());
                getCell(sheet6, cc2, 44).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemNmDesc());
                getCell(sheet6, cc2, 61).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemGradeView());
                getCell(sheet6, cc2, 63).setCellValue(assetSwAuditReportDtoList2.get(dd2).getHostCount());
                getCell(sheet6, cc2, 64).setCellValue(assetSwAuditReportDtoList2.get(dd2).getHostCount());
                getCell(sheet6, cc2, 65).setCellValue(assetSwAuditReportDtoList2.get(dd2).getEtc());
                dd2++;
            } // end for assetSwAuditReportDtoList2
//            for (int cc = 157; cc < 157 + assetSwAuditReportDtoList2.size(); cc++) {
//
//                for (int ff = 36; ff <= 65; ff++) {
//                    getCell(sheet6, cc, ff).setCellStyle(cellStyle(gWorkbook, 10)); // style
//                }
//            } // end for style
        }

        // 취약점 상세 항목 가이드. 테이블. 이행점검
        if (assetSwAuditReportDtoList2 != null && assetSwAuditReportDtoList2.size() > 0) {
            int dd = 0;
            for (int cc = 211-1-1-1-2-4; cc < 211-1-1-1-2-4 + assetSwAuditReportDtoList2.size(); cc++) {

//                getCell(sheet6, cc, 36).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemGrpNm());
//                getCell(sheet6, cc, 41).setCellValue(assetSwAuditReportDtoList2.get(dd).getDivideNum());
//                getCell(sheet6, cc, 44).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemStandard());
//                getCell(sheet6, cc, 61).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());

                String itemCMstr = assetSwAuditReportDtoList2.get(dd).getItemCountermeasure();
                String[] chars = null;
                boolean autoRowHeightFlag = false;
                short charsLength = 1;
                try {
                    chars = itemCMstr.split("\n");

                    charsLength = (short) (chars.length);
                    autoRowHeightFlag = true;
                } catch (Exception e) {

                    autoRowHeightFlag = false;
                }

                getCell(sheet6, cc, 36).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 41).setCellValue(assetSwAuditReportDtoList2.get(dd).getDivideNum());
                getCell(sheet6, cc, 44).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemStandard());
                if (autoRowHeightFlag) {
                    getCellAutoRowHeight(sheet6, cc, 61, charsLength).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());
                } else {
                    getCell(sheet6, cc, 61).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());
                }

                dd++;
            } // end for
        } // end if

        /* end DB*/
    } // end method createTotalReportTargets3

    // Windows
    private void createTotalReportTargets2(XSSFWorkbook gWorkbook, List<TotalOverallResultDto> checkTargetsList, Map gAssetSwJob1, RestTemplate rest) throws Exception {

        logger.info("*[Windows]");
        XSSFSheet sheet6 = gWorkbook.getSheet("Windows");
        // 점검군별 (Linux, Window, DB, WEB, WAS, NW, Web App(URL), 기타)

        // (1) 보안점검 Windows

        // 현황
        String statusStr = "CONCATENATE(\" 서버 보안점검 결과, 전체 점검대상(\",D32,\"EA)의 평균 보안수준은 \",AB56,\"입니다. 분류 항목 중 보안 설정이 가장 양호하게 설정된 분류 항목은 \",\"\"\"\",INDEX(B38:B55,MATCH(MAX(AB38:AB55),AB38:AB55,0),1),\"\"\"\",\"로 보안준수율은 \", MAX(AB38:AB55),\"점이며 \",\"\"\"\",INDEX(B38:B55,MATCH(MIN(AB38:AB55),AB38:AB55,0),1),\"\"\"\",\" 항목이 \",MIN(AB38:AB55),\"점으로 보안 설정이 가장 미흡하게 설정되어 있습니다.\")";
        getCell(sheet6, 11-1-1-1-2, 5).setCellFormula(statusStr);

        String statusMigStr = "CONCATENATE(\" 서버 이행점검 결과, 전체 점검대상(\",AM32,\"EA)의 평균 보안수준은 \",BD56,\"에서 \",BO56,\"으로 변경되었습니다. 보안설정이 가장 미흡하게 설정되어 있던 \",\"\"\"\",INDEX(AK38:AK55,MATCH(MIN(BD38:BD55),BD38:BD55,0),1),\"\"\"\",\" 분류 항목이 \", MIN(BD38:BD55),\"점에서 \",INDEX(BO38:BO55,MATCH(MIN(BD38:BD55),BD38:BD55,0),1),\"점으로 보안준수율이 변경되었으며, 현재 보안준수율이 가장 낮은 분류 항목으로는 \",\"\"\"\",INDEX(AK38:AK55,MATCH(MIN(BO38:BO55),BO38:BO55,0),1),\"\"\"\",\"으로 보안준수율은 \",MIN(BO38:BO55),\"점 입니다.\")";
        getCell(sheet6, 11-1-1-1-2, 40).setCellFormula(statusMigStr);


        /*----------------------------------------------------------------------------------------------------*/
        List<DetailResultDto> linuxTargetsList = testService.getDetailTargetsList(checkTargetsList, gAssetSwJob1, "Windows", true);

        if (linuxTargetsList == null || linuxTargetsList.size() < 1) {

            throw new ErrorException("NoSuchElementException: No value");
        }

        // 점검수량 (EA)
        for (DetailResultDto dto : linuxTargetsList) {

            getCell(sheet6, 40-1-1-1-2-4, 3).setCellValue(dto.getValue1());
//            getCell(sheet6, 64-1-1-1-2-4, 5).setCellValue(dto.getValue16());
//            getCell(sheet6, 64-1-1-1-2-4, 8).setCellValue(dto.getValue16());
//        -> 현재는 개별항목수 x 점검수량 개수로 맞춤
            break;
        }

        // 양호 상중하Total.테이블
        int t1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 20).setCellValue(linuxTargetsList.get(t1).getValue17());
            getCell(sheet6, k2, 21).setCellValue(linuxTargetsList.get(t1).getValue18());
            getCell(sheet6, k2, 22).setCellValue(linuxTargetsList.get(t1).getValue19());
            t1++;
        }

        // 인터뷰필요 상중하Total.테이블
        int i1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 24).setCellValue(linuxTargetsList.get(i1).getValue20());
            getCell(sheet6, k2, 25).setCellValue(linuxTargetsList.get(i1).getValue21());
            getCell(sheet6, k2, 26).setCellValue(linuxTargetsList.get(i1).getValue22());
            i1++;
        }
        // 불가 상중하Total.테이블
        int d1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 28).setCellValue(linuxTargetsList.get(d1).getValue23());
            getCell(sheet6, k2, 29).setCellValue(linuxTargetsList.get(d1).getValue24());
            getCell(sheet6, k2, 30).setCellValue(linuxTargetsList.get(d1).getValue25());
            d1++;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h = 0;
        int h2 = 23-1-1-1-2-4;
        for (int g = 46-1-1-1-2-4; g < 46-1-1-1-2-4 + (linuxTargetsList.size()); g+=1) {

            getCell(sheet6, g, 1).setCellValue(linuxTargetsList.get(h).getKey());
            getCell(sheet6, g, 5).setCellValue(linuxTargetsList.get(h).getValue2());
            getCell(sheet6, g, 8).setCellValue(linuxTargetsList.get(h).getValue3());
            getCell(sheet6, g, 11).setCellValue(linuxTargetsList.get(h).getValue4());
            getCell(sheet6, g, 15).setCellValue(linuxTargetsList.get(h).getValue5());
            getCell(sheet6, g, 19).setCellValue(linuxTargetsList.get(h).getValue6());
            getCell(sheet6, g, 23).setCellValue(linuxTargetsList.get(h).getValue7());

            // 보안준수율 (웹UI 의 audit_rate 산정식을 기반으로 다시 계산함)
            getCell(sheet6, g, 27).setCellFormula(String.format(
                    "IF(F%d=0,100,ROUND(IFERROR((U%d*3+V%d*2+W%d*1) / ((U%d*3+V%d*2+W%d*1)+(L%d*3+P%d*2+T%d*1)+(Y%d*3+Z%d*2+AA%d*1)+(AC%d*3+AD%d*2+AE%d*1))*100,0),2))"
                    ,g+1
                    ,h2+1,h2+1,h2+1
                    ,h2+1,h2+1,h2+1
                    ,g+1,g+1,g+1
                    ,h2+1,h2+1,h2+1
                    ,h2+1,h2+1,h2+1)
            );
            // 보안수준
            getCell(sheet6, g, 31).setCellFormula(String.format("IF(AB%d>=90,\"우수\",IF(AND(AB%d>=80,AB%d<90),\"양호\",IF(AND(AB%d>=70,AB%d<80),\"보통\",\"미흡\")))"
                    , g + 1, g + 1, g + 1, g + 1, g + 1));
            h++;
            h2++;
        }

        // 전체 점검 개수(N/A 제외) Total
        getCell(sheet6, 33-1-1-2, 5).setCellFormula("SUM(F15:F29)");  // 25
        getCell(sheet6, 33-1-1-2, 8).setCellFormula("SUM(I15:I29)");  // 16
        getCell(sheet6, 33-1-1-2, 11).setCellFormula("SUM(L15:L29)"); // 5
        getCell(sheet6, 33-1-1-2, 14).setCellFormula("SUM(O15:O29)"); // 0

        getCell(sheet6, 33-1-1-2, 20).setCellFormula("SUM(U15:U20)"); // 0
        getCell(sheet6, 33-1-1-2, 21).setCellFormula("SUM(V15:V20)"); // 0
        getCell(sheet6, 33-1-1-2, 22).setCellFormula("SUM(W15:W20)"); // 0

        getCell(sheet6, 33-1-1-2, 24).setCellFormula("SUM(Y15:Y20)"); // 0
        getCell(sheet6, 33-1-1-2, 25).setCellFormula("SUM(Z15:Z20)"); // 0
        getCell(sheet6, 33-1-1-2, 26).setCellFormula("SUM(AA15:AA20)"); // 0

        getCell(sheet6, 33-1-1-2, 28).setCellFormula("SUM(AC15:AC20)"); // 0
        getCell(sheet6, 33-1-1-2, 29).setCellFormula("SUM(AD15:AD20)"); // 0
        getCell(sheet6, 33-1-1-2, 30).setCellFormula("SUM(AE15:AE20)"); // 0

        /*
            Total. 합계. 점검항목수
         */
        getCell(sheet6, 64-1-1-1-2-4, 5).setCellFormula("SUM(F38:H55)");
        getCell(sheet6, 64-1-1-1-2-4, 8).setCellFormula("SUM(I38:K55)");
//        getCell(sheet6, 64, 8).setCellFormula("SUM(I38:K55)");
//        -> value16 으로 대체
//        -> 현재는 개별항목수 x 점검수량 개수로 맞춤

        getCell(sheet6, 64-1-1-1-2-4, 11).setCellFormula("IFERROR(SUM(L38:O55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 15).setCellFormula("IFERROR(SUM(P38:S55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 19).setCellFormula("IFERROR(SUM(T38:W55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 23).setCellFormula("SUM(L56:W57)");

        //보안준수율 Total
        getCell(sheet6, 64-1-1-1-2-4, 34).setCellFormula("ROUND(AVERAGEA(AB38:AE55),2)");

        double sCheckValue = 0.0;
        double tempSCheckValue1 = 0.0;
        double tempSCheckValue2 = 0.0;

        // 전체 점검 개수(N/A 제외) 상중하Total.테이블
        int value10 = 0;
        int value11 = 0;
        int value12 = 0;
        for (DetailResultDto detailResultDto : linuxTargetsList) {

            value10 += detailResultDto.getValue10();  // 상
            value11 += detailResultDto.getValue11();  // 중
            value12 += detailResultDto.getValue12();  // 하
        }
        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int value4 = 0;
        int value5 = 0;
        int value6 = 0;
        for (DetailResultDto detailResultDto : linuxTargetsList) {

            value4 += detailResultDto.getValue4();   // 상
            value5 += detailResultDto.getValue5();   // 중
            value6 += detailResultDto.getValue6();   // 하
        }

        // 보안준수율 에서 보안준수율 로 변경
        tempSCheckValue1 = ((value10*3+value11*2+value12*1)-(value4*3+value5*2+value6*1));
        tempSCheckValue2 = (value10*3+value11*2+value12*1);
        sCheckValue = (tempSCheckValue1/tempSCheckValue2) * 100;
        sCheckValue = Math.round( sCheckValue * 100d ) / 100d;

        XSSFSheet sheet5 = gWorkbook.getSheet("Ⅳ. 전체종합결과");

        getCell(sheet6, 64-1-1-1-2-4, 27).setCellFormula("IF(AI56>=90,\"우수\",IF(AND(AI56>=80,AI56<90),\"양호\",IF(AND(AI56>=70,AI56<80),\"보통\",\"미흡\")))");

        /*
            Total. 평균. 점검결과
         */
        getCell(sheet6, 66-1-1-1-2-4, 11).setCellFormula("ROUND(IFERROR((U30*3)/((U30*3)+(L56*3)+(Y30*3)+(AC30*3))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 15).setCellFormula("ROUND(IFERROR((V30*2)/((V30*2)+(P56*2)+(Z30*2)+(AD30*2))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 19).setCellFormula("ROUND(IFERROR((W30*1)/((W30*1)+(T56*1)+(AA30*1)+(AE30*1))*100,0),2)");

        // 발견된 취약점 기간별 조치 방안
        // 현황 [74, 5]
        String statusStr2 = "CONCATENATE(\" 발견된 취약한 항목 중 위험도 \",\"\"\"\",\"상\",\"\"\"\",\"에 해당하는 즉시 조치가 필요한 항목의 비율은 평균 \",ROUND(Q112*100,1),\"%를 차지하고 있으며, 위험도 \",\"\"\"\",\"중\",\"\"\"\",\",\",\"\"\"\",\"하\",\"\"\"\",\"에 해당하는 중장기적으로 조치가 필요한 항목의 비율은 \",ROUND(AC112*100,1),\"%로 분석 되었습니다. 또한 즉시 조치가 필요한 항목 중, 가장 높은 비율을 차지하고 있는 상위 10개의 점검 대상은 아래와 같습니다.\")";
        getCell(sheet6, 74-1-1-1-2-4, 5).setCellFormula(statusStr2);
        // 현황 [74, 40]
        String statusMigStr2 = "CONCATENATE(\" 발견된 취약한 항목 중 평균 조치율은 \",ROUND(BR112*100,1),\"%로써 위험도 \",\"\"\"\",\"상\",\"\"\"\",\"에 해당하는 즉시 조치가 필요한 항목의 비율은 평균 \",ROUND(IFERROR(AR112/AO112*100,0),1),\"%에서 \",ROUND(BD112*100,1),\"%로 감소하였으며 위험도 \",\"\"\"\",\"중\",\"\"\"\",\",\",\"\"\"\",\"하\",\"\"\"\",\"에 해당하는 중장기적으로 조치가 필요한 항목의 비율은 \",ROUND(IFERROR(AV112/AO112*100,0),1),\"%에서 \",ROUND(BK112*100,1),\"%로 감소한 것으로 분석 되었습니다. 또한 잔여 취약점이 존재하는 대상 중, 즉시 조치가 필요한 항목이 높은 비율을 차지하고 있는 상위 10개의 점검 대상은 아래와 같습니다.\")";
        getCell(sheet6, 74-1-1-1-2-4, 40).setCellFormula(statusMigStr2);

        // 구분, 발견된취약점 수, 기간별 조치 방안 표
        int jj = 0;
        int kk = 47-1-1-1-2-4;
        int ll = 103-1-1-1-2-4;
        for (int ii = 102-1-1-1-2-4; ii < 102-1-1-1-2-4 + (linuxTargetsList.size()); ii+=1) {

            getCell(sheet6, ii, 1).setCellValue(linuxTargetsList.get(jj).getKey());

            getCell(sheet6, ii, 5).setCellFormula(String.format("X%d", kk));

            getCell(sheet6, ii, 10).setCellFormula(String.format("L%d", kk));

            getCell(sheet6, ii, 16).setCellFormula(String.format("IFERROR(K%d/F%d,0)",ll,ll));

            getCell(sheet6, ii, 22).setCellFormula(String.format("P%d+T%d",kk,kk));

            getCell(sheet6, ii, 28).setCellFormula(String.format("IFERROR(W%d/F%d,0)",ll,ll));
            jj++;
            kk+=1;
            ll+=1;
        }

        // 구분, 발견된취약점 수, 기간별 조치 방안 표 Total
        getCell(sheet6, 120-1-1-1-2-4, 5).setCellFormula("SUM(F94:J111)");
        getCell(sheet6, 120-1-1-1-2-4, 10).setCellFormula("SUM(K94:P111)");
        getCell(sheet6, 120-1-1-1-2-4, 16).setCellFormula("IFERROR(K112/F112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 22).setCellFormula("SUM(W94:AB111)");
        getCell(sheet6, 120-1-1-1-2-4, 28).setCellFormula("IFERROR(W112/F112,0)");

        // 즉시 조치 필요 대상시스템 (TOP 10). 테이블
        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList = testService.getReportResultProcessTargets(gAssetSwJob1, true);

        snetAssetSwAuditExcelDtoList.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate).reversed());

        snetAssetSwAuditExcelDtoList = snetAssetSwAuditExcelDtoList.stream().limit(10).collect(Collectors.toList());

        int bb = 0;
        if (snetAssetSwAuditExcelDtoList != null && snetAssetSwAuditExcelDtoList.size() > 0) {
            for (int aa = 126-1-1-1-2-4; aa < 126-1-1-1-2-4 + snetAssetSwAuditExcelDtoList.size(); aa++) {

                getCell(sheet6, aa, 182).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getHostNm());
                getCell(sheet6, aa, 183).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getAuditRate());

                if (snetAssetSwAuditExcelDtoList.get(bb).getWeekHighRate() == 0.0) {

                    getCell(sheet6, aa, 184).setCellValue("");
                } else {

                    getCell(sheet6, aa, 184).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getWeekHighRate());
                }

                if (snetAssetSwAuditExcelDtoList.get(bb).getWeekMiddleLowRate() == 0.0) {

                    getCell(sheet6, aa, 185).setCellValue("");
                } else {

                    getCell(sheet6, aa, 185).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getWeekMiddleLowRate());
                }
                bb++;
            }
        }

        // 취약점 상세 항목. 테이블. 보안점검
        List<SnetAssetSwAuditReportDto> assetSwAuditReportDtoList = testService.selectAssetSwAuditReportNew(gAssetSwJob1, true);

        if (assetSwAuditReportDtoList != null && assetSwAuditReportDtoList.size() > 0) {
            int dd = 0;
            for (int cc = 157-1-1-1-2-4; cc < 157-1-1-1-2-4 + assetSwAuditReportDtoList.size(); cc++) {

                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemNmDesc());
                getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGradeView());
                getCell(sheet6, cc, 26).setCellValue(assetSwAuditReportDtoList.get(dd).getHostCount());
                getCell(sheet6, cc, 28).setCellValue(assetSwAuditReportDtoList.get(dd).getEtc());
                dd++;
            } // end for assetSwAuditReportDtoList
//            for (int cc = 157; cc < 157 + assetSwAuditReportDtoList.size(); cc++) {
//
//                for (int ee = 1; ee <= 33; ee++) {
//                    getCell(sheet6, cc, ee).setCellStyle(cellStyle(gWorkbook, 10)); // style
//                }
//            } // end for style
        } // end if

        // 취약점 상세 항목 가이드. 테이블. 보안점검
        int colWithinchars = (sheet6.getColumnWidth(2) + sheet6.getColumnWidth(3));
        if (assetSwAuditReportDtoList != null && assetSwAuditReportDtoList.size() > 0) {
            int dd = 0;
            for (int cc = 211-1-1-1-2-4; cc < 211-1-1-1-2-4 + assetSwAuditReportDtoList.size(); cc++) {

//                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
//                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
//                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemStandard());
//                getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());


                String itemCMstr = assetSwAuditReportDtoList.get(dd).getItemCountermeasure();
                String[] chars = null;
                boolean autoRowHeightFlag = false;
                short charsLength = 1;
                try {
                    chars = itemCMstr.split("\n");

                    charsLength = (short) (chars.length);
                    autoRowHeightFlag = true;
                } catch (Exception e) {

                    autoRowHeightFlag = false;
                }


                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemStandard());
                if (autoRowHeightFlag) {
                    getCellAutoRowHeight(sheet6, cc, 24, charsLength).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());
                } else {
                    getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());
                }
                dd++;

            } // end for
        } // end if



        // (2) 이행점검 Windows
        List<DetailResultDto> migTargetsList = testService.getMigDetailTargetsList(checkTargetsList, gAssetSwJob1, "Windows", true);

        // 점검수량 (EA)
        getCell(sheet6, 40-1-1-1-2-4, 38).setCellFormula("D32");

        // 양호 상중하Total.테이블
        int t2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 36).setCellValue(migTargetsList.get(t2).getValue17());
            getCell(sheet6, k2, 37).setCellValue(migTargetsList.get(t2).getValue18());
            getCell(sheet6, k2, 38).setCellValue(migTargetsList.get(t2).getValue19());
            t2++;
        }

        // 인터뷰필요 상중하Total.테이블
        int i2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 40).setCellValue(migTargetsList.get(i2).getValue20());
            getCell(sheet6, k2, 41).setCellValue(migTargetsList.get(i2).getValue21());
            getCell(sheet6, k2, 42).setCellValue(migTargetsList.get(i2).getValue22());
            i2++;
        }
        // 불가 상중하Total.테이블
        int d2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 44).setCellValue(migTargetsList.get(d2).getValue23());
            getCell(sheet6, k2, 45).setCellValue(migTargetsList.get(d2).getValue24());
            getCell(sheet6, k2, 46).setCellValue(migTargetsList.get(d2).getValue25());
            d2++;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h3 = 0;
        int h4 = 47-1-1-1-2-4;
        for (int g2 = 46-1-1-1-2-4; g2 < 46-1-1-1-2-4 + (linuxTargetsList.size()); g2+=1) {

            getCell(sheet6, g2, 36).setCellValue(linuxTargetsList.get(h3).getKey());
            getCell(sheet6, g2, 40).setCellFormula(String.format("F%d", g2 + 1));
            getCell(sheet6, g2, 43).setCellFormula(String.format("I%d", g2 + 1));
            getCell(sheet6, g2, 46).setCellFormula(String.format("L%d", g2 + 1));
            getCell(sheet6, g2, 48).setCellFormula(String.format("P%d", g2 + 1));
            getCell(sheet6, g2, 50).setCellFormula(String.format("T%d", g2 + 1));
            getCell(sheet6, g2, 53).setCellFormula(String.format("SUM(AU%d:BA%d)", g2 + 1, g2 + 1));

            // 보안준수율
            getCell(sheet6, g2, 55).setCellFormula(String.format("AB%d", g2 + 1));
            h3++;
            h4++;
        }

        int h5 = 0;
        int h6 = 23-1-1-1-2-4;
        for (int g2 = 46-1-1-1-2-4; g2 < 46-1-1-1-2-4 + (migTargetsList.size()); g2+=1) {
            // 이행점검
            // 상
            getCell(sheet6, g2, 58).setCellValue(migTargetsList.get(h5).getValue4());
            // 중
            getCell(sheet6, g2, 60).setCellValue(migTargetsList.get(h5).getValue5());
            // 하
            getCell(sheet6, g2, 62).setCellValue(migTargetsList.get(h5).getValue6());
            // Total
            getCell(sheet6, g2, 64).setCellFormula(String.format("SUM(BG%d:BL%d)",g2+1, g2+1));

            // 전체 점검 개수(N/A 제외) 상중하Total.테이블 옆에 위치
            //      보안준수율   :
            // 이행점검 진단일에 대한 개수 반영해야 함
            getCell(sheet6, g2, 66).setCellFormula(String.format(
                    "IF(AO%d=0,100,ROUND(IFERROR((AK%d*3+AL%d*2+AM%d*1) / ((AK%d*3+AL%d*2+AM%d*1)+(BG%d*3+BI%d*2+BK%d*1)+(AO%d*3+AP%d*2+AQ%d*1)+(AS%d*3+AT%d*2+AU%d*1))*100,0),2))"
                    ,g2+1
                    ,h6+1,h6+1,h6+1
                    ,h6+1,h6+1,h6+1
                    ,g2+1,g2+1,g2+1
                    ,h6+1,h6+1,h6+1
                    ,h6+1,h6+1,h6+1)
            );

            //      보안수준   :
            getCell(sheet6, g2, 68).setCellFormula(String.format("IF(BO%d>=90,\"우수\",IF(AND(BO%d>=80,BO%d<90),\"양호\",IF(AND(BO%d>=70,BO%d<80),\"보통\",\"미흡\")))"
                    ,g2+1,g2+1,g2+1,g2+1,g2+1));
            h5++;
            h6++;
        }

        getCell(sheet6, 33-1-1-2, 36).setCellFormula("SUM(AK15:AK20)"); // 20
        getCell(sheet6, 33-1-1-2, 37).setCellFormula("SUM(AL15:AL20)"); // 12
        getCell(sheet6, 33-1-1-2, 38).setCellFormula("SUM(AM15:AM20)"); // 2

        getCell(sheet6, 33-1-1-2, 40).setCellFormula("SUM(AO15:AO20)"); // 0
        getCell(sheet6, 33-1-1-2, 41).setCellFormula("SUM(AP15:AP20)"); // 1
        getCell(sheet6, 33-1-1-2, 42).setCellFormula("SUM(AQ15:AQ20)"); // 0

        getCell(sheet6, 33-1-1-2, 44).setCellFormula("SUM(AS15:AS20)"); // 0
        getCell(sheet6, 33-1-1-2, 45).setCellFormula("SUM(AT15:AT20)"); // 0
        getCell(sheet6, 33-1-1-2, 46).setCellFormula("SUM(AU15:AU20)"); // 0
        // Total
        //      합계 :

        getCell(sheet6, 64-1-1-1-2-4, 40).setCellFormula("F56");
        getCell(sheet6, 64-1-1-1-2-4, 43).setCellFormula("I56");
        getCell(sheet6, 64-1-1-1-2-4, 46).setCellFormula("SUM(AU38:AV55)");
        getCell(sheet6, 64-1-1-1-2-4, 48).setCellFormula("SUM(AW38:AX55)");
        getCell(sheet6, 64-1-1-1-2-4, 50).setCellFormula("SUM(AY38:BA55)");
        getCell(sheet6, 64-1-1-1-2-4, 53).setCellFormula("SUM(BB38:BC55)");
        getCell(sheet6, 64-1-1-1-2-4, 55).setCellFormula("AB56");

        //          - 이행점검
        getCell(sheet6, 64-1-1-1-2-4, 58).setCellFormula("IFERROR(SUM(BG38:BH55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 60).setCellFormula("IFERROR(SUM(BI38:BJ55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 62).setCellFormula("IFERROR(SUM(BK38:BL55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 64).setCellFormula("SUM(BM38:BN55)");

        //보안준수율 Total
        getCell(sheet6, 64-1-1-1-2-4, 71).setCellFormula("ROUND(AVERAGEA(BO38:BP55),2)");

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int pvalue4 = 0;
        int pvalue5 = 0;
        int pvalue6 = 0;
        int pvalue13 = 0;
        int pvalue14 = 0;
        int pvalue15 = 0;
        for (DetailResultDto detailResultDto : migTargetsList) {

            pvalue4 += detailResultDto.getValue4();   // 상.취약점
            pvalue5 += detailResultDto.getValue5();   // 중.취약점
            pvalue6 += detailResultDto.getValue6();   // 하.취약점
        }

        getCell(sheet6, 64-1-1-1-2-4, 66).setCellFormula("IF(BT56>=90,\"우수\",IF(AND(BT56>=80,BT56<90),\"양호\",IF(AND(BT56>=70,BT56<80),\"보통\",\"미흡\")))");

        //      평균 :
        getCell(sheet6, 66-1-1-1-2-4, 46).setCellFormula("L58");
        getCell(sheet6, 66-1-1-1-2-4, 48).setCellFormula("P58");
        getCell(sheet6, 66-1-1-1-2-4, 50).setCellFormula("T58");

        //     - 이행점검 Total. 평균. 점검결과
        getCell(sheet6, 66-1-1-1-2-4, 58).setCellFormula("ROUND(IFERROR((AK30*3)/((AK30*3)+(BG56*3)+(AO30*3)+(AS30*3))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 60).setCellFormula("ROUND(IFERROR((AL30*2)/((AL30*2)+(BI56*2)+(AP30*2)+(AT30*2))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 62).setCellFormula("ROUND(IFERROR((AM30*1)/((AM30*1)+(BK56*1)+(AQ30*1)+(AU30*1))*100,0),2)");

        // 발견된 취약점 기간별 조치 방안
        // 구분.기간별 조치 방안 표
        int n1 = 47-1-1-1-2-4;
        for (int m1 = 102-1-1-1-2-4; m1 < 102-1-1-1-2-4 + (linuxTargetsList.size()); m1+=1) {
            getCell(sheet6, m1, 36).setCellFormula(String.format("B%d", m1+1));
            getCell(sheet6, m1, 40).setCellFormula(String.format("BB%d", n1));
            getCell(sheet6, m1, 43).setCellFormula(String.format("AU%d", n1));
            getCell(sheet6, m1, 47).setCellFormula(String.format("AW%d+AY%d", n1, n1));
            n1+=1;
        }

        int n2 = 47-1-1-1-2-4;
        for (int m2 = 102-1-1-1-2-4; m2 < 102-1-1-1-2-4 + (migTargetsList.size()); m2+=1) {
            getCell(sheet6, m2, 52).setCellFormula(String.format("BG%d", n2));
            getCell(sheet6, m2, 55).setCellFormula(String.format("IFERROR(BA%d/AO%d,0)", m2+1, m2+1));
            getCell(sheet6, m2, 59).setCellFormula(String.format("BI%d+BK%d", n2, n2));
            getCell(sheet6, m2, 62).setCellFormula(String.format("IFERROR(BH%d/AO%d,0)", m2+1, m2+1));
            getCell(sheet6, m2, 65).setCellFormula(String.format("BA%d+BH%d", m2+1, m2+1));
            getCell(sheet6, m2, 67).setCellFormula(String.format("AO%d-BN%d", m2+1, m2+1));
            getCell(sheet6, m2, 69).setCellFormula(String.format("IFERROR(BP%d/AO%d,0)", m2+1, m2+1));
            n2+=1;
        }

        // 구분.기간별 조치 방안 표 Total
        getCell(sheet6, 120-1-1-1-2-4, 40).setCellFormula("SUM(AO94:AQ111)");
        getCell(sheet6, 120-1-1-1-2-4, 43).setCellFormula("SUM(AR94:AU111)");
        getCell(sheet6, 120-1-1-1-2-4, 47).setCellFormula("SUM(AV94:AY111)");
        getCell(sheet6, 120-1-1-1-2-4, 52).setCellFormula("SUM(BA94:BC111)");
        getCell(sheet6, 120-1-1-1-2-4, 55).setCellFormula("IFERROR(BA112/AO112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 59).setCellFormula("SUM(BH94:BJ111)");
        getCell(sheet6, 120-1-1-1-2-4, 62).setCellFormula("IFERROR(BH112/AO112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 65).setCellFormula("SUM(BN94:BO111)");
        getCell(sheet6, 120-1-1-1-2-4, 67).setCellFormula("SUM(BP94:BQ111)");
        getCell(sheet6, 120-1-1-1-2-4, 69).setCellFormula("IFERROR(BP112/AO112,0)");

        // 즉시 조치 필요 대상시스템 (TOP 10). 테이블
        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList2 = testService.getReportResultProcessTargets(gAssetSwJob1, false);

        snetAssetSwAuditExcelDtoList2.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate).reversed());

        snetAssetSwAuditExcelDtoList2 = snetAssetSwAuditExcelDtoList2.stream().limit(10).collect(Collectors.toList());

        int bb2 = 0;
        if (snetAssetSwAuditExcelDtoList2 != null && snetAssetSwAuditExcelDtoList2.size() > 0) {

            for (int aa2 = 126-1-1-1-2-4; aa2 < 126-1-1-1-2-4 + snetAssetSwAuditExcelDtoList2.size(); aa2++) {

                getCell(sheet6, aa2, 186).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getHostNm());
                getCell(sheet6, aa2, 187).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getAuditRate());

                if (snetAssetSwAuditExcelDtoList.get(bb2).getWeekHighRate() == 0.0) {

                    getCell(sheet6, aa2, 188).setCellValue("");
                } else {

                    getCell(sheet6, aa2, 188).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getWeekHighRate());
                }

                if (snetAssetSwAuditExcelDtoList.get(bb2).getWeekMiddleLowRate() == 0.0) {

                    getCell(sheet6, aa2, 189).setCellValue("");
                } else {

                    getCell(sheet6, aa2, 189).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getWeekMiddleLowRate());
                }
                bb2++;
            }
        }

        // 취약점 상세 항목. 테이블. 이행점검
        List<SnetAssetSwAuditReportDto> assetSwAuditReportDtoList2 = testService.selectAssetSwAuditReportNew(gAssetSwJob1, false);

        if (assetSwAuditReportDtoList2 != null && assetSwAuditReportDtoList2.size() > 0) {

            int dd2 = 0;
            for (int cc2 = 157-1-1-1-2-4; cc2 < 157-1-1-1-2-4 + assetSwAuditReportDtoList2.size(); cc2++) {

                getCell(sheet6, cc2, 36).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemGrpNm());
                getCell(sheet6, cc2, 41).setCellValue(assetSwAuditReportDtoList2.get(dd2).getDivideNum());
                getCell(sheet6, cc2, 44).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemNmDesc());
                getCell(sheet6, cc2, 61).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemGradeView());
                getCell(sheet6, cc2, 63).setCellValue(assetSwAuditReportDtoList2.get(dd2).getHostCount());
                getCell(sheet6, cc2, 64).setCellValue(assetSwAuditReportDtoList2.get(dd2).getHostCount());
                getCell(sheet6, cc2, 65).setCellValue(assetSwAuditReportDtoList2.get(dd2).getEtc());
                dd2++;
            } // end for assetSwAuditReportDtoList2
//            for (int cc = 157; cc < 157 + assetSwAuditReportDtoList2.size(); cc++) {
//
//                for (int ff = 36; ff <= 65; ff++) {
//                    getCell(sheet6, cc, ff).setCellStyle(cellStyle(gWorkbook, 10)); // style
//                }
//            } // end for style
        }

        // 취약점 상세 항목 가이드. 테이블. 이행점검
        if (assetSwAuditReportDtoList2 != null && assetSwAuditReportDtoList2.size() > 0) {
            int dd = 0;
            for (int cc = 211-1-1-1-2-4; cc < 211-1-1-1-2-4 + assetSwAuditReportDtoList2.size(); cc++) {

//                getCell(sheet6, cc, 36).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemGrpNm());
//                getCell(sheet6, cc, 41).setCellValue(assetSwAuditReportDtoList2.get(dd).getDivideNum());
//                getCell(sheet6, cc, 44).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemStandard());
//                getCell(sheet6, cc, 61).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());

                String itemCMstr = assetSwAuditReportDtoList2.get(dd).getItemCountermeasure();
                String[] chars = null;
                boolean autoRowHeightFlag = false;
                short charsLength = 1;
                try {
                    chars = itemCMstr.split("\n");

                    charsLength = (short) (chars.length);
                    autoRowHeightFlag = true;
                } catch (Exception e) {

                    autoRowHeightFlag = false;
                }

                getCell(sheet6, cc, 36).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 41).setCellValue(assetSwAuditReportDtoList2.get(dd).getDivideNum());
                getCell(sheet6, cc, 44).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemStandard());
                if (autoRowHeightFlag) {
                    getCellAutoRowHeight(sheet6, cc, 61, charsLength).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());
                } else {
                    getCell(sheet6, cc, 61).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());
                }

                dd++;
            } // end for
        } // end if

        /* end Windows*/
    } // end method createTotalReportTargets2

    // Linux
    private void createTotalReportTargets1(XSSFWorkbook gWorkbook, List<TotalOverallResultDto> checkTargetsList, Map gAssetSwJob1, RestTemplate rest) throws Exception {

        logger.info("*[Linux]");
        XSSFSheet sheet6 = gWorkbook.getSheet("Linux");
        // 점검군별 (Linux, Window, DB, WEB, WAS, NW, Web App(URL), 기타)

        // (1) 보안점검 Linux

        // 현황
        String statusStr = "CONCATENATE(\" 서버 보안점검 결과, 전체 점검대상(\",D32,\"EA)의 평균 보안수준은 \",AB56,\"입니다. 분류 항목 중 보안 설정이 가장 양호하게 설정된 분류 항목은 \",\"\"\"\",INDEX(B38:B55,MATCH(MAX(AB38:AB55),AB38:AB55,0),1),\"\"\"\",\"로 보안준수율은 \", MAX(AB38:AB55),\"점이며 \",\"\"\"\",INDEX(B38:B55,MATCH(MIN(AB38:AB55),AB38:AB55,0),1),\"\"\"\",\" 항목이 \",MIN(AB38:AB55),\"점으로 보안 설정이 가장 미흡하게 설정되어 있습니다.\")";
        getCell(sheet6, 11-1-1-1-2, 5).setCellFormula(statusStr);

        String statusMigStr = "CONCATENATE(\" 서버 이행점검 결과, 전체 점검대상(\",AM32,\"EA)의 평균 보안수준은 \",BD56,\"에서 \",BO56,\"으로 변경되었습니다. 보안설정이 가장 미흡하게 설정되어 있던 \",\"\"\"\",INDEX(AK38:AK55,MATCH(MIN(BD38:BD55),BD38:BD55,0),1),\"\"\"\",\" 분류 항목이 \", MIN(BD38:BD55),\"점에서 \",INDEX(BO38:BO55,MATCH(MIN(BD38:BD55),BD38:BD55,0),1),\"점으로 보안준수율이 변경되었으며, 현재 보안준수율이 가장 낮은 분류 항목으로는 \",\"\"\"\",INDEX(AK38:AK55,MATCH(MIN(BO38:BO55),BO38:BO55,0),1),\"\"\"\",\"으로 보안준수율은 \",MIN(BO38:BO55),\"점 입니다.\")";
        getCell(sheet6, 11-1-1-1-2, 40).setCellFormula(statusMigStr);

        List<DetailResultDto> linuxTargetsList = testService.getDetailTargetsList(checkTargetsList, gAssetSwJob1, "Linux", true);

//        // -> to do test : 보고서 API
//        List<DetailResultDto> linuxTargetsListReportApi = Lists.newArrayList();
//        List<DetailReportHostNmResultDto> resultHostNmList = Lists.newArrayList();
//        List<DetailReportResultDto> resultDtoList = Lists.newArrayList();
//        {
//
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
//            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//            String userId = gAssetSwJob1.get("REQ_USER").toString();
//            logger.info("*[rest.exchange] userId : {}", userId);
//            String swTypeNmKeyStr = "1";
//            ApiRequestDto apiRequestDto = ApiRequestDto.builder()
//                    .userId(userId)
//                    .swType(swTypeNmKeyStr)
//                    .startDay(gAssetSwJob1.get("startAuditDay").toString())
//                    .endDay(gAssetSwJob1.get("endAuditDay").toString())
//                    .auditType(gAssetSwJob1.get("auditType").toString())
//                    .build();
//            logger.info("*[rest.exchange] apiRequestDto : {}", apiRequestDto);
//
//            UriComponentsBuilder builderDetail = UriComponentsBuilder.fromHttpUrl(DETAIL_SECU_URL)
//                    .queryParam("restApiKey", REST_API_KEY)
//                    ;
//            HttpEntity<ApiRequestDto> httpEntity = new HttpEntity<>(apiRequestDto, httpHeaders);
//            ApiResult apiResult = null;
//            try {
//
//                apiResult = rest.exchange(builderDetail.toUriString(), HttpMethod.POST, httpEntity, ApiResult.class).getBody();
//
//                if (apiResult != null) {
//
//                    logger.info("*[ApiResult] resultCode         : {}", apiResult.getResultCode());
//                    logger.info("*[ApiResult] returnType         : {}", apiResult.getReturnType());
//                    logger.info("*[ApiResult] resultMessageCode  : {}", apiResult.getResultMessageCode());
//                    logger.info("*[ApiResult] resultData         : {}", apiResult.getResultData());
//
//                    ModelMapper modelMapper = new ModelMapper();
//
//                    DetailReportDto tempDto = modelMapper.map(apiResult.getResultData(), DetailReportDto.class);
//                    logger.info("*[DetailReportDto] tempDto : {}", tempDto);
//
//                    resultHostNmList = tempDto.getResultHostNmList().stream().collect(Collectors.toList());
//                    resultDtoList = tempDto.getResultDtoList().stream().collect(Collectors.toList());
//                }
//            } catch (Exception e) {
//
//                logger.error(e.toString());
//            }
//        } // end api client
//
//        logger.info("*[---------------------------------------------------------------------------------------------]");
//        logger.info("*[점검군별 점검결과 (보안점검)] 보고서 API 호출");

        if (linuxTargetsList == null || linuxTargetsList.size() < 1) {

            throw new ErrorException("NoSuchElementException: No value");
        }

        // 점검수량 (EA)
        for (DetailResultDto dto : linuxTargetsList) {

            getCell(sheet6, 40-1-1-1-2-4, 3).setCellValue(dto.getValue1());
//            getCell(sheet6, 64-1-1-1-2-4, 5).setCellValue(dto.getValue16());
//            getCell(sheet6, 64-1-1-1-2-4, 8).setCellValue(dto.getValue16());
//        -> 현재는 개별항목수 x 점검수량 개수로 맞춤
            break;
        }

        // 양호 상중하Total.테이블
        int t1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 20).setCellValue(linuxTargetsList.get(t1).getValue17());
            getCell(sheet6, k2, 21).setCellValue(linuxTargetsList.get(t1).getValue18());
            getCell(sheet6, k2, 22).setCellValue(linuxTargetsList.get(t1).getValue19());
            t1++;
        }

        // 인터뷰필요 상중하Total.테이블
        int i1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 24).setCellValue(linuxTargetsList.get(i1).getValue20());
            getCell(sheet6, k2, 25).setCellValue(linuxTargetsList.get(i1).getValue21());
            getCell(sheet6, k2, 26).setCellValue(linuxTargetsList.get(i1).getValue22());
            i1++;
        }
        // 불가 상중하Total.테이블
        int d1 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + linuxTargetsList.size(); k2++) {

            getCell(sheet6, k2, 28).setCellValue(linuxTargetsList.get(d1).getValue23());
            getCell(sheet6, k2, 29).setCellValue(linuxTargetsList.get(d1).getValue24());
            getCell(sheet6, k2, 30).setCellValue(linuxTargetsList.get(d1).getValue25());
            d1++;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h = 0;
        int h2 = 23-1-1-1-2-4;
        for (int g = 46-1-1-1-2-4; g < 46-1-1-1-2-4 + (linuxTargetsList.size()); g+=1) {

            getCell(sheet6, g, 1).setCellValue(linuxTargetsList.get(h).getKey());       // 분류항목
            getCell(sheet6, g, 5).setCellValue(linuxTargetsList.get(h).getValue2());    // 개별항목수
            getCell(sheet6, g, 8).setCellValue(linuxTargetsList.get(h).getValue3());    // 전체항목수
            getCell(sheet6, g, 11).setCellValue(linuxTargetsList.get(h).getValue4());   // 상
            getCell(sheet6, g, 15).setCellValue(linuxTargetsList.get(h).getValue5());   // 중
            getCell(sheet6, g, 19).setCellValue(linuxTargetsList.get(h).getValue6());   // 하
            getCell(sheet6, g, 23).setCellValue(linuxTargetsList.get(h).getValue7());   // Total

            // 보안준수율 (웹UI 의 audit_rate 산정식을 기반으로 다시 계산함)
            getCell(sheet6, g, 27).setCellFormula(String.format(
                    "IF(F%d=0,100,ROUND(IFERROR((U%d*3+V%d*2+W%d*1) / ((U%d*3+V%d*2+W%d*1)+(L%d*3+P%d*2+T%d*1)+(Y%d*3+Z%d*2+AA%d*1)+(AC%d*3+AD%d*2+AE%d*1))*100,0),2))"
                    ,g+1
                    ,h2+1,h2+1,h2+1
                    ,h2+1,h2+1,h2+1
                    ,g+1,g+1,g+1
                    ,h2+1,h2+1,h2+1
                    ,h2+1,h2+1,h2+1)
            );
            // 보안수준
            getCell(sheet6, g, 31).setCellFormula(String.format("IF(AB%d>=90,\"우수\",IF(AND(AB%d>=80,AB%d<90),\"양호\",IF(AND(AB%d>=70,AB%d<80),\"보통\",\"미흡\")))"
                    , g + 1, g + 1, g + 1, g + 1, g + 1));
            h++;
            h2++;
        }

        // 전체 점검 개수(N/A 제외) Total
        getCell(sheet6, 33-1-1-2, 5).setCellFormula("SUM(F15:F29)");  // 25
        getCell(sheet6, 33-1-1-2, 8).setCellFormula("SUM(I15:I29)");  // 16
        getCell(sheet6, 33-1-1-2, 11).setCellFormula("SUM(L15:L29)"); // 5
        getCell(sheet6, 33-1-1-2, 14).setCellFormula("SUM(O15:O29)"); // 0

        getCell(sheet6, 33-1-1-2, 20).setCellFormula("SUM(U15:U20)"); // 0
        getCell(sheet6, 33-1-1-2, 21).setCellFormula("SUM(V15:V20)"); // 0
        getCell(sheet6, 33-1-1-2, 22).setCellFormula("SUM(W15:W20)"); // 0

        getCell(sheet6, 33-1-1-2, 24).setCellFormula("SUM(Y15:Y20)"); // 0
        getCell(sheet6, 33-1-1-2, 25).setCellFormula("SUM(Z15:Z20)"); // 0
        getCell(sheet6, 33-1-1-2, 26).setCellFormula("SUM(AA15:AA20)"); // 0

        getCell(sheet6, 33-1-1-2, 28).setCellFormula("SUM(AC15:AC20)"); // 0
        getCell(sheet6, 33-1-1-2, 29).setCellFormula("SUM(AD15:AD20)"); // 0
        getCell(sheet6, 33-1-1-2, 30).setCellFormula("SUM(AE15:AE20)"); // 0

        /*
            Total. 합계. 점검항목수
         */
        getCell(sheet6, 64-1-1-1-2-4, 5).setCellFormula("SUM(F38:H55)");
        getCell(sheet6, 64-1-1-1-2-4, 8).setCellFormula("SUM(I38:K55)");
//        getCell(sheet6, 64, 8).setCellFormula("SUM(I38:K55)");
//        -> value16 으로 대체
//        -> 현재는 개별항목수 x 점검수량 개수로 맞춤

        getCell(sheet6, 64-1-1-1-2-4, 11).setCellFormula("IFERROR(SUM(L38:O55),0)"); // 6
        getCell(sheet6, 64-1-1-1-2-4, 15).setCellFormula("IFERROR(SUM(P38:S55),0)"); // 3
        getCell(sheet6, 64-1-1-1-2-4, 19).setCellFormula("IFERROR(SUM(T38:W55),0)"); // 3
        getCell(sheet6, 64-1-1-1-2-4, 23).setCellFormula("SUM(L56:W57)");

        //보안준수율 Total
        getCell(sheet6, 64-1-1-1-2-4, 34).setCellFormula("ROUND(AVERAGEA(AB38:AE55),2)");

        double sCheckValue = 0.0;
        double tempSCheckValue1 = 0.0;
        double tempSCheckValue2 = 0.0;

        // 전체 점검 개수(N/A 제외) 상중하Total.테이블
        int value10 = 0;
        int value11 = 0;
        int value12 = 0;
        for (DetailResultDto detailResultDto : linuxTargetsList) {

            value10 += detailResultDto.getValue10();  // 상
            value11 += detailResultDto.getValue11();  // 중
            value12 += detailResultDto.getValue12();  // 하
        }
        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int value4 = 0;
        int value5 = 0;
        int value6 = 0;
        for (DetailResultDto detailResultDto : linuxTargetsList) {

            value4 += detailResultDto.getValue4();   // 상
            value5 += detailResultDto.getValue5();   // 중
            value6 += detailResultDto.getValue6();   // 하
        }

        // 보안준수율 에서 보안준수율 로 변경
        tempSCheckValue1 = ((value10*3+value11*2+value12*1)-(value4*3+value5*2+value6*1));
        tempSCheckValue2 = (value10*3+value11*2+value12*1);
        sCheckValue = (tempSCheckValue1/tempSCheckValue2) * 100;
        sCheckValue = Math.round( sCheckValue * 100d ) / 100d;

        XSSFSheet sheet5 = gWorkbook.getSheet("Ⅳ. 전체종합결과");

        getCell(sheet6, 64-1-1-1-2-4, 27).setCellFormula("IF(AI56>=90,\"우수\",IF(AND(AI56>=80,AI56<90),\"양호\",IF(AND(AI56>=70,AI56<80),\"보통\",\"미흡\")))");

        /*
            Total. 평균. 점검결과
         */
        getCell(sheet6, 66-1-1-1-2-4, 11).setCellFormula("ROUND(IFERROR((U30*3)/((U30*3)+(L56*3)+(Y30*3)+(AC30*3))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 15).setCellFormula("ROUND(IFERROR((V30*2)/((V30*2)+(P56*2)+(Z30*2)+(AD30*2))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 19).setCellFormula("ROUND(IFERROR((W30*1)/((W30*1)+(T56*1)+(AA30*1)+(AE30*1))*100,0),2)");

        // 발견된 취약점 기간별 조치 방안
        // 현황 [74, 5]
        String statusStr2 = "CONCATENATE(\" 발견된 취약한 항목 중 위험도 \",\"\"\"\",\"상\",\"\"\"\",\"에 해당하는 즉시 조치가 필요한 항목의 비율은 평균 \",ROUND(Q112*100,1),\"%를 차지하고 있으며, 위험도 \",\"\"\"\",\"중\",\"\"\"\",\",\",\"\"\"\",\"하\",\"\"\"\",\"에 해당하는 중장기적으로 조치가 필요한 항목의 비율은 \",ROUND(AC112*100,1),\"%로 분석 되었습니다. 또한 즉시 조치가 필요한 항목 중, 가장 높은 비율을 차지하고 있는 상위 10개의 점검 대상은 아래와 같습니다.\")";
        getCell(sheet6, 74-1-1-1-2-4, 5).setCellFormula(statusStr2);
        // 현황 [74, 40]
        String statusMigStr2 = "CONCATENATE(\" 발견된 취약한 항목 중 평균 조치율은 \",ROUND(BR112*100,1),\"%로써 위험도 \",\"\"\"\",\"상\",\"\"\"\",\"에 해당하는 즉시 조치가 필요한 항목의 비율은 평균 \",ROUND(IFERROR(AR112/AO112*100,0),1),\"%에서 \",ROUND(BD112*100,1),\"%로 감소하였으며 위험도 \",\"\"\"\",\"중\",\"\"\"\",\",\",\"\"\"\",\"하\",\"\"\"\",\"에 해당하는 중장기적으로 조치가 필요한 항목의 비율은 \",ROUND(IFERROR(AV112/AO112*100,0),1),\"%에서 \",ROUND(BK112*100,1),\"%로 감소한 것으로 분석 되었습니다. 또한 잔여 취약점이 존재하는 대상 중, 즉시 조치가 필요한 항목이 높은 비율을 차지하고 있는 상위 10개의 점검 대상은 아래와 같습니다.\")";
        getCell(sheet6, 74-1-1-1-2-4, 40).setCellFormula(statusMigStr2);

        // 구분, 발견된취약점 수, 기간별 조치 방안 표
        int jj = 0;
        int kk = 47-1-1-1-2-4;
        int ll = 103-1-1-1-2-4;
        for (int ii = 102-1-1-1-2-4; ii < 102-1-1-1-2-4 + (linuxTargetsList.size()); ii+=1) {

            getCell(sheet6, ii, 1).setCellValue(linuxTargetsList.get(jj).getKey());

            getCell(sheet6, ii, 5).setCellFormula(String.format("X%d", kk));

            getCell(sheet6, ii, 10).setCellFormula(String.format("L%d", kk));

            getCell(sheet6, ii, 16).setCellFormula(String.format("IFERROR(K%d/F%d,0)", ll, ll));

            getCell(sheet6, ii, 22).setCellFormula(String.format("P%d+T%d", kk, kk));

            getCell(sheet6, ii, 28).setCellFormula(String.format("IFERROR(W%d/F%d,0)", ll, ll));
            jj++;
            kk += 1;
            ll += 1;
        }

        // 구분, 발견된취약점 수, 기간별 조치 방안 표 Total
        getCell(sheet6, 120-1-1-1-2-4, 5).setCellFormula("SUM(F94:J111)");
        getCell(sheet6, 120-1-1-1-2-4, 10).setCellFormula("SUM(K94:P111)");
        getCell(sheet6, 120-1-1-1-2-4, 16).setCellFormula("IFERROR(K112/F112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 22).setCellFormula("SUM(W94:AB111)");
        getCell(sheet6, 120-1-1-1-2-4, 28).setCellFormula("IFERROR(W112/F112,0)");

        // 즉시 조치 필요 대상시스템 (TOP 10). 테이블
        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList = testService.getReportResultProcessTargets(gAssetSwJob1, true);

//        // -> to do test : 보고서 API
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(DETAIL_IMPL_URL)
//                .queryParam("restApiKey", REST_API_KEY)
//                ;
//
//        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList1ReportApi = Lists.newArrayList();
//        List<?> tempList1 = Lists.newArrayList();
//        List<ImpkChkOfTargetSystemRequireImdActionDto> imdActionDtoList1 = Lists.newArrayList();
//        {
//
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
//            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//            String userId = gAssetSwJob1.get("REQ_USER").toString();
//            logger.info("*[rest.exchange] userId : {}", userId);
//            String swTypeNmKeyStr = "1";
//            ApiRequestDto apiRequestDto = ApiRequestDto.builder()
//                    .userId(userId)
//                    .swType(swTypeNmKeyStr)
//                    .startDay(gAssetSwJob1.get("startAuditDay").toString())
//                    .endDay(gAssetSwJob1.get("endAuditDay").toString())
//                    .auditType(gAssetSwJob1.get("auditType").toString())
//                    .build();
//            logger.info("*[rest.exchange] apiRequestDto : {}", apiRequestDto);
//
//            UriComponentsBuilder builderSecu = UriComponentsBuilder.fromHttpUrl(TOTAL_SECU_ACT_URL)
//                    .queryParam("restApiKey", REST_API_KEY)
//                    ;
//            HttpEntity<ApiRequestDto> httpEntity = new HttpEntity<>(apiRequestDto, httpHeaders);
//            ApiResult apiResult = null;
//            try {
//
//                apiResult = rest.exchange(builderSecu.toUriString(), HttpMethod.POST, httpEntity, ApiResult.class).getBody();
//
//                if (apiResult != null) {
//
//                    logger.info("*[ApiResult] resultCode         : {}", apiResult.getResultCode());
//                    logger.info("*[ApiResult] returnType         : {}", apiResult.getReturnType());
//                    logger.info("*[ApiResult] resultMessageCode  : {}", apiResult.getResultMessageCode());
//                    logger.info("*[ApiResult] resultData         : {}", apiResult.getResultData());
//
//                    // object convertTo List<?>
//                    if (apiResult.getResultData() instanceof Collection) {
//
//                        tempList1 = new ArrayList<>((Collection<?>)apiResult.getResultData());
//                    } // end if
//                }
//            } catch (Exception e) {
//
//                logger.error(e.toString());
//            }
//        } // end api client
//
//        logger.info("*[---------------------------------------------------------------------------------------------]");
//        logger.info("*[즉시 조치 필요 대상 시스템 (보안점검)] 보고서 API 호출");
//
//        for (int i = 0; i < tempList1.size(); i++) {
//
//            ModelMapper modelMapper = new ModelMapper();
//
//            ImpkChkOfTargetSystemRequireImdActionDto tempDto = modelMapper.map(tempList1.get(i), ImpkChkOfTargetSystemRequireImdActionDto.class);
//            imdActionDtoList1.add(tempDto);
//        } // end for
//        logger.info("*[---------------------------------------------------------------------------------------------]");

        snetAssetSwAuditExcelDtoList.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate).reversed());

        snetAssetSwAuditExcelDtoList = snetAssetSwAuditExcelDtoList.stream().limit(10).collect(Collectors.toList());

        int bb = 0;
        if (snetAssetSwAuditExcelDtoList != null && snetAssetSwAuditExcelDtoList.size() > 0) {
            for (int aa = 126-1-1-1-2-4; aa < 126-1-1-1-2-4 + snetAssetSwAuditExcelDtoList.size(); aa++) {

                getCell(sheet6, aa, 182).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getHostNm());
                getCell(sheet6, aa, 183).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getAuditRate());

                if (snetAssetSwAuditExcelDtoList.get(bb).getWeekHighRate() == 0.0) {

                    getCell(sheet6, aa, 184).setCellValue("");
                } else {

                    getCell(sheet6, aa, 184).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getWeekHighRate());
                }

                if (snetAssetSwAuditExcelDtoList.get(bb).getWeekMiddleLowRate() == 0.0) {

                    getCell(sheet6, aa, 185).setCellValue("");
                } else {

                    getCell(sheet6, aa, 185).setCellValue(snetAssetSwAuditExcelDtoList.get(bb).getWeekMiddleLowRate());
                }
                bb++;
            }
        }

        // 취약점 상세 항목. 테이블. 보안점검
        // 취약점 항목 분류항목 그룹핑 수정
        List<SnetAssetSwAuditReportDto> assetSwAuditReportDtoList = testService.selectAssetSwAuditReportNew(gAssetSwJob1, true);

        if (assetSwAuditReportDtoList != null && assetSwAuditReportDtoList.size() > 0) {
            int dd = 0;
            for (int cc = 157-1-1-1-2-4; cc < 157-1-1-1-2-4 + assetSwAuditReportDtoList.size(); cc++) {

                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemNmDesc());
                getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGradeView());
                getCell(sheet6, cc, 26).setCellValue(assetSwAuditReportDtoList.get(dd).getHostCount());
                getCell(sheet6, cc, 28).setCellValue(assetSwAuditReportDtoList.get(dd).getEtc());
                dd++;
            } // end for assetSwAuditReportDtoList
//            for (int cc = 157; cc < 157 + assetSwAuditReportDtoList.size(); cc++) {
//
//                for (int ee = 1; ee <= 33; ee++) {
//                    getCell(sheet6, cc, ee).setCellStyle(cellStyle(gWorkbook, 10)); // style
//                }
//            } // end for style
        } // end if

        // 취약점 상세 항목 가이드. 테이블. 보안점검
        int colWithinchars = (sheet6.getColumnWidth(2) + sheet6.getColumnWidth(3));
        if (assetSwAuditReportDtoList != null && assetSwAuditReportDtoList.size() > 0) {
            int dd = 0;
            for (int cc = 211-1-1-1-2-4; cc < 211-1-1-1-2-4 + assetSwAuditReportDtoList.size(); cc++) {

//                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
//                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
//                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemStandard());
//                getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());


                String itemCMstr = assetSwAuditReportDtoList.get(dd).getItemCountermeasure();
                String[] chars = null;
                boolean autoRowHeightFlag = false;
                short charsLength = 1;
                try {
                    chars = itemCMstr.split("\n");

                    charsLength = (short) (chars.length);
                    autoRowHeightFlag = true;
                } catch (Exception e) {

                    autoRowHeightFlag = false;
                }


                getCell(sheet6, cc, 1).setCellValue(assetSwAuditReportDtoList.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 6).setCellValue(assetSwAuditReportDtoList.get(dd).getDivideNum());
                getCell(sheet6, cc, 9).setCellValue(assetSwAuditReportDtoList.get(dd).getItemStandard());
                if (autoRowHeightFlag) {
                    getCellAutoRowHeight(sheet6, cc, 24, charsLength).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());
                } else {
                    getCell(sheet6, cc, 24).setCellValue(assetSwAuditReportDtoList.get(dd).getItemCountermeasure());
                }
                dd++;

            } // end for
        } // end if

        // (2) 이행점검 Linux
        List<DetailResultDto> migTargetsList = testService.getMigDetailTargetsList(checkTargetsList, gAssetSwJob1, "Linux", true);

//        // -> to do test : 보고서 API
//        List<DetailResultDto> migTargetsListReportApi = Lists.newArrayList();
//        List<DetailReportHostNmResultDto> resultHostNmMigList = Lists.newArrayList();
//        List<DetailReportResultDto> resultDtoMigList = Lists.newArrayList();
//        {
//
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
//            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//            String userId = gAssetSwJob1.get("REQ_USER").toString();
//            logger.info("*[rest.exchange] userId : {}", userId);
//            String swTypeNmKeyStr = "1";
//            ApiRequestDto apiRequestDto = ApiRequestDto.builder()
//                    .userId(userId)
//                    .swType(swTypeNmKeyStr)
//                    .startDay(gAssetSwJob1.get("startAuditDay").toString())
//                    .endDay(gAssetSwJob1.get("endAuditDay").toString())
//                    .auditType(gAssetSwJob1.get("auditType").toString())
//                    .build();
//            logger.info("*[rest.exchange] apiRequestDto : {}", apiRequestDto);
//
//            UriComponentsBuilder builderMig = UriComponentsBuilder.fromHttpUrl(DETAIL_IMPL_URL)
//                    .queryParam("restApiKey", REST_API_KEY)
//                    ;
//            HttpEntity<ApiRequestDto> httpEntity = new HttpEntity<>(apiRequestDto, httpHeaders);
//            ApiResult apiResult = null;
//            try {
//
//                apiResult = rest.exchange(builderMig.toUriString(), HttpMethod.POST, httpEntity, ApiResult.class).getBody();
//
//                if (apiResult != null) {
//
//                    logger.info("*[ApiResult] resultCode         : {}", apiResult.getResultCode());
//                    logger.info("*[ApiResult] returnType         : {}", apiResult.getReturnType());
//                    logger.info("*[ApiResult] resultMessageCode  : {}", apiResult.getResultMessageCode());
//                    logger.info("*[ApiResult] resultData         : {}", apiResult.getResultData());
//
//                    ModelMapper modelMapper = new ModelMapper();
//
//                    DetailReportDto tempDto = modelMapper.map(apiResult.getResultData(), DetailReportDto.class);
//
//                    resultHostNmMigList = tempDto.getResultHostNmList().stream().collect(Collectors.toList());
//                    resultDtoMigList = tempDto.getResultDtoList().stream().collect(Collectors.toList());
//                }
//            } catch (Exception e) {
//
//                logger.error(e.toString());
//            }
//        } // end api client
//
//        logger.info("*[---------------------------------------------------------------------------------------------]");
//        logger.info("*[점검군별 점검결과 (이행점검)] 보고서 API 호출");

        // 점검수량 (EA)
        getCell(sheet6, 40-1-1-1-2-4, 38).setCellFormula("D32");

        // 양호 상중하Total.테이블
        int t2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 36).setCellValue(migTargetsList.get(t2).getValue17());
            getCell(sheet6, k2, 37).setCellValue(migTargetsList.get(t2).getValue18());
            getCell(sheet6, k2, 38).setCellValue(migTargetsList.get(t2).getValue19());
            t2++;
        }

        // 인터뷰필요 상중하Total.테이블
        int i2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 40).setCellValue(migTargetsList.get(i2).getValue20());
            getCell(sheet6, k2, 41).setCellValue(migTargetsList.get(i2).getValue21());
            getCell(sheet6, k2, 42).setCellValue(migTargetsList.get(i2).getValue22());
            i2++;
        }
        // 불가 상중하Total.테이블
        int d2 = 0;
        for (int k2 = 23-1-1-1-2-4; k2 < 23-1-1-1-2-4 + migTargetsList.size(); k2++) {

            getCell(sheet6, k2, 44).setCellValue(migTargetsList.get(d2).getValue23());
            getCell(sheet6, k2, 45).setCellValue(migTargetsList.get(d2).getValue24());
            getCell(sheet6, k2, 46).setCellValue(migTargetsList.get(d2).getValue25());
            d2++;
        }

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int h3 = 0;
        int h4 = 47-1-1-1-2-4;
        for (int g2 = 46-1-1-1-2-4; g2 < 46-1-1-1-2-4 + (linuxTargetsList.size()); g2+=1) {

            getCell(sheet6, g2, 36).setCellValue(linuxTargetsList.get(h3).getKey());
            getCell(sheet6, g2, 40).setCellFormula(String.format("F%d", g2 + 1));
            getCell(sheet6, g2, 43).setCellFormula(String.format("I%d", g2 + 1));
            getCell(sheet6, g2, 46).setCellFormula(String.format("L%d", g2 + 1));
            getCell(sheet6, g2, 48).setCellFormula(String.format("P%d", g2 + 1));
            getCell(sheet6, g2, 50).setCellFormula(String.format("T%d", g2 + 1));
            getCell(sheet6, g2, 53).setCellFormula(String.format("SUM(AU%d:BA%d)", g2 + 1, g2 + 1));

            // 보안준수율
            getCell(sheet6, g2, 55).setCellFormula(String.format("AB%d", g2 + 1));

            h3++;
            h4++;
        }

        int h5 = 0;
        int h6 = 23-1-1-1-2-4;
        for (int g2 = 46-1-1-1-2-4; g2 < 46-1-1-1-2-4 + (migTargetsList.size()); g2+=1) {
            // 이행점검
            // 상
            getCell(sheet6, g2, 58).setCellValue(migTargetsList.get(h5).getValue4());
            // 중
            getCell(sheet6, g2, 60).setCellValue(migTargetsList.get(h5).getValue5());
            // 하
            getCell(sheet6, g2, 62).setCellValue(migTargetsList.get(h5).getValue6());
            // Total
            getCell(sheet6, g2, 64).setCellFormula(String.format("SUM(BG%d:BL%d)",g2+1, g2+1));

            // 전체 점검 개수(N/A 제외) 상중하Total.테이블 옆에 위치
            //      보안준수율   :
            // 이행점검 진단일에 대한 개수 반영해야 함
            getCell(sheet6, g2, 66).setCellFormula(String.format(
                    "IF(AO%d=0,100,ROUND(IFERROR((AK%d*3+AL%d*2+AM%d*1) / ((AK%d*3+AL%d*2+AM%d*1)+(BG%d*3+BI%d*2+BK%d*1)+(AO%d*3+AP%d*2+AQ%d*1)+(AS%d*3+AT%d*2+AU%d*1))*100,0),2))"
                    ,g2+1
                    ,h6+1,h6+1,h6+1
                    ,h6+1,h6+1,h6+1
                    ,g2+1,g2+1,g2+1
                    ,h6+1,h6+1,h6+1
                    ,h6+1,h6+1,h6+1)
            );

            //      보안수준   :
            getCell(sheet6, g2, 68).setCellFormula(String.format("IF(BO%d>=90,\"우수\",IF(AND(BO%d>=80,BO%d<90),\"양호\",IF(AND(BO%d>=70,BO%d<80),\"보통\",\"미흡\")))"
                    ,g2+1,g2+1,g2+1,g2+1,g2+1));
            h5++;
            h6++;
        }

        getCell(sheet6, 33-1-1-2, 36).setCellFormula("SUM(AK15:AK20)"); // 20
        getCell(sheet6, 33-1-1-2, 37).setCellFormula("SUM(AL15:AL20)"); // 12
        getCell(sheet6, 33-1-1-2, 38).setCellFormula("SUM(AM15:AM20)"); // 2

        getCell(sheet6, 33-1-1-2, 40).setCellFormula("SUM(AO15:AO20)"); // 0
        getCell(sheet6, 33-1-1-2, 41).setCellFormula("SUM(AP15:AP20)"); // 1
        getCell(sheet6, 33-1-1-2, 42).setCellFormula("SUM(AQ15:AQ20)"); // 0

        getCell(sheet6, 33-1-1-2, 44).setCellFormula("SUM(AS15:AS20)"); // 0
        getCell(sheet6, 33-1-1-2, 45).setCellFormula("SUM(AT15:AT20)"); // 0
        getCell(sheet6, 33-1-1-2, 46).setCellFormula("SUM(AU15:AU20)"); // 0
        // Total
        //      합계 :
        getCell(sheet6, 64-1-1-1-2-4, 40).setCellFormula("F56");
        getCell(sheet6, 64-1-1-1-2-4, 43).setCellFormula("I56");
        getCell(sheet6, 64-1-1-1-2-4, 46).setCellFormula("SUM(AU38:AV55)");
        getCell(sheet6, 64-1-1-1-2-4, 48).setCellFormula("SUM(AW38:AX55)");
        getCell(sheet6, 64-1-1-1-2-4, 50).setCellFormula("SUM(AY38:BA55)");
        getCell(sheet6, 64-1-1-1-2-4, 53).setCellFormula("SUM(BB38:BC55)");
        getCell(sheet6, 64-1-1-1-2-4, 55).setCellFormula("AB56");

        //          - 이행점검
        getCell(sheet6, 64-1-1-1-2-4, 58).setCellFormula("IFERROR(SUM(BG38:BH55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 60).setCellFormula("IFERROR(SUM(BI38:BJ55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 62).setCellFormula("IFERROR(SUM(BK38:BL55),0)");
        getCell(sheet6, 64-1-1-1-2-4, 64).setCellFormula("SUM(BM38:BN55)");

        //보안준수율 Total
        getCell(sheet6, 64-1-1-1-2-4, 71).setCellFormula("ROUND(AVERAGEA(BO38:BP55),2)");

        // 분류항목, 점검 항목 수, 점검 결과.발견된 취약점 수
        int pvalue4 = 0;
        int pvalue5 = 0;
        int pvalue6 = 0;
        int pvalue13 = 0;
        int pvalue14 = 0;
        int pvalue15 = 0;
        for (DetailResultDto detailResultDto : migTargetsList) {

            pvalue4 += detailResultDto.getValue4();   // 상.취약점
            pvalue5 += detailResultDto.getValue5();   // 중.취약점
            pvalue6 += detailResultDto.getValue6();   // 하.취약점
        }

        getCell(sheet6, 64-1-1-1-2-4, 66).setCellFormula("IF(BT56>=90,\"우수\",IF(AND(BT56>=80,BT56<90),\"양호\",IF(AND(BT56>=70,BT56<80),\"보통\",\"미흡\")))");

        //      평균 :
        getCell(sheet6, 66-1-1-1-2-4, 46).setCellFormula("L58");
        getCell(sheet6, 66-1-1-1-2-4, 48).setCellFormula("P58");
        getCell(sheet6, 66-1-1-1-2-4, 50).setCellFormula("T58");

        //     - 이행점검 Total. 평균. 점검결과
        // 이행점검 진단일에 대한 개수 반영해야 함
        getCell(sheet6, 66-1-1-1-2-4, 58).setCellFormula("ROUND(IFERROR((AK30*3)/((AK30*3)+(BG56*3)+(AO30*3)+(AS30*3))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 60).setCellFormula("ROUND(IFERROR((AL30*2)/((AL30*2)+(BI56*2)+(AP30*2)+(AT30*2))*100,0),2)");
        getCell(sheet6, 66-1-1-1-2-4, 62).setCellFormula("ROUND(IFERROR((AM30*1)/((AM30*1)+(BK56*1)+(AQ30*1)+(AU30*1))*100,0),2)");


        // 발견된 취약점 기간별 조치 방안
        // 구분.기간별 조치 방안 표
        int n1 = 47-1-1-1-2-4;
        for (int m1 = 102-1-1-1-2-4; m1 < 102-1-1-1-2-4 + (linuxTargetsList.size()); m1+=1) {
            getCell(sheet6, m1, 36).setCellFormula(String.format("B%d", m1+1));
            getCell(sheet6, m1, 40).setCellFormula(String.format("BB%d", n1));
            getCell(sheet6, m1, 43).setCellFormula(String.format("AU%d", n1));
            getCell(sheet6, m1, 47).setCellFormula(String.format("AW%d+AY%d", n1, n1));
            n1+=1;
        }

        int n2 = 47-1-1-1-2-4;
        for (int m2 = 102-1-1-1-2-4; m2 < 102-1-1-1-2-4 + (migTargetsList.size()); m2+=1) {
            getCell(sheet6, m2, 52).setCellFormula(String.format("BG%d", n2));
            getCell(sheet6, m2, 55).setCellFormula(String.format("IFERROR(BA%d/AO%d,0)", m2+1, m2+1));
            getCell(sheet6, m2, 59).setCellFormula(String.format("BI%d+BK%d", n2, n2));
            getCell(sheet6, m2, 62).setCellFormula(String.format("IFERROR(BH%d/AO%d,0)", m2+1, m2+1));
            getCell(sheet6, m2, 65).setCellFormula(String.format("BA%d+BH%d", m2+1, m2+1));
            getCell(sheet6, m2, 67).setCellFormula(String.format("AO%d-BN%d", m2+1, m2+1));
            getCell(sheet6, m2, 69).setCellFormula(String.format("IFERROR(BP%d/AO%d,0)", m2+1, m2+1));
            n2+=1;
        }

        // 구분.기간별 조치 방안 표 Total
        getCell(sheet6, 120-1-1-1-2-4, 40).setCellFormula("SUM(AO94:AQ111)");
        getCell(sheet6, 120-1-1-1-2-4, 43).setCellFormula("SUM(AR94:AU111)");
        getCell(sheet6, 120-1-1-1-2-4, 47).setCellFormula("SUM(AV94:AY111)");
        getCell(sheet6, 120-1-1-1-2-4, 52).setCellFormula("SUM(BA94:BC111)");
        getCell(sheet6, 120-1-1-1-2-4, 55).setCellFormula("IFERROR(BA112/AO112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 59).setCellFormula("SUM(BH94:BJ111)");
        getCell(sheet6, 120-1-1-1-2-4, 62).setCellFormula("IFERROR(BH112/AO112,0)");
        getCell(sheet6, 120-1-1-1-2-4, 65).setCellFormula("SUM(BN94:BO111)");
        getCell(sheet6, 120-1-1-1-2-4, 67).setCellFormula("SUM(BP94:BQ111)");
        getCell(sheet6, 120-1-1-1-2-4, 69).setCellFormula("IFERROR(BP112/AO112,0)");

        // 즉시 조치 필요 대상시스템 (TOP 10). 테이블
        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList2 = testService.getReportResultProcessTargets(gAssetSwJob1, false);

//        // -> to do test : 보고서 API
//        List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList2ReportApi = Lists.newArrayList();
//        List<?> tempList2 = Lists.newArrayList();
//        List<ImpkChkOfTargetSystemRequireImdActionDto> imdActionDtoList2 = Lists.newArrayList();
//        {
//
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
//            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//            String userId = gAssetSwJob1.get("REQ_USER").toString();
//            logger.info("*[rest.exchange] userId : {}", userId);
//            String swTypeNmKeyStr = "1";
//            ApiRequestDto apiRequestDto = ApiRequestDto.builder()
//                    .userId(userId)
//                    .swType(swTypeNmKeyStr)
//                    .startDay(gAssetSwJob1.get("startAuditDay").toString())
//                    .endDay(gAssetSwJob1.get("endAuditDay").toString())
//                    .auditType(gAssetSwJob1.get("auditType").toString())
//                    .build();
//            logger.info("*[rest.exchange] apiRequestDto : {}", apiRequestDto);
//
//            UriComponentsBuilder builderImpl = UriComponentsBuilder.fromHttpUrl(TOTAL_IMPL_ACT_URL)
//                    .queryParam("restApiKey", REST_API_KEY)
//                    ;
//            HttpEntity<ApiRequestDto> httpEntity = new HttpEntity<>(apiRequestDto, httpHeaders);
//            ApiResult apiResult = null;
//            try {
//
//                apiResult = rest.exchange(builderImpl.toUriString(), HttpMethod.POST, httpEntity, ApiResult.class).getBody();
//
//                if (apiResult != null) {
//
//                    logger.info("*[ApiResult] resultCode         : {}", apiResult.getResultCode());
//                    logger.info("*[ApiResult] returnType         : {}", apiResult.getReturnType());
//                    logger.info("*[ApiResult] resultMessageCode  : {}", apiResult.getResultMessageCode());
//                    logger.info("*[ApiResult] resultData         : {}", apiResult.getResultData());
//
//                    // object convertTo List<?>
//                    if (apiResult.getResultData() instanceof Collection) {
//
//                        tempList2 = new ArrayList<>((Collection<?>)apiResult.getResultData());
//                    } // end if
//                }
//
//            } catch (Exception e) {
//
//                logger.error(e.toString());
//            }
//        } // end api client
//
//        logger.info("*[---------------------------------------------------------------------------------------------]");
//        logger.info("*[즉시 조치 필요 대상 시스템 (이행점검)] 보고서 API 호출");
//
//        for (int i = 0; i < tempList2.size(); i++) {
//
//            ModelMapper modelMapper = new ModelMapper();
//
//            ImpkChkOfTargetSystemRequireImdActionDto tempDto = modelMapper.map(tempList2.get(i), ImpkChkOfTargetSystemRequireImdActionDto.class);
//            imdActionDtoList2.add(tempDto);
//        } // end for
//        logger.info("*[---------------------------------------------------------------------------------------------]");

        snetAssetSwAuditExcelDtoList2.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate).reversed());

        snetAssetSwAuditExcelDtoList2 = snetAssetSwAuditExcelDtoList2.stream().limit(10).collect(Collectors.toList());

        int bb2 = 0;
        if (snetAssetSwAuditExcelDtoList2 != null && snetAssetSwAuditExcelDtoList2.size() > 0) {

            for (int aa2 = 126-1-1-1-2-4; aa2 < 126-1-1-1-2-4 + snetAssetSwAuditExcelDtoList2.size(); aa2++) {

                getCell(sheet6, aa2, 186).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getHostNm());
                getCell(sheet6, aa2, 187).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getAuditRate());

                if (snetAssetSwAuditExcelDtoList2.get(bb2).getWeekHighRate() == 0.0) {

                    getCell(sheet6, aa2, 188).setCellValue("");
                } else {

                    getCell(sheet6, aa2, 188).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getWeekHighRate());
                }

                if (snetAssetSwAuditExcelDtoList2.get(bb2).getWeekMiddleLowRate() == 0.0) {

                    getCell(sheet6, aa2, 189).setCellValue("");
                } else {

                    getCell(sheet6, aa2, 189).setCellValue(snetAssetSwAuditExcelDtoList2.get(bb2).getWeekMiddleLowRate());
                }
                bb2++;
            }
        }

        // 취약점 상세 항목. 테이블. 이행점검
        List<SnetAssetSwAuditReportDto> assetSwAuditReportDtoList2 = testService.selectAssetSwAuditReportNew(gAssetSwJob1, false);

        if (assetSwAuditReportDtoList2 != null && assetSwAuditReportDtoList2.size() > 0) {

            int dd2 = 0;
            for (int cc2 = 157-1-1-1-2-4; cc2 < 157-1-1-1-2-4 + assetSwAuditReportDtoList2.size(); cc2++) {

                getCell(sheet6, cc2, 36).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemGrpNm());
                getCell(sheet6, cc2, 41).setCellValue(assetSwAuditReportDtoList2.get(dd2).getDivideNum());
                getCell(sheet6, cc2, 44).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemNmDesc());
                getCell(sheet6, cc2, 61).setCellValue(assetSwAuditReportDtoList2.get(dd2).getItemGradeView());
                getCell(sheet6, cc2, 63).setCellValue(assetSwAuditReportDtoList2.get(dd2).getHostCount());
                getCell(sheet6, cc2, 64).setCellValue(assetSwAuditReportDtoList2.get(dd2).getHostCount());
                getCell(sheet6, cc2, 65).setCellValue(assetSwAuditReportDtoList2.get(dd2).getEtc());
                dd2++;
            } // end for assetSwAuditReportDtoList2
//            for (int cc = 157; cc < 157 + assetSwAuditReportDtoList2.size(); cc++) {
//
//                for (int ff = 36; ff <= 65; ff++) {
//                    getCell(sheet6, cc, ff).setCellStyle(cellStyle(gWorkbook, 10)); // style
//                }
//            } // end for style
        }
        // 취약점 상세 항목 가이드. 테이블. 이행점검
        if (assetSwAuditReportDtoList2 != null && assetSwAuditReportDtoList2.size() > 0) {
            int dd = 0;
            for (int cc = 211-1-1-1-2-4; cc < 211-1-1-1-2-4 + assetSwAuditReportDtoList2.size(); cc++) {

//                getCell(sheet6, cc, 36).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemGrpNm());
//                getCell(sheet6, cc, 41).setCellValue(assetSwAuditReportDtoList2.get(dd).getDivideNum());
//                getCell(sheet6, cc, 44).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemStandard());
//                getCell(sheet6, cc, 61).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());

                String itemCMstr = assetSwAuditReportDtoList2.get(dd).getItemCountermeasure();
                String[] chars = null;
                boolean autoRowHeightFlag = false;
                short charsLength = 1;
                try {
                    chars = itemCMstr.split("\n");

                    charsLength = (short) (chars.length);
                    autoRowHeightFlag = true;
                } catch (Exception e) {

                    autoRowHeightFlag = false;
                }

                getCell(sheet6, cc, 36).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemGrpNm());
                getCell(sheet6, cc, 41).setCellValue(assetSwAuditReportDtoList2.get(dd).getDivideNum());
                getCell(sheet6, cc, 44).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemStandard());
                if (autoRowHeightFlag) {

                    getCellAutoRowHeight(sheet6, cc, 61, charsLength).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());
                } else {
                    getCell(sheet6, cc, 61).setCellValue(assetSwAuditReportDtoList2.get(dd).getItemCountermeasure());
                }

                dd++;
            } // end for
        } // end if

        /* end linux*/
    } // end method createTotalReportTargets1

    public Cell getCell(Row row, int cellnum) {

        Cell cell = row.getCell(cellnum);
        if (cell == null) {
            cell = row.createCell(cellnum);
        }
        return cell;
    }

    public Row getRow(Sheet sheet, int rownum) {

        Row row = sheet.getRow(rownum);
        if (row == null) {
            row = sheet.createRow(rownum);
        }
        return row;
    }

    public Cell getCell(Sheet sheet, int rownum, int cellnum) {

        Row row = getRow(sheet, rownum);
        return getCell(row, cellnum);
    }

    public Cell getCellAutoRowHeight(Sheet sheet, int rownum, int cellnum, short rowHeight) {

        Row row = getRow(sheet, rownum);

        float defaultRowHeight = 20;
        row.setHeightInPoints(rowHeight * defaultRowHeight);
        return getCell(row, cellnum);
    }

    /**
     * title cell style 설정
     */
    private CellStyle mainStyle(XSSFWorkbook gWorkbook, int size){

        CellStyle mainTitleStyle = gWorkbook.createCellStyle();
        mainTitleStyle.setAlignment(HorizontalAlignment.CENTER);
        mainTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        if(size == 8){

            mainTitleStyle.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
            mainTitleStyle.setBorderRight(BorderStyle.THIN);
            mainTitleStyle.setBorderLeft(BorderStyle.THIN);
            mainTitleStyle.setBorderTop(BorderStyle.THIN);
            mainTitleStyle.setBorderBottom(BorderStyle.THIN);
        } else if(size == 12){

            mainTitleStyle.setFillForegroundColor(HSSFColor.ROYAL_BLUE.index);
            mainTitleStyle.setBorderRight(BorderStyle.THIN);
            mainTitleStyle.setBorderLeft(BorderStyle.THIN);
            mainTitleStyle.setBorderTop(BorderStyle.THIN);
            mainTitleStyle.setBorderBottom(BorderStyle.THIN);

        }

        mainTitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        mainTitleStyle.setFont (fontStyle(gWorkbook, (short)size));

        return mainTitleStyle;
    }

    /**
     * item cell style 설정
     */
    private CellStyle cellStyle(XSSFWorkbook gWorkbook, int type){

        CellStyle cellStyle = gWorkbook.createCellStyle();

        if (type == 99){
            cellStyle.setWrapText(true);
        } else if (type == 2){
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment (VerticalAlignment.CENTER);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setFont (fontStyle(gWorkbook, (short)11));
            cellStyle.setWrapText(true);
        } else if (type == 8) {
            cellStyle.setVerticalAlignment (VerticalAlignment.CENTER);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setFont (fontStyle(gWorkbook, (short)8));
            cellStyle.setWrapText(true);
        } else if (type == 10) {
            cellStyle.setVerticalAlignment (VerticalAlignment.CENTER);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cellStyle.setFont (fontStyle(gWorkbook, (short)10));
            cellStyle.setWrapText(true);
        } else if (type == 12) {
            cellStyle.setFont (fontStyle(gWorkbook, (short)12));
        } else {
            cellStyle.setVerticalAlignment (VerticalAlignment.CENTER);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setFont (fontStyle(gWorkbook, (short)7));
            cellStyle.setWrapText(true);
        }

        return cellStyle;
    }

    /**
     * item cell style 설정
     */
    private Font fontStyle(XSSFWorkbook gWorkbook, short size){

        Font cellFont = gWorkbook.createFont();

        if(size == 8){

            cellFont.setFontHeightInPoints ((short) size); // 폰트 크기 지정
            cellFont.setColor(IndexedColors.BLACK.getIndex());
            cellFont.setFontName ("맑은 고딕");
            cellFont.setBold(false);

        } else if(size == 10){

            cellFont.setFontHeightInPoints ((short) size); // 폰트 크기 지정
            cellFont.setColor(IndexedColors.BLACK.getIndex());
            cellFont.setFontName ("맑은 고딕");
            cellFont.setBold(false);

        } else if(size == 11){

            cellFont.setFontHeightInPoints ((short) size); // 폰트 크기 지정
            cellFont.setColor(IndexedColors.BLACK.getIndex());
            cellFont.setFontName ("맑은 고딕");
            cellFont.setBold(false);

        } else {

            cellFont.setFontHeightInPoints ((short) size); // 폰트 크기 지정
            cellFont.setColor(IndexedColors.WHITE.getIndex());
            cellFont.setFontName ("맑은 고딕");
            cellFont.setBold(true);
        }

        return cellFont;
    }
}
