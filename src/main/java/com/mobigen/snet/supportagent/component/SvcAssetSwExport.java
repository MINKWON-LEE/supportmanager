package com.mobigen.snet.supportagent.component;

import com.mobigen.snet.supportagent.dao.ExcelExportMapper;
import com.mobigen.snet.supportagent.service.ParserServiceImpl;
import com.mobigen.snet.supportagent.utils.CommonUtils;
import com.sk.snet.manipulates.EncryptUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Component
@SuppressWarnings("rawtypes")
public class SvcAssetSwExport extends AbstractComponent {

	@Autowired
	private ExcelExportMapper excelExportMapper;

	@Value("${snet.support.excel.path}")
	private String excelPath;

	XSSFWorkbook gWorkbook = null;
	
	Map gSvcAssetSwJob;

	private void setSvcInfo() throws Exception {

		// Create a blank sheet
		XSSFSheet sheet = gWorkbook.createSheet("진단결과 요약");

		int rownum = 1;
		int cellnum = 1;

		String titles[] = { "구분", "전체항목수", "양호", "취약", "제외", "불가", "보안규격 준수율%" };

		XSSFRow row = sheet.createRow(rownum++);
		XSSFCell cell = null;

		for (int i = 0; i < titles.length; i++) {
			cell = row.createCell(cellnum++);
			cell.setCellStyle(mainStyle(16));

			if (i == 0) {
				cell.setCellValue(gSvcAssetSwJob.get("SVC_NM").toString() + " 보안진단 결과요약");
			}
		}

		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, titles.length));

		rownum += 2;
		cellnum = 1;

		row = sheet.createRow(rownum++);
		for (String title : titles) {
			cell = row.createCell(cellnum);
			cell.setCellStyle(mainStyle(12));
			cell.setCellValue(title);

			sheet.autoSizeColumn(cellnum++);
		}

		float[] report = new float[6];
		String auditDay = "";

		List<Map> svcAssetSwInfoList = excelExportMapper.selectSvcAssetSwInfoList(gSvcAssetSwJob);
		for (Map svcAssetSwInfo : svcAssetSwInfoList) {
			cellnum = 1;
			row = sheet.createRow(rownum++);
			auditDay = svcAssetSwInfo.get("AUDIT_DAY").toString();

			String[] items = new String[titles.length];
			items[0] = svcAssetSwInfo.get("SW_TYPE").toString();
			items[1] = svcAssetSwInfo.get("AD_TOTAL").toString();
			items[2] = svcAssetSwInfo.get("AD_RESULT_OK").toString();
			items[3] = svcAssetSwInfo.get("AD_RESULT_NOK").toString();
			items[4] = svcAssetSwInfo.get("AD_RESULT_PASS").toString();
			items[5] = svcAssetSwInfo.get("AD_RESULT_NA").toString();
			items[6] = svcAssetSwInfo.get("AUDIT_RATE").toString();

			report[0] += Float.parseFloat(items[1]);
			report[1] += Float.parseFloat(items[2]);
			report[2] += Float.parseFloat(items[3]);
			report[3] += Float.parseFloat(items[4]);
			report[4] += Float.parseFloat(items[5]);
			report[5] += Float.parseFloat(items[6]);

			for (int i = 0; i < items.length; i++) {

				cell = row.createCell(cellnum);
				cell.setCellValue(items[i]);
				if (i == 0) {
					cell.setCellStyle(mainStyle(11));
				} else {
					cell.setCellStyle(cellStyle(2));
				}

				sheet.autoSizeColumn(cellnum++);
			}
		}

		cellnum = 1;
		row = sheet.createRow(rownum++);
		report[5] = report[5] / svcAssetSwInfoList.size();
		for (int i = 0; i < report.length; i++) {
			if (i == 0) {
				cell = row.createCell(cellnum);
				cell.setCellValue("합계");
				cell.setCellStyle(mainStyle(11));
				sheet.autoSizeColumn(cellnum++);
			}

			cell = row.createCell(cellnum);
			if (i < 5) {
				cell.setCellValue(report[i]);
			} else if (i == 5) {
				DecimalFormat format = new DecimalFormat(".##");
				String result = format.format(report[i]);
				cell.setCellValue(result);
			}
			cell.setCellStyle(mainStyle(11));
			sheet.autoSizeColumn(cellnum++);
		}

		cellnum = 1;
		rownum += 2;
		row = sheet.createRow(rownum++);
		cell = row.createCell(cellnum++);
		cell.setCellValue("진단 기간 : " + auditDay);

	}

	private List<Map> setSvcAssetSwInfo() throws Exception {

		// Create a blank sheet
		XSSFSheet sheet = gWorkbook.createSheet("시스템 정보");

		int idx = 1;
		int rownum = 1;
		int cellnum = 1;
		String titles[] = { "NO", "구분", "제품명", "버전", "HOST명", "IP주소", "진단일", "보안준수율", "사용자ID", "사용자명", "팀명", "부서명" };

		XSSFRow row = sheet.createRow(rownum++);
		XSSFCell cell = null;

		for (int i = 0; i < titles.length; i++) {
			cell = row.createCell(cellnum++);
			if (i == 0) {
				cell.setCellValue("진단정보");
			}
			cell.setCellStyle(mainStyle(16));
		}

		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, titles.length));

		rownum++;

		row = sheet.createRow(rownum++);
		cellnum = 1;

		for (String title : titles) {
			cell = row.createCell(cellnum);
			cell.setCellValue(title);
			if (cellnum < 10) {
				cell.setCellStyle(mainStyle(12));
			} else {
				cell.setCellStyle(mainStyle(11));
			}
			sheet.autoSizeColumn(cellnum++);
		}

		cellnum = 1;
		List<Map> assetSwExportList = excelExportMapper.selectSvcAssetSwList(gSvcAssetSwJob);

		for (Map assetSwExport : assetSwExportList) {
			row = sheet.createRow(rownum++);

			cell = row.createCell(cellnum);
			cell.setCellStyle(cellStyle(2));
			cell.setCellValue(idx);
			sheet.autoSizeColumn(cellnum++);

			cell = row.createCell(cellnum);
			cell.setCellStyle(cellStyle(2));
			cell.setCellValue(assetSwExport.get("SW_TYPE").toString());
			sheet.autoSizeColumn(cellnum++);

			cell = row.createCell(cellnum);
			cell.setCellStyle(cellStyle(2));
			cell.setCellValue(assetSwExport.get("SW_NM").toString());
			sheet.autoSizeColumn(cellnum++);

			cell = row.createCell(cellnum);
			cell.setCellStyle(cellStyle(2));
			cell.setCellValue(assetSwExport.get("SW_INFO").toString());
			sheet.autoSizeColumn(cellnum++);

			cell = row.createCell(cellnum);
			cell.setCellStyle(cellStyle(2));
			cell.setCellValue(assetSwExport.get("HOST_NM").toString());
			sheet.autoSizeColumn(cellnum++);

			cell = row.createCell(cellnum);
			cell.setCellStyle(cellStyle(2));
			cell.setCellValue(assetSwExport.get("IP_ADDRESS").toString());
			sheet.autoSizeColumn(cellnum++);

			cell = row.createCell(cellnum);
			cell.setCellStyle(cellStyle(2));
			cell.setCellValue(assetSwExport.get("AUDIT_DAY").toString());
			sheet.autoSizeColumn(cellnum++);

			cell = row.createCell(cellnum);
			cell.setCellStyle(cellStyle(2));
			cell.setCellValue(assetSwExport.get("AUDIT_RATE").toString());
			sheet.autoSizeColumn(cellnum++);

			cell = row.createCell(cellnum);
			cell.setCellStyle(cellStyle(2));
			cell.setCellValue((assetSwExport.get("USER_ID") != null) ? assetSwExport.get("USER_ID").toString() : "");
			sheet.autoSizeColumn(cellnum++);

			cell = row.createCell(cellnum);
			cell.setCellStyle(cellStyle(2));
			cell.setCellValue((assetSwExport.get("USER_NM") != null) ? assetSwExport.get("USER_NM").toString() : "");
			sheet.autoSizeColumn(cellnum++);

			cell = row.createCell(cellnum);
			cell.setCellStyle(cellStyle(2));
			cell.setCellValue((assetSwExport.get("TEAM_NM") != null) ? assetSwExport.get("TEAM_NM").toString() : "");
			sheet.autoSizeColumn(cellnum++);

			cell = row.createCell(cellnum);
			cell.setCellStyle(cellStyle(2));
			cell.setCellValue(
					(assetSwExport.get("BRANCH_NM") != null) ? assetSwExport.get("BRANCH_NM").toString() : "");
			sheet.autoSizeColumn(cellnum++);

			cellnum = 1;
			idx++;
		}

		return assetSwExportList;
	}

	private XSSFFont fontStyle(short size) {

		XSSFFont cellFont = gWorkbook.createFont();

		if (size == 10) {

			cellFont.setFontHeightInPoints((short) size); // 폰트 크기 지정
			cellFont.setFontName("Arial"); // Arial

		} else if (size == 11) {
			cellFont.setColor(IndexedColors.BLACK.getIndex());
			cellFont.setFontHeightInPoints(size);
			cellFont.setFontName("Arial");
			cellFont.setBold(true);

		} else {

			cellFont.setColor(IndexedColors.WHITE.getIndex());
			cellFont.setFontHeightInPoints(size);
			cellFont.setFontName("Arial");
			cellFont.setBold(true);
		}

		return cellFont;

	}

	private CellStyle mainStyle(int size) {

		CellStyle mainTitleStyle = gWorkbook.createCellStyle();
		mainTitleStyle.setAlignment(CellStyle.ALIGN_CENTER);
		mainTitleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		if (size == 16) {

			mainTitleStyle.setFillForegroundColor(HSSFColor.BLUE.index);
			mainTitleStyle.setBorderRight(XSSFCellStyle.BORDER_THICK);
			mainTitleStyle.setBorderLeft(XSSFCellStyle.BORDER_THICK);
			mainTitleStyle.setBorderTop(XSSFCellStyle.BORDER_THICK);
			mainTitleStyle.setBorderBottom(XSSFCellStyle.BORDER_THICK);

		} else if (size == 14) {

			mainTitleStyle.setFillForegroundColor(HSSFColor.BLUE_GREY.index);
			mainTitleStyle.setBorderRight(XSSFCellStyle.BORDER_THICK);
			mainTitleStyle.setBorderLeft(XSSFCellStyle.BORDER_THICK);
			mainTitleStyle.setBorderTop(XSSFCellStyle.BORDER_THICK);
			mainTitleStyle.setBorderBottom(XSSFCellStyle.BORDER_THICK);

		} else if (size == 12) {

			mainTitleStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			mainTitleStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
			mainTitleStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			mainTitleStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
			mainTitleStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);

		} else if (size == 11) {

			mainTitleStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			mainTitleStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
			mainTitleStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
			mainTitleStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
			mainTitleStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		}

		mainTitleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		mainTitleStyle.setFont(fontStyle((short) size));

		return mainTitleStyle;
	}

	private CellStyle cellStyle(int type) {

		CellStyle cellStyle = gWorkbook.createCellStyle();

		if (type == 2) {
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		}

		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		cellStyle.setFont(fontStyle((short) 10)); // 데이터의 폰트스타일 지정
		cellStyle.setWrapText(true);

		return cellStyle;
	}

	@SuppressWarnings("unchecked")
	private void setAuditSWReport(List<List> assetGp, String preSwType, String preSwNm) throws Exception {

		try {
			int idx = 0;
			int rownum = 1;
			XSSFRow row = null;

			int cellnum = 0;
			XSSFCell cell = null;

			int cellNo = 0;
			String preItemGrpNm = "";

			int headgap = 0;
			int titlegap = 0;

			// Create a blank sheet
			XSSFSheet sheet = gWorkbook.createSheet(preSwNm + "(" + preSwType + ")");

			for (List<Map> assetReport : assetGp) {

				rownum = 1;

				for (Map report : assetReport) {

					if (idx == 0) {

						if (rownum == 1) {

							// HEADER
							row = sheet.createRow(rownum++);

							for (int i = 0; i < 5; i++) {
								cell = row.createCell(cellnum++);
								if (i == 0) {
									cell.setCellValue(preSwNm);
								}

								cell.setCellStyle(mainStyle(16));
							}

							sheet.addMergedRegion(
									new CellRangeAddress(row.getRowNum(), row.getRowNum(), headgap, headgap + 5));

							headgap += 6;
							cellnum = headgap;
							for (int i = 0; i < 4; i++) {
								cell = row.createCell(cellnum++);
								if (i == 0) {
									cell.setCellValue(report.get("HOST_NM").toString() + "("
											+ report.get("IP_ADDRESS").toString() + ")");
								}
								cell.setCellStyle(mainStyle(14));
							}

							sheet.addMergedRegion(
									new CellRangeAddress(row.getRowNum(), row.getRowNum(), headgap, headgap + 3));

							// TITLE
							row = sheet.createRow(rownum++);

							String titleList[] = { "진단항목", "No", "세부 진단항목", "진단기준", "중요도", "가중치", "결과", "항목상태", "조치여부",
									"적용불가사유" };

							for (String title : titleList) {
								cell = row.createCell(titlegap++);
								cell.setCellStyle(mainStyle(12));
								cell.setCellValue(title);
							}
						}

						// ITEM
						cellnum = 0;
						row = sheet.createRow(rownum++);

						if (!preItemGrpNm.equals(report.get("ITEM_GRP_NM").toString())) {

							if (cellNo > 0) {
								sheet.addMergedRegion(
										new CellRangeAddress(row.getRowNum() - cellNo, row.getRowNum() - 1, 0, 0));
							}

							cellNo = 0;
							preItemGrpNm = report.get("ITEM_GRP_NM").toString();
						}

						cell = row.createCell(cellnum++);
						cell.setCellStyle(cellStyle(2));
						cell.setCellValue(report.get("ITEM_GRP_NM").toString());
						sheet.autoSizeColumn(cellnum);

						cell = row.createCell(cellnum++);
						cell.setCellStyle(cellStyle(2));
						cell.setCellValue(++cellNo);

						cell = row.createCell(cellnum++);
						cell.setCellStyle(cellStyle(1));
						cell.setCellValue(EncryptUtil.aes_decrypt(report.get("ITEM_NM").toString()));
						sheet.autoSizeColumn(cellnum);

						String itemStandard = "";
						if (report.get("ITEM_STANDARD") != null) {
							itemStandard = EncryptUtil.aes_decrypt(report.get("ITEM_STANDARD").toString());
						}
						cell = row.createCell(cellnum);
						cell.setCellStyle(cellStyle(1));
						cell.setCellValue(itemStandard);
						sheet.autoSizeColumn(cellnum++);

						String grade = "";
						String level = "";
						if (report.get("ITEM_GRADE").toString().toString().equals("H")) {
							grade = "상";
							level = "3";
						} else if (report.get("ITEM_GRADE").toString().toString().equals("M")) {
							grade = "중";
							level = "2";
						} else if (report.get("ITEM_GRADE").toString().toString().equals("L")) {
							grade = "하";
							level = "1";
						}

						cell = row.createCell(cellnum++);
						cell.setCellStyle(cellStyle(2));
						cell.setCellValue(grade);
						sheet.autoSizeColumn(cellnum);

						cell = row.createCell(cellnum++);
						cell.setCellStyle(cellStyle(2));
						cell.setCellValue(level);
						sheet.autoSizeColumn(cellnum);

						cell = row.createCell(cellnum++);

						String itemResult = "";
						if (report.get("ITEM_RESULT") != null) {
							itemResult = report.get("ITEM_RESULT").toString();
						}
						if (itemResult.equals("T")) {
							itemResult = "O";
						} else if (itemResult.equals("F")) {
							itemResult = "X";
						} else if (itemResult.equals("C")) {
							itemResult = "불가";
						} else if (itemResult.equals("NA")) {
							itemResult = "N/A";
						} else {
							itemResult = "R";
						}
						cell.setCellStyle(cellStyle(2));
						cell.setCellValue(itemResult);
						sheet.autoSizeColumn(cellnum);

						cell = row.createCell(cellnum++);

						String itemStatus = "";
						if (report.get("ITEM_STATUS") != null) {
							itemStatus = EncryptUtil.aes_decrypt(report.get("ITEM_STATUS").toString());
						}
						cell.setCellStyle(cellStyle(1));
						cell.setCellValue(itemStatus);
						sheet.autoSizeColumn(cellnum);

						// String itemStandard = "";
						// if (report.get("ITEM_STANDARD") != null){
						// itemStandard = report.get("ITEM_STANDARD").toString();
						// }
						cell = row.createCell(cellnum++);
						cell.setCellStyle(cellStyle(1));
						cell.setCellValue("");
						sheet.autoSizeColumn(cellnum);

						// String itemCounterMeasureDetail = "";
						// if (report.get("ITEM_COUNTERMEASURE") != null){
						// itemCounterMeasureDetail =
						// report.get("ITEM_COUNTERMEASURE").toString();
						// }
						cell = row.createCell(cellnum++);
						cell.setCellStyle(cellStyle(1));
						cell.setCellValue("");
						sheet.autoSizeColumn(cellnum);

					} else {

						if (rownum == 1) {

							String titleList[] = { "결과", "항목상태", "조치여부", "적용불가사유" };

							// HEADER
							row = sheet.getRow(rownum++);
							headgap += 4;
							cellnum = headgap;

							for (int i = 0; i < titleList.length; i++) {

								cell = row.createCell(cellnum++);

								if (i == 0) {
									cell.setCellValue(report.get("HOST_NM").toString() + "("
											+ report.get("IP_ADDRESS").toString() + ")");
								}

								cell.setCellStyle(mainStyle(14));
							}

							sheet.addMergedRegion(
									new CellRangeAddress(row.getRowNum(), row.getRowNum(), headgap, headgap + 3));

							// TITLE
							row = sheet.getRow(rownum++);

							for (String title : titleList) {
								cell = row.createCell(titlegap++);
								cell.setCellValue(title);
								cell.setCellStyle(mainStyle(12));
							}
						} // end rownum = 1

						// ITEM
						cellnum = headgap;
						row = sheet.getRow(rownum++);

						cell = row.createCell(cellnum++);
						String itemResult = "";
						if (report.get("ITEM_RESULT") != null) {
							itemResult = report.get("ITEM_RESULT").toString();
						}
						if (itemResult.equals("T")) {
							itemResult = "O";
						} else if (itemResult.equals("F")) {
							itemResult = "X";
						} else if (itemResult.equals("C")) {
							itemResult = "불가";
						} else if (itemResult.equals("NA")) {
							itemResult = "N/A";
						} else {
							itemResult = "R";
						}
						cell.setCellStyle(cellStyle(2));
						cell.setCellValue(itemResult);
						sheet.autoSizeColumn(cellnum);

						cell = row.createCell(cellnum++);
						String itemStatus = "";
						if (report.get("ITEM_STATUS") != null) {
							itemStatus = report.get("ITEM_STATUS").toString();
						}
						cell.setCellStyle(cellStyle(1));
						cell.setCellValue(itemStatus);
						sheet.autoSizeColumn(cellnum);

						// String itemStandard= "";
						// if (report.get("ITEM_STANDARD") != null){
						// itemStandard = report.get("ITEM_STANDARD").toString();
						// }
						cell = row.createCell(cellnum++);
						cell.setCellStyle(cellStyle(1));
						cell.setCellValue("");
						sheet.autoSizeColumn(cellnum);

						// String itemCounterMeasureDetail = "";
						// if (report.get("ITEM_COUNTERMEASURE") != null){
						// itemCounterMeasureDetail =
						// report.get("ITEM_COUNTERMEASURE").toString();
						// }
						cell = row.createCell(cellnum++);
						cell.setCellStyle(cellStyle(1));
						cell.setCellValue("");
						sheet.autoSizeColumn(cellnum);

					} // end if

				} // end in for

				idx++;

			} // end out for

			for (int i = 0; i < sheet.getRow(2).getPhysicalNumberOfCells(); i++) {
				sheet.autoSizeColumn(i);
				// sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+512 );
			}			
		} catch (Exception e) {
			logger.error(CommonUtils.printError(e));
			throw new Exception(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void updateJobStatus(String jobFileNm, String jobFlag) throws SQLException {

		gSvcAssetSwJob.put("JOB_FILE_NM", jobFileNm);
		gSvcAssetSwJob.put("JOB_FLAG", jobFlag);
		excelExportMapper.updateSvcAssetSwExport(gSvcAssetSwJob);
	}

	@SuppressWarnings("unused")
	public void start(Map svcAssetSwJob) {
		
		this.gSvcAssetSwJob = svcAssetSwJob;
		gWorkbook = new XSSFWorkbook(); 

		try {

			updateJobStatus("", "2");

			List<List> assetGp = new ArrayList<List>();
			String preSwType = "";
			String preSwNm = "";

			setSvcInfo();

			List<Map> assetSwInfoList = setSvcAssetSwInfo();

			if (assetSwInfoList != null && assetSwInfoList.size() > 0) {
				for (Map assetSwInfo : assetSwInfoList) {

					if (!preSwType.equals("") && !preSwType.equals(assetSwInfo.get("SW_TYPE").toString())
							|| !preSwNm.equals("") && !preSwNm.equals(assetSwInfo.get("SW_NM").toString())) {

						setAuditSWReport(assetGp, preSwType, preSwNm);

						assetGp.clear();
						assetGp = new ArrayList<List>();
					}

					List<Map> assetSwAuditReport = excelExportMapper.selectAssetSwAuditReport(assetSwInfo);

					for(Map assetSwAudit:assetSwAuditReport){
						assetSwAudit.put("ITEM_NM", EncryptUtil.aes_decrypt(String.valueOf(assetSwAudit.get("ITEM_NM"))));
						assetSwAudit.put("ITEM_STANDARD", EncryptUtil.aes_decrypt(String.valueOf(assetSwAudit.get("ITEM_STANDARD"))));
					}

					assetGp.add(assetSwAuditReport);

					preSwType = assetSwInfo.get("SW_TYPE").toString();
					preSwNm = assetSwInfo.get("SW_NM").toString();

				}

				setAuditSWReport(assetGp, preSwType, preSwNm);
			}

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String jobFileNm = gSvcAssetSwJob.get("REQ_USER").toString() + "_" + gSvcAssetSwJob.get("SVC_NM").toString()
					+ "보안진단결과.xlsx";

			//Mkdir 
			File file = new File(excelPath);
			if(!file.isDirectory())
				file.mkdirs();
			
			// Write the workbook in file system
			FileOutputStream out = new FileOutputStream(new File(excelPath + File.separator + jobFileNm));
			gWorkbook.write(out);
			out.close();

			updateJobStatusFinish(jobFileNm, "1");

//		} catch (SQLException sqle) {
//			logger.error(sqle.getMessage(), sqle.fillInStackTrace());
//			updateJobStatusFinish("", "3");
		} catch (Exception e) {
			logger.error(CommonUtils.printError(e));
			updateJobStatusFinish("", "3");
		}
	}

	@SuppressWarnings("unchecked")
	private void updateJobStatusFinish(String jobFileNm, String jobFlag) {

		gSvcAssetSwJob.put("JOB_FILE_NM", jobFileNm);
		gSvcAssetSwJob.put("JOB_FLAG", jobFlag);
		try {
			excelExportMapper.updateSvcAssetSwExportFinish(gSvcAssetSwJob);
		} catch (SQLException e) {
			logger.error(CommonUtils.printError(e));
		}
	}
}