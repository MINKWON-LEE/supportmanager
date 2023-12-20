/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 7. 12.
 * description : 
 */
package com.mobigen.snet.supportagent.service;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : ResourceMonitoringService.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 1. 20.
 * Description : 
 * 
 */
public interface ResourceMonitoringService {
	
	void getResource();

	void processReport();
	
	void diskReport();
	
	void cpuReport();
	
	void memoryReport();
	
	void restartService();
	
	void deleteResourceMorniotring();

	void deleteAuditExcelJobScheduler();

	void deleteAgentResource();
}
