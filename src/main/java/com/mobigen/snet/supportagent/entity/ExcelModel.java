package com.mobigen.snet.supportagent.entity;

import java.util.List;
import java.util.Map;

public class ExcelModel {

	private String filePath;
	private String fileName;
	private String sheetName;
	private String[] header;
	private String[] columnList;
	private List<Map<String, String>> excelData;
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public String[] getHeader() {
		return header;
	}
	public void setHeader(String[] header) {
		this.header = header;
	}
	public String[] getColumnList() {
		return columnList;
	}
	public void setColumnList(String[] columnList) {
		this.columnList = columnList;
	}
	public List<Map<String, String>> getExcelData() {
		return excelData;
	}
	public void setExcelData(List<Map<String, String>> excelData) {
		this.excelData = excelData;
	}
	
	
}