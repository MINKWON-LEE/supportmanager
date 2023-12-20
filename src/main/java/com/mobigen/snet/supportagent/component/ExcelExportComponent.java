package com.mobigen.snet.supportagent.component;

import com.google.common.collect.Lists;
import com.mobigen.snet.supportagent.dao.ExcelExportMapper;
import com.mobigen.snet.supportagent.dao.TestMapper;
import com.mobigen.snet.supportagent.models.*;
import com.mobigen.snet.supportagent.models.api.*;
import com.mobigen.snet.supportagent.service.ConfigGlobalManager;
import jodd.util.StringUtil;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.*;
import java.io.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 'sg_supprotmanager 프로젝트 - 상세보고서'
 */
@Component
@SuppressWarnings("rawtypes")
public class ExcelExportComponent extends AbstractComponent {

	@Autowired
	private ExcelExportMapper excelExportMapper;

	@Autowired(required = false)
	private TestMapper testMapper;

	@Value("${snet.support.zip.path}")
	private String excelPath;

	@Value("${snet.report.sheet.max}")
	private int SHEET_MAX_COUNT;

	// to do test : 서버에 올릴때 주석 변경할 것
//	private static final String tempReportFilesPath = "D:/report/";
    private static final String excePath = "/usr/local/snetManager/data/excel/tempFiles";
	private static final String tempReportFilesPath = "/usr/local/snetManager/data/excel/tempReportFiles/";

	// 보고서 API
	/**
	 * 점검군별 점검결과 (보안점검) API URL
	 */
	static final String DETAIL_SECU_URL = "https://localhost:10443/api/statistics/interwork/resultOfSecurityChkByInspectGrp";
	/**
	 * 점검군별 점검결과 (이행점검) API URL
	 */
	static final String DETAIL_IMPL_URL = "https://localhost:10443/api/statistics/interwork/resultOfImplChkByInspectGrp";
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
	 * 상세보고서 생성
	 */
	public int createDetailReport(Map obj, ReportRequestDto reportRequestDto) throws Exception {

		int resultCnt = 0;

		Map gAssetSwJob = new HashMap();
		boolean reportApiCheckFlag = false;

		String req_cd = obj.get("REQ_CD").toString();

		String fileType = reportRequestDto.getFileType();
		gAssetSwJob.put("REQ_CD", req_cd);
		gAssetSwJob.put("FILE_TYPE", fileType);

		// SNET_ASSET_SW_AUDIT_EXCEL_LIST 의 min, max 구하기
		SnetAssetSwAuditExcelDto list7MinMaxAuditdayDto = testMapper.getExcelListGroupbyNewMinMaxAuditDay(gAssetSwJob);
		String startAuditDay = list7MinMaxAuditdayDto.getMinAuditDay();
		String endAuditDay = list7MinMaxAuditdayDto.getMaxAuditDay();
		reportRequestDto.setStartAuditDay(startAuditDay);
		reportRequestDto.setEndAuditDay(endAuditDay);

		gAssetSwJob.put("startAuditDay", startAuditDay);
		gAssetSwJob.put("endAuditDay", endAuditDay);

		// 상세보고서 전체 장비 리스트
		gAssetSwJob.put("TOTAL_FLAG", "Y");
		List<SnetAssetSwAuditExcelDto> list7 = testMapper.selectAssetSwExcelListGroupbyNew(gAssetSwJob);
		gAssetSwJob.remove("TOTAL_FLAG");

		// 기타(ETC) 처리할 swType, swNm 체크, 세팅
		for (SnetAssetSwAuditExcelDto dto : list7) {

			if (StringUtils.isNotEmpty(dto.getSwType()) && StringUtils.isNotEmpty(dto.getSwNm())) {

				String swTypeStr = dto.getSwType();
				String swNmStr = dto.getSwNm();

				// 별도 콜렉션,설정으로 분리 가능한지 확인
				if (StringUtils.equals("OS", swTypeStr) && StringUtils.equals("mobile_os", swNmStr)) {

					dto.setSwType("ETC");
					dto.setSwNm(swNmStr);
				} // end if
			} // end if
		} // end for

		// sw_nm 추가를 위한 sw_type 체크
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
		// 엑셀 생성 점검 대상 소프트웨어 타입 :
		//   DB, NW, OS, URL, WAS, WEB -> Linux, Windows, DB, WEB, WAS, NW, Web App(URL), 기타
		//   (+ 대상SW )
		// 엑셀 생성 점검 대상 소프트웨어 타입에 있으면 true, 없으면 false
		reportApiCheckFlag = true;

		// to do test : (서버에 올릴때에는 주석 제거)
//		String excelPath = "D:/report";

		String separator = "/";
		String jobFileNm = null;
		boolean etcFlag = true;

		// 엑셀 생성 점검 대상 소프트웨어 타입 :
		//   DB, NW, OS, URL, WAS, WEB -> UNIX, WINDOWS, DB, WEB, WAS, NW, Web App(URL), 기타
		//   (+ 대상SW )
		// 엑셀 생성 점검 대상 소프트웨어 타입에 있으면 true, 없으면 false
		reportApiCheckFlag = true;

		if (reportApiCheckFlag) {

			try {

				TreeMap<String, List<SnetAssetSwAuditExcelDto>> groupbySwtype =
						swTypeList.stream().collect(Collectors.groupingBy(SnetAssetSwAuditExcelDto::getSwType, TreeMap::new, Collectors.toList()));

				for (Map.Entry<String, List<SnetAssetSwAuditExcelDto>> grpNmVo : groupbySwtype.entrySet()) {

					SnetAssetSwAuditExcelDto resultExcelDto = new SnetAssetSwAuditExcelDto();

					String key = grpNmVo.getKey();
					List<SnetAssetSwAuditExcelDto> values = grpNmVo.getValue();

					String swNmKey1 = null;
					String swNmKey2 = null;
					String swNmKey3 = null;

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

					} else {

						for (SnetAssetSwAuditExcelDto excelDto : values) {

							if (StringUtils.equalsIgnoreCase("mobile_os", excelDto.getSwNm())) {
								swNmKey3 = excelDto.getSwNm();
								break;
							}
						}
					} // end if else

					resultExcelDto.setSwType(key);
					resultExcelDto.setSwNm(swNmKey3);
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
						if (StringUtils.isNotEmpty(swTypeStr.getSwNm())) {
							excelDto.setSwNm(swTypeStr.getSwNm());
						}
						swTypeListNeo.add(excelDto);
					}
				} // end for

				if (windowsAddFlag) {

					SnetAssetSwAuditExcelDto excelDto = new SnetAssetSwAuditExcelDto();
					excelDto.setSwType("OS");
					excelDto.setSwNm("Windows");
					swTypeListNeo.add(excelDto);
				}

				if (linuxAddFlag) {
					SnetAssetSwAuditExcelDto excelDto = new SnetAssetSwAuditExcelDto();
					excelDto.setSwType("OS");
					excelDto.setSwNm("Linux");
					swTypeListNeo.add(excelDto);
				}
				for (SnetAssetSwAuditExcelDto swTypeStrVo : swTypeListNeo) {

					if (StringUtils.equals("OS", swTypeStrVo.getSwType())) {

						if (StringUtils.equals("Windows", swTypeStrVo.getSwNm())) {

							jobFileNm = "detail_Windows";
							etcFlag = true;
							linuxFlag = true;
							logger.debug("*[{},{},{}]", swTypeStrVo.getSwType(), swTypeStrVo.getSwNm(), jobFileNm);

						} else if (StringUtils.equals("Linux", swTypeStrVo.getSwNm())) {

							jobFileNm = "detail_Linux";
							etcFlag = true;
							linuxFlag = false;
							logger.debug("*[{},{},{}]", swTypeStrVo.getSwType(), swTypeStrVo.getSwNm(), jobFileNm);
						}
					} else if (StringUtils.equals("WAS", swTypeStrVo.getSwType())) {

						jobFileNm = "detail_WAS";
						etcFlag = true;
						linuxFlag = true;
						logger.debug("*[{},{}]", swTypeStrVo.getSwType(), jobFileNm);
					} else if (StringUtils.equals("WEB", swTypeStrVo.getSwType())) {

						jobFileNm = "detail_WEB";
						etcFlag = true;
						linuxFlag = true;
						logger.debug("*[{},{}]", swTypeStrVo.getSwType(), jobFileNm);
					} else if (StringUtils.equals("DB", swTypeStrVo.getSwType())) {

						jobFileNm = "detail_DB";
						etcFlag = true;
						linuxFlag = true;
						logger.debug("*[{},{}]", swTypeStrVo.getSwType(), jobFileNm);
					} else if (StringUtils.equals("NW", swTypeStrVo.getSwType())) {

						jobFileNm = "detail_NW";
						etcFlag = true;
						linuxFlag = true;
						logger.debug("*[{},{}]", swTypeStrVo.getSwType(), jobFileNm);
					} else if (StringUtils.equals("URL", swTypeStrVo.getSwType())) {

						jobFileNm = "detail_URL";
						etcFlag = true;
						linuxFlag = true;
						logger.debug("*[{},{}]", swTypeStrVo.getSwType(), jobFileNm);
					} else {

						jobFileNm = "detail_ETC";
						etcFlag = false;
						linuxFlag = true;
						logger.debug("*[{},{},{}]", swTypeStrVo.getSwType(), swTypeStrVo.getSwNm(), jobFileNm);
					}
					createReportExcel(swTypeStrVo.getSwType(), swTypeStrVo, list7, reportRequestDto, tempReportFilesPath, jobFileNm, etcFlag, linuxFlag);

				} // end for
			} catch (Exception e) {
				e.printStackTrace();
			}

		} // end if checkFlag

		return 1;
	} // end method createDetailReport


	/**
	 * jobFileNm 분기해서, create excel file 부분 실행
	 */
	private void createReportExcel(String swTypeStr,
								   SnetAssetSwAuditExcelDto swTypeStrVO, List<SnetAssetSwAuditExcelDto> list7,
								   ReportRequestDto reportRequestDto,
								   String tempReportFilesPath,
								   String jobFileNm,
								   boolean etcFlag, boolean linuxFlag) throws IOException {


		// -> to do test : 보고서 API
		boolean reportApiCheckFlag = false;
		StopWatch watch = new StopWatch();
		watch.start();

		initSSL();
		RestTemplate rest = new RestTemplate();

//        XSSFWorkbook gWorkbook1 = new XSSFWorkbook();
		// -> to do test : SXSSF 는 XSSF 의 Streaming Version 으로 메모리 적게 사용, 대용량 엑셀에 사용
		// -> to do test : HSSFHyperlink 를 이용한 LINK_FILE 추가
		Workbook gWorkbook1 =  new SXSSFWorkbook();

		String startAuditDay = reportRequestDto.getStartAuditDay();
		String endAuditDay = reportRequestDto.getEndAuditDay();

		// 보고서 API 호출 위한 flag
		SimpleDateFormat formatDay = new SimpleDateFormat("yyyyMMdd");
		Calendar timeDay = Calendar.getInstance();
		String createDay = formatDay.format(timeDay.getTime()); // createDay 는 오늘 날짜

		/*if (StringUtils.equals(createDay, startAuditDay) && StringUtils.equals(createDay, endAuditDay)) {
			reportApiCheckFlag = false;
		} else {
			reportApiCheckFlag = true;
		} // end if*/

		Map gAssetSwJob = new HashMap();
		/*---------- create excel file [start] ----------*/
		gAssetSwJob.put("REQ_CD", reportRequestDto.getReqCd());
		gAssetSwJob.put("FILE_TYPE", reportRequestDto.getFileType());
		gAssetSwJob.put("startAuditDay", startAuditDay);
		gAssetSwJob.put("endAuditDay", endAuditDay);

		// read file
		FileInputStream is = null;

		// to do test : 서버에 올릴때 주석 제거
//		String excelTempPath = "D:/tempFiles";

		try {

			// to do test : 서버에 올릴때 주석 제거
//			is = new FileInputStream(new File(excelTempPath + "/detail_report_mapping8.xlsx"));
            is = new FileInputStream(new File(excePath + "/detail_report_mapping8.xlsx"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// jXLS setting
		XLSTransformer transformer = new XLSTransformer();
		Map<String, Object> beans = new HashMap<>();
		List<SnetAssetSwAuditReportDto> assetSwAuditReportList4 = Lists.newArrayList();
		List<ExcelReportDto> excelReportList1 = Lists.newArrayList();
		List<ExcelReportDto> excelReportList2 = Lists.newArrayList();

		String checkStandard = reportRequestDto.getFileType(); // 점검기준

		// 점검 항목 : 취약점 분석 평가 기준 n개
		String checkAnalysisStandardDesc = null;
		int checkAnalysisStandard = 0;

		Map checkParam = new HashMap();
		checkParam.put("FILE_TYPE", checkStandard);

		if (StringUtils.equalsIgnoreCase("OS", swTypeStr)) {

			checkParam.put("SW_TYPE", swTypeStr);

			if (!StringUtils.equalsIgnoreCase("Windows", swTypeStrVO.getSwNm())) {

				boolean flag;

				if ("Linux".equalsIgnoreCase(swTypeStrVO.getSwNm())) {
					checkParam.put("SW_NM", "Linux");
				} else {
					checkParam.put("SW_NM", swTypeStrVO.getSwNm());
				}
			} else {

				checkParam.put("SW_NM", "Windows");
			}
		} else {

			checkParam.put("SW_TYPE", swTypeStr);

			List<CheckAnalysisDto> checkAnalysisDtoList = testMapper.getCheckSwnmAnalysisStandard(checkParam);

			checkAnalysisDtoList.sort(Comparator.comparing(CheckAnalysisDto::getCntItemGrp).reversed());

			if (checkAnalysisDtoList != null && checkAnalysisDtoList.size() > 0) {

				CheckAnalysisDto checkAnalysisDto = checkAnalysisDtoList.stream().findFirst().get();

				checkParam.put("SW_TYPE", swTypeStr);
				checkParam.put("SW_NM", checkAnalysisDto.getSwNm());
			} else {

				checkParam.put("SW_TYPE", swTypeStr);
				checkParam.put("SW_NM", "NONE");
			}
		}
		checkAnalysisStandard = testMapper.getCheckAnalysisStandard(checkParam);

		checkAnalysisStandardDesc = testMapper.getCheckAnalysisStandardDesc(checkParam);

		// 점검 항목 : 취약점 분류 n개 항목
		int checkClassifyItems = testMapper.getCheckClassifyItems(checkParam);

		// sheet 개수
		int diagnosisCount = 1;

		// -> 미사용. apache poi 로 변경
		List<SnetAssetSwAuditExcelDto> getWeakLevelsParams2 = Lists.newArrayList();
		List<SnetAssetSwAuditReportDto> weakLevelsList = Lists.newArrayList();
		List<SnetAssetSwAuditExcelDto> getWeakLevelsParams3 = Lists.newArrayList(); // dataHidden4
		List<SnetAssetSwAuditReportDto> hiddenList4 = Lists.newArrayList(); // dataHidden3

		// linuxFlag = true
		if (etcFlag && linuxFlag) {

			String swTypeNm = null;

			if (StringUtils.equalsIgnoreCase("OS", swTypeStr)) {

				checkParam.put("SW_TYPE", swTypeStr);

				if (StringUtils.equalsIgnoreCase("Windows", swTypeStrVO.getSwNm())) {

					swTypeNm = "Windows";
					gAssetSwJob.put("SW_TYPE", "OS");
					gAssetSwJob.put("SW_NM", swTypeNm);
				} else {

					swTypeNm = "Linux";
					gAssetSwJob.put("SW_TYPE", "OS");
					gAssetSwJob.put("SW_NM", swTypeNm);
				}
			} else {

				gAssetSwJob.put("SW_TYPE", swTypeStr);
				gAssetSwJob.remove("SW_NM");
			}

			// 점검 대상 : n대
			int checkTargets = 0;
			gAssetSwJob.put("HOST_GRD_FLAG", "Y");
			// SNET_ASSET_SW_AUDIT_EXCEL_LIST 의 min, max 구하기
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

				if (StringUtils.equalsIgnoreCase("OS", swTypeStr)) {

					paramMap.put("SW_TYPE", swTypeStr);

					if (StringUtils.equalsIgnoreCase("Windows", swTypeStrVO.getSwNm())) {

						swTypeNm = "Windows";
						paramMap.put("SW_TYPE", "OS");
						paramMap.put("SW_NM", swTypeNm);
					} else {

						swTypeNm = "Linux";
						paramMap.put("SW_TYPE", "OS");
						paramMap.put("SW_NM", swTypeNm);
					}
				} else {

					swTypeNm = excelDto.getSwNm();
					paramMap.put("SW_TYPE", swTypeStr);
					paramMap.put("SW_NM", swTypeNm);
				}
				paramMap.put("ASSET_CD", tempAssetCd);
				paramMap.put("FILE_TYPE", gAssetSwJob.get("FILE_TYPE"));
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
			} // end for

			checkTargets = reportDtoList.size();

			String asset_cd = null;
			String auditDay = null;

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

			auditDay = snetAssetSwAuditExcelDto2.getMaxAuditDay();
			asset_cd = snetAssetSwAuditExcelDto2.getAssetCd();
			startAuditDay = snetAssetSwAuditExcelDto.getMinAuditDay();
			endAuditDay = snetAssetSwAuditExcelDto2.getMaxAuditDay();

			Map sheetMaps1 = new HashMap();
			Map sheetMaps3 = new HashMap();
			Map sheetMaps5 = new HashMap();
			Map sheetMaps6 = new HashMap();

			String startAuditDayFormat = String.format("%s/%s/%s"
					, StringUtils.substring(startAuditDay, 0, 4)
					, StringUtils.substring(startAuditDay, 4, 6)
					, StringUtils.substring(startAuditDay, 6, 8));

			String endAuditDayFormat = String.format("%s/%s/%s"
					, StringUtils.substring(endAuditDay, 0, 4)
					, StringUtils.substring(endAuditDay, 4, 6)
					, StringUtils.substring(endAuditDay, 6, 8));

			if (etcFlag) {
				if (StringUtils.equalsIgnoreCase("OS", swTypeStr)) {

					checkParam.put("SW_TYPE", swTypeStr);

					if (!StringUtils.equalsIgnoreCase("Windows", swTypeStrVO.getSwNm())) {

						sheetMaps1.put("swTypeStr", "Linux");
					} else {

						sheetMaps1.put("swTypeStr", "Windows");
					}
				} else {

					if (StringUtils.equalsIgnoreCase("URL", swTypeStr)) {

						sheetMaps1.put("swTypeStr", "Web App(URL)");
					} else {

						sheetMaps1.put("swTypeStr", swTypeStr);
					}
				}
			} else {
				sheetMaps1.put("swTypeStr", "기타");
			}
			sheetMaps1.put("startAuditDay", startAuditDayFormat);
			sheetMaps1.put("endAuditDay", endAuditDayFormat);
			beans.put("data1", sheetMaps1);

			// Ⅱ. 수행내역
			sheetMaps3.put("checkAnalysisStandardDesc", checkAnalysisStandardDesc); // -> 아래에서 사용
//            sheetMaps3.put("checkAnalysisStandard", checkAnalysisStandard); // 미사용
//            sheetMaps3.put("checkClassifyItems", checkClassifyItems); // 미사용
			sheetMaps3.put("checkTargets", checkTargets); // -> 아래에서 사용
//            beans.put("data3", sheetMaps3); // 미사용

			// Ⅲ. 점검 대상
			// rownum -> ipAddress, hostNm 를 오름차순 정렬
			int ii2 = 0;
			for (SnetAssetSwAuditExcelDto vo : list7Re) {

				if (StringUtils.isEmpty(vo.getHostNm())) {
					vo.setHostNm("");
				}
				if (StringUtils.isEmpty(vo.getIpAddress())) {
					vo.setIpAddress("");
				}
				vo.setRownum(++ii2);
			}

			beans.put("data4", list7Re);
			// Ⅳ. 보안점검 결과_총평 (모든장비)

			// 점검항목수
			int itemTotal = 0;
			String create_date = null;
			if (list7Re != null && list7Re.size() > 0) {

				for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {
					if (StringUtils.equalsIgnoreCase(asset_cd, reportDto.getAssetCd()) && StringUtils.equalsIgnoreCase(auditDay, reportDto.getAuditDay())) {
						create_date = reportDto.getCreateDate();
						break;
					}
				}
				gAssetSwJob.put("ASSET_CD", asset_cd);
				gAssetSwJob.put("AUDIT_DAY", auditDay);
				gAssetSwJob.put("CREATE_DATE", create_date);
				itemTotal = testMapper.getItemTotal(gAssetSwJob);


			} else {

				itemTotal = 0;
			}

			int weaktotal = 0;
			int weakLevelHigh = 0;
			int weakLevelMiddle = 0;
			int weakLevelLow = 0;

			// ◎ 위험도별 발견 취약점 수
			// excel list 조회
//            List<SnetAssetSwAuditExcelDto> getWeakLevelsParams3 = Lists.newArrayList();
			List<SnetAssetSwAuditExcelDto> getWeakLevelsParams4 = Lists.newArrayList();
			for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

				Map paramMap = new HashMap();
				paramMap.put("startAuditDay", startAuditDay);
				paramMap.put("endAuditDay", endAuditDay);
				if (StringUtils.equalsIgnoreCase("OS", swTypeStr)) {

					paramMap.put("SW_TYPE", swTypeStr);

					if (StringUtils.equalsIgnoreCase("Windows", swTypeStrVO.getSwNm())) {

						swTypeNm = "Windows";
						paramMap.put("SW_TYPE", "OS");
						paramMap.put("SW_NM", swTypeNm);
					} else {

						swTypeNm = "Linux";
						paramMap.put("SW_TYPE", "OS");
						paramMap.put("SW_NM", swTypeNm);
					}
				} else {

					swTypeNm = reportDto.getSwNm();
					paramMap.put("SW_TYPE", swTypeStr);
					paramMap.put("SW_NM", swTypeNm);
				}
				paramMap.put("ASSET_CD", reportDto.getAssetCd());
				paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
				paramMap.put("CREATE_DATE", reportDto.getCreateDate());
				paramMap.put("REQ_CD", reportRequestDto.getReqCd());
                paramMap.put("SW_DIR", reportDto.getSwDir());

				SnetAssetSwAuditExcelDto getWeakLevelsParams = testMapper.selectAssetSwExcelListGroupbyWeak(paramMap);

				if (getWeakLevelsParams != null) {
					getWeakLevelsParams2.add(getWeakLevelsParams);
				} else {

					SnetAssetSwAuditExcelDto getWeakLevelsParamsNot = testMapper.selectAssetSwExcelListGroupbyNotWeak(paramMap);

					if (getWeakLevelsParams == null && getWeakLevelsParamsNot != null) {
						getWeakLevelsParams2.add(getWeakLevelsParamsNot);
					}
				}

				paramMap.remove("SW_TYPE");
				paramMap.remove("SW_NM");
				paramMap.remove("ASSET_CD");
				paramMap.remove("AUDIT_DAY");
				paramMap.remove("CREATE_DATE");
				paramMap.remove("REQ_CD");

			} // end for

			gAssetSwJob.remove("AUDIT_DAY");
			List<List<SnetAssetSwAuditReportDto>> weakLevels = Lists.newArrayList();

			for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

				Map paramMap = new HashMap();
				paramMap.put("startAuditDay", startAuditDay);
				paramMap.put("endAuditDay", endAuditDay);

				if (StringUtils.equalsIgnoreCase("OS", swTypeStr)) {

					paramMap.put("SW_TYPE", swTypeStr);

					if (StringUtils.equalsIgnoreCase("Windows", swTypeStrVO.getSwNm())) {

						swTypeNm = "Windows";
						paramMap.put("SW_TYPE", "OS");
						paramMap.put("SW_NM", swTypeNm);
					} else {

						swTypeNm = "Linux";
						paramMap.put("SW_TYPE", "OS");
						paramMap.put("SW_NM", swTypeNm);
					}
				} else {

					swTypeNm = reportDto.getSwNm();
					paramMap.put("SW_TYPE", swTypeStr);
					paramMap.put("SW_NM", swTypeNm);
				}
				paramMap.put("F_FLAG", "Y");
				paramMap.put("ASSET_CD", reportDto.getAssetCd());
				paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
				paramMap.put("CREATE_DATE", reportDto.getCreateDate());
				List<SnetAssetSwAuditReportDto> tempReportDto = testMapper.getWeakLevels(paramMap);
				weakLevels.add(tempReportDto);
			}

			// 평가점수 합,평균
			int scoreLen = 0;
			double scoreSum = 0.0;
			int scoreAvg = 0;

			// ◎ 취약점 분석 평가 점수
			if (weakLevels == null && weakLevels.size() == 0) {

				weakLevelHigh = 0;
				weakLevelMiddle = 0;
				weakLevelLow = 0;
				weaktotal = 0;
				scoreAvg = 0;
				for (SnetAssetSwAuditExcelDto dataHidden1Dto : getWeakLevelsParams2) {

					double auditRateAvg = 0 / 100.0;

					dataHidden1Dto.setAuditRateAvg(auditRateAvg);
				}
			} else {

				for (List<SnetAssetSwAuditReportDto> weakLevel : weakLevels) {

					for (SnetAssetSwAuditReportDto weekReportDto : weakLevel) {

						if (StringUtils.equals("H", weekReportDto.getItemGrade())) {

							weakLevelHigh += weekReportDto.getCount();
						}
						if (StringUtils.equals("M", weekReportDto.getItemGrade())) {

							weakLevelMiddle += weekReportDto.getCount();
						}
						if (StringUtils.equals("L", weekReportDto.getItemGrade())) {

							weakLevelLow += weekReportDto.getCount();
						}
					}
				}
				weaktotal = weakLevelHigh + weakLevelMiddle + weakLevelLow;

				scoreLen = getWeakLevelsParams2.size();
				for (SnetAssetSwAuditExcelDto sumDto : getWeakLevelsParams2) {

					scoreSum += sumDto.getAuditRate() * 100;
				}

				if (scoreSum != 0 && scoreLen != 0) {
					scoreAvg = (int)scoreSum / scoreLen;
				} else {
					scoreAvg = 0;
				}
				for (SnetAssetSwAuditExcelDto dataHidden1Dto : getWeakLevelsParams2) {

					double auditRateAvg = scoreAvg / 100.0;

					dataHidden1Dto.setAuditRateAvg(auditRateAvg);
				}
			} // end if else

			sheetMaps5.put("itemTotal", itemTotal); // 항목전체
			sheetMaps5.put("checkTargets", checkTargets); // 장비대수
			sheetMaps5.put("weakLevelHigh", weakLevelHigh); // 상
			sheetMaps5.put("weakLevelMiddle", weakLevelMiddle); // 중
			sheetMaps5.put("weakLevelLow", weakLevelLow); // 하
			sheetMaps5.put("weaktotal", weaktotal); // 취약전체
			sheetMaps5.put("scoreAvg", scoreAvg); // 취약평가점수평균

			// -> to do test : 보고서 API
			List<DetailReportHostNmResultDto> resultHostNmList = Lists.newArrayList();
			List<DetailReportResultDto> resultDtoList = Lists.newArrayList();
			int tempWeakLevelHigh = 0;
			int tempWeakLevelMiddle = 0;
			int tempWeakLevelLow = 0;

			if (reportApiCheckFlag) {

				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
				httpHeaders.setContentType(MediaType.APPLICATION_JSON);

				String userId = reportRequestDto.getReqUser();
				String swTypeNmKeyStr = getSwTypeNmKey(swTypeStrVO.getSwType(), swTypeStrVO.getSwNm());
				ApiRequestDto apiRequestDto = ApiRequestDto.builder()
						.userId(userId)
						.swType(swTypeNmKeyStr)
						.startDay(startAuditDay)
						.endDay(endAuditDay)
						.build();

				UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(DETAIL_IMPL_URL)
						.queryParam("restApiKey", REST_API_KEY);

				HttpEntity<ApiRequestDto> httpEntity = new HttpEntity<>(apiRequestDto, httpHeaders);
				ApiResult apiResult = null;

				try {

					apiResult = rest.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, ApiResult.class).getBody();

					if (apiResult != null) {

						ModelMapper modelMapper = new ModelMapper();

						DetailReportDto tempDto = modelMapper.map(apiResult.getResultData(), DetailReportDto.class);

						tempWeakLevelHigh = tempDto.getPChkweakHighTotal();
						tempWeakLevelMiddle = tempDto.getPChkweakMidTotal();
						tempWeakLevelLow = tempDto.getPChkweakLowTotal();

						resultHostNmList = tempDto.getResultHostNmList().stream().collect(Collectors.toList());
						resultDtoList = tempDto.getResultDtoList().stream().collect(Collectors.toList());
					} // end if
				} catch (Exception e) {

					logger.error(e.toString());
				}

				// 보고서 생성시 값 vs 보고서 API 호출 값 비교

				if ( (weakLevelHigh == tempWeakLevelHigh) && (weakLevelMiddle == tempWeakLevelMiddle) && (weakLevelLow == tempWeakLevelLow) ) {

					weakLevelHigh = tempWeakLevelHigh;
					weakLevelMiddle = tempWeakLevelMiddle;
					weakLevelLow = tempWeakLevelLow;
				} // end if
			}

			SnetAssetSwAuditReportDto weakLevelsDto1 = new SnetAssetSwAuditReportDto();
			weakLevelsDto1.setItemGradeView("상");
			weakLevelsDto1.setCount(weakLevelHigh);
			SnetAssetSwAuditReportDto weakLevelsDto2 = new SnetAssetSwAuditReportDto();
			weakLevelsDto2.setItemGradeView("중");
			weakLevelsDto2.setCount(weakLevelMiddle);
			SnetAssetSwAuditReportDto weakLevelsDto3 = new SnetAssetSwAuditReportDto();
			weakLevelsDto3.setItemGradeView("하");
			weakLevelsDto3.setCount(weakLevelLow);
			weakLevelsList.add(weakLevelsDto1);
			weakLevelsList.add(weakLevelsDto2);
			weakLevelsList.add(weakLevelsDto3);

			// 취약점 분석 평가 점수
			getWeakLevelsParams4 = getWeakLevelsParams2.stream().collect(Collectors.toList());
			getWeakLevelsParams4.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate));

			if (getWeakLevelsParams4 != null && getWeakLevelsParams4.size() > 0) {
				String lowTargetStr = getWeakLevelsParams4.stream().findFirst().get().getHostNm();
				String lowTargetAssetCd = getWeakLevelsParams4.stream().findFirst().get().getAssetCd();

				if (StringUtils.isEmpty(lowTargetStr)) {

					for (SnetAssetSwAuditExcelDto excelDto : list7Re) {

						if (StringUtils.equalsIgnoreCase(lowTargetAssetCd, excelDto.getAssetCd())) {

							lowTargetStr = excelDto.getHostNm();
							getWeakLevelsParams4.stream().findFirst().get().setHostNm(lowTargetStr);
						}
					}
				}
				sheetMaps5.put("lowTarget", lowTargetStr); // 취약점 분석 평가 점수 가장 낮음
			}

			// 취약점 분석 평가 점수 (audit_rate) 보고서 내림차순 정렬
			getWeakLevelsParams2.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate).reversed());
//            beans.put("data5", sheetMaps5); // -> 아래에서 처리

			// -> 미사용. apache poi 로 변경
//            beans.put("dataHidden1", getWeakLevelsParams2);
//            beans.put("dataHidden2", weakLevelsList);

			// 점검 대상별 취약점 수
			getWeakLevelsParams3 = getWeakLevelsParams2.stream().filter(x -> "F".equals(x.getItemResult())).collect(Collectors.toList());
			getWeakLevelsParams3.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getWeakCount).reversed());
			getWeakLevelsParams3 = getWeakLevelsParams3.stream().limit(10).collect(Collectors.toList());

			// -> 미사용. apache poi 로 변경
//            beans.put("dataHidden4", getWeakLevelsParams3);

			// V. 보안점검 결과_요약 sheet : hidden row 데이터 참고
			sheetMaps6.put("itemTotal", itemTotal);

			SnetAssetSwAuditExcelDto topOne = new SnetAssetSwAuditExcelDto();

			if (getWeakLevelsParams3 != null && getWeakLevelsParams3.size() > 0) {

				topOne = getWeakLevelsParams3.stream().findFirst().get();
			}

			String topHostNm = topOne.getHostNm();

			sheetMaps6.put("topHostNm", topHostNm); // 가장 많은 취약점이 발견됨

			double weekLen = 0.0;
			int weekSum = 0;
			double weekAvg = 0;
			int weekAvgFinal = 0;
			weekLen = getWeakLevelsParams2 == null ? 0 : getWeakLevelsParams2.size();

			if (weekLen != 0) {

				for (SnetAssetSwAuditExcelDto sumDto : getWeakLevelsParams2) {

					weekSum += sumDto.getWeakCount();
				}
				weekAvg = Math.floor(weekSum / weekLen);
				weekAvgFinal = (int) weekAvg;
			} else {

				weekAvgFinal = 0;
			}
			sheetMaps6.put("weekTopOne", topOne.getWeakCount());
			sheetMaps6.put("weekAvg", weekAvgFinal);


			int grpNmLen = 0;
			List<List<SnetAssetSwAuditReportDto>> groupbyReportGrpNmLists = Lists.newArrayList();

			for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

				Map paramMap = new HashMap();
				paramMap.put("startAuditDay", startAuditDay);
				paramMap.put("endAuditDay", endAuditDay);

				if (StringUtils.equalsIgnoreCase("OS", swTypeStr)) {

					paramMap.put("SW_TYPE", swTypeStr);

					if (StringUtils.equalsIgnoreCase("Windows", swTypeStrVO.getSwNm())) {

						swTypeNm = "Windows";
						paramMap.put("SW_TYPE", "OS");
						paramMap.put("SW_NM", swTypeNm);
					} else {

						swTypeNm = "Linux";
						paramMap.put("SW_TYPE", "OS");
						paramMap.put("SW_NM", swTypeNm);
					}
				} else {

					swTypeNm = reportDto.getSwNm();
					paramMap.put("SW_TYPE", swTypeStr);
					paramMap.put("SW_NM", swTypeNm);
				}
				paramMap.put("ASSET_CD", reportDto.getAssetCd());
				paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
				paramMap.put("CREATE_DATE", reportDto.getCreateDate());
				List<SnetAssetSwAuditReportDto> groupbyReportGrpNmList = testMapper.getGroupbyReportGrpNmList(paramMap);
				groupbyReportGrpNmLists.add(groupbyReportGrpNmList);
			} // end for

			int tempWeekCnt = 0;
			int dotIndexOf = 0;
			String substrGrpNm = null;
			List<SnetAssetSwAuditReportDto> hiddenList3 = Lists.newArrayList();
//            List<SnetAssetSwAuditReportDto> hiddenList4 = Lists.newArrayList();
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
								substrGrpNm = StringUtils.substring(dto3.getItemGrpNm(), dotIndexOf + 2);
							} else {
								substrGrpNm = dto3.getItemGrpNm();
							}

							break;
						} else {

							tempWeekCnt = 0;
							dotIndexOf = StringUtils.indexOf(dto3.getItemGrpNm(), ".");
							if (dotIndexOf > -1) {
								substrGrpNm = StringUtils.substring(dto3.getItemGrpNm(), dotIndexOf + 2);
							} else {
								substrGrpNm = dto3.getItemGrpNm();
							}
						}
					}

					hiddenDto3.setWeakCount(tempWeekCnt);
					hiddenDto3.setItemGrpNm(StringUtils.deleteWhitespace(substrGrpNm));

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
			} // end for

			hiddenList4.sort(Comparator.comparing(SnetAssetSwAuditReportDto::getWeakCount).reversed());
			sheetMaps6.put("grpNmLen", grpNmLen);

			String grpNmTopWeek = null;

			grpNmTopWeek = hiddenList4 != null && hiddenList4.size() > 0 ? hiddenList4.stream().findFirst().get().getItemGrpNm() : null;

			String grpNmTopWeekStr = null;

			if (StringUtils.isNotEmpty(grpNmTopWeek)) {

				grpNmTopWeekStr = grpNmTopWeek;

			} else {

				grpNmTopWeekStr = "";
			}

			sheetMaps6.put("grpNmTopWeek", grpNmTopWeekStr);

			int grpNmTopWeekCount = 0;
			SnetAssetSwAuditReportDto snetAssetSwAuditReportDto = null;

			try {
				snetAssetSwAuditReportDto = hiddenList4.stream()
						.max(Comparator.comparing(SnetAssetSwAuditReportDto::getWeakCount)).orElseThrow(NoSuchElementException::new);

				grpNmTopWeekCount = snetAssetSwAuditReportDto.getWeakCount();
			} catch (NoSuchElementException ne) {

				grpNmTopWeekCount = 0;
			}

			sheetMaps6.put("grpNmTopWeekCount", grpNmTopWeekCount);

			// 분류 항목별 취약점 수
			// -> to do test : 보고서 API

			if (hiddenList4 != null && hiddenList4.size() > 0 && resultDtoList != null && resultDtoList.size() > 0
					&& hiddenList4.size() == resultDtoList.size()) {

				for (int i = 0; i < resultDtoList.size(); i++) {

					DetailReportResultDto tempDto = resultDtoList.get(i);
					SnetAssetSwAuditReportDto targetDto = hiddenList4.get(i);

					if (StringUtils.equals(targetDto.getItemGrpNm(), tempDto.getItemGrpNm())) {
						targetDto.setItemGrpNm(tempDto.getItemGrpNm());
					}

					if (targetDto.getWeakCount() == tempDto.getPChkweakTotalCnt()) {
						targetDto.setWeakCount(tempDto.getPChkweakTotalCnt());
					}

				} // end for
			} // end if

			int grpNmTopWeekCountAvg = 0;
			double grpNmTopWeekCountDbl = 0L;

			if (hiddenList4 != null && hiddenList4.size() > 0) {
				OptionalDouble asDouble = hiddenList4.stream()
						.mapToInt(x -> x.getWeakCount())
						.average();
				grpNmTopWeekCountDbl = Math.floor(asDouble.getAsDouble());

				grpNmTopWeekCountAvg = (int) grpNmTopWeekCountDbl;

			} else {

				grpNmTopWeekCountAvg = 0;
			}
			sheetMaps6.put("grpNmTopWeekCountAvg", grpNmTopWeekCountAvg);
			beans.put("data6", sheetMaps6);

			// (hidden) 분류 항목별 취약점
//            beans.put("dataHidden3", hiddenList4);

			gAssetSwJob.put("startAuditDay", startAuditDay);
			gAssetSwJob.put("endAuditDay", endAuditDay);
			gAssetSwJob.put("BET_FLAG", "Y");

			// jXLS 값 매핑
			List<SnetAssetSwAuditReportDto> assetSwAuditReportListDetail1 = Lists.newArrayList();
			List<SnetAssetSwAuditReportDto> assetSwAuditReportListDetail2 = Lists.newArrayList();
			List<SnetAssetSwAuditReportDto> assetSwAuditReportListDetail3 = Lists.newArrayList();

			// 점검대상 : report max audit_day
			// Ⅵ. 보안점검 결과 전체
			// Ⅵ. 보안점검 결과 전체(swNm추가)
			String swNm = null;
			int listAddIndex = 0;


			// Ⅵ. 보안점검 결과 전체 (swNm / swInfo) sheet 생성을 위함
			// (1) swNm 또는 swInfo 에 따라 분기
			TreeMap<String, List<SnetAssetSwAuditExcelDto>> groupbyReportGrpNm = null;

			if (StringUtils.equalsIgnoreCase("Linux", swTypeStr) || StringUtils.equalsIgnoreCase("Windows", swTypeStr)) {

				groupbyReportGrpNm =
						list7Re.stream().collect(Collectors.groupingBy(SnetAssetSwAuditExcelDto::getSwNm, TreeMap::new, Collectors.toList()));
				diagnosisCount = groupbyReportGrpNm.entrySet().size();
			} else {
				// (2) swNm 또는 swInfo 에 대해 grouping - SwNm

				groupbyReportGrpNm =
						list7Re.stream().collect(Collectors.groupingBy(SnetAssetSwAuditExcelDto::getSwNm, TreeMap::new, Collectors.toList()));
				diagnosisCount = groupbyReportGrpNm.entrySet().size();
			} // end if else

			for (int ij2 = 0; ij2 < list7Re.size(); ij2++) {

				if (list7Re.get(ij2).getAssetCd() != null && list7Re.get(ij2).getAuditDay() != null && list7Re.get(ij2).getSwNm() != null) {

					ExcelReportDto vo = new ExcelReportDto();

					swNm = list7Re.get(ij2).getSwNm();
					String sheetNm2 = list7Re.get(ij2).getIpAddress();
					String hostNm = list7Re.get(ij2).getHostNm();
					String assetCdReport = list7Re.get(ij2).getAssetCd();

					Map reportMap = new HashMap();
					reportMap.put("startAuditDay", startAuditDay);
					reportMap.put("endAuditDay", endAuditDay);
					reportMap.put("SW_INFO", list7Re.get(ij2).getSwInfo());
					reportMap.put("SW_DIR", list7Re.get(ij2).getSwDir());
					reportMap.put("SW_USER", list7Re.get(ij2).getSwUser());
					reportMap.put("SW_ETC", list7Re.get(ij2).getSwEtc());

					if (StringUtils.equalsIgnoreCase("OS", swTypeStr)) {

						reportMap.put("SW_TYPE", swTypeStr);

						if (StringUtils.equalsIgnoreCase("Windows", swTypeStrVO.getSwNm())) {

							swTypeNm = "Windows";
							reportMap.put("SW_TYPE", "OS");
							reportMap.put("SW_NM", swTypeNm);
						} else {

							swTypeNm = "Linux";
							reportMap.put("SW_TYPE", "OS");
							reportMap.put("SW_NM", swTypeNm);
						}
					} else {

						swTypeNm = swNm;
						reportMap.put("SW_TYPE", swTypeStr);
						reportMap.put("SW_NM", swTypeNm);
					}

					reportMap.put("ASSET_CD", assetCdReport);
					SnetAssetSwAuditReportDto auditDayForReport = testMapper.selectAssetSwReportGroupbyAuditday(reportMap);

					if (auditDayForReport != null) {

						reportMap.put("AUDIT_DAY", auditDayForReport.getAuditDay());
						auditDayForReport = testMapper.selectAssetSwAuditReportNewGet(reportMap);
						reportMap.put("CREATE_DATE", auditDayForReport.getCreateDate());

						assetSwAuditReportListDetail2 = testMapper.selectAssetSwAuditReportNew(reportMap);

						if (assetSwAuditReportListDetail2 != null && assetSwAuditReportListDetail2.size() > 0) {
							assetSwAuditReportListDetail3 = assetSwAuditReportListDetail2.stream().map(this::converToDto).collect(Collectors.toList());

							vo.setSwNm(swNm);

							// -> to do test : setHostNm 에 HYPERLINK 추가
							vo.setHostNm(hostNm + "(" + sheetNm2 + ")");
							vo.setTxtFileNm(hostNm + "(" + sheetNm2 + "_" + swNm + ")");

							vo.setDiagnosisCd(assetSwAuditReportListDetail3.stream().findFirst().get().getDiagnosisCd());
							vo.setReportList(assetSwAuditReportListDetail3);

							excelReportList1.add(vo);
						}
					} // end if
				} // end if
			} // end for

			int sheetDivideCount = 1;
			List<String> checkAnalysisStrList = new ArrayList<>();
			List<String> itemTotalCheckTargetsList = new ArrayList<>();

			if (diagnosisCount > 1) {

				for (Map.Entry<String, List<SnetAssetSwAuditExcelDto>> reportDto : groupbyReportGrpNm.entrySet()) {

					String sheetStr = reportDto.getKey();
					beans.put("swNm" + sheetDivideCount, sheetStr);

					beans.put("cols" + sheetDivideCount, excelReportList1.stream().filter(x -> sheetStr.equals(x.getSwNm())).collect(Collectors.toList()));

					excelReportList2 = excelReportList1.stream().filter(x -> sheetStr.equals(x.getSwNm())).collect(Collectors.toList());

					if (excelReportList2 != null && excelReportList2.size() > 0) {

						ExcelReportDto lastExcelReportDto = excelReportList2.stream().max(Comparator.comparing(x -> x.getReportList().size())).orElseThrow(NoSuchElementException::new);
						for (SnetAssetSwAuditReportDto dto : lastExcelReportDto.getReportList()) {

							if (StringUtils.isEmpty(dto.getItemNmDesc())) {

								dto.setItemNmDesc(dto.getItemNm());
							} // end if

						} // end for

						assetSwAuditReportList4 = lastExcelReportDto.getReportList();
						beans.put("rows" + sheetDivideCount, assetSwAuditReportList4);
						sheetDivideCount++;
					} // end if
				} // end for

				// 동적 sheet 생성
				for (int x = 0; x < SHEET_MAX_COUNT-diagnosisCount; x++) {

					beans.put("cols" + sheetDivideCount, excelReportList1);
					beans.put("rows" + sheetDivideCount, assetSwAuditReportList4);

					sheetDivideCount++;
				} // end for

				TreeMap<String, List<ExcelReportDto>> groupbySwNmVo =
						excelReportList1.stream().collect(Collectors.groupingBy(ExcelReportDto::getSwNm, TreeMap::new, Collectors.toList()));

				List<ExcelReportDto> tempExcelReportList = Lists.newArrayList();
				for (Map.Entry<String, List<ExcelReportDto>> grpSwNmVo : groupbySwNmVo.entrySet()) {

					String swNmKey = grpSwNmVo.getKey();
					List<ExcelReportDto> reportListValues = grpSwNmVo.getValue();
					int eaListSize = reportListValues.size();

					ExcelReportDto excelReportDto = new ExcelReportDto();
					excelReportDto.setSwNm(swNmKey);
					excelReportDto.setSwNmGrpSize(eaListSize);
					excelReportDto.setReportList(reportListValues.stream().findFirst().get().getReportList());

					tempExcelReportList.add(excelReportDto);
				} // end for

				// diagnosisCd 뒤 4자리로 grouping
				for (ExcelReportDto dtos : tempExcelReportList) {

					for (SnetAssetSwAuditReportDto reportDto : dtos.getReportList()) {

						String diagnosisCdKeyStr = StringUtils.mid(reportDto.getDiagnosisCd(),4,2);
						reportDto.setDiagnosisCdKey(diagnosisCdKeyStr);
					}
				} // end for

				for (ExcelReportDto dtos : tempExcelReportList) {

					for (SnetAssetSwAuditReportDto tempReportDto : dtos.getReportList()) {

						tempReportDto.setItemGrpNm(StringUtils.deleteWhitespace(tempReportDto.getItemGrpNm()));
					} // end for

					String swNmKey = dtos.getSwNm();

					int grpNmVoLen = 0;
					int assetSwAuditReportListSize = 0;
					int eaListSize = dtos.getSwNmGrpSize();

					TreeMap<String, List<SnetAssetSwAuditReportDto>> groupbyAssetSwAuditReportList4 =
							dtos.getReportList().stream().collect(Collectors.groupingBy(SnetAssetSwAuditReportDto::getDiagnosisCdKey, TreeMap::new, Collectors.toList()));

					for (Map.Entry<String, List<SnetAssetSwAuditReportDto>> grpNmVo : groupbyAssetSwAuditReportList4.entrySet()) {

						grpNmVoLen++;
					} // end for

					assetSwAuditReportListSize = dtos.getReportList().size();

					String checkAnalysisStr = swNmKey + " : " +
							checkAnalysisStandardDesc + " 취약점 분석 평가 기준 " + grpNmVoLen + " 개 분류 " + assetSwAuditReportListSize + " 개 항목 (장비 : " + eaListSize +"대)";
					checkAnalysisStrList.add(checkAnalysisStr);

					String itemTotalCheckTargets = assetSwAuditReportListSize + "개 x " + eaListSize + "EA";

					itemTotalCheckTargetsList.add(itemTotalCheckTargets);

				} // end for

				// excelReportList2 리스트 사이즈 개수에 따라 Ⅱ. 수행내역 sheet 에서 ◎ 점검 항목 : 과학기술정보통신부 리스트 생성해서

				// assetSwAuditReportList4 이 리스트 사용
				// 평가기준명 : fileType 의 desc
				// 평가기준 : excelReportList2 의 getReportList 에서 itemGrpNm grouping 개수
				// 분류항목 : excelReportList2 의 getReportList 리스트 사이즈

			} else {

				for (Map.Entry<String, List<SnetAssetSwAuditExcelDto>> reportDto : groupbyReportGrpNm.entrySet()) {

					String sheetStr = reportDto.getKey();
					beans.put("swNm" + sheetDivideCount, sheetStr);

					beans.put("cols" + sheetDivideCount, excelReportList1.stream().filter(x -> sheetStr.equals(x.getSwNm())).collect(Collectors.toList()));

					excelReportList2 = excelReportList1.stream().filter(x -> sheetStr.equals(x.getSwNm())).collect(Collectors.toList());

					if (excelReportList2 != null && excelReportList2.size() > 0) {

						ExcelReportDto lastExcelReportDto = excelReportList2.stream().max(Comparator.comparing(x -> x.getReportList().size())).orElseThrow(NoSuchElementException::new);
						for (SnetAssetSwAuditReportDto dto : lastExcelReportDto.getReportList()) {

							if (StringUtils.isEmpty(dto.getItemNmDesc())) {

								dto.setItemNmDesc(dto.getItemNm());
							} // end if

						} // end for

						assetSwAuditReportList4 = lastExcelReportDto.getReportList();
						beans.put("rows" + sheetDivideCount, assetSwAuditReportList4);
					}
				}

				// 동적 sheet 생성
				for (int x = 0; x < SHEET_MAX_COUNT; x++) {

					beans.put("cols" + sheetDivideCount, excelReportList1);
					beans.put("rows" + sheetDivideCount, assetSwAuditReportList4);

					sheetDivideCount++;
				} // end for

				TreeMap<String, List<ExcelReportDto>> groupbySwNmVo =
						excelReportList1.stream().collect(Collectors.groupingBy(ExcelReportDto::getSwNm, TreeMap::new, Collectors.toList()));

				List<ExcelReportDto> tempExcelReportList = Lists.newArrayList();
				for (Map.Entry<String, List<ExcelReportDto>> grpSwNmVo : groupbySwNmVo.entrySet()) {

					String swNmKey = grpSwNmVo.getKey();
					List<ExcelReportDto> reportListValues = grpSwNmVo.getValue();

					int eaListSize = reportListValues.size();

					ExcelReportDto excelReportDto = new ExcelReportDto();
					excelReportDto.setSwNm(swNmKey);
					excelReportDto.setSwNmGrpSize(eaListSize);
					excelReportDto.setReportList(reportListValues.stream().findFirst().get().getReportList());

					tempExcelReportList.add(excelReportDto);
				} // end for

				// diagnosisCd 뒤 4자리로 grouping
				for (ExcelReportDto dtos : tempExcelReportList) {

					for (SnetAssetSwAuditReportDto reportDto : dtos.getReportList()) {

						String diagnosisCdKeyStr = StringUtils.mid(reportDto.getDiagnosisCd(),4,2);
						reportDto.setDiagnosisCdKey(diagnosisCdKeyStr);
					}
				} // end for

				for (ExcelReportDto dtos : tempExcelReportList) {

					String swNmKey = dtos.getSwNm();

					int grpNmVoLen = 0;
					int assetSwAuditReportListSize = 0;
					int eaListSize = dtos.getSwNmGrpSize();

					TreeMap<String, List<SnetAssetSwAuditReportDto>> groupbyAssetSwAuditReportList4 =
							dtos.getReportList().stream().collect(Collectors.groupingBy(SnetAssetSwAuditReportDto::getDiagnosisCdKey, TreeMap::new, Collectors.toList()));

					for (Map.Entry<String, List<SnetAssetSwAuditReportDto>> grpNmVo : groupbyAssetSwAuditReportList4.entrySet()) {

						grpNmVoLen++;
					} // end for

					assetSwAuditReportListSize = dtos.getReportList().size();

					String checkAnalysisStr = swNmKey + " : " +
							checkAnalysisStandardDesc + " 취약점 분석 평가 기준 " + grpNmVoLen + " 개 분류 " + assetSwAuditReportListSize + " 개 항목 (장비 : " + eaListSize +"대)";
					checkAnalysisStrList.add(checkAnalysisStr);

					String itemTotalCheckTargets = assetSwAuditReportListSize + "개 x " + eaListSize + "EA";

					itemTotalCheckTargetsList.add(itemTotalCheckTargets);

				} // end for

				// excelReportList2 리스트 사이즈 개수에 따라 Ⅱ. 수행내역 sheet 에서 ◎ 점검 항목 : 과학기술정보통신부 리스트 생성해서

				// assetSwAuditReportList4 이 리스트 사용
				// 평가기준명 : fileType 의 desc
				// 평가기준 : excelReportList2 의 getReportList 에서 itemGrpNm grouping 개수
				// 분류항목 : excelReportList2 의 getReportList 리스트 사이즈

			} // end if else

			sheetMaps3.put("checkAnalysisStr", checkAnalysisStrList);
			beans.put("data3", sheetMaps3);

			String tempItemTotalCheckTargetsStr = StringUtils.join(itemTotalCheckTargetsList,",");

			sheetMaps5.put("itemTotalCheckTargetsStr", tempItemTotalCheckTargetsStr);
			beans.put("data5", sheetMaps5);

		} // end if (etcFlag && linuxFlag)

		// linuxFlag = false (Linux 는 여기 분기 무조건)
		else if (etcFlag && !linuxFlag) {

			gAssetSwJob.put("LNOT_FLAG", "Y");

			// 점검 대상 : n대
			int checkTargets = 0;
			gAssetSwJob.put("HOST_GRD_FLAG", "Y");
			List<SnetAssetSwAuditExcelDto> listNot = testMapper.selectAssetSwExcelListNew(gAssetSwJob);
			gAssetSwJob.remove("HOST_GRD_FLAG");

			if (listNot == null || listNot.size() == 0) {
				return;
			}

			List<SnetAssetSwAuditReportDto> reportDtoList = Lists.newArrayList();
			String tempAssetCd = null;
			String tempAuditDay = null;
			String tempSwType = null;
			for (SnetAssetSwAuditExcelDto excelDto : listNot) {

				tempAssetCd = excelDto.getAssetCd();
				Map paramMap = new HashMap();
				paramMap.put("startAuditDay", startAuditDay);
				paramMap.put("endAuditDay", endAuditDay);
				paramMap.put("SW_TYPE", excelDto.getSwType());
				paramMap.put("SW_NM", excelDto.getSwNm());
				paramMap.put("ASSET_CD", tempAssetCd);
				paramMap.put("FILE_TYPE", gAssetSwJob.get("FILE_TYPE"));

				SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetSwAuditReportNewGetMaxAuditDay(paramMap);

				if (reportDto1 != null) {
					tempAuditDay = reportDto1.getAuditDay();

					paramMap.put("AUDIT_DAY", tempAuditDay);
					SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(paramMap);

					reportDtoList.add(reportDto2);
				}
			} // end for

			checkTargets = reportDtoList.size();

			String asset_cd = null;
			String auditDay = null;

			String[] assetCdIn = new String[listNot.size()];
			int a = 0;
			for (SnetAssetSwAuditExcelDto assetCdDto : listNot) {

				assetCdIn[a] = assetCdDto.getAssetCd();
				a++;
			}
			gAssetSwJob.put("assetCdIn", assetCdIn);

			// 상세보고서 보안점검을 위한 최종진단일
			SnetAssetSwAuditExcelDto snetAssetSwAuditExcelDto = testMapper.selectAssetSwExcelGroupbyAuditday(gAssetSwJob);

			SnetAssetSwAuditExcelDto snetAssetSwAuditExcelDto2 = testMapper.selectAssetSwExcelGroupbyMaxAuditday(gAssetSwJob);

			auditDay = snetAssetSwAuditExcelDto2.getMaxAuditDay();
			asset_cd = snetAssetSwAuditExcelDto2.getAssetCd();

			String startAuditDayFormat = String.format("%s/%s/%s"
					, StringUtils.substring(startAuditDay, 0, 4)
					, StringUtils.substring(startAuditDay, 4, 6)
					, StringUtils.substring(startAuditDay, 6, 8));

			String endAuditDayFormat = String.format("%s/%s/%s"
					, StringUtils.substring(endAuditDay, 0, 4)
					, StringUtils.substring(endAuditDay, 4, 6)
					, StringUtils.substring(endAuditDay, 6, 8));

			Map sheetMaps1 = new HashMap();
			Map sheetMaps3 = new HashMap();
			Map sheetMaps5 = new HashMap();
			Map sheetMaps6 = new HashMap();

			if (linuxFlag) {
				sheetMaps1.put("swTypeStr", swTypeStr);
			} else {
				sheetMaps1.put("swTypeStr", "Linux");
			}
			sheetMaps1.put("startAuditDay", startAuditDayFormat);
			sheetMaps1.put("endAuditDay", endAuditDayFormat);
			beans.put("data1", sheetMaps1);

			// Ⅱ. 수행내역
			sheetMaps3.put("checkAnalysisStandardDesc", checkAnalysisStandardDesc); // -> 아래에서 사용
//            sheetMaps3.put("checkAnalysisStandard", checkAnalysisStandard); // 미사용
//            sheetMaps3.put("checkClassifyItems", checkClassifyItems); // 미사용
			sheetMaps3.put("checkTargets", checkTargets); // -> 아래에서 사용
//            beans.put("data3", sheetMaps3); // 미사용

			// Ⅲ. 점검 대상
			int ii2 = 0;
			for (SnetAssetSwAuditExcelDto vo : listNot) {

				vo.setRownum(++ii2);
			}
			listNot.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getRownum));
			beans.put("data4", listNot);

			// Ⅳ. 보안점검 결과_총평 (모든장비)
			// 점검항목수
			int itemTotal = 0;
			String create_date = null;
			if (listNot != null && listNot.size() > 0) {

				for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {
					if (StringUtils.equalsIgnoreCase(asset_cd, reportDto.getAssetCd()) && StringUtils.equalsIgnoreCase(auditDay, reportDto.getAuditDay())) {
						create_date = reportDto.getCreateDate();
						break;
					}
				}

				gAssetSwJob.put("ASSET_CD", asset_cd);
				gAssetSwJob.put("AUDIT_DAY", auditDay);

				SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(gAssetSwJob);

				gAssetSwJob.put("SW_TYPE", swTypeStr);
				gAssetSwJob.put("SW_NM", reportDto2.getSwNm());
				gAssetSwJob.put("CREATE_DATE", create_date);
				itemTotal = testMapper.getItemTotal(gAssetSwJob);
			} else {

				itemTotal = 0;
			}

			int weaktotal = 0;
			int weakLevelHigh = 0;
			int weakLevelMiddle = 0;
			int weakLevelLow = 0;

			// ◎ 위험도별 발견 취약점 수
			// excel list 조회
//            List<SnetAssetSwAuditExcelDto> getWeakLevelsParams2 = Lists.newArrayList();
			List<SnetAssetSwAuditExcelDto> getWeakLevelsParams5 = Lists.newArrayList();
			Map weakLevelsParamMap = new HashMap();

			for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

				weakLevelsParamMap.put("SW_TYPE", reportDto.getSwType());
				weakLevelsParamMap.put("SW_NM", reportDto.getSwNm());
				weakLevelsParamMap.put("ASSET_CD", reportDto.getAssetCd());
				weakLevelsParamMap.put("AUDIT_DAY", reportDto.getAuditDay());
				weakLevelsParamMap.put("CREATE_DATE", reportDto.getCreateDate());
				weakLevelsParamMap.put("REQ_CD", reportRequestDto.getReqCd());
				weakLevelsParamMap.put("startAuditDay", startAuditDay);
				weakLevelsParamMap.put("endAuditDay", endAuditDay);

				SnetAssetSwAuditExcelDto getWeakLevelsParams = testMapper.selectAssetSwExcelListGroupbyWeak(weakLevelsParamMap);

				// selectAssetSwExcelListGroupbyWeak 는 유지하고 not weak DTO 를 구해서 리스트에 add 할 것
				SnetAssetSwAuditExcelDto getWeakLevelsParamsNot = testMapper.selectAssetSwExcelListGroupbyNotWeak(weakLevelsParamMap);

				weakLevelsParamMap.remove("startAuditDay");
				weakLevelsParamMap.remove("endAuditDay");
				weakLevelsParamMap.remove("SW_TYPE");
				weakLevelsParamMap.remove("SW_NM");
				weakLevelsParamMap.remove("ASSET_CD");
				weakLevelsParamMap.remove("AUDIT_DAY");
				weakLevelsParamMap.remove("CREATE_DATE");
				weakLevelsParamMap.remove("REQ_CD");

				if (getWeakLevelsParams != null) {
					getWeakLevelsParams2.add(getWeakLevelsParams);
				}

				if (getWeakLevelsParams == null && getWeakLevelsParamsNot != null) {
					getWeakLevelsParams2.add(getWeakLevelsParamsNot);
				}
			} // end for

			List<List<SnetAssetSwAuditReportDto>> weakLevels = Lists.newArrayList();

			for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

				Map paramMap = new HashMap();
				paramMap.put("startAuditDay", startAuditDay);
				paramMap.put("endAuditDay", endAuditDay);
				paramMap.put("F_FLAG", "Y");
				paramMap.put("SW_TYPE", reportDto.getSwType());
				paramMap.put("SW_NM", reportDto.getSwNm());
				paramMap.put("ASSET_CD", reportDto.getAssetCd());
				paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
				paramMap.put("CREATE_DATE", reportDto.getCreateDate());

				List<SnetAssetSwAuditReportDto> tempReportDto = testMapper.getWeakLevels(paramMap);
				weakLevels.add(tempReportDto);
			}

			// 평가점수 합,평균
			int scoreLen = 0;
			double scoreSum = 0.0;
			int scoreAvg = 0;

			// ◎ 취약점 분석 평가 점수
			if (weakLevels == null && weakLevels.size() == 0) {

				weakLevelHigh = 0;
				weakLevelMiddle = 0;
				weakLevelLow = 0;
				weaktotal = 0;
			} else {

				for (List<SnetAssetSwAuditReportDto> weakLevel : weakLevels) {

					for (SnetAssetSwAuditReportDto weekReportDto : weakLevel) {

						if (StringUtils.equals("H", weekReportDto.getItemGrade())) {

							weakLevelHigh += weekReportDto.getCount();
						}
						if (StringUtils.equals("M", weekReportDto.getItemGrade())) {

							weakLevelMiddle += weekReportDto.getCount();
						}
						if (StringUtils.equals("L", weekReportDto.getItemGrade())) {

							weakLevelLow += weekReportDto.getCount();
						}
					}
				}

				weaktotal = weakLevelHigh + weakLevelMiddle + weakLevelLow;

				// AuditRate 오름차순 정렬
				getWeakLevelsParams2.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate));
				scoreLen = getWeakLevelsParams2.size();
				for (SnetAssetSwAuditExcelDto sumDto : getWeakLevelsParams2) {

					scoreSum += sumDto.getAuditRate() * 100;
				}

				if (scoreSum != 0 && scoreLen != 0) {
					scoreAvg = (int)scoreSum / scoreLen;
				} else {
					scoreAvg = 0;
				}
				for (SnetAssetSwAuditExcelDto dataHidden1Dto : getWeakLevelsParams2) {

					double auditRateAvg = scoreAvg / 100.0;

					dataHidden1Dto.setAuditRateAvg(auditRateAvg);
				}
			} // end if else

			sheetMaps5.put("itemTotal", itemTotal); // 항목전체
			sheetMaps5.put("checkTargets", checkTargets); // 장비대수
			sheetMaps5.put("weakLevelHigh", weakLevelHigh); // 상
			sheetMaps5.put("weakLevelMiddle", weakLevelMiddle); // 중
			sheetMaps5.put("weakLevelLow", weakLevelLow); // 하
			sheetMaps5.put("weaktotal", weaktotal); // 취약전체
			sheetMaps5.put("scoreAvg", scoreAvg); // 취약평가점수평균

			// -> to do test : 보고서 API
			List<DetailReportHostNmResultDto> resultHostNmList = Lists.newArrayList();
			List<DetailReportResultDto> resultDtoList = Lists.newArrayList();
			int tempWeakLevelHigh = 0;
			int tempWeakLevelMiddle = 0;
			int tempWeakLevelLow = 0;

			if (reportApiCheckFlag) {

				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
				httpHeaders.setContentType(MediaType.APPLICATION_JSON);

				String userId = reportRequestDto.getReqUser();
				String swTypeNmKeyStr = getSwTypeNmKey(swTypeStrVO.getSwType(), swTypeStrVO.getSwNm());
				ApiRequestDto apiRequestDto = ApiRequestDto.builder()
						.userId(userId)
						.swType(swTypeNmKeyStr)
						.startDay(startAuditDay)
						.endDay(endAuditDay)
						.build();

				UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(DETAIL_IMPL_URL)
						.queryParam("restApiKey", REST_API_KEY);

				HttpEntity<ApiRequestDto> httpEntity = new HttpEntity<>(apiRequestDto, httpHeaders);
				ApiResult apiResult = null;

				try {

					apiResult = rest.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, ApiResult.class).getBody();

					if (apiResult != null) {

						ModelMapper modelMapper = new ModelMapper();

						DetailReportDto tempDto = modelMapper.map(apiResult.getResultData(), DetailReportDto.class);

						tempWeakLevelHigh = tempDto.getPChkweakHighTotal();
						tempWeakLevelMiddle = tempDto.getPChkweakMidTotal();
						tempWeakLevelLow = tempDto.getPChkweakLowTotal();

						resultHostNmList = tempDto.getResultHostNmList().stream().collect(Collectors.toList());
						resultDtoList = tempDto.getResultDtoList().stream().collect(Collectors.toList());
					} // end if
				} catch (Exception e) {

					logger.error(e.toString());
				}

				// 보고서 생성시 값 vs 보고서 API 호출 값 비교
				if ( (weakLevelHigh == tempWeakLevelHigh) && (weakLevelMiddle == tempWeakLevelMiddle) && (weakLevelLow == tempWeakLevelLow) ) {

					weakLevelHigh = tempWeakLevelHigh;
					weakLevelMiddle = tempWeakLevelMiddle;
					weakLevelLow = tempWeakLevelLow;
				} // end if
			}

			SnetAssetSwAuditReportDto weakLevelsDto1 = new SnetAssetSwAuditReportDto();
			weakLevelsDto1.setItemGradeView("상");
			weakLevelsDto1.setCount(weakLevelHigh);
			SnetAssetSwAuditReportDto weakLevelsDto2 = new SnetAssetSwAuditReportDto();
			weakLevelsDto2.setItemGradeView("중");
			weakLevelsDto2.setCount(weakLevelMiddle);
			SnetAssetSwAuditReportDto weakLevelsDto3 = new SnetAssetSwAuditReportDto();
			weakLevelsDto3.setItemGradeView("하");
			weakLevelsDto3.setCount(weakLevelLow);
			weakLevelsList.add(weakLevelsDto1);
			weakLevelsList.add(weakLevelsDto2);
			weakLevelsList.add(weakLevelsDto3);

			String lowTargetStr = null;
			String lowTargetAssetCd = null;

			getWeakLevelsParams5 = getWeakLevelsParams2.stream().collect(Collectors.toList());
			getWeakLevelsParams5.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate));

			if (getWeakLevelsParams5 != null && getWeakLevelsParams5.size() > 0) {

				lowTargetStr = getWeakLevelsParams5.stream().findFirst().get().getHostNm();
				lowTargetAssetCd = getWeakLevelsParams5.stream().findFirst().get().getAssetCd();

				if (StringUtils.isEmpty(lowTargetStr)) {

					for (SnetAssetSwAuditExcelDto excelDto : listNot) {

						if (StringUtils.equalsIgnoreCase(lowTargetAssetCd, excelDto.getAssetCd())) {

							lowTargetStr = excelDto.getHostNm();
							getWeakLevelsParams5.stream().findFirst().get().setHostNm(lowTargetStr);
						}
					}
				}
				sheetMaps5.put("lowTarget", lowTargetStr); // 취약평가가장낮음
			}
//            beans.put("data5", sheetMaps5); // -> 아래에서 처리

			// -> 미사용. apache poi 로 변경
//            beans.put("dataHidden1", getWeakLevelsParams2);
//            beans.put("dataHidden2", weakLevelsList);

			// 점검 대상별 취약점 수
			getWeakLevelsParams3 = getWeakLevelsParams2.stream().filter(x -> "F".equals(x.getItemResult())).collect(Collectors.toList());

			getWeakLevelsParams3.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getWeakCount).reversed());
			getWeakLevelsParams3 = getWeakLevelsParams3.stream().limit(10).collect(Collectors.toList());

			// -> 미사용. apache poi 로 변경
//            beans.put("dataHidden4", getWeakLevelsParams3);

			// V. 보안점검 결과_요약 sheet : hidden row 데이터 참고
			sheetMaps6.put("itemTotal", itemTotal);

			SnetAssetSwAuditExcelDto topOne = new SnetAssetSwAuditExcelDto();

			if (getWeakLevelsParams3 != null && getWeakLevelsParams3.size() > 0) {

				topOne = getWeakLevelsParams3.stream().findFirst().get();
			} else {
				topOne = new SnetAssetSwAuditExcelDto();
				topOne.setHostNm("NONE");
			}
			String topHostNm = topOne.getHostNm();

			// topHostNm -> lowTarget
			sheetMaps6.put("topHostNm", topHostNm);

			int weekLen = 0;
			int weekSum = 0;
			int weekAvg = 0;
			weekLen = getWeakLevelsParams2 == null ? 0 : getWeakLevelsParams2.size();

			if (weekLen != 0) {

				for (SnetAssetSwAuditExcelDto sumDto : getWeakLevelsParams2) {

					weekSum += sumDto.getWeakCount();
				}
				weekAvg = weekSum / weekLen;
			} else {

				weekAvg = 0;
			}

			sheetMaps6.put("weekTopOne", topOne.getWeakCount());
			sheetMaps6.put("weekAvg", weekAvg);


			int grpNmLen = 0;
			List<List<SnetAssetSwAuditReportDto>> groupbyReportGrpNmLists = Lists.newArrayList();

			for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

				Map paramMap = new HashMap();
				paramMap.put("startAuditDay", startAuditDay);
				paramMap.put("endAuditDay", endAuditDay);
				paramMap.put("SW_TYPE", reportDto.getSwType());
				paramMap.put("SW_NM", reportDto.getSwNm());
				paramMap.put("ASSET_CD", reportDto.getAssetCd());
				paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
				paramMap.put("CREATE_DATE", reportDto.getCreateDate());
				List<SnetAssetSwAuditReportDto> groupbyReportGrpNmList = testMapper.getGroupbyReportGrpNmList(paramMap);
				groupbyReportGrpNmLists.add(groupbyReportGrpNmList);
			}

			int tempWeekCnt = 0;
			int dotIndexOf = 0;
			String substrGrpNm = null;
			List<SnetAssetSwAuditReportDto> hiddenList3 = Lists.newArrayList();
//            List<SnetAssetSwAuditReportDto> hiddenList4 = Lists.newArrayList();
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
								substrGrpNm = StringUtils.substring(dto3.getItemGrpNm(), dotIndexOf + 2);
							} else {
								substrGrpNm = dto3.getItemGrpNm();
							}

							break;
						} else {

							tempWeekCnt = 0;
							dotIndexOf = StringUtils.indexOf(dto3.getItemGrpNm(), ".");
							if (dotIndexOf > -1) {
								substrGrpNm = StringUtils.substring(dto3.getItemGrpNm(), dotIndexOf + 2);
							} else {
								substrGrpNm = dto3.getItemGrpNm();
							}

						}
					}

					hiddenDto3.setWeakCount(tempWeekCnt);
					hiddenDto3.setItemGrpNm(StringUtils.deleteWhitespace(substrGrpNm));

					hiddenList3.add(hiddenDto3);
				}
			}

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

			hiddenList4.sort(Comparator.comparing(SnetAssetSwAuditReportDto::getWeakCount).reversed());
			sheetMaps6.put("grpNmLen", grpNmLen);

			String grpNmTopWeek = null;

			if (hiddenList4 != null && hiddenList4.size() > 0) {

				grpNmTopWeek = hiddenList4 == null && hiddenList4.size() == 0 ? null : hiddenList4.stream().findFirst().get().getItemGrpNm();
			}
			String grpNmTopWeekStr = null;

			if (StringUtils.isNotEmpty(grpNmTopWeek)) {

				grpNmTopWeekStr = grpNmTopWeek;

			} else {

				grpNmTopWeekStr = null;
			}
			sheetMaps6.put("grpNmTopWeek", grpNmTopWeekStr);

			int grpNmTopWeekCount = 0;
			SnetAssetSwAuditReportDto snetAssetSwAuditReportDto = null;

			try {
				snetAssetSwAuditReportDto = hiddenList4.stream()
						.max(Comparator.comparing(SnetAssetSwAuditReportDto::getWeakCount)).orElseThrow(NoSuchElementException::new);

				grpNmTopWeekCount = snetAssetSwAuditReportDto.getWeakCount();
			} catch (NoSuchElementException ne) {

				grpNmTopWeekCount = 0;
			}

			sheetMaps6.put("grpNmTopWeekCount", grpNmTopWeekCount);

			// 분류 항목별 취약점 수
			// -> to do test : 보고서 API

			if (hiddenList4 != null && hiddenList4.size() > 0 && resultDtoList != null && resultDtoList.size() > 0
					&& hiddenList4.size() == resultDtoList.size()) {

				for (int i = 0; i < resultDtoList.size(); i++) {

					DetailReportResultDto tempDto = resultDtoList.get(i);
					SnetAssetSwAuditReportDto targetDto = hiddenList4.get(i);

					if (StringUtils.equals(targetDto.getItemGrpNm(), tempDto.getItemGrpNm())) {
						targetDto.setItemGrpNm(tempDto.getItemGrpNm());
					}

					if (targetDto.getWeakCount() == tempDto.getPChkweakTotalCnt()) {
						targetDto.setWeakCount(tempDto.getPChkweakTotalCnt());
					}

				} // end for
			} // end if

			// ◎ 분류 항목별 취약점 수
			Long grpNmTopWeekCountAvg = 0L;
			double grpNmTopWeekCountDbl = 0L;

			if (hiddenList4 != null && hiddenList4.size() > 0) {
				OptionalDouble asDouble = hiddenList4.stream()
						.mapToInt(x -> x.getWeakCount())
						.average();
				grpNmTopWeekCountDbl = asDouble.getAsDouble();
				grpNmTopWeekCountAvg = Math.round(grpNmTopWeekCountDbl);

			} else {

				grpNmTopWeekCountAvg = 0L;
			}
			sheetMaps6.put("grpNmTopWeekCountAvg", grpNmTopWeekCountAvg);
			beans.put("data6", sheetMaps6);

			// (hidden) 분류 항목별 취약점
//            beans.put("dataHidden3", hiddenList4);

			gAssetSwJob.put("startAuditDay", startAuditDay);
			gAssetSwJob.put("endAuditDay", endAuditDay);
			gAssetSwJob.put("BET_FLAG", "Y");

			// jXLS 값 매핑
			List<SnetAssetSwAuditReportDto> assetSwAuditReportListDetail1 = null;
			List<SnetAssetSwAuditReportDto> assetSwAuditReportListDetail2 = Lists.newArrayList();

			// 점검대상 : report max audit_day
			// Ⅵ. 보안점검 결과 전체
			// Ⅵ. 보안점검 결과 전체(swNm추가)
			int listAddIndex = 0;


			// Ⅵ. 보안점검 결과 전체 (swNm / swInfo) sheet 생성을 위함
			// (1) swNm 또는 swInfo 에 따라 분기
			TreeMap<String, List<SnetAssetSwAuditExcelDto>> groupbyReportGrpNm = null;

			if (StringUtils.equalsIgnoreCase("Linux", swTypeStr) || StringUtils.equalsIgnoreCase("Windows", swTypeStr)) {


				groupbyReportGrpNm =
						listNot.stream().collect(Collectors.groupingBy(SnetAssetSwAuditExcelDto::getSwNm, TreeMap::new, Collectors.toList()));

				diagnosisCount = groupbyReportGrpNm.entrySet().size();
			} else {
				// (2) swNm 또는 swInfo 에 대해 grouping - SwNm

				groupbyReportGrpNm =
						listNot.stream().collect(Collectors.groupingBy(SnetAssetSwAuditExcelDto::getSwNm, TreeMap::new, Collectors.toList()));

				diagnosisCount = groupbyReportGrpNm.entrySet().size();
			} // end if else

			for (int ij2 = 0; ij2 < listNot.size(); ij2++) {

				if (listNot.get(ij2).getAssetCd() != null && listNot.get(ij2).getAuditDay() != null && listNot.get(ij2).getSwNm() != null) {

					ExcelReportDto vo = new ExcelReportDto();
					Map reportMap = new HashMap();

					String etcSwType = listNot.get(ij2).getSwType();
					String etsSwNm = listNot.get(ij2).getSwNm();
					String sheetNm2 = listNot.get(ij2).getIpAddress();
					String hostNm = listNot.get(ij2).getHostNm();
					String assetCdReport = listNot.get(ij2).getAssetCd();

					reportMap.put("startAuditDay", startAuditDay);
					reportMap.put("endAuditDay", endAuditDay);
					reportMap.put("SW_INFO", listNot.get(ij2).getSwInfo());
					reportMap.put("SW_DIR", listNot.get(ij2).getSwDir());
					reportMap.put("SW_USER", listNot.get(ij2).getSwUser());
					reportMap.put("SW_ETC", listNot.get(ij2).getSwEtc());
					reportMap.put("SW_TYPE", etcSwType);
					reportMap.put("SW_NM", etsSwNm);
					reportMap.put("ASSET_CD", assetCdReport);

					SnetAssetSwAuditReportDto auditDayForReport = testMapper.selectAssetSwReportGroupbyAuditday(reportMap);

					reportMap.put("AUDIT_DAY", auditDayForReport.getAuditDay());
					reportMap.put("CREATE_DATE", auditDayForReport.getCreateDate());

					assetSwAuditReportListDetail1 = testMapper.selectAssetSwAuditReportNew(reportMap);

					assetSwAuditReportListDetail2 = assetSwAuditReportListDetail1.stream().map(this::converToDto).collect(Collectors.toList());

					if (assetSwAuditReportListDetail2 != null && assetSwAuditReportListDetail2.size() > 0) {

						vo.setSwNm(etsSwNm);

						// -> to do test : setHostNm 에 HYPERLINK 추가
						vo.setHostNm(hostNm + "(" + sheetNm2 + ")");
						vo.setTxtFileNm(hostNm + "(" + sheetNm2 + "_" + etsSwNm + ")");

						vo.setDiagnosisCd(assetSwAuditReportListDetail2.stream().findFirst().get().getDiagnosisCd());
						vo.setReportList(assetSwAuditReportListDetail2);

						excelReportList1.add(vo);
					} // end if
				} // end if
			} // end for

			int sheetDivideCount = 1;
			List<String> checkAnalysisStrList = new ArrayList<>();
			List<String> itemTotalCheckTargetsList = new ArrayList<>();

			if (diagnosisCount > 1) {

				for (Map.Entry<String, List<SnetAssetSwAuditExcelDto>> reportDto : groupbyReportGrpNm.entrySet()) {

					String sheetStr = reportDto.getKey();
					beans.put("swNm" + sheetDivideCount, sheetStr);

					beans.put("cols" + sheetDivideCount, excelReportList1.stream().filter(x -> sheetStr.equals(x.getSwNm())).collect(Collectors.toList()));

					excelReportList2 = excelReportList1.stream().filter(x -> sheetStr.equals(x.getSwNm())).collect(Collectors.toList());

					if (excelReportList2 != null && excelReportList2.size() > 0) {

						ExcelReportDto lastExcelReportDto = excelReportList2.stream().max(Comparator.comparing(x -> x.getReportList().size())).orElseThrow(NoSuchElementException::new);
						assetSwAuditReportList4 = lastExcelReportDto.getReportList();
						beans.put("rows" + sheetDivideCount, assetSwAuditReportList4);

						sheetDivideCount++;
					}
				} // end for

				for (int x = 0; x < SHEET_MAX_COUNT-diagnosisCount; x++) {

					beans.put("cols" + sheetDivideCount, excelReportList1);
					beans.put("rows" + sheetDivideCount, assetSwAuditReportList4);

					sheetDivideCount++;
				} // end for

				TreeMap<String, List<ExcelReportDto>> groupbySwNmVo =
						excelReportList1.stream().collect(Collectors.groupingBy(ExcelReportDto::getSwNm, TreeMap::new, Collectors.toList()));

				List<ExcelReportDto> tempExcelReportList = Lists.newArrayList();
				for (Map.Entry<String, List<ExcelReportDto>> grpSwNmVo : groupbySwNmVo.entrySet()) {

					String swNmKey = grpSwNmVo.getKey();
					List<ExcelReportDto> reportListValues = grpSwNmVo.getValue();

					int eaListSize = reportListValues.size();

					ExcelReportDto excelReportDto = new ExcelReportDto();
					excelReportDto.setSwNm(swNmKey);
					excelReportDto.setSwNmGrpSize(eaListSize);
					excelReportDto.setReportList(reportListValues.stream().findFirst().get().getReportList());

					tempExcelReportList.add(excelReportDto);
				} // end for

				// diagnosisCd 뒤 4자리로 grouping
				for (ExcelReportDto dtos : tempExcelReportList) {

					for (SnetAssetSwAuditReportDto reportDto : dtos.getReportList()) {

						String diagnosisCdKeyStr = StringUtils.mid(reportDto.getDiagnosisCd(),4,2);
						reportDto.setDiagnosisCdKey(diagnosisCdKeyStr);
					}
				} // end for

				// excelReportList2 리스트 사이즈 개수에 따라 Ⅱ. 수행내역 sheet 에서 ◎ 점검 항목 : 과학기술정보통신부 리스트 생성해서

				// assetSwAuditReportList4 이 리스트 사용
				// 평가기준명 : fileType 의 desc
				// 평가기준 : excelReportList2 의 getReportList 에서 itemGrpNm grouping 개수
				// 분류항목 : excelReportList2 의 getReportList 리스트 사이즈

			} else {

				for (Map.Entry<String, List<SnetAssetSwAuditExcelDto>> reportDto : groupbyReportGrpNm.entrySet()) {

					String sheetStr = reportDto.getKey();

					beans.put("swNm" + sheetDivideCount, sheetStr);

					beans.put("cols" + sheetDivideCount, excelReportList1.stream().filter(x -> sheetStr.equals(x.getSwNm())).collect(Collectors.toList()));

					excelReportList2 = excelReportList1.stream().filter(x -> sheetStr.equals(x.getSwNm())).collect(Collectors.toList());

					if (excelReportList2 != null && excelReportList2.size() > 0) {

						ExcelReportDto lastExcelReportDto = excelReportList2.stream().max(Comparator.comparing(x -> x.getReportList().size())).orElseThrow(NoSuchElementException::new);
						assetSwAuditReportList4 = lastExcelReportDto.getReportList();
						beans.put("rows" + sheetDivideCount, assetSwAuditReportList4);
					} // end if
				}

				for (int x = 0; x < SHEET_MAX_COUNT; x++) {

					beans.put("cols" + sheetDivideCount, excelReportList1);
					beans.put("rows" + sheetDivideCount, assetSwAuditReportList4);

					sheetDivideCount++;
				} // end for
			} // end if else

			TreeMap<String, List<ExcelReportDto>> groupbySwNmVo =
					excelReportList1.stream().collect(Collectors.groupingBy(ExcelReportDto::getSwNm, TreeMap::new, Collectors.toList()));

			List<ExcelReportDto> tempExcelReportList = Lists.newArrayList();
			for (Map.Entry<String, List<ExcelReportDto>> grpSwNmVo : groupbySwNmVo.entrySet()) {

				String swNmKey = grpSwNmVo.getKey();
				List<ExcelReportDto> reportListValues = grpSwNmVo.getValue();

				int eaListSize = reportListValues.size();

				ExcelReportDto excelReportDto = new ExcelReportDto();
				excelReportDto.setSwNm(swNmKey);
				excelReportDto.setSwNmGrpSize(eaListSize);
				excelReportDto.setReportList(reportListValues.stream().findFirst().get().getReportList());

				tempExcelReportList.add(excelReportDto);
			} // end for

			// diagnosisCd 뒤 4자리로 grouping
			for (ExcelReportDto dtos : tempExcelReportList) {

				for (SnetAssetSwAuditReportDto reportDto : dtos.getReportList()) {

					String diagnosisCdKeyStr = StringUtils.mid(reportDto.getDiagnosisCd(),4,2);
					reportDto.setDiagnosisCdKey(diagnosisCdKeyStr);
				}
			} // end for

			for (ExcelReportDto dtos : tempExcelReportList) {

				String swNmKey = dtos.getSwNm();

				int grpNmVoLen = 0;
				int assetSwAuditReportListSize = 0;
				int eaListSize = dtos.getSwNmGrpSize();

				TreeMap<String, List<SnetAssetSwAuditReportDto>> groupbyAssetSwAuditReportList4 =
						dtos.getReportList().stream().collect(Collectors.groupingBy(SnetAssetSwAuditReportDto::getDiagnosisCdKey, TreeMap::new, Collectors.toList()));

				for (Map.Entry<String, List<SnetAssetSwAuditReportDto>> grpNmVo : groupbyAssetSwAuditReportList4.entrySet()) {

					grpNmVoLen++;
				} // end for

				assetSwAuditReportListSize = dtos.getReportList().size();

				String checkAnalysisStr = swNmKey + " : " +
						checkAnalysisStandardDesc + " 취약점 분석 평가 기준 " + grpNmVoLen + " 개 분류 " + assetSwAuditReportListSize + " 개 항목 (장비 : " + eaListSize +"대)";
				checkAnalysisStrList.add(checkAnalysisStr);

				String itemTotalCheckTargets = assetSwAuditReportListSize + "개 x " + eaListSize + "EA";

				itemTotalCheckTargetsList.add(itemTotalCheckTargets);

			} // end for

			// excelReportList2 리스트 사이즈 개수에 따라 Ⅱ. 수행내역 sheet 에서 ◎ 점검 항목 : 과학기술정보통신부 리스트 생성해서

			// assetSwAuditReportList4 이 리스트 사용
			// 평가기준명 : fileType 의 desc
			// 평가기준 : excelReportList2 의 getReportList 에서 itemGrpNm grouping 개수
			// 분류항목 : excelReportList2 의 getReportList 리스트 사이즈

			sheetMaps3.put("checkAnalysisStr", checkAnalysisStrList);
			beans.put("data3", sheetMaps3);

			String tempItemTotalCheckTargetsStr = StringUtils.join(itemTotalCheckTargetsList,",");

			sheetMaps5.put("itemTotalCheckTargetsStr", tempItemTotalCheckTargetsStr);
			beans.put("data5", sheetMaps5);

		} // end if etcFlag = false & linuxFlag = false

		// etcFlag = false
		else {

			gAssetSwJob.put("NOT_FLAG", "Y");

			// 점검 대상 : n대
			int checkTargets = 0;
			gAssetSwJob.put("HOST_GRD_FLAG", "Y");
			List<SnetAssetSwAuditExcelDto> listEtc = testMapper.selectAssetSwExcelListNew(gAssetSwJob);
			gAssetSwJob.remove("HOST_GRD_FLAG");

			// 점검대상이 없으면 해당 ETC 점검군에 대하여 보고서파일 생성하지 않음
			if (listEtc == null || listEtc.size() == 0) {
				return;
			} // end if

			List<SnetAssetSwAuditReportDto> reportDtoList = Lists.newArrayList();
			String tempAssetCd = null;
			String tempAuditDay = null;
			String tempSwType = null;
			for (SnetAssetSwAuditExcelDto excelDto : listEtc) {

				tempAssetCd = excelDto.getAssetCd();
				Map paramMap = new HashMap();
				paramMap.put("startAuditDay", startAuditDay);
				paramMap.put("endAuditDay", endAuditDay);
				paramMap.put("SW_TYPE", getEtcSwTypeStr(excelDto.getSwType()));

				if (StringUtils.equals("OS", excelDto.getSwType()) && StringUtils.equals("mobile_os", excelDto.getSwNm())) {
					paramMap.put("SW_NM", excelDto.getSwNm());
				}
				paramMap.put("ASSET_CD", tempAssetCd);
				paramMap.put("FILE_TYPE", gAssetSwJob.get("FILE_TYPE"));

				SnetAssetSwAuditReportDto reportDto1 = testMapper.selectAssetSwAuditReportNewGetMaxAuditDay(paramMap);

				if (reportDto1 != null) {
					tempAuditDay = reportDto1.getAuditDay();

					paramMap.put("AUDIT_DAY", tempAuditDay);
					SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(paramMap);
					reportDtoList.add(reportDto2);
				}
			} // end for

			checkTargets = reportDtoList.size();

			String asset_cd = null;
			String auditDay = null;
			String etcSwtype = null;

			String[] assetCdIn = new String[listEtc.size()];
			int a = 0;
			for (SnetAssetSwAuditExcelDto assetCdDto : listEtc) {

				assetCdIn[a] = assetCdDto.getAssetCd();
				a++;
			}
			gAssetSwJob.put("assetCdIn", assetCdIn);

			// 상세보고서 보안점검을 위한 최종진단일
			SnetAssetSwAuditExcelDto snetAssetSwAuditExcelDto = testMapper.selectAssetSwExcelGroupbyAuditday(gAssetSwJob);

			SnetAssetSwAuditExcelDto snetAssetSwAuditExcelDto2 = testMapper.selectAssetSwExcelGroupbyMaxAuditday(gAssetSwJob);

			auditDay = snetAssetSwAuditExcelDto2.getMaxAuditDay();
			asset_cd = snetAssetSwAuditExcelDto2.getAssetCd();
			startAuditDay = snetAssetSwAuditExcelDto.getMinAuditDay();
			endAuditDay = snetAssetSwAuditExcelDto2.getMaxAuditDay();

			String startAuditDayFormat = String.format("%s/%s/%s"
					, StringUtils.substring(startAuditDay, 0, 4)
					, StringUtils.substring(startAuditDay, 4, 6)
					, StringUtils.substring(startAuditDay, 6, 8));

			String endAuditDayFormat = String.format("%s/%s/%s"
					, StringUtils.substring(endAuditDay, 0, 4)
					, StringUtils.substring(endAuditDay, 4, 6)
					, StringUtils.substring(endAuditDay, 6, 8));

			Map sheetMaps1 = new HashMap();
			Map sheetMaps3 = new HashMap();
			Map sheetMaps5 = new HashMap();
			Map sheetMaps6 = new HashMap();

			etcSwtype = swTypeStr;

			if (!etcFlag) {

				sheetMaps1.put("swTypeStr", "기타");
			}
			sheetMaps1.put("startAuditDay", startAuditDayFormat);
			sheetMaps1.put("endAuditDay", endAuditDayFormat);

			beans.put("data1", sheetMaps1);

			// Ⅱ. 수행내역
			sheetMaps3.put("checkAnalysisStandardDesc", checkAnalysisStandardDesc); // -> 아래에서 사용
//            sheetMaps3.put("checkAnalysisStandard", checkAnalysisStandard); // 미사용
//            sheetMaps3.put("checkClassifyItems", checkClassifyItems); // 미사용
			sheetMaps3.put("checkTargets", checkTargets); // -> 아래에서 사용
//            beans.put("data3", sheetMaps3); // 미사용

			// Ⅲ. 점검 대상
			int ii2 = 0;
			for (SnetAssetSwAuditExcelDto vo : listEtc) {

				vo.setRownum(++ii2);
			}
			listEtc.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getRownum));
			beans.put("data4", listEtc);

			// Ⅳ. 보안점검 결과_총평 (모든장비)
			// 점검항목수
			int itemTotal = 0;
			if (listEtc != null && listEtc.size() > 0) {
				gAssetSwJob.put("ASSET_CD", asset_cd);
				gAssetSwJob.put("AUDIT_DAY", auditDay);

				SnetAssetSwAuditReportDto reportDto2 = testMapper.selectAssetSwAuditReportNewGet(gAssetSwJob);

				gAssetSwJob.put("SW_TYPE", reportDto2.getSwType());
				itemTotal = testMapper.getItemTotal(gAssetSwJob);
			} else {

				itemTotal = 0;
			}

			int weaktotal = 0;
			int weakLevelHigh = 0;
			int weakLevelMiddle = 0;
			int weakLevelLow = 0;

			// ◎ 위험도별 발견 취약점 수
			// excel list 조회
//            List<SnetAssetSwAuditExcelDto> getWeakLevelsParams2 = Lists.newArrayList();
			List<SnetAssetSwAuditExcelDto> getWeakLevelsParams5 = Lists.newArrayList();
			Map weakLevelsParamMap = new HashMap();

			for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

				weakLevelsParamMap.put("startAuditDay", startAuditDay);
				weakLevelsParamMap.put("endAuditDay", endAuditDay);
				weakLevelsParamMap.put("SW_TYPE", reportDto.getSwType());
				weakLevelsParamMap.put("SW_NM", reportDto.getSwNm());
				weakLevelsParamMap.put("ASSET_CD", reportDto.getAssetCd());
				weakLevelsParamMap.put("AUDIT_DAY", reportDto.getAuditDay());
				weakLevelsParamMap.put("CREATE_DATE", reportDto.getCreateDate());
				weakLevelsParamMap.put("REQ_CD", reportRequestDto.getReqCd());

				SnetAssetSwAuditExcelDto getWeakLevelsParams = testMapper.selectAssetSwExcelListGroupbyWeak(weakLevelsParamMap);

				SnetAssetSwAuditExcelDto getWeakLevelsParamsNot = testMapper.selectAssetSwExcelListGroupbyNotWeak(weakLevelsParamMap);

				weakLevelsParamMap.remove("SW_TYPE");
				weakLevelsParamMap.remove("SW_NM");
				weakLevelsParamMap.remove("ASSET_CD");
				weakLevelsParamMap.remove("AUDIT_DAY");
				weakLevelsParamMap.remove("CREATE_DATE");
				weakLevelsParamMap.remove("REQ_CD");
				weakLevelsParamMap.remove("startAuditDay");
				weakLevelsParamMap.remove("endAuditDay");

				if (getWeakLevelsParams != null) {
					getWeakLevelsParams2.add(getWeakLevelsParams);
				}

				if (getWeakLevelsParams == null && getWeakLevelsParamsNot != null) {
					getWeakLevelsParams2.add(getWeakLevelsParamsNot);
				}
			} // end for

			List<List<SnetAssetSwAuditReportDto>> weakLevels = Lists.newArrayList();
			for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

				Map paramMap = new HashMap();
				paramMap.put("startAuditDay", startAuditDay);
				paramMap.put("endAuditDay", endAuditDay);
				paramMap.put("F_FLAG", "Y");
				paramMap.put("SW_TYPE", reportDto.getSwType());
				paramMap.put("ASSET_CD", reportDto.getAssetCd());
				paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
				List<SnetAssetSwAuditReportDto> tempReportDto = testMapper.getWeakLevels(paramMap);
				weakLevels.add(tempReportDto);
			}

			// 평가점수 합,평균
			int scoreLen = 0;
			double scoreSum = 0.0;
			int scoreAvg = 0;

			// ◎ 취약점 분석 평가 점수
			if (weakLevels == null && weakLevels.size() == 0) {

				weakLevelHigh = 0;
				weakLevelMiddle = 0;
				weakLevelLow = 0;
				weaktotal = 0;
			} else {

				for (List<SnetAssetSwAuditReportDto> weakLevel : weakLevels) {

					for (SnetAssetSwAuditReportDto weekReportDto : weakLevel) {

						if (StringUtils.equals("H", weekReportDto.getItemGrade())) {

							weakLevelHigh += weekReportDto.getCount();
						}
						if (StringUtils.equals("M", weekReportDto.getItemGrade())) {

							weakLevelMiddle += weekReportDto.getCount();
						}
						if (StringUtils.equals("L", weekReportDto.getItemGrade())) {

							weakLevelLow += weekReportDto.getCount();
						}
					}
				}

				weaktotal = weakLevelHigh + weakLevelMiddle + weakLevelLow;

				// AuditRate 오름차순 정렬
				getWeakLevelsParams2.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate));
				scoreLen = getWeakLevelsParams2.size();
				for (SnetAssetSwAuditExcelDto sumDto : getWeakLevelsParams2) {

					scoreSum += sumDto.getAuditRate() * 100;
				}

				if (scoreSum != 0 && scoreLen != 0) {
					scoreAvg = (int)scoreSum / scoreLen;
				} else {
					scoreAvg = 0;
				}
				for (SnetAssetSwAuditExcelDto dataHidden1Dto : getWeakLevelsParams2) {

					double auditRateAvg = scoreAvg / 100.0;

					dataHidden1Dto.setAuditRateAvg(auditRateAvg);
				}
			} // end if else

			sheetMaps5.put("itemTotal", itemTotal); // 항목전체
			sheetMaps5.put("checkTargets", checkTargets); // 장비대수
			sheetMaps5.put("weakLevelHigh", weakLevelHigh); // 상
			sheetMaps5.put("weakLevelMiddle", weakLevelMiddle); // 중
			sheetMaps5.put("weakLevelLow", weakLevelLow); // 하
			sheetMaps5.put("weaktotal", weaktotal); // 취약전체
			sheetMaps5.put("scoreAvg", scoreAvg); // 취약평가점수평균

			// -> to do test : 보고서 API
			List<DetailReportHostNmResultDto> resultHostNmList = Lists.newArrayList();
			List<DetailReportResultDto> resultDtoList = Lists.newArrayList();
			int tempWeakLevelHigh = 0;
			int tempWeakLevelMiddle = 0;
			int tempWeakLevelLow = 0;

			if (reportApiCheckFlag) {

				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
				httpHeaders.setContentType(MediaType.APPLICATION_JSON);

				String userId = reportRequestDto.getReqUser();
				String swTypeNmKeyStr = getSwTypeNmKey(swTypeStrVO.getSwType(), swTypeStrVO.getSwNm());
				ApiRequestDto apiRequestDto = ApiRequestDto.builder()
						.userId(userId)
						.swType(swTypeNmKeyStr)
						.startDay(startAuditDay)
						.endDay(endAuditDay)
						.build();

				UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(DETAIL_IMPL_URL)
						.queryParam("restApiKey", REST_API_KEY);

				HttpEntity<ApiRequestDto> httpEntity = new HttpEntity<>(apiRequestDto, httpHeaders);
				ApiResult apiResult = null;

				try {

					apiResult = rest.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, ApiResult.class).getBody();

					if (apiResult != null) {

						ModelMapper modelMapper = new ModelMapper();

						DetailReportDto tempDto = modelMapper.map(apiResult.getResultData(), DetailReportDto.class);

						tempWeakLevelHigh = tempDto.getPChkweakHighTotal();
						tempWeakLevelMiddle = tempDto.getPChkweakMidTotal();
						tempWeakLevelLow = tempDto.getPChkweakLowTotal();

						resultHostNmList = tempDto.getResultHostNmList().stream().collect(Collectors.toList());
						resultDtoList = tempDto.getResultDtoList().stream().collect(Collectors.toList());
					} // end if
				} catch (Exception e) {

					logger.error(e.toString());
				}

				// 보고서 생성시 값 vs 보고서 API 호출 값 비교
				if ( (weakLevelHigh == tempWeakLevelHigh) && (weakLevelMiddle == tempWeakLevelMiddle) && (weakLevelLow == tempWeakLevelLow) ) {

					weakLevelHigh = tempWeakLevelHigh;
					weakLevelMiddle = tempWeakLevelMiddle;
					weakLevelLow = tempWeakLevelLow;
				} // end if
			}

			SnetAssetSwAuditReportDto weakLevelsDto1 = new SnetAssetSwAuditReportDto();
			weakLevelsDto1.setItemGradeView("상");
			weakLevelsDto1.setCount(weakLevelHigh);
			SnetAssetSwAuditReportDto weakLevelsDto2 = new SnetAssetSwAuditReportDto();
			weakLevelsDto2.setItemGradeView("중");
			weakLevelsDto2.setCount(weakLevelMiddle);
			SnetAssetSwAuditReportDto weakLevelsDto3 = new SnetAssetSwAuditReportDto();
			weakLevelsDto3.setItemGradeView("하");
			weakLevelsDto3.setCount(weakLevelLow);
			weakLevelsList.add(weakLevelsDto1);
			weakLevelsList.add(weakLevelsDto2);
			weakLevelsList.add(weakLevelsDto3);

			String lowTargetStr = null;
			String lowTargetAssetCd = null;

			// 취약점 분석 평가 점수
			getWeakLevelsParams5 = getWeakLevelsParams2.stream().collect(Collectors.toList());
			getWeakLevelsParams5.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getAuditRate));

			if (getWeakLevelsParams5 != null && getWeakLevelsParams5.size() > 0) {

				lowTargetStr = getWeakLevelsParams5.stream().findFirst().get().getHostNm();
				lowTargetAssetCd = getWeakLevelsParams5.stream().findFirst().get().getAssetCd();

				if (StringUtils.isEmpty(lowTargetStr)) {

					for (SnetAssetSwAuditExcelDto excelDto : listEtc) {

						if (StringUtils.equalsIgnoreCase(lowTargetAssetCd, excelDto.getAssetCd())) {

							lowTargetStr = excelDto.getHostNm();
							getWeakLevelsParams5.stream().findFirst().get().setHostNm(lowTargetStr);
						}
					}
				}
				sheetMaps5.put("lowTarget", lowTargetStr); // 취약평가가장낮음
			}
//            beans.put("data5", sheetMaps5); // -> 아래에서 처리

			// -> 미사용. apache poi 로 변경
//            beans.put("dataHidden1", getWeakLevelsParams2);
//            beans.put("dataHidden2", weakLevelsList);

			// 점검 대상별 취약점 수
			getWeakLevelsParams3 = getWeakLevelsParams2.stream().filter(x -> "F".equals(x.getItemResult())).collect(Collectors.toList());

			getWeakLevelsParams3.sort(Comparator.comparing(SnetAssetSwAuditExcelDto::getWeakCount).reversed());
			getWeakLevelsParams3 = getWeakLevelsParams3.stream().limit(10).collect(Collectors.toList());

			// -> 미사용. apache poi 로 변경
//            beans.put("dataHidden4", getWeakLevelsParams3);

			// V. 보안점검 결과_요약 sheet : hidden row 데이터 참고
			sheetMaps6.put("itemTotal", itemTotal);

			SnetAssetSwAuditExcelDto topOne = new SnetAssetSwAuditExcelDto();

			if (getWeakLevelsParams2 != null && getWeakLevelsParams2.size() > 0) {

				topOne = getWeakLevelsParams3.stream().findFirst().get();
			}
			String topHostNm = topOne.getHostNm();

			// topHostNm -> lowTarget
			sheetMaps6.put("topHostNm", topHostNm);

			int weekLen = 0;
			int weekSum = 0;
			int weekAvg = 0;
			weekLen = getWeakLevelsParams2 == null ? 0 : getWeakLevelsParams2.size();

			if (weekLen != 0) {

				for (SnetAssetSwAuditExcelDto sumDto : getWeakLevelsParams2) {

					weekSum += sumDto.getWeakCount();
				}
				weekAvg = weekSum / weekLen;
			} else {

				weekAvg = 0;
			}

			sheetMaps6.put("weekTopOne", topOne.getWeakCount());
			sheetMaps6.put("weekAvg", weekAvg);


			int grpNmLen = 0;
			List<List<SnetAssetSwAuditReportDto>> groupbyReportGrpNmLists = Lists.newArrayList();

			for (SnetAssetSwAuditReportDto reportDto : reportDtoList) {

				Map paramMap = new HashMap();
				paramMap.put("startAuditDay", startAuditDay);
				paramMap.put("endAuditDay", endAuditDay);
				paramMap.put("SW_TYPE", reportDto.getSwType());
				paramMap.put("ASSET_CD", reportDto.getAssetCd());
				paramMap.put("AUDIT_DAY", reportDto.getAuditDay());
				List<SnetAssetSwAuditReportDto> groupbyReportGrpNmList = testMapper.getGroupbyReportGrpNmList(paramMap);
				groupbyReportGrpNmLists.add(groupbyReportGrpNmList);
			}

			int tempWeekCnt = 0;
			int dotIndexOf = 0;
			String substrGrpNm = null;
			List<SnetAssetSwAuditReportDto> hiddenList3 = Lists.newArrayList();
//            List<SnetAssetSwAuditReportDto> hiddenList4 = Lists.newArrayList();
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
								substrGrpNm = StringUtils.substring(dto3.getItemGrpNm(), dotIndexOf + 2);
							} else {
								substrGrpNm = dto3.getItemGrpNm();
							}

							break;
						} else {

							tempWeekCnt = 0;
							dotIndexOf = StringUtils.indexOf(dto3.getItemGrpNm(), ".");
							if (dotIndexOf > -1) {
								substrGrpNm = StringUtils.substring(dto3.getItemGrpNm(), dotIndexOf + 2);
							} else {
								substrGrpNm = dto3.getItemGrpNm();
							}
						}
					}

					hiddenDto3.setWeakCount(tempWeekCnt);
					hiddenDto3.setItemGrpNm(StringUtils.deleteWhitespace(substrGrpNm));

					hiddenList3.add(hiddenDto3);
				}
			}

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

			hiddenList4.sort(Comparator.comparing(SnetAssetSwAuditReportDto::getWeakCount).reversed());
			sheetMaps6.put("grpNmLen", grpNmLen);

			String grpNmTopWeek = null;

			grpNmTopWeek = hiddenList4 == null && hiddenList4.size() == 0 ? null : hiddenList4.stream().findFirst().get().getItemGrpNm();

			String grpNmTopWeekStr = null;

			if (StringUtils.isNotEmpty(grpNmTopWeek)) {

				grpNmTopWeekStr = grpNmTopWeek;

			} else {

				grpNmTopWeekStr = null;
			}
			sheetMaps6.put("grpNmTopWeek", grpNmTopWeekStr);

			int grpNmTopWeekCount = 0;
			SnetAssetSwAuditReportDto snetAssetSwAuditReportDto = null;

			try {
				snetAssetSwAuditReportDto = hiddenList4.stream()
						.max(Comparator.comparing(SnetAssetSwAuditReportDto::getWeakCount)).orElseThrow(NoSuchElementException::new);

				grpNmTopWeekCount = snetAssetSwAuditReportDto.getWeakCount();
			} catch (NoSuchElementException ne) {

				grpNmTopWeekCount = 0;
			}

			sheetMaps6.put("grpNmTopWeekCount", grpNmTopWeekCount);

			// 분류 항목별 취약점 수
			// -> to do test : 보고서 API

			if (hiddenList4 != null && hiddenList4.size() > 0 && resultDtoList != null && resultDtoList.size() > 0
					&& hiddenList4.size() == resultDtoList.size()) {

				for (int i = 0; i < resultDtoList.size(); i++) {

					DetailReportResultDto tempDto = resultDtoList.get(i);
					SnetAssetSwAuditReportDto targetDto = hiddenList4.get(i);

					if (StringUtils.equals(targetDto.getItemGrpNm(), tempDto.getItemGrpNm())) {
						targetDto.setItemGrpNm(tempDto.getItemGrpNm());
					}

					if (targetDto.getWeakCount() == tempDto.getPChkweakTotalCnt()) {
						targetDto.setWeakCount(tempDto.getPChkweakTotalCnt());
					}

				} // end for
			} // end if

			// ◎ 분류 항목별 취약점 수
			Long grpNmTopWeekCountAvg = 0L;
			double grpNmTopWeekCountDbl = 0L;

			if (hiddenList4 != null && hiddenList4.size() > 0) {
				OptionalDouble asDouble = hiddenList4.stream()
						.mapToInt(x -> x.getWeakCount())
						.average();
				grpNmTopWeekCountDbl = asDouble.getAsDouble();
				grpNmTopWeekCountAvg = Math.round(grpNmTopWeekCountDbl);

			} else {

				grpNmTopWeekCountAvg = 0L;
			}
			sheetMaps6.put("grpNmTopWeekCountAvg", grpNmTopWeekCountAvg);
			beans.put("data6", sheetMaps6);

			// (hidden) 분류 항목별 취약점
//            beans.put("dataHidden3", hiddenList4);

			gAssetSwJob.put("startAuditDay", startAuditDay);
			gAssetSwJob.put("endAuditDay", endAuditDay);
			gAssetSwJob.put("BET_FLAG", "Y");

			// jXLS 값 매핑
			List<SnetAssetSwAuditReportDto> assetSwAuditReportListDetail1 = null;
			List<SnetAssetSwAuditReportDto> assetSwAuditReportListDetail2 = Lists.newArrayList();

			// 점검대상 : report max audit_day
			// Ⅵ. 보안점검 결과 전체
			// Ⅵ. 보안점검 결과 전체(swNm추가)
			String swNm = null;
			int listAddIndex = 0;


			// Ⅵ. 보안점검 결과 전체 (swNm / swInfo) sheet 생성을 위함
			// (1) swNm 또는 swInfo 에 따라 분기
			TreeMap<String, List<SnetAssetSwAuditExcelDto>> groupbyReportGrpNm = null;

			if (StringUtils.equalsIgnoreCase("Linux", swTypeStr) || StringUtils.equalsIgnoreCase("Windows", swTypeStr)) {


				groupbyReportGrpNm =
						listEtc.stream().collect(Collectors.groupingBy(SnetAssetSwAuditExcelDto::getSwNm, TreeMap::new, Collectors.toList()));

				diagnosisCount = groupbyReportGrpNm.entrySet().size();
			} else {
				// (2) swNm 또는 swInfo 에 대해 grouping - SwNm

				groupbyReportGrpNm =
						listEtc.stream().collect(Collectors.groupingBy(SnetAssetSwAuditExcelDto::getSwNm, TreeMap::new, Collectors.toList()));

				diagnosisCount = groupbyReportGrpNm.entrySet().size();
			} // end if else

			for (int ij2 = 0; ij2 < listEtc.size(); ij2++) {

				if (listEtc.get(ij2).getHostNm() != null && listEtc.get(ij2).getIpAddress() != null) {

					ExcelReportDto vo = new ExcelReportDto();

					swNm = listEtc.get(ij2).getSwNm();
					String etcSwType = listEtc.get(ij2).getSwType();
					String sheetNm2 = listEtc.get(ij2).getIpAddress();
					String hostNm = listEtc.get(ij2).getHostNm();
					String assetCdReport = listEtc.get(ij2).getAssetCd();
					String auditDayReport = listEtc.get(ij2).getAuditDay();
					gAssetSwJob.put("SW_TYPE", etcSwType);
					gAssetSwJob.put("ASSET_CD", assetCdReport);
					gAssetSwJob.put("AUDIT_DAY", auditDayReport);
					gAssetSwJob.put("SW_INFO", listEtc.get(ij2).getSwInfo());
					gAssetSwJob.put("SW_DIR", listEtc.get(ij2).getSwDir());
					gAssetSwJob.put("SW_USER", listEtc.get(ij2).getSwUser());
					gAssetSwJob.put("SW_ETC", listEtc.get(ij2).getSwEtc());

					assetSwAuditReportListDetail1 = testMapper.selectAssetSwAuditReportNew(gAssetSwJob);

					if (assetSwAuditReportListDetail1.size() > 0) {

					} else {
						Map reportMap = new HashMap();
						reportMap.put("startAuditDay", startAuditDay);
						reportMap.put("endAuditDay", endAuditDay);
						reportMap.put("SW_TYPE", etcSwType);
						reportMap.put("ASSET_CD", assetCdReport);
						SnetAssetSwAuditReportDto auditDayForReport = testMapper.selectAssetSwReportGroupbyAuditday(reportMap);
						gAssetSwJob.put("AUDIT_DAY", auditDayForReport.getAuditDay());
						assetSwAuditReportListDetail1 = testMapper.selectAssetSwAuditReportNew(gAssetSwJob);
					}


					assetSwAuditReportListDetail2 = assetSwAuditReportListDetail1.stream().map(this::converToDto).collect(Collectors.toList());

					vo.setSwNm(swNm);

					// -> to do test : setHostNm 에 HYPERLINK 추가
					vo.setHostNm(hostNm + "(" + sheetNm2 + ")");
					vo.setTxtFileNm(hostNm + "(" + sheetNm2 + ")");

					vo.setDiagnosisCd(assetSwAuditReportListDetail2.stream().findFirst().get().getDiagnosisCd());
					vo.setReportList(assetSwAuditReportListDetail2);

					excelReportList1.add(vo);

				} // end if
			} // end for

			int sheetDivideCount = 1;
			List<String> checkAnalysisStrList = new ArrayList<>();
			List<String> itemTotalCheckTargetsList = new ArrayList<>();

			if (diagnosisCount > 1) {

				for (Map.Entry<String, List<SnetAssetSwAuditExcelDto>> reportDto : groupbyReportGrpNm.entrySet()) {

					String sheetStr = reportDto.getKey();

					beans.put("swNm" + sheetDivideCount, sheetStr);

					beans.put("cols" + sheetDivideCount, excelReportList1.stream().filter(x -> sheetStr.equals(x.getSwNm())).collect(Collectors.toList()));

					excelReportList2 = excelReportList1.stream().filter(x -> sheetStr.equals(x.getSwNm())).collect(Collectors.toList());

					if (excelReportList2 != null && excelReportList2.size() > 0) {

						ExcelReportDto lastExcelReportDto = excelReportList2.stream().max(Comparator.comparing(x -> x.getReportList().size())).orElseThrow(NoSuchElementException::new);
						assetSwAuditReportList4 = lastExcelReportDto.getReportList();
						beans.put("rows" + sheetDivideCount, assetSwAuditReportList4);
						sheetDivideCount++;
					}
				} // end for

				for (int x = 0; x < SHEET_MAX_COUNT-diagnosisCount; x++) {

					beans.put("cols" + sheetDivideCount, excelReportList1);
					beans.put("rows" + sheetDivideCount, assetSwAuditReportList4);

					sheetDivideCount++;
				} // end for

				TreeMap<String, List<ExcelReportDto>> groupbySwNmVo =
						excelReportList1.stream().collect(Collectors.groupingBy(ExcelReportDto::getSwNm, TreeMap::new, Collectors.toList()));

				List<ExcelReportDto> tempExcelReportList = Lists.newArrayList();
				for (Map.Entry<String, List<ExcelReportDto>> grpSwNmVo : groupbySwNmVo.entrySet()) {

					String swNmKey = grpSwNmVo.getKey();
					List<ExcelReportDto> reportListValues = grpSwNmVo.getValue();
					int eaListSize = reportListValues.size();

					ExcelReportDto excelReportDto = new ExcelReportDto();
					excelReportDto.setSwNm(swNmKey);
					excelReportDto.setSwNmGrpSize(eaListSize);
					excelReportDto.setReportList(reportListValues.stream().findFirst().get().getReportList());

					tempExcelReportList.add(excelReportDto);
				} // end for

				// diagnosisCd 뒤 4자리로 grouping
				for (ExcelReportDto dtos : tempExcelReportList) {

					for (SnetAssetSwAuditReportDto reportDto : dtos.getReportList()) {

						String diagnosisCdKeyStr = StringUtils.mid(reportDto.getDiagnosisCd(),4,2);
						reportDto.setDiagnosisCdKey(diagnosisCdKeyStr);
					}
				} // end for

				for (ExcelReportDto dtos : tempExcelReportList) {

					String swNmKey = dtos.getSwNm();

					int grpNmVoLen = 0;
					int assetSwAuditReportListSize = 0;
					int eaListSize = dtos.getSwNmGrpSize();

					TreeMap<String, List<SnetAssetSwAuditReportDto>> groupbyAssetSwAuditReportList4 =
							dtos.getReportList().stream().collect(Collectors.groupingBy(SnetAssetSwAuditReportDto::getDiagnosisCdKey, TreeMap::new, Collectors.toList()));

					for (Map.Entry<String, List<SnetAssetSwAuditReportDto>> grpNmVo : groupbyAssetSwAuditReportList4.entrySet()) {

						grpNmVoLen++;
					} // end for

					assetSwAuditReportListSize = dtos.getReportList().size();

					String checkAnalysisStr = swNmKey + " : " +
							checkAnalysisStandardDesc + " 취약점 분석 평가 기준 " + grpNmVoLen + " 개 분류 " + assetSwAuditReportListSize + " 개 항목 (장비 : " + eaListSize +"대)";
					checkAnalysisStrList.add(checkAnalysisStr);

					String itemTotalCheckTargets = assetSwAuditReportListSize + "개 x " + eaListSize + "EA";

					itemTotalCheckTargetsList.add(itemTotalCheckTargets);

				} // end for

				// excelReportList2 리스트 사이즈 개수에 따라 Ⅱ. 수행내역 sheet 에서 ◎ 점검 항목 : 과학기술정보통신부 리스트 생성해서

				// assetSwAuditReportList4 이 리스트 사용
				// 평가기준명 : fileType 의 desc
				// 평가기준 : excelReportList2 의 getReportList 에서 itemGrpNm grouping 개수
				// 분류항목 : excelReportList2 의 getReportList 리스트 사이즈
			} else {

				for (Map.Entry<String, List<SnetAssetSwAuditExcelDto>> reportDto : groupbyReportGrpNm.entrySet()) {

					String sheetStr = reportDto.getKey();

					beans.put("swNm" + sheetDivideCount, sheetStr);

					beans.put("cols" + sheetDivideCount, excelReportList1.stream().filter(x -> sheetStr.equals(x.getSwNm())).collect(Collectors.toList()));

					excelReportList2 = excelReportList1.stream().filter(x -> sheetStr.equals(x.getSwNm())).collect(Collectors.toList());

					if (excelReportList2 != null && excelReportList2.size() > 0) {

						ExcelReportDto lastExcelReportDto = excelReportList2.stream().max(Comparator.comparing(x -> x.getReportList().size())).orElseThrow(NoSuchElementException::new);
						assetSwAuditReportList4 = lastExcelReportDto.getReportList();
						beans.put("rows" + sheetDivideCount, assetSwAuditReportList4);
					}
				}

				for (int x = 0; x < SHEET_MAX_COUNT; x++) {

					beans.put("cols" + sheetDivideCount, excelReportList1);
					beans.put("rows" + sheetDivideCount, assetSwAuditReportList4);

					sheetDivideCount++;
				} // end for

				TreeMap<String, List<ExcelReportDto>> groupbySwNmVo =
						excelReportList1.stream().collect(Collectors.groupingBy(ExcelReportDto::getSwNm, TreeMap::new, Collectors.toList()));

				List<ExcelReportDto> tempExcelReportList = Lists.newArrayList();
				for (Map.Entry<String, List<ExcelReportDto>> grpSwNmVo : groupbySwNmVo.entrySet()) {

					String swNmKey = grpSwNmVo.getKey();
					List<ExcelReportDto> reportListValues = grpSwNmVo.getValue();
					int eaListSize = reportListValues.size();

					ExcelReportDto excelReportDto = new ExcelReportDto();
					excelReportDto.setSwNm(swNmKey);
					excelReportDto.setSwNmGrpSize(eaListSize);
					excelReportDto.setReportList(reportListValues.stream().findFirst().get().getReportList());

					tempExcelReportList.add(excelReportDto);
				} // end for

				// diagnosisCd 뒤 4자리로 grouping
				for (ExcelReportDto dtos : tempExcelReportList) {

					for (SnetAssetSwAuditReportDto reportDto : dtos.getReportList()) {

						String diagnosisCdKeyStr = StringUtils.mid(reportDto.getDiagnosisCd(),4,2);
						reportDto.setDiagnosisCdKey(diagnosisCdKeyStr);
					}
				} // end for

				for (ExcelReportDto dtos : tempExcelReportList) {

					String swNmKey = dtos.getSwNm();

					int grpNmVoLen = 0;
					int assetSwAuditReportListSize = 0;
					int eaListSize = dtos.getSwNmGrpSize();

					TreeMap<String, List<SnetAssetSwAuditReportDto>> groupbyAssetSwAuditReportList4 =
							dtos.getReportList().stream().collect(Collectors.groupingBy(SnetAssetSwAuditReportDto::getDiagnosisCdKey, TreeMap::new, Collectors.toList()));

					for (Map.Entry<String, List<SnetAssetSwAuditReportDto>> grpNmVo : groupbyAssetSwAuditReportList4.entrySet()) {

						grpNmVoLen++;
					} // end for

					assetSwAuditReportListSize = dtos.getReportList().size();

					String checkAnalysisStr = swNmKey + " : " +
							checkAnalysisStandardDesc + " 취약점 분석 평가 기준 " + grpNmVoLen + " 개 분류 " + assetSwAuditReportListSize + " 개 항목 (장비 : " + eaListSize +"대)";
					checkAnalysisStrList.add(checkAnalysisStr);

					String itemTotalCheckTargets = assetSwAuditReportListSize + "개 x " + eaListSize + "EA";

					itemTotalCheckTargetsList.add(itemTotalCheckTargets);

				} // end for

				// excelReportList2 리스트 사이즈 개수에 따라 Ⅱ. 수행내역 sheet 에서 ◎ 점검 항목 : 과학기술정보통신부 리스트 생성해서

				// assetSwAuditReportList4 이 리스트 사용
				// 평가기준명 : fileType 의 desc
				// 평가기준 : excelReportList2 의 getReportList 에서 itemGrpNm grouping 개수
				// 분류항목 : excelReportList2 의 getReportList 리스트 사이즈
			} // end if else

			sheetMaps3.put("checkAnalysisStr", checkAnalysisStrList);
			beans.put("data3", sheetMaps3);

			String tempItemTotalCheckTargetsStr = StringUtils.join(itemTotalCheckTargetsList,",");

			sheetMaps5.put("itemTotalCheckTargetsStr", tempItemTotalCheckTargetsStr);
			beans.put("data5", sheetMaps5);

		} // end if else etcFlag

		// Apache POI, jXLS 로 create sheet
		try {

            int deleteSheetIndex = 0;
            int sheetNum = diagnosisCount;
            XSSFWorkbook gWorkbook = new XSSFWorkbook(is);

            for (int y = sheetNum; y < SHEET_MAX_COUNT; y++) {
                sheetNum++;
                deleteSheetIndex = gWorkbook.getSheetIndex("Ⅵ. 보안점검 결과 전체 (" + sheetNum + ")");
                gWorkbook.removeSheetAt(deleteSheetIndex);

            } // end for

            gWorkbook.write(new FileOutputStream(excePath + "/detail_report_mapping8_temp.xlsx"));

            FileInputStream is1 = new FileInputStream(new File(excePath + "/detail_report_mapping8_temp.xlsx"));


			gWorkbook1 = transformer.transformXLS(is1, beans);

			Sheet sheet1 = gWorkbook1.getSheet("I. 개요");
			String sheet1Str = "스마트 가드 3.1 에서 운영 중인 서버를 대상으로 보안점검을 수행하여 현재의 보안 수준을 파악, 발견된 취약점에 대한 기술적인 대책을 수립함으로써 스마트 가드 3.1 의 IT환경을 더욱 신뢰성 있고 안전한 환경을 구축하여, 서비스의 안정성을 확보하는데 있습니다.";
			getCell(sheet1, 4, 1).setCellValue(sheet1Str);

			Sheet sheet4 = gWorkbook1.getSheet("Ⅳ. 보안점검 결과_총평");

			sheet4.addMergedRegion(new CellRangeAddress(5, 8, 2, 24));
			sheet4.addMergedRegion(new CellRangeAddress(9, 10, 2, 24));

			// -> 미사용. apache poi 로 변경

			// [18 ~ < 28, 3] hostNm
			// [18 ~ < 28, 4] weekCount
			// [18 ~ < 28, 5] auditRate
			// [18 ~ < 28, 6] auditRateAvg
			getWeakLevelsParams2 = getWeakLevelsParams2.stream().limit(10).collect(Collectors.toList());
			int b = 0;
			for (int a = 18; a < 18 + getWeakLevelsParams2.size(); a++) {

				getCell(sheet4, a, 3).setCellValue(getWeakLevelsParams2.get(b).getHostNm());
				getCell(sheet4, a, 4).setCellValue(getWeakLevelsParams2.get(b).getWeakCount());
				getCell(sheet4, a, 5).setCellValue(getWeakLevelsParams2.get(b).getAuditRate());
				getCell(sheet4, a, 6).setCellValue(getWeakLevelsParams2.get(b).getAuditRateAvg());
				b++;
			}

			// [18 ~ < 21, 17] itemGradeView
			// [18 ~ < 21, 18] count
			int d = 0;
			for (int c = 18; c < 18 + weakLevelsList.size(); c++) {

				getCell(sheet4, c, 17).setCellValue(weakLevelsList.get(d).getItemGradeView());
				getCell(sheet4, c, 18).setCellValue(weakLevelsList.get(d).getCount());
				d++;
			}

			Sheet sheet3 = gWorkbook1.getSheet("Ⅲ. 점검 대상");

		    List<SnetAssetSwAuditExcelDto> snetAssetSwAuditExcelDtoList = (List<SnetAssetSwAuditExcelDto>) beans.get("data4");
		    XSSFCellStyle style = (XSSFCellStyle) gWorkbook1.createCellStyle();
			XSSFFont font = (XSSFFont) gWorkbook1.createFont();

			font.setUnderline(Font.U_SINGLE);
			font.setColor(IndexedColors.BLUE.getIndex());
			style.setFont(font);
			style.setBorderBottom(BorderStyle.THIN);
			style.setAlignment(CellStyle.ALIGN_CENTER);

			int ii = 0;
		    for(int e = 4; e < 4 + snetAssetSwAuditExcelDtoList.size(); e++) {
			   String textFileNm = "\\"+ gAssetSwJob.get("REQ_CD").toString() + "_guide\\" + gAssetSwJob.get("REQ_CD").toString() + "_"  + excelReportList1.get(ii).getTxtFileNm() + ".txt";
			   String formula = "HYPERLINK(CONCATENATE(LEFT(CELL(\"filename\",A1),FIND(\"[\",CELL(\"filename\",A1))-2),\"" + textFileNm + "\"),\""+ snetAssetSwAuditExcelDtoList.get(ii).getHostNm() + "\")";

			   getCell(sheet3, e, 2).setCellFormula(formula);
			   getCell(sheet3, e, 2).setCellStyle(style);

			   ii ++;

		   } // end for
			Sheet sheet5 = gWorkbook1.getSheet("Ⅴ. 보안점검 결과_요약");

			sheet5.addMergedRegion(new CellRangeAddress(16, 18, 2, 24));
			sheet5.addMergedRegion(new CellRangeAddress(31, 33, 2, 24));

			// -> 미사용. apache poi 로 변경

			// [3,10]
			// hostNm
			// weekCount
			int f = 0;
			for (int e = 6; e < 6 + getWeakLevelsParams3.size(); e++) {

				getCell(sheet5, e, 15).setCellValue(getWeakLevelsParams3.get(f).getHostNm());
				getCell(sheet5, e, 16).setCellValue(getWeakLevelsParams3.get(f).getWeakCount());
				f++;
			}

			// itemGradeView
			// count
			int h = 0;
			for (int g = 19; g < 19 + hiddenList4.size(); g++) {

				getCell(sheet5, g, 10).setCellValue(hiddenList4.get(h).getItemGrpNm());
				getCell(sheet5, g, 12).setCellValue(hiddenList4.get(h).getWeakCount());
				h++;
			}

			/*-------------------------------------------------- start sheet6 --------------------------------------------------*/
			int editSheetNum = 1;
			int editSheetLocation = 6;

			for (int y = 0; y < diagnosisCount; y++) {
				Sheet sheet6 = gWorkbook1.getSheet("Ⅵ. 보안점검 결과 전체 (" + editSheetNum + ")");

				int cellnum = 4;
				int cellnumTemp = 4;
				int rownum = 6;
				List <SnetAssetSwAuditReportDto> tempVoList = Lists.newArrayList();
				boolean itemNmEqualFlag = false;
				String tempSwtyeStr = null;
				String sheetNameStr = null;

				List<ExcelReportDto> var1List = (List<ExcelReportDto>) beans.get("cols" + editSheetNum + "");

				for (ExcelReportDto excelReportDto : var1List) {

					sheetNameStr = excelReportDto.getSwNm();
					break;
				} // end for

				for (ExcelReportDto vo1 : var1List) {

					tempSwtyeStr = assetSwAuditReportList4.stream().findFirst().get().getSwNm();
					tempVoList = (List<SnetAssetSwAuditReportDto>) beans.get("rows" + editSheetNum + "");

					String textFileNm = "\\" + gAssetSwJob.get("REQ_CD").toString() + "_guide\\" + gAssetSwJob.get("REQ_CD").toString() + "_" + vo1.getTxtFileNm() + ".txt";
					String formula = "HYPERLINK(CONCATENATE(LEFT(CELL(\"filename\",A1),FIND(\"[\",CELL(\"filename\",A1))-2),\"" + textFileNm + "\"),\"" + vo1.getHostNm() + "\")";
					getCell(sheet6, 3, rownum).setCellFormula(formula);

					if (tempVoList.size() > vo1.getReportList().size()) {

						if (vo1.getReportList().size() < tempVoList.size()) {

							int minusSize = tempVoList.size() - vo1.getReportList().size();

							for (int z = 0; z < minusSize; z++) {

								SnetAssetSwAuditReportDto vo = new SnetAssetSwAuditReportDto();
								vo.setSwNm(vo1.getReportList().get(z).getSwNm());
								vo.setItemNm(vo1.getReportList().get(z).getItemNm());

								vo1.getReportList().add(vo);
							}
						}

						int l = 5;
						for (int k = 4; k < 4 + vo1.getReportList().size(); k++) {
							getCell(sheet6, k, 5).setCellFormula(String.format("COUNTIF(G%d:Z%d, \"취약\")", l, l));
							l++;
						}

						int listIndex = 0;
						for (SnetAssetSwAuditReportDto vo2 : tempVoList) {

							String finalTempSwtyeStr = tempSwtyeStr;
							itemNmEqualFlag = vo1.getReportList().stream().filter(x -> finalTempSwtyeStr.equals(vo2.getSwNm())).anyMatch(x -> StringUtils.equalsIgnoreCase(x.getItemNm(), vo2.getItemNm()));
							if (itemNmEqualFlag) {
								getCell(sheet6, cellnum, rownum).setCellValue(vo1.getReportList().get(listIndex).getItemResultView());
								listIndex++;
							} else {
								//getCell(sheet6, cellnum, rownum).setCellValue("불가(확인필요)");
								listIndex++;
							}
							getCell(sheet6, cellnum, rownum).setCellStyle(cellStyle(gWorkbook1, 1));

							cellnum++;
						}
					} else {

						int l = 5;
						for (int k = 4; k < 4 + vo1.getReportList().size(); k++) {
							getCell(sheet6, k, 5).setCellFormula(String.format("COUNTIF(G%d:Z%d, \"취약\")", l, l));
							l++;
						}

						for (SnetAssetSwAuditReportDto vo2 : vo1.getReportList()) {

							getCell(sheet6, cellnum, rownum).setCellValue(vo2.getItemResultView());
							getCell(sheet6, cellnum, rownum).setCellStyle(cellStyle(gWorkbook1, 1));
							cellnum++;
						}
					}

					cellnum = cellnumTemp;
					rownum = rownum + 1;
				} // end for
				gWorkbook1.setSheetName(editSheetLocation, "Ⅵ. 보안점검 결과 전체" + "(" + sheetNameStr + ")");
				editSheetNum++;
				editSheetLocation++;
			}

			/*-------------------------------------------------- end sheet6 --------------------------------------------------*/

		} catch (Exception e) {
			e.printStackTrace();
		}


		// write reportList

		String beforejobFileNm = jobFileNm;
		if (StringUtils.equalsIgnoreCase("detail_Linux", beforejobFileNm)) {

			jobFileNm = "스마트가드 Linux 보안점검 상세 결과보고서";
		} else if (StringUtils.equalsIgnoreCase("detail_Windows", beforejobFileNm)) {

			jobFileNm = "스마트가드 Window 보안점검 상세 결과보고서";
		} else if (StringUtils.equalsIgnoreCase("detail_WEB", beforejobFileNm)) {

			jobFileNm = "스마트가드 WEB 보안점검 상세 결과보고서";
		} else if (StringUtils.equalsIgnoreCase("detail_WAS", beforejobFileNm)) {

			jobFileNm = "스마트가드 WAS 보안점검 상세 결과보고서";
		} else if (StringUtils.equalsIgnoreCase("detail_DB", beforejobFileNm)) {

			jobFileNm = "스마트가드 DB 보안점검 상세 결과보고서";
		} else if (StringUtils.equalsIgnoreCase("detail_NW", beforejobFileNm)) {

			jobFileNm = "스마트가드 NW 보안점검 상세 결과보고서";
		} else if (StringUtils.equalsIgnoreCase("detail_URL", beforejobFileNm)) {

			jobFileNm = "스마트가드 Web App(URL) 보안점검 상세 결과보고서";
		} else {

			jobFileNm = "스마트가드 기타 보안점검 상세 결과보고서";
		}

		FileOutputStream os = null;

		try {

			File tempReportFolder = new File(tempReportFilesPath + "/" + reportRequestDto.getReqCd());
			if (!tempReportFolder.exists()) {

				tempReportFolder.mkdirs();
			}

			// 취약점가이드 텍스트 파일 폴더 분리해서 압축파일 생성
			String guideDir = gAssetSwJob.get("REQ_CD").toString() + "_guide/";
			File textFileFolder = new File(tempReportFilesPath + "/"+ gAssetSwJob.get("REQ_CD").toString() +"/"+ guideDir);
			if (!textFileFolder.exists()) {

				textFileFolder.mkdirs();
			}

			BufferedWriter fw = null;
			String dashBarStr = "------------------------------------------------------------------------------------------";
			for (ExcelReportDto fileDto : excelReportList1) {

				String textFileNm = tempReportFilesPath + "/"+ gAssetSwJob.get("REQ_CD").toString() +"/"+ guideDir + "/" + gAssetSwJob.get("REQ_CD").toString() + "_"  + fileDto.getTxtFileNm() + ".txt";
				fw = new BufferedWriter(new FileWriter(textFileNm, true));
				String joinStr = StringUtils.join(fileDto.getReportList(), dashBarStr+"\n");
				joinStr = joinStr.replaceAll("\n", "\r\n");
				fw.write(joinStr+"\r\n");
                fw.flush();
			}
			fw.close();

			os = new FileOutputStream(tempReportFilesPath + "/" + reportRequestDto.getReqCd() + "/" + reportRequestDto.getReqCd() + "_" + jobFileNm + ".xlsx");
			gWorkbook1.write(os);
			os.flush();
			os.close();
			is.close();

			watch.stop();

			long millis = watch.getTotalTimeMillis();

			String pattern = "mm:ss";
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			String date = format.format(new Timestamp(millis));
			logger.info(jobFileNm + " 생성 완료, Total time : {} sec", date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		gWorkbook1.close();
		/*---------- create excel file [end] ----------*/
	} // end method

	/**
	 * swType, swNm 을 swNmKey 로 변환
	 */
	private String getSwTypeNmKey(String swType, String swNm) {

		String swNmKey = null;

		if (StringUtils.equalsIgnoreCase("OS", swType)) {

			if (!StringUtils.equalsIgnoreCase("Windows", swNm)) {

				swNmKey = "1";
			} else {

				swNmKey = "2";
			}
		} else if (StringUtils.equalsIgnoreCase("DB", swType)){

			swNmKey = "3";
		} else if (StringUtils.equalsIgnoreCase("WEB", swType)){

			swNmKey = "4";
		} else if (StringUtils.equalsIgnoreCase("WAS", swType)){

			swNmKey = "5";
		} else if (StringUtils.equalsIgnoreCase("NW", swType)){

			swNmKey = "6";
		} else if (StringUtils.equalsIgnoreCase("URL", swType)){

			swNmKey = "7";
		} else {

			swNmKey = "8";
		}

		return swNmKey;
	} // end method

	/**
	 * 기타 (ETC) 점검군 swType 변경
	 */
	private String getEtcSwTypeStr(String swType) {

		String etcSwTypeStr = null;

		if (StringUtils.isNotEmpty(swType) && StringUtils.equals("ETC", swType)) {

			etcSwTypeStr = "OS";
		} else {
			etcSwTypeStr = swType;
		}

		return etcSwTypeStr;
	}


	/**
	 * swNm sheet 생성
	 */
	private void setSheetMapping(Workbook gWorkbook1,
								 int editSheetNum,
								 Map<String, Object> beans,
								 List<SnetAssetSwAuditReportDto> assetSwAuditReportList4, int editSheetLocation) {

		Sheet sheet = gWorkbook1.getSheet("Ⅵ. 보안점검 결과 전체 ("+editSheetNum+")");

		int cellnum = 4;
		int cellnumTemp2 = 4;
		int rownum = 6;
		int rownumTemp2 = 6;
		List <SnetAssetSwAuditReportDto> tempVoList = Lists.newArrayList();
		boolean itemNmEqualFlag = false;
		String tempSwtyeStr = null;
		String sheetNameStr = null;

		List<ExcelReportDto> var1List = (List<ExcelReportDto>) beans.get("cols"+editSheetNum+"");


		for(ExcelReportDto excelReportDto : var1List) {

			sheetNameStr = excelReportDto.getSwNm();
			break;
		} // end for

		for(ExcelReportDto vo1 : var1List) {

			tempSwtyeStr = assetSwAuditReportList4.stream().findFirst().get().getSwNm();
			tempVoList = (List<SnetAssetSwAuditReportDto>) beans.get("rows"+editSheetNum+"");


			if (tempVoList.size() > vo1.getReportList().size()) {

				if (vo1.getReportList().size() < tempVoList.size()) {

					int minusSize = tempVoList.size()-vo1.getReportList().size();

					for (int z = 0; z < minusSize; z++) {

						SnetAssetSwAuditReportDto vo = new SnetAssetSwAuditReportDto();
						vo.setSwNm(vo1.getReportList().get(z).getSwNm());
						vo.setItemNm(vo1.getReportList().get(z).getItemNm());
						vo1.getReportList().add(vo);
					}
				}

				int l = 5;
				for (int k = 4; k < 4 + vo1.getReportList().size(); k++) {
					getCell(sheet, k, 5).setCellFormula(String.format("COUNTIF(G%d:Z%d, \"취약\")", l,l));
					l++;
				}

				int listIndex = 0;
				for (SnetAssetSwAuditReportDto vo2 : tempVoList) {

					String finalTempSwtyeStr = tempSwtyeStr;
					itemNmEqualFlag = vo1.getReportList().stream().filter(x -> finalTempSwtyeStr.equals(vo2.getSwNm())).anyMatch(x -> StringUtils.equalsIgnoreCase(x.getItemNm(), vo2.getItemNm()));

					if (itemNmEqualFlag) {
						getCell(sheet, cellnum, rownum).setCellValue(vo1.getReportList().get(listIndex).getItemResultView());
						listIndex++;
					} else {
						getCell(sheet, cellnum, rownum).setCellValue("불가(확인필요)");
					}
					getCell(sheet, cellnum, rownum).setCellStyle(cellStyle(gWorkbook1, 1));

					cellnum++;
				}
			} else {

				int l = 5;
				for (int k = 4; k < 4 + vo1.getReportList().size(); k++) {
					getCell(sheet, k, 5).setCellFormula(String.format("COUNTIF(G%d:Z%d, \"취약\")", l,l));
					l++;
				}

				for (SnetAssetSwAuditReportDto vo2 : vo1.getReportList()) {

					getCell(sheet, cellnum, rownum).setCellValue(vo2.getItemResultView());
					getCell(sheet, cellnum, rownum).setCellStyle(cellStyle(gWorkbook1, 1));
					cellnum++;
				}
			}

			cellnum = cellnumTemp2;
			rownum = rownum + 1;
		} // end for

//            sheet6.addMergedRegion(new CellRangeAddress(4, 4 + assetSwAuditReportList4.size()-1, 1, 1));

		// java.lang.IllegalStateException: The maximum number of Cell Styles was exceeded. You can define up to 64000 style in a .xlsx Workbook
/*
		for (int j=1;j<sheet.getRow(2).getPhysicalNumberOfCells();j++){
			sheet.autoSizeColumn(j);
//            // width의 최대값은 10000 으로.
//            int width = sheet.getColumnWidth(j);
//            if(width>10000){
//                sheet.setColumnWidth(j, 10000);
//            }
		} // end for
*/

		sheetNameStr = sheetNameStr + "(" + editSheetNum + ")";

		try {

			gWorkbook1.setSheetName(editSheetLocation, "Ⅵ. 보안점검 결과 전체" + "(" + sheetNameStr + ")");

		} catch (Exception e) {

			e.printStackTrace();
			gWorkbook1.setSheetName(editSheetLocation, "Ⅵ. 보안점검 결과 전체" + "(" + sheetNameStr + ")");

		}
	} // end method setSheetMapping

	/**
	 * item cell style 설정
	 */
	private CellStyle cellStyle(Workbook gWorkbook, int type){

		CellStyle cellStyle = gWorkbook.createCellStyle();

		if (type == 2){
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
			cellStyle.setFont (fontStyle(gWorkbook, (short)7));
			cellStyle.setWrapText(true);
		}else {
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
	private Font fontStyle(Workbook gWorkbook, short size){

		Font cellFont = gWorkbook.createFont();

		if(size == 11){

			cellFont.setFontHeightInPoints ((short) size); // 폰트 크기 지정
			cellFont.setColor(IndexedColors.BLACK.getIndex());
			cellFont.setFontName ("맑은 고딕"); // 맑은 고딕
			cellFont.setBold(false);

		} else if(size == 7){

			cellFont.setFontHeightInPoints ((short) size); // 폰트 크기 지정
			cellFont.setColor(IndexedColors.BLACK.getIndex());
			cellFont.setFontName ("맑은 고딕"); // 맑은 고딕
			cellFont.setBold(false);

		} else if(size == 8){

			cellFont.setFontHeightInPoints ((short) size); // 폰트 크기 지정
			cellFont.setColor(IndexedColors.WHITE.getIndex());
			cellFont.setFontName ("맑은 고딕"); // 맑은 고딕
			cellFont.setBold(false);

		} else {

			cellFont.setFontHeightInPoints ((short) size); // 폰트 크기 지정
			cellFont.setColor(IndexedColors.WHITE.getIndex());
			cellFont.setFontName ("맑은 고딕");
			cellFont.setBold(true);
		}

		return cellFont;
	}


//    /**
//     * title cell style 설정
//     */
//    private CellStyle mainStyle(XSSFWorkbook gWorkbook, int size){
//
//        CellStyle mainTitleStyle = gWorkbook.createCellStyle();
//        mainTitleStyle.setAlignment(HorizontalAlignment.CENTER);
//        mainTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//
//        if(size == 12){
//
//            mainTitleStyle.setFillForegroundColor(HSSFColor.ROYAL_BLUE.index);
//            mainTitleStyle.setBorderRight(BorderStyle.THIN);
//            mainTitleStyle.setBorderLeft(BorderStyle.THIN);
//            mainTitleStyle.setBorderTop(BorderStyle.THIN);
//            mainTitleStyle.setBorderBottom(BorderStyle.THIN);
//
//        } else if(size == 11){
//
//            mainTitleStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
//            mainTitleStyle.setBorderRight(BorderStyle.THIN);
//            mainTitleStyle.setBorderLeft(BorderStyle.THIN);
//            mainTitleStyle.setBorderTop(BorderStyle.THIN);
//            mainTitleStyle.setBorderBottom(BorderStyle.THIN);
//        } else if(size == 7){
//
//            mainTitleStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
//            mainTitleStyle.setBorderRight(BorderStyle.THIN);
//            mainTitleStyle.setBorderLeft(BorderStyle.THIN);
//            mainTitleStyle.setBorderTop(BorderStyle.THIN);
//            mainTitleStyle.setBorderBottom(BorderStyle.THIN);
//        } else if(size == 8){
//
//            mainTitleStyle.setFillForegroundColor(HSSFColor.ROYAL_BLUE.index);
//            mainTitleStyle.setBorderRight(BorderStyle.THIN);
//            mainTitleStyle.setBorderLeft(BorderStyle.THIN);
//            mainTitleStyle.setBorderTop(BorderStyle.THIN);
//            mainTitleStyle.setBorderBottom(BorderStyle.THIN);
//        }
//
//        mainTitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        mainTitleStyle.setFont (fontStyle(gWorkbook, (short)size));
//
//        return mainTitleStyle;
//    }

	private String makeCellString(String cellData){
		String result = "";

		if (StringUtils.isNotEmpty(cellData)) {
			if (cellData.length() > 2000)
				result = StringUtil.substring(cellData, 0, 2000) + " ......";
			else
				result = StringUtil.substring(cellData, 0, 2000);
		} else {
			result = "";
		}
		return result;
	}

	/**
	 * grid command for addComment
	 */
	private Comment addComment(XSSFWorkbook gWorkbook1, XSSFSheet sheet1, int i, int i1, String the_author, String s) {

		CreationHelper factory = gWorkbook1.getCreationHelper();
		ClientAnchor anchor = factory.createClientAnchor();
		anchor.setCol1(getCell(getRow(sheet1, i), i1).getColumnIndex() + 1);
		anchor.setCol2(getCell(getRow(sheet1, i), i1).getColumnIndex() + 3);
		anchor.setRow1(i + 1);
		anchor.setRow2(i + 5);
		Drawing drawing = sheet1.createDrawingPatriarch();
		Comment comment = drawing.createCellComment(anchor);
		comment.setString(factory.createRichTextString(s));
		comment.setAuthor(the_author);

		return comment;
	}

	/**
	 hostNM=
	 swNm=MariaDB
	 swType=
	 swInfo=10.1.32
	 ipAddress=
	 diagnosisCd=MMRD0101
	 itemGrpNm=1. 계정 관리
	 itemNmDesc=D-01 기본 계정의 패스워드, 정책 등을 변경하여 사용
	 itemGradeView=상
	 itemResultView=양호
	 itemStatus=
	 itemStandard=양호: 기본 계정의 디폴트 패스워드 및 권한 정책을 변경하여 사용하는 경우
	 itemCountermeasure=기본(관리자) 계정의 디폴트 패스워드 및 권한 정책 변경
	 */
	private SnetAssetSwAuditReportDto converToDto(SnetAssetSwAuditReportDto beforeDto) {

		try {

			SnetAssetSwAuditReportDto dto = new SnetAssetSwAuditReportDto();

			// ASSET_CD
			dto.setAssetCd(beforeDto.getAssetCd());

			// AUDIT_DAY
			dto.setAuditDay(beforeDto.getAuditDay());

			// HOST_NM
			dto.setHostNM(beforeDto.getHostNM());

			// IP_ADDRESS
			dto.setIpAddress(beforeDto.getIpAddress());

			// SW_NM
			dto.setSwNm(beforeDto.getSwNm());

			// SW_TYPE
			dto.setSwType(beforeDto.getSwType());

			// SW_INFO
			dto.setSwInfo(beforeDto.getSwInfo());

			// DIAGNOSIS_CD
			if (StringUtils.isNotEmpty(beforeDto.getDiagnosisCd())) {
				dto.setDiagnosisCd(beforeDto.getDiagnosisCd());
			} else {
				dto.setDiagnosisCd("NONE");
			}

			// ITEM_GRP_NM
			dto.setItemGrpNm(beforeDto.getItemGrpNm());

			// ITEM_NM
			dto.setItemNm(beforeDto.getItemNm());

			// ITEM_NM_DESC
			if (StringUtils.isNotEmpty(beforeDto.getItemNm())) {
				dto.setItemNmDesc((beforeDto.getItemNmDesc()));
			}

			// ITEM_GRADE_VIEW
			dto.setItemGradeView(beforeDto.getItemGradeView());

			// ITEM_STANDARD
			if (StringUtils.isNotEmpty(beforeDto.getItemStandard())) {
				dto.setItemStandard((beforeDto.getItemStandard()));
			}

			// ITEM_RESULT_VIEW
			dto.setItemResultView(beforeDto.getItemResultView());

			// ITEM_STATUS
			if (StringUtils.isNotEmpty(beforeDto.getItemStatus())) {
				dto.setItemStatus((beforeDto.getItemStatus()));
			}

			// ITEM_COUNTERMEASURE
			if (StringUtils.isNotEmpty(beforeDto.getItemCountermeasure())) {
				dto.setItemCountermeasure((beforeDto.getItemCountermeasure()));
			}

			// ITEM_COUNTERMEASURE_DETAIL
			if (StringUtils.isNotEmpty(beforeDto.getItemCountermeasureDetail())) {
				dto.setItemCountermeasureDetail((beforeDto.getItemCountermeasureDetail()));
			}

			// ACTION_PLAN_COMPLATE_DATE
			dto.setActionPlanComplateDate(beforeDto.getActionPlanComplateDate());

			// ACTION_PLAN_REASON
			dto.setActionPlanReason(beforeDto.getActionPlanReason());

			return dto;
		} catch (Exception e) {

			e.printStackTrace();
			return new SnetAssetSwAuditReportDto();
		}
	}

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
}