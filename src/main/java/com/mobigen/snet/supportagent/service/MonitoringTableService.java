/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 5. 12.
 * description : 
 */
package com.mobigen.snet.supportagent.service;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : MonitoringTableService.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 1. 20.
 * Description : 
 *  특정 테이블 모니터링 용도로 제작
 */
public interface MonitoringTableService {

	/**
	 * TABLE SNET_CONFIG_AUDIT_AVAILABLE
	 * 해당 테이블의 정보를 업데이트하는 용도
	 * @throws Exception
	 */
	void snetConfigAuditAvailable() throws Exception;
	
	void snetConfigPkms() throws Exception;
	
	void snetConfigAccount() throws Exception;
	
	/**
	 * 로그인 90 경과 계정 정보 삭제 후 
	 * 히스토리 테이블에 결과 저장
	 * @throws Exception
	 */
	void snetConfigUserLoginTerm() throws Exception;
	
	/**
	 * SNET_ASSET_SW_AUDIT_SCHEDULE 테이블에서 자동 진단 대상 정보를  
	 * SNET_AGENT_JOB_HISTORY, SNET_AGENT_JOB_RDATE
	 * 테이블에 데이터를 넣어준다.
	 * 
	 * @throws Exception
	 */
	void batchDiagnosisAgentSet() throws Exception;

	void snetDataSaveTerm() throws Exception;
}
