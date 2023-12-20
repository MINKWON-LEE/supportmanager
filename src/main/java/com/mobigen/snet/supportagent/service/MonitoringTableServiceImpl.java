/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 5. 12.
 * description :
 */
package com.mobigen.snet.supportagent.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mobigen.snet.supportagent.dao.DaoMapper;
import com.mobigen.snet.supportagent.entity.BatchDiagnosis;
import com.mobigen.snet.supportagent.entity.ConfigAuditAvailableEntity;
import com.mobigen.snet.supportagent.entity.ConfigPkmsEntity;
import com.mobigen.snet.supportagent.entity.ServiceMaster;
import com.mobigen.snet.supportagent.entity.ServiceUser;
import com.mobigen.snet.supportagent.utils.DateUtil;

import jodd.util.StringUtil;


/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : MonitoringTableServiceImpl.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 1. 20.
 * Description : 
 *
 */
@Service
public class MonitoringTableServiceImpl extends AbstractService implements MonitoringTableService {

	@Autowired
	private DaoMapper daoMapper;

	@Value("${snet.support.script.unix.path}")
	private String unixPath;

	@Value("${snet.support.script.windows.path}")
	private String windowsPath;

	@Value("${snet.schedule.account.skip}")
	private String skipId;

	@Value("${snet.schedule.batch.diagnosis.delay}")
	private String delayRDate;

	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.MonitoringTableService#snetConfigAuditAvailable()
	 */
	@Override
	public void snetConfigAuditAvailable() throws Exception {
		List<ConfigAuditAvailableEntity>  list = daoMapper.selectAvailableConfig();
		for(ConfigAuditAvailableEntity entity : list){
			String unixResult = "N";

			if(entity.getUnixYn()!=null){
				StringBuilder sb = new StringBuilder();
				sb
				.append(unixPath)
				.append(File.separator)
				.append(entity.getUnixFileNm());

				if(checkFile(sb.toString()))
					unixResult= "Y";

				logger.debug("Update SW_Type :: {}, SW_Name:: {}, Unix YN origin :: {} ,  result :: {}", entity.getSwType(), entity.getSwNm(), entity.getUnixYn(), unixResult);
				entity.setUnixYn(unixResult);

			}

			String winResult = "N";
			if(entity.getWinYn()!=null){
				StringBuilder winSb = new StringBuilder();
				winSb
				.append(windowsPath)
				.append(File.separator)
				.append(entity.getWinFileNm());

				if(checkFile(winSb.toString()))
					winResult= "Y";

				logger.debug("Update SW_Type :: {}, SW_Name:: {}, Windows YN origin :: {} ,  result :: {}", entity.getSwType(), entity.getSwNm(), entity.getWinYn(), winResult);
				entity.setWinYn(winResult);
			}

			if(entity.getUnixYn()!=null || entity.getWinYn()!=null)
				daoMapper.updateConfigAvailable(entity);
		}
	}


	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.MonitoringTableService#snetConfigPkms()
	 */
	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public void snetConfigPkms() throws Exception {
		List<ConfigPkmsEntity> list =(List<ConfigPkmsEntity>) daoMapper.getConfigPkms();
		logger.info("== snetConfigPkms (건수:{})", list.size());


		for(ConfigPkmsEntity entity : list){

			int count = daoMapper.selectServiceMaster(Integer.parseInt(entity.getSeqNum()));

			if(count == 0 ){
				// make service code
				// SC + yyMMddHHmmssSSS 
				String svcCd = "SC"+ DateUtil.getCurrDateByStringFormat("yyMMddHHmmssSSS");

				// insert service master
				ServiceMaster sMaster = new ServiceMaster();

				// make service name
				// [PKMS-'SEQ_NUM'] PKG_NM
				StringBuilder sb = new StringBuilder();
				sb
				.append("[PKMS-")
				.append(entity.getSeqNum())
				.append("] ")
				.append(entity.getPkgNm().trim());

				sMaster.setSvcCd(svcCd);
				sMaster.setSvcNm(sb.toString());
				sMaster.setSvcCreateDay(DateUtil.getCurrDate());
				sMaster.setSvcPkmsSeq(Integer.parseInt(entity.getSeqNum()));
				daoMapper.insertServiceMaster(sMaster);

				// insert service user
				ServiceUser sUser = new ServiceUser();
				sUser.setSvcCd(svcCd);
				sUser.setTeamId(entity.getTeamId());
				sUser.setTeamNm(entity.getTeamNm());
				sUser.setUserId(entity.getUserId());
				sUser.setUserNm(entity.getUserNm());
				sUser.setUserMs(entity.getUserMs());
				sUser.setUserMail(entity.getUserMail());
				daoMapper.insertServiceUser(sUser);
			}
		}

	}


	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.MonitoringTableService#snetConfigAccount()
	 */
	@Override
	@Transactional
	public void snetConfigAccount() throws Exception {
		Map<String, String> param = new HashMap<>();
		param.put("skipId", skipId);
		daoMapper.updateLoginStatus(param);
		daoMapper.updatePwStatus(param);
	}



	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.MonitoringTableService#snetConfigUserLoginTerm()
	 */
	@Override
	@Transactional
	public void snetConfigUserLoginTerm() throws Exception {
		daoMapper.insertConfigUserAuthHistory();
		daoMapper.deleteConfigUserAuth();
	}

	@Override
	@Transactional
	public void batchDiagnosisAgentSet() throws Exception {

		//SNET_ASSET_SW_AUDIT_SCHEDULE 테이블에서 삭제된 장비 delete
		daoMapper.deleteSnetAssetSwAuditSchedule();

		List<BatchDiagnosis> list = daoMapper.selectBatchDiagnosis();
		logger.info("*[list] : {}", list);
		int delayTime = 0;

		if(!"".equals(delayRDate) && delayRDate != null && !delayRDate.equals("0")) {
			delayTime = Integer.parseInt(delayRDate);
		}

		if (list != null && list.size() > 0) {

			List<BatchDiagnosis> batchList = new ArrayList<BatchDiagnosis>();
			// day
			for (BatchDiagnosis batch : list) {
				if (batch.getAuditType().equals("D")) {
					batch.setDelayTime(delayTime);
					batchList.add(batch);
				}
			}

			String day = DateUtil.getFormatString("d");
			String month = DateUtil.getFormatString("M");

			// month, year 특정일에따른 스케쥴링 기능 추가
			for (BatchDiagnosis batch : list) {
				if (batch.getAuditType().equals("M")) {
					List<String> dayList = Arrays.asList(StringUtil.split(batch.getAuditDayList(), ","));
					if (dayList.contains(day)) {
						batch.setDelayTime(delayTime);
						batchList.add(batch);
					}
				} else if (batch.getAuditType().equals("Y")) {
					//특정월 기준 로직 추가
					List<String> monthList = Arrays.asList(StringUtil.split(batch.getAuditMonthList(), ","));
					if (monthList.contains(month)) {
						List<String> dayList = Arrays.asList(StringUtil.split(batch.getAuditDayList(), ","));
						if (dayList.contains(day)) {
							batch.setDelayTime(delayTime);
							batchList.add(batch);
						}
					}
				}
			}

			for (BatchDiagnosis batch : batchList) {
				//delete rdate
				daoMapper.deleteSnetAgentJobRdate(batch);

				// insert agent_job_history
				daoMapper.insertSnetAgentJobHistory(batch);

				// insert rdate
				daoMapper.insertSnetAgentJobRdate(batch);
			}
		} else {

			logger.info("*[selectBatchDiagnosis] size is 0");
		}
	}


	private boolean checkFile(String fileName){
		logger.debug("Check File Path :: {}", fileName);
		File file = new File(fileName);
		return file.isFile();
	}

	@Override
	public void snetDataSaveTerm() throws Exception {

		int dataSaveTerm = daoMapper.selectDateSaveTerm();

		String dateSaveTramAuditDay = DateUtil.getBeforeYearDay(dataSaveTerm);

		if (dateSaveTramAuditDay != null) {
			daoMapper.deleteDateSaveTermAssetSwAuditHistory(dateSaveTramAuditDay);
			daoMapper.deleteDateSaveTermAssetSwAuditReport(dateSaveTramAuditDay);
		}

	}
}
