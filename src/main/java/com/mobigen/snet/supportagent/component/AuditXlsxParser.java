package com.mobigen.snet.supportagent.component;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import com.sk.snet.manipulates.EncryptUtil;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.mobigen.snet.supportagent.dao.ExcelImportMapper;
import com.mobigen.snet.supportagent.utils.CommonUtils;

@Component
@SuppressWarnings("rawtypes")
public class AuditXlsxParser extends AbstractComponent{
	
	@Autowired
	private ExcelImportMapper excelImportMapper;
	
	@Value("${snet.excel.upload.path}")
	private String uploadPath;
	
	Workbook workbook = null;
	
	Map gFileInfo;
		
	public String getCellValue(Cell cell){
		
		String value = null;
		
		if (cell != null) {
		
			switch (cell.getCellType()) {
				case Cell.CELL_TYPE_FORMULA:
					value = cell.getCellFormula();
					break;
				case Cell.CELL_TYPE_NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)){
						Date date = cell.getDateCellValue();
						value = new SimpleDateFormat("yyyyMMdd").format(date);
					}else
						value = "" + cell.getNumericCellValue();
					break;
				case Cell.CELL_TYPE_STRING:
					value = "" + cell.getStringCellValue();
					break;
				case Cell.CELL_TYPE_BLANK:
					value = "";
					break;
				case Cell.CELL_TYPE_ERROR:
					value = "" + cell.getErrorCellValue();
					break;
				default:
			}
		} else {
			value = "";
		}
		
		return value.trim();
	}
	
	public Map<String, String> getAuditReportInfo(int sheetIdx){
		
		Map<String, String> reportInfo = new HashMap<String, String>();

		Sheet sheet = workbook.getSheetAt(sheetIdx);
		Row row = sheet.getRow(1); // row 가져오기

		reportInfo.put("Title", getCellValue(row.getCell(0)));
		
		for (int r = 3; r < sheet.getPhysicalNumberOfRows(); r++) {
			
			row = sheet.getRow(r); // row 가져오기
			
			if (row != null) {

				String value;			
				
				for(int c = 1; c < row.getLastCellNum(); c++){
					
					value = getCellValue(row.getCell(c)).trim();

					logger.debug("1",value);
					
					if(value.indexOf("진단 기간") > 0){
						int beginIndex = value.indexOf(":") + 1;
						int endIndex = value.indexOf("(");
						logger.debug("2",value);
						value = value.substring(beginIndex, endIndex).trim().replaceAll("\\.", "");
						logger.debug("3",value);
						reportInfo.put("AuditDay",value.replaceAll("-", ""));
					}
				}
			}
		}

		return reportInfo;
	}
	
	public List<Map<String, String>> getAuditSystemInfo(int sheetIdx) throws Exception {
		
		Row row;
		boolean rowFlag = true;
		
		List<Map<String, String>> systemInfoList = new ArrayList<Map<String, String>>();
		
		//0번째 sheet 정보 취득
		Sheet sheet = workbook.getSheetAt(sheetIdx);

		//취득된 sheet에서 rows수 취득
		int rows = sheet.getLastRowNum();

		logger.debug("rows : " + rows);
		for (int r = 0; r <= rows; r++) {
//			logger.debug("under 3rows count : " + r );
			if( r <= 3 ) continue;

//			logger.debug("upper 3 rows count : " + r );
			row = sheet.getRow(r); // row 가져오기

			if (row != null) {
				
				String value;
				Map<String, String> systemInfo = new HashMap<String, String>();
				
				for(int c = 1; c < row.getLastCellNum(); c++){
					logger.debug("====" + getCellValue(row.getCell(c)));
					if(c == 1){

					}else if(c == 2){
						value = getCellValue(row.getCell(c)).toUpperCase();
						if (value.equals("")) continue;
						systemInfo.put("AssetCd", value);
						
					}else if(c == 3){
						value = getCellValue(row.getCell(c));
						systemInfo.put("HostNm",value);

					}else if(c == 4){
						value = getCellValue(row.getCell(c));
						systemInfo.put("IpAddress",value);
						
					}else if(c == 5){
						value = getCellValue(row.getCell(c));
						systemInfo.put("SwType", value);
						
					}else if(c == 6){
						value = getCellValue(row.getCell(c));
						systemInfo.put("SwNm",value);

					}else if(c == 7){
						value = getCellValue(row.getCell(c));
						systemInfo.put("SwInfo",value);

					}else if(c == 8){
						value = getCellValue(row.getCell(c));
						systemInfo.put("SwDir",value);

					}else if(c == 9){
						value = getCellValue(row.getCell(c));
						systemInfo.put("SwEtc",value);

					}else if(c == 10){
						value = getCellValue(row.getCell(c));
						systemInfo.put("SwUser",value);

					}else if(c == 11){
						value = getCellValue(row.getCell(c));
						systemInfo.put("AuditDay",value.replaceAll("-", ""));

					}
				} // end for cell

				systemInfo.put("FileId", gFileInfo.get("FILE_ID").toString());

				systemInfoList.add(systemInfo);
				
			}// end if row != null

		} // for(r) row 문

		return systemInfoList;
		
	}
	
	public HashMap<String, String> getCommonItems(Row row, String preAuditClass, int beginIdx, int endIdx){
		
		HashMap<String, String> commonMap = new HashMap<String, String>();

		for(int c=beginIdx; c <= endIdx;c++){
			
			String value = getCellValue(row.getCell(c));
			logger.debug("getCommonItems value :: {} {}", c, value);
			switch(c){
				/* 공통 */
				case 0: 
					if ("".equals(value.trim())){
						value = preAuditClass.trim();
					}
					
					commonMap.put("ItemGrpNm", value.trim());
					break;
				case 1:
					commonMap.put("ItemDiagnosisCd", value.trim());
					break;
				case 2:
					commonMap.put("ItemNm", value.trim());
					
					break;
				case 3:
					break;
				case 4:
					
					if(value.trim().equals("상")){
						value = "H";
					}else if(value.trim().equals("중")){
						value = "M";
					}else if(value.trim().equals("하")){
						value = "L";
					}
					
					commonMap.put("ItemGrade", value.trim());
					break;
			}
		}
		logger.debug("commonMap :: {}", commonMap);
		return commonMap;		
	}
	
	public void setAuditResultData(int sheetIdx, List<Map<String, String>> systemInfoList) throws Exception{
		
		String value = "";
		String preItemGrpNm = "";
		
		List<Map<String, String>> itemList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> formatList = new ArrayList<Map<String, String>>();
		
		Sheet sheet = workbook.getSheetAt(sheetIdx);
		
		int rows = sheet.getPhysicalNumberOfRows();

		List<Map> auditItemList = null;
		Map<String, Map> auditItems = new HashMap<>();


//		logger.debug("Rows :: {}", rows);
		for (int r = 1; r <= rows; r++) {
			
			Row row = sheet.getRow(r); // row 가져오기
			
			if (row != null) {
				
				int cells = row.getLastCellNum(); 
				
//				logger.debug("XSSFRow r :: {}", r);
				if(r == 1){
//					logger.debug("R == 1 :: {}", r);
					String swNm = getCellValue(row.getCell(0));
					String assetCd = getCellValue(row.getCell(6));
					logger.debug("R == 1 swNm :: {}", swNm);
					logger.debug("R == 1 assetCd :: {}", assetCd);

					for(Map<String, String> sysInfo:systemInfoList){
						logger.debug("R == 1 sysInfo.get(\"SwNm\") :: {}", sysInfo.get("SwNm"));
						if(sysInfo.get("SwNm").equals(swNm) && sysInfo.get("AssetCd").equals(assetCd)){

							Map<String, String> itemFormat = new HashMap<>();

							itemFormat.put("SwNm", swNm);
							itemFormat.put("HostNm", sysInfo.get("HostNm"));
							itemFormat.put("IpAddress", sysInfo.get("IpAddress"));
							itemFormat.put("AssetCd", sysInfo.get("AssetCd"));
							itemFormat.put("SwType", sysInfo.get("SwType"));
							itemFormat.put("SwInfo", sysInfo.get("SwInfo"));
							itemFormat.put("SwDir", sysInfo.get("SwDir"));
							itemFormat.put("SwEtc", sysInfo.get("SwEtc"));
							itemFormat.put("SwUser", sysInfo.get("SwUser"));
							itemFormat.put("AuditDay", sysInfo.get("AuditDay"));
//							itemFormat.put("ItemIdx", Integer.toString(c));
							itemFormat.put("FileId", sysInfo.get("FileId"));

							excelImportMapper.deleteAssetSwReport(itemFormat);
							excelImportMapper.deleteAssetSwHistory(itemFormat);
//									excelImportMapper.deleteAssetSwDay(itemFormat);

							formatList.add(itemFormat);
							auditItemList = excelImportMapper.selectAuditItems(sysInfo);
							for(Map auditItem : auditItemList){
								auditItems.put(auditItem.get("DIAGNOSIS_CD").toString(), auditItem);
							}
							logger.debug("R == 1 auditItems size :: {}", auditItems.size());
							break;
						}
//						logger.debug("R == 1 formatList :: {}", formatList);
					} // end for					
					
				}else if(r >= 3){
					
					logger.debug("R >= 3 :: {}", r);
					HashMap<String, String> commonMap = getCommonItems(row,  preItemGrpNm, 0, 4);
					
					if("".equals(commonMap.get("ItemDiagnosisCd"))) {

						break;
					}
					
					if(!"".equals(commonMap.get("ItemGrpNm"))){
						preItemGrpNm = commonMap.get("ItemGrpNm").toString();
					}					
	
					for(Map itemFormat:formatList){
						Map tmp = auditItems.get(commonMap.get("ItemDiagnosisCd"));
						if (tmp == null)
							tmp = new HashMap();

						HashMap<String, String> itemMap = new HashMap<String, String>();
						
						int idx = 6;

						itemMap.put("ItemGrpNm", commonMap.get("ItemGrpNm"));
						itemMap.put("ItemDiagnosisCd", commonMap.get("ItemDiagnosisCd"));
						itemMap.put("ItemNm", EncryptUtil.aes_encrypt(commonMap.get("ItemNm")));
						itemMap.put("ItemGrade", commonMap.get("ItemGrade"));

						itemMap.put("ItemStandard", tmp.get("ITEM_STANDARD") != null ? EncryptUtil.aes_encrypt(tmp.get("ITEM_STANDARD").toString()) : "");
						itemMap.put("ItemCounterMeasure", tmp.get("ITEM_COUNTERMEASURE") != null ? EncryptUtil.aes_encrypt(tmp.get("ITEM_COUNTERMEASURE").toString()) : "");
						itemMap.put("ItemCounterMeasureDetail", tmp.get("ITEM_COUNTERMEASURE_DETAIL") != null ? EncryptUtil.aes_encrypt(tmp.get("ITEM_COUNTERMEASURE_DETAIL").toString()) : "");
						
						itemMap.put("AssetCd", itemFormat.get("AssetCd").toString());
						itemMap.put("SwNm", itemFormat.get("SwNm").toString());
						itemMap.put("HostNm", itemFormat.get("HostNm").toString());
						itemMap.put("IpAddress", itemFormat.get("IpAddress").toString());
						itemMap.put("SwType", itemFormat.get("SwType").toString());
						itemMap.put("SwInfo", itemFormat.get("SwInfo").toString());
						itemMap.put("SwDir", itemFormat.get("SwDir").toString());
						itemMap.put("SwEtc", itemFormat.get("SwEtc").toString());
						itemMap.put("SwUser", itemFormat.get("SwUser").toString());
						itemMap.put("AuditDay", itemFormat.get("AuditDay").toString());
						
						String resultVal = getCellValue(row.getCell(idx));
						if(resultVal.equals("O") || resultVal.equals("T")){
							resultVal = "T";
						}else if(resultVal.equals("X") || resultVal.equals("F")){
							resultVal = "F";
						}else if(resultVal.equals("불가") || resultVal.equals("C")){
							resultVal = "C";
						}else if(resultVal.equals("N/A") || resultVal.equals("NA")){
							resultVal = "NA";
						}else {
							resultVal = "R";
						}
						
						itemMap.put("ItemResult", resultVal);
						itemMap.put("ItemStatus", EncryptUtil.aes_encrypt(getCellValue(row.getCell(idx+1))));
//						itemMap.put("ItemCounterMeasureDetail", getCellValue(row.getCell(idx+2)));
//						itemMap.put("ItemCokReason", getCellValue(row.getCell(idx+3)));
						
						itemList.add(itemMap);						
					}
				}
			}
		}
		
		logger.info("=========================== result Insert [ Start ]=====================================");
		for(Map<String, String> item:itemList){
			
			excelImportMapper.insertAuditSwReport(item);
		}
		
		for(Map<String, String> itemFormat:formatList){	

			excelImportMapper.insertAuditSwHistory(itemFormat);
			
			Map swDay = excelImportMapper.selectAuditSwDay(itemFormat);
			
			//audit day of asset : if asset_cd is exist and audit_day is not exist
			if(swDay != null){
				
				if(swDay.get("AUDIT_DAY") == null){
					excelImportMapper.updateAuditSwDay(itemFormat);
				}else{					
					if(itemFormat.get("AuditDay").toString().compareTo(swDay.get("AUDIT_DAY").toString()) >= 0){
						excelImportMapper.updateAuditSwDay(itemFormat);
					}
				}
				
			}else {
				excelImportMapper.insertAuditSwDay(itemFormat);
			}			
			
			Map excelResult = excelImportMapper.selectAssetExcelResult(itemFormat);
			if(excelResult != null){
				excelImportMapper.updateAssetExcelResult(excelResult);
			}
		}
		
		/* update audit_day, audit_rate of Asset Master */
		String preAssetCd = "";
		for(Map<String, String> itemFormat:formatList){	
			String assetCd = itemFormat.get("AssetCd");
			if(!preAssetCd.equals(assetCd)){
				
				/*Map assetInfo = excelImportMapper.selectAssetRateData(itemFormat);
				excelImportMapper.updateAssetRateData(assetInfo);*/
				updateSnetAssetMaster(assetCd);
				
				preAssetCd = assetCd;
			}
		}
		
		logger.info("=========================== result Insert [ END ]=====================================");
	}
	
	@SuppressWarnings("unchecked")
	public void updateAssetMaster(Map reportInfo) throws Exception{
		try {
			Map params = new HashMap();
			params.put("FileId", gFileInfo.get("FILE_ID"));
			List<Map> resultList = excelImportMapper.selectAssetExcelResultByFile(params);
			
			for(Map result:resultList){
				
				params.put("HostNm", result.get("HOST_NM"));
				params.put("IpAddress", result.get("IP_ADDRESS"));
				
				Map assetInfo = excelImportMapper.selectAssetCd(params);

				if(assetInfo != null){
					if(assetInfo.get("AUDIT_DAY") == null || 
						reportInfo.get("AuditDay").toString().compareTo(assetInfo.get("AUDIT_DAY").toString()) >= 0){
						excelImportMapper.updateAssetExcelResultByFile(result);
					}
				}else{
					logger.info("Asset is NUll :: {}", new Gson().toJson(params));
				}
					
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e.fillInStackTrace());
			CommonUtils.printError(e);
			throw new Exception(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public int start(Map gFileInfo){
		this.gFileInfo = gFileInfo;
		
		int rtn = 2; //JOB_COMPLETE = 2		
		String path = "";
		
		logger.debug(new Gson().toJson(gFileInfo));
		
		if(gFileInfo.get("FILE_UPLOAD_FOLDER") != null && !"".equals(gFileInfo.get("FILE_UPLOAD_FOLDER").toString())) {
			path = gFileInfo.get("FILE_UPLOAD_FOLDER").toString() + File.separator + gFileInfo.get("AD_FILE_NM");
		}else {
			if( gFileInfo.get("P_FILE_ID") !=null && !gFileInfo.get("P_FILE_ID").equals(""))
				path = uploadPath +  File.separator + gFileInfo.get("P_FILE_ID").toString() +File.separator + gFileInfo.get("AD_FILE_NM").toString();
			else
				path = uploadPath +  File.separator + gFileInfo.get("AD_FILE_NM").toString();
		}
		
		logger.debug("EXCEL File path :: {}", path);
		File file = new File(path);		

		if(file.isFile()){
			logger.debug("EXCEL is File......");
			try {
			
				FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());

				this.workbook = WorkbookFactory.create(inputStream);

				//sheet수 취득
				int numOfSheets = workbook.getNumberOfSheets();
				
				Map<String, String> reportInfo = new HashMap<String, String>();
				List<Map<String, String>> sysInfoList = new ArrayList<Map<String, String>>();
				
				for(int idx = 0; idx < numOfSheets; idx++){
					
					String sheetNm = workbook.getSheetName(idx);
					logger.debug("시트명 : " + sheetNm);
					if(idx == 0){
						sysInfoList = getAuditSystemInfo(idx);
						for(Map sysInfo:sysInfoList){
							excelImportMapper.insertAssetExcelResult(sysInfo);
						}

					}else {

						boolean flag = false;
//						Map<String, String> sysInfo = null; //sysInfoList.get(idx - 1);

						for(Map<String, String> tmpSysInfo:sysInfoList){
//							logger.debug("==========sysinfo.swNm " + sysInfo.get("SwNm"));
							if(sheetNm.toLowerCase().indexOf(tmpSysInfo.get("SwNm").toString().toLowerCase()) > -1){
								flag = true;
//								sysInfo = tmpSysInfo;
								break;
							}
						}
						logger.debug("FLAG :: {}", flag);
						if(flag){
							setAuditResultData(idx, sysInfoList);
						}
					}
				}

				// IP와 hostname 으로 SNET_ASSET_MASTER 의 진단일, 보안준수율, 업데이트 시간을 업데이트하는 로직임.
//				updateAssetMaster(reportInfo);
				for(Map<String, String> sysInfo:sysInfoList) {
					updateSnetAssetMaster(sysInfo.get("AssetCd"));
				}
				inputStream.close();
			
			}catch(Exception e){
				rtn = 4; //FILE_READ_ERROR = 4;
				logger.error("Exception :: {}", e.getMessage(), e);
				CommonUtils.printError(e);
			}
			
		}else {
			/* Result File Not Exist */
			rtn = 5; //FILE_NOT_EXIST= 5
		}
		logger.info("Result RTN :: {}", rtn);
		
		return rtn;		
	}

	private void updateSnetAssetMaster(String assetCd){
		double mstAdWeightOk = 0.000;
		double mstAdWeightNok = 0.000;
		double mstAdWeightR = 0.000;
		double mstAdWeightPass = 0.000;
		double mstAuditRate = 0.000;
		double mstAuditRateFirewall = 0.000;

		try {
			// 보안준수율 계산을 위한 장비의 진단대상 (미진단 포함) 정보 조회
			Map<String, BigDecimal> masterResult = excelImportMapper.selectSnetAssetMasterTot ( assetCd );

			mstAdWeightOk = Integer.valueOf(masterResult.get("adWeightOk").intValue());
			mstAdWeightNok = Integer.valueOf(masterResult.get("adWeightNok").intValue());
			mstAdWeightR = Integer.valueOf(masterResult.get("adWeightReq").intValue());
			mstAdWeightPass = Integer.valueOf(masterResult.get("adWeightPass").intValue());

			mstAuditRate = ( mstAdWeightOk / ( mstAdWeightOk + mstAdWeightNok + mstAdWeightR ) ) * 100;		//  보안준수율 = T  /  T + F + R
			mstAuditRate = Math.round( mstAuditRate * 100d ) / 100d ;		// 소수 둘째 자리

			mstAuditRateFirewall = ( mstAdWeightOk / ( mstAdWeightOk + mstAdWeightNok + mstAdWeightR + mstAdWeightPass ) ) * 100;		//  보안준수율 = T  /  T + F + R
			mstAuditRateFirewall = Math.round( mstAuditRateFirewall * 100d ) / 100d ;		// 소수 둘째 자리

//			System.out.println(">>>>>>>>>> " + mstAdWeightOk + ", " + mstAdWeightNok + ", " + mstAdWeightR + ", sum=" + ( mstAdWeightOk + mstAdWeightNok + mstAdWeightR ));
//			System.out.println(">>>>>>>>>> mstAuditRate : " + mstAuditRate);
//
//			System.out.println(">>>>>>>>>> " + mstAdWeightOk + ", " + mstAdWeightNok + ", " + mstAdWeightR + ", " + mstAdWeightPass + ", sum=" + ( mstAdWeightOk + mstAdWeightNok + mstAdWeightR + mstAdWeightPass ));
//			System.out.println(">>>>>>>>>> mstAuditRateFirewall : " + mstAuditRateFirewall);

			if ( mstAuditRate > 100 )
				mstAuditRate = 100;

			Map<String, Object> param = new HashMap<>();
			param.put("assetCd", assetCd);
			param.put("auditRate",mstAuditRate);
			param.put("auditRateFirewall", mstAuditRateFirewall);

			// 마스터 테이블에 해당장비의 보안 준수율 업데이트
			excelImportMapper.updateAssetMaster( param );
		} catch (Exception ex ){
			logger.error(ex.toString(), ex);
		}
	}

}
