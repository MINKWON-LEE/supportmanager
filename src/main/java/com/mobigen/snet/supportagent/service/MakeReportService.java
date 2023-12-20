/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 7. 1.
 * description : 
 */
package com.mobigen.snet.supportagent.service;


/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : MakeReportService.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 1. 20.
 * Description : 
 * 
 */
public interface MakeReportService {

	void makeReport();
	
	void updateCount();
	
	void makeAgentReport();

	void branchWeeklyReport();

	void branchMonthlyReport();

	void deleteBranchReport();
}
