/**
 * project : SupportAgent
 * package : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * 
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 17.
 * Description : 
 * 
 */
package com.mobigen.snet.supportagent.service;

import com.mobigen.snet.supportagent.models.ReportRequestDto;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : ExportService.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 17.
 * Description : 
 * 
 */
public interface ExportService {

	/**
	 * Excel Export
	 */
	public void excelExport(String key);

	/**
	 * 'sg_supprotmanager 프로젝트 - 상세보고서'
	 * Excel Export by request
	 */
	public void excelExportRequest(ReportRequestDto reportRequestDto);
}
