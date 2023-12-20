/**
 * project : SupportAgent
 * package : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * 
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 9.
 * Description : 
 * 
 */
package com.mobigen.snet.supportagent.service;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : ExternalInterWorkingService.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 9.
 * Description : 
 * 
 */
public interface ExternalInterWorkingService {

	
	/**
	 * TMS 외부 연동 데이터를 현행화 한다.
	 * @throws Exception
	 */
	public void TMSInterWorkingService() throws Exception;
	
	
	/**
	 * 네트워크 장비 진단 데이터 연동
	 * @throws Exception
	 */
	public void NetWorkEquipDiagService() throws Exception;
}
