/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 7. 18.
 * description : 
 */
package com.mobigen.snet.supportagent.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mobigen.snet.supportagent.component.MakeReportMailHtmlTemplate;
import com.mobigen.snet.supportagent.concurrents.OneTimeThread;
import com.mobigen.snet.supportagent.dao.DaoMapper;
import com.mobigen.snet.supportagent.entity.MailEntity;
import com.mobigen.snet.supportagent.entity.SMSEntity;
import com.mobigen.snet.supportagent.entity.SendMailReport;
import com.mobigen.snet.supportagent.utils.MailSendClient;
import com.mobigen.snet.supportagent.utils.SmsSendClient;


/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : SendServiceImpl.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 1. 20.
 * Description : 
 *
 */
@Slf4j
@Service
public class SendServiceImpl extends AbstractService implements SendService {

	@Autowired
	private DaoMapper daoMapper;

	@Autowired
	private MailSendClient mailSendClient;

	@Autowired
	private SmsSendClient smsSendClient;

	@Autowired
	private MakeReportMailHtmlTemplate makeReportMailHtmlTemplate;

	@Autowired
	private ThreadPoolTaskExecutor executor;

	@Value("${snet.mail.use}")
	private String mailUse;
	
	@Value("${snet.sms.use}")
	private String smsUse;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mobigen.snet.supportagent.service.SendService#sendMail(java.lang.
	 * String)
	 */
	@Override
	@Transactional
	public void sendMail(String mailCd) throws Exception {

		// 메일 대상자
		List<String> receiverList = (List<String>) daoMapper.selectSendMailUser(mailCd);
		// 메일 내용 N
		List<MailEntity> mailerList = (List<MailEntity>) daoMapper.selectMailInfo(mailCd);

		if (receiverList.size() > 0 && mailerList.size() > 0) {
			for (String receiver : receiverList) {

				logger.info("*[sendMail] receiver :: " + receiver);
				for (MailEntity mailer : mailerList) {
					OneTimeThread worker = new OneTimeThread() {
						@Override
						public void task() throws Exception {
							if (Boolean.parseBoolean(mailUse))
								mailSendClient.sendMailMessage(receiver, mailer.getMailFrom(), mailer.getMailCc(), mailer.getMailTitle(), mailer.getSendMsg(), mailer.getFileList());
						}
					};
					worker.start();
					worker.join(60000);
					if (worker.isAlive()) {
						worker.interrupt();
					}
				}
			}

			// 메일 보낸 후 상태 업데이트
			daoMapper.updateMailInfo(mailCd);
		} else {

			logger.info("*[sendMail] receiverList, mailerList size is 0");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mobigen.snet.supportagent.service.SendService#sendSms(java.lang.
	 * String)
	 */
	@Override
	@Transactional
	public void sendSms(String sendKey) throws Exception {

		if(Boolean.valueOf(smsUse)){
			SMSEntity entity = new SMSEntity();
			entity.setSendKey(sendKey);
			List<SMSEntity> smsList = (List<SMSEntity>) daoMapper.selectSms(entity);
			for (SMSEntity sms : smsList) {
				String userMs = sms.getUserMs();
				String sendMsg = sms.getSendMsg();
				// send message
				smsSendClient.sendSMS(userMs, sendMsg);
				
				// update sms status
				daoMapper.updateSmsStatus(sms.getSendKey());
			}
		}else{
			logger.info("Not supported sms service");
		}
			
	}

	@Override
	@Transactional
	public void sendOtp(String sendKey) throws Exception {
		
		if(Boolean.valueOf(smsUse)){
			SMSEntity entity = new SMSEntity();
			entity.setSendKey(sendKey);
			List<SMSEntity> smsList = (List<SMSEntity>) daoMapper.selectSms(entity);
			for (SMSEntity otp : smsList) {
				String userMs = otp.getUserMs();
				String sendMsg = otp.getSendMsg();
				// send message
				smsSendClient.sendSMS(userMs, sendMsg);
				
				// update sms status
				daoMapper.updateSmsStatus(otp.getSendKey());
			}
		}else{
			logger.info("Not supported sms service");
		}
	}

	@Override
	public void sendReportMail(boolean immediately) throws Exception {

		if(Boolean.valueOf(mailUse)){
			// Mail 대상자
			List<SendMailReport> mailList = new ArrayList<>(); 
			
			if(immediately) {
				mailList = daoMapper.sendMailReportImmediately();
			} else {
				/*
				 *  MAIL_TYPE
				 *  - 1 매일 주기
				 *  - 2 매주 금요일 
				 *  - 3 매월 1일
				 */
				
				// daily report
				mailList.addAll(daoMapper.dailySendMailReport());
				
				Calendar cal = Calendar.getInstance();
				int dayOfWeek  = cal.get(Calendar.DAY_OF_WEEK);
				int dayOfMonth = cal.getMaximum(Calendar.DAY_OF_MONTH);
				
				
				// weekly check 금요일
				if(dayOfWeek == 6)
					mailList.addAll(daoMapper.weeklySendMailReport());
				
				// monthly check 매월 1일
				if(dayOfMonth == 1)
					mailList.addAll(daoMapper.monthlySendMailReport());
			}
			
			if(mailList.size() > 0)
				multipleSendMailThread(mailList);
		}else{
			logger.info("Not supported SMTP service");
		}
			
	}

	public boolean sendReportMail(SendMailReport sendMail) throws Exception {
		
		boolean result = false;
		
		if(Boolean.valueOf(mailUse)){
			String textHtml = sendMail.getMailData();
			String userId = sendMail.getUserId();
			String userMail = sendMail.getUserMail();
			
			try {
				// Make Message for user
				textHtml = makeReportMailHtmlTemplate.getTextHtmlTemplate(userId, textHtml);
				mailSendClient.sendMailMessage(userMail, "", "", sendMail.getMailTitle(), textHtml, "");
				
				// update Send mail..???
				if(sendMail.getMailNow().equals("Y"))
					sendMail.setMailNow("N");
				
				daoMapper.updateSendMail(sendMail);
				result = true;
			} catch (Exception e) {
				logger.error("SendMail Exception :: {}", e.getMessage(), e.fillInStackTrace());
				throw new Exception("SendMail Exception, USER_ID :: "+userId +", USER_MAIL :: "+userMail);
			}
			
		}else{
			logger.info("Not supported SMTP service");
		}

		return result;
	}

	private void multipleSendMailThread(List<SendMailReport> mailList) throws Exception {

		logger.info("*[multipleSendMailThread] : {}", "start");

		try {
			List<List<SendMailReport>> partitionArray = new ArrayList<List<SendMailReport>>();

			int size = (int) Math.ceil(mailList.size() / (double) 3);
			partitionArray = Lists.partition(mailList, size);

			executor.setKeepAliveSeconds(60);
			executor.setAwaitTerminationSeconds(120);

			for (List<SendMailReport> sendMailJobList : partitionArray) {
				executor.execute(new SendMailTask(sendMailJobList), 60000);
			}
		} catch (Exception e) {
			logger.error("multipleSendMailThread Executor Exception :: {}", e.getMessage(), e.fillInStackTrace());
			executor.destroy();
			throw new Exception(e);
		}

		logger.info("*[multipleSendMailThread] : {}", "end");
	}

	private class SendMailTask implements Runnable {

		private List<SendMailReport> mailList;

		public SendMailTask(List<SendMailReport> mailList) {
			this.mailList = mailList;
		}

		public void run() {
			int size = mailList.size();
			try {
				for (int i = 0; i < size; i++) {
					SendMailReport sendMail = mailList.get(i);

					logger.debug("START_Send_Mail JOB NUM :: {}, USER_ID :: {}, USER_MAIL :: {}", i,
							sendMail.getUserId(), sendMail.getUserMail());

					OneTimeThread worker = new OneTimeThread() {
						@Override
						public void task() throws Exception {
							sendReportMail(sendMail);
						}
					};
					worker.start();
					worker.join(60000);
					if (worker.isAlive()) {
						worker.interrupt();
					}

					logger.debug("END_Send_Mail JOB NUM :: {}, USER_ID :: {}, USER_MAIL :: {}", i, sendMail.getUserId(), sendMail.getUserMail());
				}

//			} catch (InterruptedException e) {
//				logger.info("Thread Interrupted...");
			} catch (Exception e) {
				logger.error("Thread SEND MAIL REPORT Exception ", e);
			}
		}
	}
}
