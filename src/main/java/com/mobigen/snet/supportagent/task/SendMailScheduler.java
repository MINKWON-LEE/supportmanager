package com.mobigen.snet.supportagent.task;

import com.mobigen.snet.supportagent.service.SendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SendMailScheduler {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired (required = false)
	private SendService sendService;

	@Value("${snet.mail.use}")
	private String mailUse;

	/**
	 * 분단위로 즉시 발송이 있을 경우 리포트 메일 발송
	 */
	@Scheduled(cron = "${snet.report.mail.schedule.immediately}")
	public void sendMailImmediately(){
		try {
			if(Boolean.valueOf(mailUse)) {
				logger.info("*[sendMailImmediately] start");

				sendService.sendReportMail(true);

				logger.info("*[sendMailImmediately] end");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * 일정 주기로 리포트 메일대상이 있을 경에 발송
	 * 발송 시간은 오전 7시로
	 * MAIL_TYPE
	 * - 1 매일
	 * - 2 매주 금요일
	 * - 3 매월 1일
	 *
	 */
	@Scheduled(cron = "${snet.report.mail.schedule.term}")
	public void sendMailTerm(){
		try {
			if(Boolean.valueOf(mailUse)) {
				logger.info("*[sendMailTerm] start");

				sendService.sendReportMail(false);

				logger.info("*[sendMailTerm] end");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e.fillInStackTrace());
		}
	}
}
