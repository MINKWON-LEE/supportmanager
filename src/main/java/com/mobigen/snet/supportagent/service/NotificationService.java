/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 8. 4.
 * description : 
 */
package com.mobigen.snet.supportagent.service;


/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : NotificationService.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 1. 20.
 * Description : 
 * 
 */
public interface NotificationService {

	/**
	 * 서버 임계치 초과시 메일과 SMS를 보낸다.
	 * @return
	 */
	Boolean sendNotification();
	
}
