/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 7. 18.
 * description : 
 */
package com.mobigen.snet.supportagent.service;


/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : SendService.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 1. 20.
 * Description : 
 * 
 */
public interface SendService {

	/**
	 * WAS에서 메일 발송 요청시 특정 테이블의 내용을 발송한다.
	 * @param mailCd
	 */
	void sendMail(String mailCd) throws Exception;
	
	/**
	 * WAS에서 SMS 발송 요청시 사용
	 * @param sendKey
	 */
	void sendSms(String sendKey) throws Exception;
	
	/**
	 * WAS에서 OTP SMS발송시 사용
	 * @param sendKey
	 */
	void sendOtp(String sendKey) throws Exception;
	
	/**
	 * 주기적으로 메일 발송
	 * default : false
	 * immediately - true 즉시 발송
	 */
	void sendReportMail(boolean immediately) throws Exception;
}
