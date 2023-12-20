package com.mobigen.snet.supportagent.service;

import com.mobigen.snet.supportagent.models.ReportRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobigen.snet.supportagent.memory.ManagerJobFactory;

import jodd.util.StringUtil;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : ManageMentJobServiceImpl.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 1. 20.
 * Description : 
 *
 */
@Service
public class ManageMentJobServiceImpl extends AbstractService implements ManageMentJobService {

	@Autowired
	private ExportService exportService;

	@Autowired (required = false)
	private PortScanService portScanService;

	@Autowired (required = false)
	private SendService sendService;

	/**
	 * Frontend -> WAS -> 호출 파라미터에 따라 분기
	 */
	@Override
	public void jobManager(String msg) {

		logger.debug("Message :: {}", msg);

		try{

			// Frontend -> WAS -> 호출 파라미터
			String[] header = StringUtil.split(msg, "|");
			String jobType   = null;
			String key = null;
			String reqCd	 = null;
			String fileType	 = null;

			for (int i = 0; i < header.length; i++) {

				logger.debug("*[msg] : {}", header[i]);

				if (i == 0) {

					jobType = header[i];
				} else if (i == 1) {
					key = header[i];
				} else if (i == 2) {
					reqCd = header[i];
				} else if (i == 3) {
					fileType = header[i];
				}
			}

			logger.info("jobType 	: {}", jobType);
			logger.info("excelType 	: {}", key);
			logger.info("reqCd 		: {}", reqCd);
			logger.info("fileType 	: {}", fileType);

			ReportRequestDto reportRequestDto = new ReportRequestDto();
			reportRequestDto.setJobType(jobType);
			reportRequestDto.setExcelType(key);
			reportRequestDto.setReqCd(reqCd);
			reportRequestDto.setFileType(fileType);

			logger.debug("*[reportRequestDto] : {}", reportRequestDto);

			switch(jobType){
				case ManagerJobFactory.EXCEL :

					logger.debug("*[jobType] : {}", ManagerJobFactory.EXCEL);

					exportService.excelExportRequest(reportRequestDto);
					break;
				case ManagerJobFactory.NMAP :
					portScanService.portScan(key);
					break;
				case ManagerJobFactory.FNMAP :
					portScanService.nmapXmlInsert(key, header[2]);
					break;
				case ManagerJobFactory.MNMAP :
					portScanService.multiplePortScan(key);
					break;
				case ManagerJobFactory.MAIL :
					sendService.sendMail(key);
					break;
				case ManagerJobFactory.SMS :
					sendService.sendSms(key);
					break;
				case ManagerJobFactory.OTP :
					sendService.sendOtp(key);
					break;
				default : throw new Exception();
			}
		}catch(Exception e){

			logger.error("Invalid Job Request Exception :: {}", e.getMessage());
		}
	}
}
