/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 8. 4.
 * description : 
 */
package com.mobigen.snet.supportagent.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobigen.snet.supportagent.dao.ResourceMonitoringMapper;
import com.mobigen.snet.supportagent.entity.AlarmUser;
import com.mobigen.snet.supportagent.entity.DiskEntity;
import com.mobigen.snet.supportagent.entity.Threshold;
import com.mobigen.snet.supportagent.memory.GLOBALVAR.APPLICATION;
import com.mobigen.snet.supportagent.memory.GLOBALVAR.PROCESS;
import com.mobigen.snet.supportagent.memory.GLOBALVAR.TARGET;
import com.mobigen.snet.supportagent.memory.SafeThread;
import com.mobigen.snet.supportagent.utils.MailSendClient;
import com.mobigen.snet.supportagent.utils.SmsSendClient;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : NotificationServiceImpl.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 1. 20.
 * Description : 
 *
 */
@Service
public class NotificationServiceImpl extends AbstractService implements NotificationService {

	@Autowired
	private ResourceMonitoringMapper resourceMonitoringMapper;
	
	@Autowired
	private SmsSendClient smsSendClient;
	
	@Autowired
	private MailSendClient mailSendClient;
	
	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.NotificationService#sendNotification()
	 */
	@Override
	public Boolean sendNotification() {
		
		boolean result = true;
		try {
			AlarmUser setUser = new AlarmUser();
			setUser.setAlarmLevel("1");
			
			List<AlarmUser> userlist = resourceMonitoringMapper.selectAlarmUser(setUser);
			
			Threshold processTh = new Threshold();
			processTh.setCheckTarget(TARGET.PROCESS.NAME);
			processTh = resourceMonitoringMapper.selectThreshold(processTh);
			if(sendBeforeCheckProcess(processTh)){
				sendMessage(userlist, processTh.getAlarmDesc());
			}
			
			Threshold cpuTh = new Threshold();
			cpuTh.setCheckTarget(TARGET.CPU.NAME);
			cpuTh = resourceMonitoringMapper.selectThreshold(cpuTh);
			if(sendBeforeCheckCpu(cpuTh)){
				sendMessage(userlist, cpuTh.getAlarmDesc());
			}
			
			Threshold diskTh = new Threshold();
			diskTh.setCheckTarget(TARGET.DISK.NAME);
			diskTh = resourceMonitoringMapper.selectThreshold(diskTh);
			if(sendBeforeCheckDisk()){
				sendMessage(userlist, diskTh.getAlarmDesc());
			}
			
			Threshold memoryTh = new Threshold();
			memoryTh.setCheckTarget(TARGET.MEMORY.NAME);
			memoryTh = resourceMonitoringMapper.selectThreshold(memoryTh);
			if(sendBeforeCheckMemory(memoryTh)){
				sendMessage(userlist, memoryTh.getAlarmDesc());
			}
			
		} catch (Exception e) {
			logger.error("Send Notification Exception :: {}", e.getMessage(), e.fillInStackTrace());
			result = false;
		}
		
		return result;
	}

	private Boolean sendBeforeCheckProcess(Threshold th) throws Exception{
		
		boolean result = false;
		logger.debug("Target :: {},  Threshold :: {}", th.getCheckTarget(), th.getCheckValue());
		
		//agentManager process cpu udage
		double agentManagetUsage = resourceMonitoringMapper.checkProcess(PROCESS.AGENTMANAGER.NAME);
		if(Double.parseDouble(th.getCheckValue()) <=  agentManagetUsage)
			result =  true;
		
		logger.debug("AgentManager CPU Usage :: {}", agentManagetUsage);
		
		//supportAgent process cpu suage
		double supportUsage = resourceMonitoringMapper.checkProcess(PROCESS.SUPPORTAGENT.NAME);
		if(Double.parseDouble(th.getCheckValue()) <=  supportUsage)
			result =  true;
		
		logger.debug("SupportAgent CPU Usage :: {}", supportUsage);
		return result;
	}

	private Boolean sendBeforeCheckCpu(Threshold th) throws Exception{
		boolean result = false;
		
		double webServerUsage = resourceMonitoringMapper.checkCpu(APPLICATION.WEB.NAME);
		if(Double.parseDouble(th.getCheckValue()) <=  webServerUsage)
			result = true;
		logger.debug("Web server CPU usage :: {}", webServerUsage);
		
		double dbServerUsage  = resourceMonitoringMapper.checkCpu(APPLICATION.DB.NAME);
		if(Double.parseDouble(th.getCheckValue()) <=  dbServerUsage)
			result = true;
		logger.debug("Db server CPU usage :: {}", dbServerUsage);
		return result;
	}

	private Boolean sendBeforeCheckDisk() throws Exception{
		boolean result = false;
		
		List<DiskEntity> webServerDisk = resourceMonitoringMapper.checkDisk(APPLICATION.WEB.NAME);
		if(webServerDisk !=null && webServerDisk.size() > 0)
			result = true;
		
		List<DiskEntity> dbServerDisk  = resourceMonitoringMapper.checkDisk(APPLICATION.DB.NAME);
		if(dbServerDisk !=null && dbServerDisk.size() > 0)
			result = true;			
		return result;
	}

	private Boolean sendBeforeCheckMemory(Threshold th) throws Exception{
		boolean result = false;
		
		double webServerUsage = resourceMonitoringMapper.checkMemory(APPLICATION.WEB.NAME);
		if(Double.parseDouble(th.getCheckValue()) <=  webServerUsage)
			result = true;
		logger.debug("Web server Memory usage :: {}", webServerUsage);
		
		double dbServerUsage  = resourceMonitoringMapper.checkMemory(APPLICATION.DB.NAME);
		if(Double.parseDouble(th.getCheckValue()) <=  dbServerUsage)
			result = true;
		logger.debug("Db server Memory usage :: {}", dbServerUsage);
		return result;
	}
	
	private void sendMessage(List<AlarmUser> userList, String message){
		for(AlarmUser user : userList){
			SmsWorker smsWorker = new SmsWorker(user, message);
			smsWorker.start();
			
			MailWorker mailWorker = new MailWorker(user, message);
			mailWorker.start();
		}
	}
	
	class SmsWorker extends SafeThread {
		
		private String message;
		private AlarmUser user;
		
		SmsWorker(AlarmUser user, String message){
			this.user = user;
			this.message = message;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			try {
				smsSendClient.sendSMS(user.getUserMs(), message);
			} catch (Exception e) {
				logger.error("Send Sms Exception :: {}", e.getMessage(), e.fillInStackTrace());
			}
		}
		
	}
	
	class MailWorker extends SafeThread {
		
		private String message;
		private AlarmUser user;
		
		MailWorker(AlarmUser user, String message){
			this.user = user;
			this.message = message;
		}
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			try {
				String subject = "[smartGuard] System Notification Message";
				mailSendClient.sendMailMessage(user.getUserMail(), "", "", subject, message, "");
			} catch (Exception e) {
				logger.error("Send Sms Exception :: {}", e.getMessage(), e.fillInStackTrace());
			}
		}
		
	}
}
