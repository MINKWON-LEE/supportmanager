/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 5. 27.
 * description : 
 */
package com.mobigen.snet.supportagent.service;


/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : PortScanService.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 1. 20.
 * Description : 
 * 
 */
public interface PortScanService {

	void portScan(String key);
	
	void multiplePortScan(String key);
	
	void nmapXmlInsert(String key, String fileName);
}
