package com.mobigen.snet.supportagent.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobigen.snet.supportagent.component.SvcAssetSwExport;
import com.mobigen.snet.supportagent.dao.ExcelExportMapper;
import com.mobigen.snet.supportagent.memory.SafeThread;
import com.mobigen.snet.supportagent.memory.SyncQueue;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : SvcAssetSwExportService.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 1. 20.
 * Description : 
 * 
 */
@Service
public class SvcAssetSwExportService extends AbstractService {

	@Autowired
	private ExcelExportMapper excelExportMapper;
	
	@Autowired
	private SvcAssetSwExport svcAssetSwExport;
	
	SyncQueue exportQueue;
	
	public SvcAssetSwExportService(){
		this.exportQueue = new SyncQueue();
	}
	
	@SuppressWarnings("rawtypes")
	public void start(){
		
		logger.info("SvcAssetSwExportService Start..!!");
		
		try {
			
			List<Map> svcAssetSwExportList = excelExportMapper.selectSvcAssetSwExportList();
			
			for(Map svcAssetSwExport:svcAssetSwExportList){
				
				exportQueue.push(svcAssetSwExport);
				
				Worker worker = new Worker();
				worker.startup();
			}
			
		}catch (SQLException e) {
			logger.error(e.getMessage(), e.fillInStackTrace());
		}		
	}
	@SuppressWarnings("rawtypes")
	class Worker extends SafeThread {
		@Override
		public void run() {
			Map obj = (Map)exportQueue.pop();
			if(obj != null) {
				svcAssetSwExport.start(obj);
			}
		}
	}
}

