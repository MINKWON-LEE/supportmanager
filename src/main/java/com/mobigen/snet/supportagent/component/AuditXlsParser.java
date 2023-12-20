package com.mobigen.snet.supportagent.component;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.mobigen.snet.supportagent.dao.ExcelImportMapper;
import com.mobigen.snet.supportagent.utils.CommonUtils;

@Component
@SuppressWarnings("rawtypes")
public class AuditXlsParser extends AbstractComponent{
	
	@Autowired
	private ExcelImportMapper excelImportMapper;
	
	@Value("${snet.excel.upload.path}")
	private String uploadPath;
	
	HSSFWorkbook workbook = null;

	private Map gFileInfo;
	
	public String getCellValue(Cell cell){
		
		String value = null;
		
		if (cell != null) {
		
			switch (cell.getCellType()) {
				case HSSFCell.CELL_TYPE_FORMULA:
					value = cell.getCellFormula();
					break;
				case HSSFCell.CELL_TYPE_NUMERIC:
					value = "" + cell.getNumericCellValue();
					break;
				case HSSFCell.CELL_TYPE_STRING:
					value = "" + cell.getStringCellValue();
					break;
				case HSSFCell.CELL_TYPE_BLANK:
					value = "";
					break;
				case HSSFCell.CELL_TYPE_ERROR:
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

		HSSFSheet sheet = workbook.getSheetAt(sheetIdx);
		HSSFRow row = sheet.getRow(1); // row 가져오기

		reportInfo.put("Title", getCellValue(row.getCell(0)));
		
		for (int r = 3; r < sheet.getPhysicalNumberOfRows(); r++) {
			
			row = sheet.getRow(r); // row 가져오기
			
			if (row != null) {

				String value;			
				
				for(int c = 1; c < row.getLastCellNum(); c++){
					
					value = getCellValue(row.getCell(c)).trim();
					
					if(value.indexOf("진단 기간") > 0){
						int beginIndex = value.indexOf(":") + 1;
						int endIndex = value.indexOf("(");
		
						value = value.substring(beginIndex, endIndex).trim().replaceAll("\\.", ""); 
						
						reportInfo.put("AuditDay",value);
					}
				}
			}
		}

		return reportInfo;
	}
	
	public List<Map<String, String>> getAuditSystemInfo(int sheetIdx) throws Exception {
		
		HSSFRow row;
		boolean rowFlag = true;
		
		List<Map<String, String>> systemInfoList = new ArrayList<Map<String, String>>();
		
		//0번째 sheet 정보 취득
		HSSFSheet sheet = workbook.getSheetAt(sheetIdx);
		
		String swType = "";
		String swNm= "";
		String swVersion= "";
		String hostNm= "";
		String ipAddress= "";		

		//취득된 sheet에서 rows수 취득
		int rows = sheet.getPhysicalNumberOfRows();

		for (int r = 0; r < rows; r++) {
			
			if( r <= 4 ) continue;
			
			row = sheet.getRow(r); // row 가져오기

			if (row != null) {
				
				String value;
				Map<String, String> systemInfo = new HashMap<String, String>();
				
				for(int c = 1; c < row.getLastCellNum(); c++){
					
					if(c == 1){
						value = getCellValue(row.getCell(c));
						if(value.equals("")){
							rowFlag = false;
							break;
						}
					}else if(c == 2){
						value = getCellValue(row.getCell(c));
						if(value.equals("")){
							value = swType;
						}else {
							swType = value;
						}
						
						if(value.toUpperCase().indexOf("WEB S") > -1){
							value = "WEB";
						}else if(value.toUpperCase().indexOf("WEB A") > -1){
								value = "URL";
						}else {
							value = swType.toUpperCase();
						}
						
						systemInfo.put("SwType", value);
						
					}else if(c == 3){
						value = getCellValue(row.getCell(c));
						if(value.equals("")){
							value = swNm;
						}else {
							swNm = value;
						}
						systemInfo.put("SwNm",value);
						
					}else if(c == 4){
						value = getCellValue(row.getCell(c));
						if(value.equals("")){
							value = swVersion;
						}else {
							swVersion = value;
						}
						systemInfo.put("SwInfo",value);
						
					}else if(c == 5){
						value = getCellValue(row.getCell(c));
						if(value.equals("")){
							value = hostNm;
						}else {
							hostNm = value;
						}
						systemInfo.put("HostNm", value);
						
					}else if(c == 6){
						value = getCellValue(row.getCell(c));
						if(value.equals("")){
							value = ipAddress;
						}else {
							if(swNm.toLowerCase().equals("jsp")){
								ipAddress = "";
							}else {							
								ipAddress = value;
							}
						}
						systemInfo.put("IpAddress",value);
						
					}
				} // end for cell
				
				if (!rowFlag) break;
				
				Map assetCd = excelImportMapper.selectAssetCd(systemInfo);
				
				if(assetCd != null){
					systemInfo.put("AssetCd", assetCd.get("ASSET_CD").toString());
				}else {
					
					Calendar calendar = Calendar.getInstance();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
					String assetCode = "AC" + dateFormat.format(calendar.getTime());
					
					systemInfo.put("AssetCd", assetCode);
//					if(excelImportMapper.insertAssetCdToMaster(systemInfo)==0)
//						excelImportMapper.insertAssetCdToIp(systemInfo);				
				}
				
				systemInfo.put("FileId", gFileInfo.get("FILE_ID").toString());

				systemInfoList.add(systemInfo);
				
			}// end if row != null

		} // for(r) row 문

		return systemInfoList;
		
	}
	
	public HashMap<String, String> getCommonItems(HSSFRow row, String preAuditClass, int beginIdx, int endIdx){
		
		HashMap<String, String> commonMap = new HashMap<String, String>();

		for(int c=beginIdx; c<endIdx;c++){
			
			String value = getCellValue(row.getCell(c));

			switch(c){
				/* 공통 */
				case 0: 
					if ("".equals(value.trim())){
						value = preAuditClass.trim();
					}
					
					commonMap.put("ItemGrpNm", value.trim());
					break;
				case 1:
					commonMap.put("ItemNum", value.trim());
					break;
				case 2:
					commonMap.put("ItemNm", value.trim());
					
					break;
				case 3:
					
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

		return commonMap;		
	}
	
	public void setAuditResultData(int sheetIdx, Map<String, String> reportInfo, List<Map<String, String>> systemInfoList) throws Exception{
		
		String value = "";
		String preItemGrpNm = "";
		
		List<Map<String, String>> itemList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> formatList = new ArrayList<Map<String, String>>();
		
		HSSFSheet sheet = workbook.getSheetAt(sheetIdx);
		
		int rows = sheet.getPhysicalNumberOfRows();
		
		for (int r = 1; r < rows; r++) {
			
			HSSFRow row = sheet.getRow(r); // row 가져오기			
			
			if (row != null) {
				
				int cells = row.getLastCellNum(); 

				if(r == 1){
					
					String swNm = getCellValue(row.getCell(0));
					
					for (int c = 1; c < cells; c++) {
						
						value = getCellValue(row.getCell(c));
						if(!value.equals("")){
							
							String hostNm = value.substring(0, value.lastIndexOf("(")).trim();
							String ipAddr = value.substring(value.lastIndexOf("(") + 1, value.lastIndexOf(")")).trim();
							
							for(Map<String, String> sysInfo:systemInfoList){
								
								if(sysInfo.get("SwNm").equals(swNm) && 
										sysInfo.get("HostNm").equals(hostNm) && 
										sysInfo.get("IpAddress").equals(ipAddr)){
									
									Map<String, String> itemFormat = new HashMap<String, String>();
									
									itemFormat.put("SwNm", swNm);
									itemFormat.put("HostNm", hostNm);
									itemFormat.put("IpAddress", ipAddr);
									itemFormat.put("AssetCd", sysInfo.get("AssetCd"));
									itemFormat.put("SwType", sysInfo.get("SwType"));
									itemFormat.put("SwInfo", sysInfo.get("SwInfo"));
									itemFormat.put("AuditDay", reportInfo.get("AuditDay"));
									itemFormat.put("ItemIdx", Integer.toString(c));
									itemFormat.put("FileId", sysInfo.get("FileId"));	
									
									excelImportMapper.deleteAssetSwReport(itemFormat);
									excelImportMapper.deleteAssetSwHistory(itemFormat);
									excelImportMapper.deleteAssetSwDay(itemFormat);
									
									formatList.add(itemFormat);							
								}								
							}							
						}						
					} // end for					
					
				}else if(r >= 3){

					HashMap<String, String> commonMap = getCommonItems(row,  preItemGrpNm, 0, 4);
					
					if(commonMap.get("ItemNum").equals("")) {					
						break;
					}
					
					if(!"".equals(commonMap.get("ItemGrpNm").toString())){
						preItemGrpNm = commonMap.get("ItemGrpNm").toString();
					}					
	
					for(Map itemFormat:formatList){		
						
						HashMap<String, String> itemMap = new HashMap<String, String>();
						
						int idx = Integer.parseInt(itemFormat.get("ItemIdx").toString());
						
						itemMap.put("ItemGrpNm", commonMap.get("ItemGrpNm"));
						itemMap.put("ItemNum", commonMap.get("ItemNum"));
						itemMap.put("ItemNm", commonMap.get("ItemNm"));
						itemMap.put("ItemGrade", commonMap.get("ItemGrade"));
						
						itemMap.put("AssetCd", itemFormat.get("AssetCd").toString());
						itemMap.put("SwNm", itemFormat.get("SwNm").toString());
						itemMap.put("HostNm", itemFormat.get("HostNm").toString());
						itemMap.put("IpAddress", itemFormat.get("IpAddress").toString());
						itemMap.put("SwType", itemFormat.get("SwType").toString());
						itemMap.put("SwInfo", itemFormat.get("SwInfo").toString());
						itemMap.put("AuditDay", itemFormat.get("AuditDay").toString());
						
						String resultVal = getCellValue(row.getCell(idx));
						if(resultVal.equals("O")){
							resultVal = "T";
						}else if(resultVal.equals("X")){
							resultVal = "F";
						}else if(resultVal.equals("불가")){
							resultVal = "C";
						}else if(resultVal.equals("N/A")){
							resultVal = "NA";
						}else {
							resultVal = "R";
						}
						
						itemMap.put("ItemResult", resultVal);
						itemMap.put("ItemStatus", getCellValue(row.getCell(idx+1)));
						itemMap.put("ItemCounterMeasureDetail", getCellValue(row.getCell(idx+2)));
						itemMap.put("ItemCokReason", getCellValue(row.getCell(idx+3)));
						
						itemList.add(itemMap);						
					}
				}
			}
		}
		
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
				
				Map assetInfo = excelImportMapper.selectAssetRateData(itemFormat);
				excelImportMapper.updateAssetRateData(assetInfo);
				
				preAssetCd = assetCd;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void updateAssetMaster(Map reportInfo) throws Exception{
		try{	
			Map params = new HashMap();
			params.put("FileId", gFileInfo.get("FILE_ID"));
			List<Map> resultList = excelImportMapper.selectAssetExcelResultByFile(params);
			for(Map result:resultList){
				
				params.put("HostNm", result.get("HOST_NM"));
				params.put("IpAddress", result.get("IP_ADDRESS"));
				
				Map assetInfo = excelImportMapper.selectAssetCd(params);
				if(assetInfo!=null){
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
		
//		if(gFileInfo.get("FILE_UPLOAD_FOLDER") != null && !"".equals(gFileInfo.get("FILE_UPLOAD_FOLDER").toString())) {
//			path = gFileInfo.get("FILE_UPLOAD_FOLDER").toString() + File.separator + gFileInfo.get("P_FILE_ID").toString() + File.separator + gFileInfo.get("AD_FILE_NM");
//		}else {
//			path = uploadPath +  File.separator + gFileInfo.get("P_FILE_ID").toString() +File.separator + gFileInfo.get("AD_FILE_NM").toString();
//		}
		
		if(gFileInfo.get("FILE_UPLOAD_FOLDER") != null && !"".equals(gFileInfo.get("FILE_UPLOAD_FOLDER").toString())) {
			path = gFileInfo.get("FILE_UPLOAD_FOLDER").toString() + File.separator + gFileInfo.get("AD_FILE_NM");
		}else {
			if( gFileInfo.get("P_FILE_ID") !=null && !gFileInfo.get("P_FILE_ID").equals(""))
				path = uploadPath +  File.separator + gFileInfo.get("P_FILE_ID").toString() +File.separator + gFileInfo.get("AD_FILE_NM").toString();
			else
				path = uploadPath +  File.separator + gFileInfo.get("AD_FILE_NM").toString();
		}
		
		
		File file = new File(path);		

		if(file.isFile()){

			try {
			
				FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
				
				this.workbook = new HSSFWorkbook(inputStream);	
				
				//sheet수 취득
				int numOfSheets = workbook.getNumberOfSheets();
				
				Map<String, String> reportInfo = new HashMap<String, String>();
				List<Map<String, String>> sysInfoList = new ArrayList<Map<String, String>>();
				
				for(int idx = 0; idx < numOfSheets; idx++){
					
					String sheetNm = workbook.getSheetName(idx);
					
					if(idx == 0){
						reportInfo = getAuditReportInfo(idx);
					}else if(idx == 1){
						sysInfoList = getAuditSystemInfo(idx);
						
						for(Map sysInfo:sysInfoList){
							sysInfo.put("FileId", gFileInfo.get("FILE_ID"));
							sysInfo.put("AuditDay", reportInfo.get("AuditDay"));
							excelImportMapper.insertAssetExcelResult(sysInfo);
						}						
						
					}else {
						
						boolean flag = false;
						for(Map<String, String> sysInfo:sysInfoList){
							
							if(sheetNm.toLowerCase().indexOf(sysInfo.get("SwNm").toString().toLowerCase()) > -1){							
								flag = true;
								break;
							}
						}
							
						if(flag){
							setAuditResultData(idx, reportInfo, sysInfoList);
						}
					}
				}
				
				updateAssetMaster(reportInfo);
				
				try {
					inputStream.close();
				} catch (Exception e) {
					logger.error("inputStream close exception :: {}", e.getMessage(), e.fillInStackTrace());
				}
			
//			}catch(SQLException sqle){
//				rtn = 6; //SQL_ERROR = 6;
//				CommonUtils.printError(sqle);
			}catch(Exception e){
				rtn = 4; //FILE_READ_ERROR = 4;
				CommonUtils.printError(e);
				logger.error(e.getMessage(), e.fillInStackTrace());
			}
			
		}else {
			/* Result File Not Exist */
			rtn = 5; //FILE_NOT_EXIST= 5
		}
		
		return rtn;		
	}

}
