package com.mobigen.snet.supportagent.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mobigen.snet.supportagent.entity.ExcelModel;

public class ExcelUtil {

	@SuppressWarnings("static-access")
	public static File exportExcel(ExcelModel excel ) throws Exception
	{
		String excelName = excel.getFileName();
		
	 	String[] header 	= excel.getHeader();
	 	String[] colmunList	= excel.getColumnList();
    	List<Map<String, String>> excelData	= excel.getExcelData(); 
	 	
    	XSSFWorkbook xswk = new XSSFWorkbook();
    	XSSFSheet xss = xswk.createSheet ( );
    	
    	XSSFCell cell = null;
    	xswk.setSheetName(0, excel.getSheetName());
    	
    	// *** Style--------------------------------------------------
		XSSFFont headerFont =  xswk.createFont();
		headerFont.setFontHeightInPoints ((short) 10);
		headerFont.setFontName ("Arial");
		headerFont.setBold(true);
		
		CellStyle headerStyle = xswk.createCellStyle();
		headerStyle.setAlignment (headerStyle.ALIGN_CENTER);
		headerStyle.setVerticalAlignment (headerStyle.VERTICAL_CENTER);
		headerStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		headerStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);   
		headerStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);   
		headerStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);   
		headerStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		headerStyle.setFont (headerFont);
		
		XSSFFont cellFont =  xswk.createFont();
		cellFont.setFontHeightInPoints ((short) 10);
		cellFont.setFontName ("Arial"); // Arial
        
        CellStyle cellStyle = xswk.createCellStyle();
		cellStyle.setVerticalAlignment (cellStyle.VERTICAL_CENTER);
        cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);   
        cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);   
        cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);   
        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        cellStyle.setFont (cellFont);
        cellStyle.setWrapText(true);
        //----------------------------------------------------------
    	        
    	// Header
    	XSSFRow row = xss.createRow(0);
    	for (int i = 0 ; i < header.length; i++){
    		cell = row.createCell((short)i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(headerStyle);
        }
        
    	// Contents
    	if(excelData != null)
    	{
    		for ( int j = 0 ; j < excelData.size() ; j ++ )
    		{
    			Map<String, String> rowData  = excelData.get(j);
    			row = xss.createRow ( j + 1 );
    			for ( int x = 0 ; x < colmunList.length ; x ++ )
    			{
    				cell = row.createCell((short)x);
    				cell.setCellValue (rowData.get(colmunList[x]));
    				cell.setCellStyle(cellStyle);
    			}
    			
    			xss.autoSizeColumn(j);
    		}
    	}
    	
    	String excelUploadPath = excel.getFilePath();
    	
        File fileDirectory = new File(excelUploadPath);
		if(!fileDirectory.isDirectory()){
			fileDirectory.mkdirs();
		}
		
        File file = new File ( excelUploadPath , excelName);
        
        try {
        	FileOutputStream fileOutput = new FileOutputStream(file);
        	xswk.write(fileOutput);
            fileOutput.close();
        }
        catch (Exception e) {  
            e.printStackTrace();
            throw new Exception();
        }
		return file;
	}
}
