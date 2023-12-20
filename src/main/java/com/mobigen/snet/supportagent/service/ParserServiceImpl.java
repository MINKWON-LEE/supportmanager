package com.mobigen.snet.supportagent.service;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mobigen.snet.supportagent.component.AuditXlsParser;
import com.mobigen.snet.supportagent.component.AuditXlsxParser;
import com.mobigen.snet.supportagent.dao.ExcelImportMapper;
import com.mobigen.snet.supportagent.memory.SafeThread;
import com.mobigen.snet.supportagent.memory.SyncQueue;
import com.mobigen.snet.supportagent.utils.Decompress;


/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : ParserServiceImpl.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 1. 20.
 * Description : 
 * 
 */
@Service
public class ParserServiceImpl extends AbstractService implements ParserService{
	
	
	@Autowired
	private ExcelImportMapper excelImportMapper;
	
	@Autowired
	private AuditXlsParser auditXlsParser;
	
	@Autowired
	private AuditXlsxParser auditXlsxParser;
	
	@Value("${snet.excel.upload.path}")
	private String uploadPath;
	
	SyncQueue fileQueue = new SyncQueue();
	SyncQueue zipQueue = new SyncQueue();	
	
	
	private List<File> fileInfoList;

	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.ParserService#start(int)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void start(int sw) {
		try {
			
			List<Map> adUpFileInfoList = excelImportMapper.selectUploadFileList();
			
			for(Map adUpFileInfo:adUpFileInfoList){
				
				fileQueue.push(adUpFileInfo);
				
				Worker worker = new Worker();
				worker.startup();
			}
			
		}catch (SQLException e) {
			logger.error(e.getMessage() );
		}
	}

	private void getSrcFileList(String path){		
		
		File dir = new File(path); 
		
		File[] fileList = dir.listFiles();
		
		logger.debug("getSrcFileList File path :: {}", path );
		logger.debug("getSrcFileList File path listFiles size:: {}", fileList.length );
		try{
			for(int i = 0 ; i < fileList.length ; i++){
				
				logger.debug("fileList :: {}", i);
				
				File file = fileList[i]; 
				logger.debug("file Name :: {}", file.getName());
				
				if(file.isFile()){
					fileInfoList.add(file);
					logger.info("fileInfoList File path :: {}", file.getCanonicalPath());
				}else if(file.isDirectory()){
					logger.info("getSrcFileList is directory :: {}", file.getCanonicalPath());
					getSrcFileList(file.getCanonicalPath().toString()); 
				}else{
					logger.debug("Else case -  is File :: {}, is Directory :: {}", file.isFile(), file.isDirectory());
				}
				
			}	
			
		}catch(IOException e){
			logger.error(e.getMessage(), e.fillInStackTrace());
		}		
	}
	
	@SuppressWarnings("rawtypes")
	private void UnZipProcess(Map adUpFileInfo){
		
		String srcPath = adUpFileInfo.get("FILE_UPLOAD_FOLDER") !=null ? adUpFileInfo.get("FILE_UPLOAD_FOLDER").toString() 
				+ File.separator + adUpFileInfo.get("AD_FILE_NM"): uploadPath + File.separator + adUpFileInfo.get("AD_FILE_NM");
		String zipPath = adUpFileInfo.get("FILE_UPLOAD_FOLDER") !=null ? adUpFileInfo.get("FILE_UPLOAD_FOLDER").toString() 
				+ File.separator + adUpFileInfo.get("FILE_ID") + File.separator :uploadPath + File.separator + adUpFileInfo.get("FILE_ID") + File.separator;
		try {

			List<Map> zipFileList = excelImportMapper.selectSwZipFileList(adUpFileInfo);			
			
			excelImportMapper.deleteSwZipFileList(adUpFileInfo);
			for(Map zipFile:zipFileList){
				excelImportMapper.deleteAssetExcelResult(zipFile);
			}

			Decompress decompress = new Decompress();

			File zippedFile = new File(srcPath);
			if(!zippedFile.exists()){
				return;
			}
			
			File zipDirPath = new File(zipPath);
			if(!zipDirPath.exists()){
				zipDirPath.mkdirs();
			}
			
			decompress.unzip(zippedFile,zipDirPath, "CP949");
				
			
			fileInfoList = new ArrayList<File>();
			getSrcFileList(zipPath);				
			
			int fIdx = 0;
			
			for (File file : fileInfoList) { 
				
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				String fileId = "FD" + dateFormat.format(calendar.getTime()) + fIdx;				
				
				Map<String, String> zipFileInfo = new HashMap<String, String>();
				zipFileInfo.put("PFileId", adUpFileInfo.get("FILE_ID").toString());
				zipFileInfo.put("FileId", fileId);
				zipFileInfo.put("AdFileNm", file.getName());
				zipFileInfo.put("UserId", adUpFileInfo.get("USER_ID").toString());
				
				excelImportMapper.insertZipFileInfo(zipFileInfo);
				
				fIdx++;
			}		
			
		}catch(SQLException sqle){
			logger.error(sqle.getMessage(), sqle.fillInStackTrace());
		}catch(Exception e){
			logger.error(e.getMessage(), e.fillInStackTrace());
		}		
		
	}
	
	@SuppressWarnings({"rawtypes"})
	private int adZipParser(){
		
		int rtn = 2; //JOB_COMPLETE = 2	
		
		logger.info("adZipParser Start..!!");
		
		try {
			List<Map> zipFileInfoList = excelImportMapper.selectZipFileList();
			
			for(Map zipFileInfo:zipFileInfoList){
				
				zipQueue.push(zipFileInfo);
				
				ZipWorker zipWorker = new ZipWorker();
				zipWorker.startup();
				
				//Thread.sleep(1000);
			}
			
		}catch (SQLException e) {
			logger.error(e.getMessage(), e.fillInStackTrace());
		}
				
		return rtn;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	class Worker extends SafeThread {
		
		@Override
		public void run() {
			
			int rtn = 0;
			
			Map obj = (Map)fileQueue.pop();
			
			try {
			
				if(obj != null) {
					
					excelImportMapper.deleteAssetExcelResult(obj);
					
					obj.put("JOB_FLAG", "1");
					excelImportMapper.updateUploadFileStart(obj);
					
					String ext = obj.get("AD_FILE_TYPE").toString(); //xls (1), xlsx (2), zip (3)';

					logger.debug("=====================[Worker Thread]=======================");
					logger.debug("Excel Import EXT :: {}", ext);
					if(ext.equals("1") || ext.equals("2")){
						rtn = auditXlsxParser.start(obj);
					}else if(ext.equals("3")){ 
						UnZipProcess(obj);		
						rtn = adZipParser();
					}
					
					obj.put("JOB_FLAG", rtn);
					excelImportMapper.updateUploadFileEnd(obj);
				}
				
			}catch (Exception e) {
				logger.error("Worker Exception :: {}", e.getMessage(), e.fillInStackTrace());
			}
		}
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	class ZipWorker extends SafeThread {
		
		@Override
		public void run() {
			
			int rtn = 0;
			Map obj = (Map)zipQueue.pop();
			
			try{
			
				if(obj != null) {
					
					logger.debug("==============[ ZipWorker start ]==============");
					obj.put("JOB_FLAG", "1");
					excelImportMapper.updateZipFileStart(obj);
					
					String path = "";

					logger.debug(new Gson().toJson(obj));
					
					if(obj.get("FILE_UPLOAD_FOLDER") != null && !"".equals(obj.get("FILE_UPLOAD_FOLDER").toString())) {
						path = obj.get("FILE_UPLOAD_FOLDER").toString() + File.separator + obj.get("P_FILE_ID").toString() + File.separator + obj.get("AD_FILE_NM");
					}else {
						path = uploadPath +  File.separator + obj.get("P_FILE_ID").toString() +File.separator + obj.get("AD_FILE_NM").toString();
					}
					
					logger.debug("Excel file path :: {}", path);
					
					File file = new File(path);			
					String ext = file.getName().substring(file.getName().lastIndexOf(".")+1);		
					
					logger.debug("File Ext :: {}", ext);
					if(ext.equals("xls") || ext.equals("xlsx")){
						rtn = auditXlsxParser.start(obj);
					}else{
						rtn = 6;
						logger.warn("Not Allowed File Extension :: {}", ext);
					}
					
//					if(file.isFile()){
//						if(file.exists()){
//							File pFile = new File(file.getParent());
//							boolean result = file.delete();
//							if(result) logger.info("Delete File : " + file.getCanonicalPath());
//							
//							if(pFile.isDirectory()){
//								
//								File[] pFileList = pFile.listFiles();
//								if(pFileList.length == 0) {
//									pFile.delete();
//									if(result) logger.info("Delete Parent File : " + pFile.getCanonicalPath());
//								}
//							}
//						}
//					}
					
					obj.put("JOB_FLAG", rtn);
					excelImportMapper.updateZipFileEnd(obj);
					logger.debug("==============[ ZipWorker end ]==============");
				}
//			}catch (IOException ioe){
//				logger.error("ZipWorker Exception :: {}", ioe.getMessage(), ioe.fillInStackTrace());
//			}catch (SQLException e) {
//				logger.error("ZipWorker Exception :: {}", e.getMessage(), e.fillInStackTrace());
			}catch (Exception e){
				logger.error("ZipWorker Exception :: {}", e.getMessage(), e.fillInStackTrace());
			}
				
		}
	}
}
