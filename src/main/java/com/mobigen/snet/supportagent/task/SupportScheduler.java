/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.schedule
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 4. 5.
 * description :
 */
package com.mobigen.snet.supportagent.task;

import com.google.common.base.Stopwatch;
import com.mobigen.snet.supportagent.component.AbstractComponent;
import com.mobigen.snet.supportagent.dao.DaoMapper;
import com.mobigen.snet.supportagent.entity.NmapJob;
import com.mobigen.snet.supportagent.entity.NpsNmapJob;
import com.mobigen.snet.supportagent.models.HealthChkHistoryDto;
import com.mobigen.snet.supportagent.models.was.SnetNotificationDataModel;
import com.mobigen.snet.supportagent.models.was.SnetNotificationModel;
import com.mobigen.snet.supportagent.service.*;
import com.mobigen.snet.supportagent.service.scheduler.AuditStatisticsManager;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Hyeon-sik Jung
 */
@Slf4j
@EnableScheduling
@Component
public class SupportScheduler extends AbstractComponent {

	@Autowired (required = false)
	private MonitoringTableService monitoringTableService;
	@Autowired (required = false)
	private PortScanService portScanService;
	@Autowired (required = false)
	private MakeReportService makeReportService;
	@Autowired (required = false)
	private ResourceMonitoringService resourceMonitoringService;
	@Autowired (required = false)
	private DaoMapper daoMapper;
	@Autowired (required = false)
	private ParserService parserService;
	@Autowired (required = false)
	private NotificationService notificationService;
	@Autowired (required = false)
	private FireWallConfigService fireWallConfigService;
	@Autowired (required = false)
	private EtcService etcService;
	@Autowired (required = false)
	private SynService synService;
	@Autowired
	private CTISyncService ctiSyncService;

	@Value("${snet.schedule.nmap.use}")
	private boolean nmapUse;
	@Value("${snet.schedule.account.status.use}")
	private boolean accountStatusUse;
	@Value("${snet.schedule.batch.diagnosis.use}")
	private boolean diagnosisUse;
	@Value("${snet.schedule.makereport.use}")
	private boolean makereportUse;
	@Value("${snet.schedule.makereport.agent.use}")
	private boolean makereportAgentUse;
	@Value("${snet.excel.upload.check.use}")
	private boolean excelUploadUse;
	@Value("${snet.notification.use}")
	private boolean notiUse;
	@Value("${snet.schedule.resourcemonitoring.use}")
	private boolean resourceMonitoringUse;
	@Value("${snet.schedule.restartservice.use}")
	private boolean restartUse;
	@Value("${snet.schedule.batch.firewallrate.use}")
	private boolean firewallUse;
	@Value("${snet.ctisync.use}")
	private boolean ctiSyncUse;
	@Value("${snet.schedule.intro.use}")
	private boolean introUse;
	@Value("${snet.schedule.itgo.sync.use}")
	private boolean itgoUse;
	@Value("${snet.schedule.config.pkms.use}")
	private boolean pkmsUse;
	@Value("${snet.schedule.data.save.use}")
	private boolean dataSaveUse;
	@Value("${snet.schedule.excel.job.delete.data.use}")
	private boolean excelJobDeleteUse;
	@Value("${snet.schedule.resource.job.delete.data.use}")
	private boolean agentJobDeleteUse;
	@Value("${snet.schedule.branchReport.job.delete.data.use}")
	private boolean branchReportJobDeleteUse;
	@Value("${snet.schedule.branchReport.week.use}")
	private boolean branchWeeklyReportUse;
	@Value("${snet.schedule.branchReport.month.use}")
	private boolean branchMonthlyReportUse;


	@Autowired
	private ExportService exportService;
	@Autowired
	StatisticsService statisticsService;
	@Autowired
	private AuditStatisticsManager auditStatisticsManager;

	/**
	 * CTI 연동데이터 SNET_ASSET_SW_AUDIT_DAY CVE_ 관련컬럼 업데이트
	 */
	@Scheduled(cron="${snet.ctisync.schedule}")
	public void tiSyncJobScheduler() {
		try {
			if (this.ctiSyncUse) {
				logger.info("*[ctiSyncJobScheduler] start");

				this.ctiSyncService.runSync();

				logger.info("*[ctiSyncJobScheduler] end");
			} else {
				this.logger.info("Not supported igloo cti sync service.");
			}
		}
		catch (Exception e)
		{
			this.logger.error(e.getMessage(), e.fillInStackTrace());
		}
	}
//
	/**
	 * 진단 결과 일괄 다운로드 스케쥴러
	 */
	//@Scheduled(cron = "${snet.schedule.excel}")
	public void excelJobScheduler(){

		logger.info("*[excelJobScheduler] start");

		try {

//			exportService.excelExport("0");
		} catch (Exception e) {
			logger.error(e.getMessage(), e.fillInStackTrace());
		}

		logger.info("*[excelJobScheduler] end");
	}

	/**
	 * 서버 임계치 초과시 메일과 SMS 보냄
	 */
	@Scheduled(cron = "${snet.notification.schedule}")
	public void notificatinoScheduler(){
		try {
			if(notiUse) {
				logger.info("*[notificatinoScheduler] start");

				if (!notificationService.sendNotification())
					throw new Exception("Send Notification Failed");

				logger.info("*[notificatinoScheduler] end");
			}
		} catch (Exception e) {
			logger.error("Notification Scheduler Exception :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * 60일이상 로그인 정보 / 90일 이상 비밀번호 변경여부 체크
	 * skt는 ID가 1로 시작하거나 P로 시작
	 */
	@Scheduled(cron = "${snet.schedule.account.status}")
	public void accountStatus(){
		try {
			if(this.accountStatusUse) {
				logger.info("*[accountStatus] start");

				monitoringTableService.snetConfigAccount();

				logger.info("*[accountStatus] end");
			}
		} catch (Exception e) {
			logger.error("Account Status update Scheduler Exception :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * Nmap 포트스캔 스케쥴러
	 * nmap 별도 설치
	 */
	@Scheduled(cron = "${snet.schedule.nmap}")
	public void nmapScheduler(){
		try {
			if(this.nmapUse) {
				logger.info("*[nmapScheduler] start");

				List<NmapJob> nmapJoblist = daoMapper.monitoringNmapJob();
				for(NmapJob job : nmapJoblist){
					portScanService.portScan(job.getJobKey());
				}

				logger.info("*[nmapScheduler] end");
			}
		} catch (Exception e) {
			logger.error("Nmap Scheduler Exception :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * Multiple Nmap 포트스캔 스케쥴러
	 * nmap 별도 설치
	 */
	@Scheduled(cron = "${snet.schedule.nmap}")
	public void npsNmapScheduler(){
		try {
			if(this.nmapUse) {
				logger.info("*[npsNmapScheduler] start");

				List<NpsNmapJob> nmapJoblist = daoMapper.monitoringTempletJob();

				for(NpsNmapJob job : nmapJoblist){
					portScanService.multiplePortScan(job.getJobId());
				}

				logger.info("*[npsNmapScheduler] end");
			}
		} catch (Exception e) {
			logger.error("Nmap Scheduler Exception :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * 리포트 데이터 생성
	 */
	@Scheduled(cron = "${snet.schedule.makereport}")
	public void makeReportScheduler(){
		try{
			if(this.makereportUse) {
				logger.info("*[makeReportScheduler] start");

				makeReportService.makeReport();

				logger.info("*[makeReportScheduler] end");
			}
		}catch(Exception e){
			logger.error("Make Report Excetion :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * SNET_REPORT_AGENT 등록
	 */
	@Scheduled(cron = "${snet.schedule.makereport.agent}")
	public void makeAgentReportSchdueler(){
		try{
			if(this.makereportAgentUse) {
				logger.info("*[makeAgentReportSchdueler] start");

				makeReportService.makeAgentReport();

				logger.info("*[makeAgentReportSchdueler] end");
			}
		}catch(Exception e){
			logger.error("Make Agent Report Excetion :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * 리소스 리포트 데이터 생성
	 */
	@Scheduled(cron = "${snet.schedule.resourcemonitoring}")
	public void resourceMonitoring(){
		try{
			if(resourceMonitoringUse){
				logger.info("*[resourceMonitoring] start");

				resourceMonitoringService.getResource();

				logger.info("*[resourceMonitoring] end");
			}
		}catch(Exception e){
			logger.error("Resource Monitoring Excetion :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * 매니저서버 재시작
	 */
	@Scheduled(cron = "${snet.schedule.restartservice}")
	public void restartService(){
		try{
			if(restartUse) {
				logger.info("*[restartService] start");

				resourceMonitoringService.restartService();

				logger.info("*[restartService] end");
			}
		}catch(Exception e){
			logger.error("Restart Service Excetion :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * 진단결과 엑셀업로드 큐로 등록
	 * ASSET_SW_AUDIT_UPLOAD - JOB_FLAG = 0
	 */
	@Scheduled(cron = "${snet.excel.upload.check}")
	public void excelImportScheduler(){
		try{
			if(this.excelUploadUse) {
				logger.info("*[excelImportScheduler] start");

				parserService.start(2);

				logger.info("*[excelImportScheduler] end");
			}

		}catch(Exception e){
			logger.error("Excel Import Excetion :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}
//
//	//2017.12.11 계정 삭제 스케쥴러 주석 - 이상준
////	@Scheduled(cron = "${snet.schedule.account.status}")
////	public void loginUserTermManagerScheduler(){
////		try{
////			monitoringTableService.snetConfigUserLoginTerm();
////		}catch(Exception e){
////			logger.error("Login User Term Manager Excetion :: {}", e.getMessage(), e.fillInStackTrace());
////		}
////	}
//

	/**
	 * SNET_ASSET_SW_AUDIT_SCHEDULE 테이블에서 자동 진단 대상 정보를
	 * SNET_AGENT_JOB_HISTORY, SNET_AGENT_JOB_RDATE 테이블에
	 * 데이터를 넣어준다
	 */
	@Scheduled(cron = "${snet.schedule.batch.diagnosis}")
	public void batchDiagnosis(){
		try{
			if(this.diagnosisUse) {
				logger.info("*[batchDiagnosis] start");

				monitoringTableService.batchDiagnosisAgentSet();

				logger.info("*[batchDiagnosis] end");
			}
		}catch(Exception e){
			logger.error("Batch Diagnosis Agent set Excetion :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}
	/**
	 * 1시간 전의 에이전트 상태 히스토리 삭제
	 */
	@Scheduled(cron = "${snet.schedule.batch.diagnosis}")
	public void batchDeleteAgentHealthChkHistory(){
		try{
			if(this.diagnosisUse) {
				logger.info("*[batchDeleteAgentHealthChkHistory] start");

				// agent_status 1 (online) 을 3 (offline) 으로 변경
				Map beforeParamMap = new HashMap();
				beforeParamMap.put("AGENT_STATUS", 1);
				List<HealthChkHistoryDto> agentHealthChkList = daoMapper.selectAgentHealthChkHistory(beforeParamMap);
				logger.info("*[batchDeleteAgentHealthChkHistory] agentHealthChkList size : {}", agentHealthChkList.size());
				for (HealthChkHistoryDto agentHealthChkStr : agentHealthChkList) {
					Map paramMap = new HashMap();
					paramMap.put("AGENT_BEFORE_STATUS", 1);
					paramMap.put("AGENT_STATUS", 3);
					paramMap.put("ASSET_CD", agentHealthChkStr.getAssetCd());
					int cnt = daoMapper.updateAgentHealthChkHistory(paramMap);

					if(cnt > 0){
						//알림 처리
						//noti는 오류 발생해도 무시
						try {
							//SNET_NOTIFICATION_CONFIG 알림 사용 체크
							String notiType = "6";
							String useYn = daoMapper.getNotificationUseYn(notiType);

							if ("Y".equalsIgnoreCase(useYn)) {
								//진단실행한 유저
								String notiUserId = agentHealthChkStr.getUserId();
								//notificationmodel
								SnetNotificationModel snetNotificationModel = SnetNotificationModel
										.builder()
										.notiType(notiType)
										.notiUserId(notiUserId)
										.notiDataYn("Y")
										.notiLinkUrl("")
										.reqUserId(notiUserId)
										.build();
								//notificationdatamodel
								SnetNotificationDataModel snetNotificationDataModel = SnetNotificationDataModel
										.builder()
										.assetCd(agentHealthChkStr.getAssetCd())
										.hostNm(agentHealthChkStr.getHostNm())
										.ipAddress(agentHealthChkStr.getIpAddress())
										.assetUserId(agentHealthChkStr.getUserId())
										.build();

								if (notiUserId != null && !"".equals(notiUserId)) {
									insertNotification(snetNotificationModel, snetNotificationDataModel);
								}

								//설정에 추가된 알림 유저
								List<String> notiUserList = daoMapper.selectNotificationUserList(notiType);
								if (notiUserList != null && !notiUserList.isEmpty()) {
									for (String userId : notiUserList) {
										if (!StringUtil.isEmpty(userId) && !userId.equals(notiUserId)) {
											snetNotificationModel.setNotiUserId(userId);
											insertNotification(snetNotificationModel, snetNotificationDataModel);
										}
									}
								}
							}
						}catch(Exception ee){
							logger.error("notification insert error {}", ee.getLocalizedMessage());
						}
					}

				}
				logger.info("*[batchDeleteAgentHealthChkHistory] end");
			}

			// SNET_RELAY_STATUS_HISTORY 없어졌으므로 미사용
//			beforeParamMap.put("RELAY_STATUS", 1);
//			List<HealthChkHistoryDto> relayHealthChkList = daoMapper.selectRelayHealthChkHistory(beforeParamMap);
//			logger.info("*[batchDeleteAgentHealthChkHistory] relayHealthChkList size : {}", relayHealthChkList.size());
//			for (HealthChkHistoryDto relayHealthChkStr : relayHealthChkList) {
//				Map paramMap = new HashMap();
//				paramMap.put("RELAY_BEFORE_STATUS", 1);
//				paramMap.put("RELAY_STATUS", 3);
//				paramMap.put("ASSET_CD", relayHealthChkStr.getAssetCd());
//				daoMapper.updateRelayHealthChkHistory(paramMap);
//			}

			// agent_status 3 (offline) 을 1 (online) 로 변경
//			Map afterParamMap = new HashMap();
//			afterParamMap.put("AGENT_STATUS", 3);
//			List<HealthChkHistoryDto> agentHealthChkRestoreList = daoMapper.selectAgentHealthChkHistory(afterParamMap);
//			logger.info("*[batchDeleteAgentHealthChkHistory] agentHealthChkRestoreList size : {}", agentHealthChkRestoreList.size());
//			for (HealthChkHistoryDto agentHealthChkRestoreStr : agentHealthChkRestoreList) {
//				Map paramMap = new HashMap();
//				paramMap.put("AGENT_BEFORE_STATUS", 3);
//				paramMap.put("AGENT_STATUS", 1);
//				paramMap.put("ASSET_CD", agentHealthChkRestoreStr.getAssetCd());
//				daoMapper.updateAgentHealthChkHistory(paramMap);
//			}

			// SNET_RELAY_STATUS_HISTORY 없어졌으므로 미사용
//			List<HealthChkHistoryDto> relayHealthChkRestoreList = daoMapper.selectRelayHealthChkHistory(afterParamMap);
//			logger.info("*[batchDeleteAgentHealthChkHistory] relayHealthChkRestoreList size : {}", relayHealthChkRestoreList.size());
//			for (HealthChkHistoryDto relayHealthChkRestore : relayHealthChkRestoreList) {
//				Map paramMap = new HashMap();
//				paramMap.put("RELAY_BEFORE_STATUS", 3);
//				paramMap.put("RELAY_STATUS", 1);
//				paramMap.put("ASSET_CD", relayHealthChkRestore.getAssetCd());
//				daoMapper.updateRelayHealthChkHistory(paramMap);
//			}

		}catch(Exception e){

			logger.error("Batch Delete Agent HealthChk History Excetion :: {}", e.getMessage(), e.fillInStackTrace());
		}
	} // end method

	/**
	 * 알림 저장
	 */

	public void insertNotification(SnetNotificationModel snetNotificationModel, SnetNotificationDataModel snetNotificationDataModel){

		if(snetNotificationModel == null) return;
		if(StringUtil.isEmpty(snetNotificationModel.getNotiUserId())) return;
		if(StringUtil.isEmpty(snetNotificationModel.getNotiType())) return;

		try {
			//notiseq 체크
			long notiSeq = 0;
			notiSeq = daoMapper.getDuplicateNotiSeq(snetNotificationModel);
			snetNotificationModel.setNotiFlag("1");
			snetNotificationModel.setUseYn("Y");
			snetNotificationModel.setNotiSeq(notiSeq);
			if(notiSeq == 0){                        //insert
				daoMapper.insertNotification(snetNotificationModel);
			}

			if(snetNotificationDataModel == null) return;

			snetNotificationDataModel.setNotiSeq(snetNotificationModel.getNotiSeq());
			//data insert
			if("Y".equalsIgnoreCase(snetNotificationModel.getNotiDataYn())){
				daoMapper.insertNotificationData(snetNotificationDataModel);
			}
		} catch (Exception e) {}
	}

	/**
	 * 리소스 모니터링 14일 이전 데이터 삭제
	 */
	@Scheduled(cron = "${snet.schedule.resourcemonitoring.delete.data}")
	public void deleteResourceMonitoringData(){
		try{
			if(this.resourceMonitoringUse) {
				logger.info("*[deleteResourceMonitoringData] start");

				resourceMonitoringService.deleteResourceMorniotring();

				logger.info("*[deleteResourceMonitoringData] end");
			}
		}catch(Exception e){
			logger.error("Delete Resource monitoring data Excetion :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * AUDIT_CONFIG_FIREWALL_RATE 데이터 등록
	 * SKT 내부 방화벽정책관리시스템 연동데이터(현재 연동안됨)
	 */
	@Scheduled(cron = "${snet.schedule.batch.firewallrate.insert}")
	public void batchInsertAuditConfigFirewallRate(){
		try{
			if(this.firewallUse) {
                logger.info("*[batchInsertAuditConfigFirewallRate] start");

				fireWallConfigService.dataInsert();

                logger.info("*[batchInsertAuditConfigFirewallRate] end");
			}
		}catch(Exception e){
			logger.error("Batch INSERT AUDIT_CONFIG_FIREWALL_RATE Excetion :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * DB backup scheduler
	 */
	@Scheduled(cron = "${snet.schedule.maria.db.backup}")
	public void mariaDBBackUp(){
		try{
			// DB backup service
			String dbBackupUse = daoMapper.BackupUse();
			if(dbBackupUse.equals("1")) {
				logger.info("*[mariaDBBackUp] start");

				etcService.backupDB();

				logger.info("*[mariaDBBackUp] end");
			} else {
				logger.info("Not supported maria db backup");
			}
		}catch(Exception e){
			logger.error("MariaDB Backup Excetion :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 *	ITGO 데이터 연동 스케줄러 - 03:00 에 동작
	 **/
	@Scheduled(cron = "${snet.schedule.itgo.sync.data}")
	public void itgoSyncData(){
		try{
			//  ITGO 데이터 연동
			if(this.itgoUse) {
				logger.info("*[itgoSyncData] start");

				synService.itgoSync();

				logger.info("*[itgoSyncData] end");
			}
		}catch(Exception e){
			logger.error("ITGOSYNCDATA Service Error :: " + e.getMessage(), e);
		}
	}

	/**
	 * PKMS에서 SmartGuard로 진단신청 요청정보
	 * SNET_SERVICE_MASTER | SNET_SERVICE_USER 등록
	 */
	@Scheduled(cron = "${snet.schedule.config.pkms}")
	public void snetConfigPkms(){
		try{
			if(this.pkmsUse) {
				logger.info("*[snetConfigPkms] start");

				Stopwatch sw = Stopwatch.createStarted();
				monitoringTableService.snetConfigPkms();
				logger.info("sec:{}", sw.stop().elapsed(TimeUnit.SECONDS));

				logger.info("*[snetConfigPkms] end");
			}
		}catch(Exception e){
			logger.error("== monitorSnetConfigPkms :: " + e.getMessage(), e);
		}
	}

	/**
	 *	진단 데이터 삭제 스케줄러 - 01:00 에 동작
	 **/
	@Scheduled(cron = "${snet.schedule.data.save.term}")
	public void snetDataSaveTerm(){
		try{
			if(this.dataSaveUse) {
				logger.info("*[snetDataSaveTerm] start");

				Stopwatch sw = Stopwatch.createStarted();

				monitoringTableService.snetDataSaveTerm();

				logger.info("sec:{}", sw.stop().elapsed(TimeUnit.SECONDS));
				logger.info("*[snetDataSaveTerm] end");
			}
		}catch(Exception e){
			logger.error("== monitorsnetDataSaveTerm :: " + e.getMessage(), e);
		}
	}

	/**
	 * 'sg_supprotmanager 프로젝트 - 스케줄러'
	 *  통계 테이블 데이터 생성 스케줄러 - 통계 테이블 1일 주기 (미사용)
	 */
//	@Scheduled(cron = "${snet.schedule.audit.statistics.day}")
//	public void auditStatisticsDayJobScheduler() throws Exception {
//
//		log.info("*[IntroBatch4] auditStatisticsDayJobScheduler start [---------------------------------------------------------------------------------------------]");
//		// watch start
//		StopWatch watch = new StopWatch("test4");
//		watch.start();
//
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//		Calendar cal = Calendar.getInstance();
//		boolean todayFlag = false;
//
//		// 인트로 어제날짜 데이터 입력시 사용
//		// -> 실제 배치 처리는 오늘날짜로 입력
////		cal.add(Calendar.DATE, -1);
//
//		String createDate = dateFormat.format(cal.getTime());
//		log.info("*[createDate] : {}", createDate);
//
//		auditStatisticsManager.introBatch(createDate, todayFlag);
//
//		watch.stop();
//		// watch end
//
//		log.info("\n{}", watch.prettyPrint());
//
//		long millis = watch.getTotalTimeMillis();
//
//		String pattern = "mm:ss";
//		SimpleDateFormat format = new SimpleDateFormat(pattern);
//		String date = format.format(new Timestamp(millis));
//		log.info("\nTotal time : {} sec", date);
//
//		log.info("*[IntroBatch4] auditStatisticsDayJobScheduler end [---------------------------------------------------------------------------------------------]");
//	} // end method auditStatisticsJobScheduler

	/**
	 * 'sg_supprotmanager 프로젝트 - 스케줄러'
	 *  통계 테이블 데이터 생성 스케줄러 - TODAY 테이블 1시간 주기
	 */
	//@Scheduled(cron = "${snet.schedule.audit.statistics.hour}")
	/*public void auditStatisticsHourJobScheduler() throws Exception {
		try {
			if(this.statisticsHourUse) {
				log.info("*[IntroBatch4] auditStatisticsHourJobScheduler start [---------------------------------------------------------------------------------------------]");

				// watch start
				StopWatch watch = new StopWatch("test4");
				watch.start();

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				Calendar cal = Calendar.getInstance();
				boolean todayFlag = true;

				// 인트로 어제날짜 데이터 입력시 사용
				// -> 실제 배치 처리는 오늘날짜로 입력
		//		cal.add(Calendar.DATE, -1);

				String createDate = dateFormat.format(cal.getTime());
				log.info("*[createDate] : {}", createDate);

				auditStatisticsManager.introBatch(createDate, todayFlag);

				watch.stop();
				// watch end

				log.info("\n{}", watch.prettyPrint());

				long millis = watch.getTotalTimeMillis();

				String pattern = "mm:ss";
				SimpleDateFormat format = new SimpleDateFormat(pattern);
				String date = format.format(new Timestamp(millis));
				log.info("\nTotal time : {} sec", date);

				log.info("*[IntroBatch4] auditStatisticsHourJobScheduler end [---------------------------------------------------------------------------------------------]");
			}
		} catch (Exception e) {
			logger.error("[IntroBatch4] auditStatisticsHourJobScheduler Excetion" + e.getMessage(), e);
		}
	} // end method auditStatisticsJobScheduler*/

	/**
	 * 'sg_supprotmanager 프로젝트 - 스케줄러'
	 *  통계 테이블 데이터 생성 스케줄러 - TODAY 테이블 1시간 데이터 -> (이관) -> 통계 테이블 1일 데이터
	 */
	//@Scheduled(cron = "${snet.schedule.audit.statistics.mig}")
	/*public void auditStatisticsMigrationJobScheduler() throws Exception {
		try {
			if(this.statisticsMigUse) {
				log.info("*[IntroBatch4] auditStatisticsMigrationJobScheduler start [---------------------------------------------------------------------------------------------]");

				// watch start
				StopWatch watch = new StopWatch("test4");
				watch.start();

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				Calendar cal = Calendar.getInstance();

				String createDate = dateFormat.format(cal.getTime());
				log.info("*[createDate] : {}", createDate);

				// 인트로 어제날짜 데이터 입력시 사용
				// -> 실제 배치 처리는 오늘날짜로 입력
				cal.add(Calendar.DATE, -1);

				String beforeCreateDate = dateFormat.format(cal.getTime());
				log.info("*[beforeCreateDate] : {}", beforeCreateDate);

				auditStatisticsManager.migBatch(createDate, beforeCreateDate);

				watch.stop();
				// watch end

				log.info("\n{}", watch.prettyPrint());

				long millis = watch.getTotalTimeMillis();

				String pattern = "mm:ss";
				SimpleDateFormat format = new SimpleDateFormat(pattern);
				String date = format.format(new Timestamp(millis));
				log.info("\nTotal time : {} sec", date);

				log.info("*[IntroBatch4] auditStatisticsMigrationJobScheduler end [---------------------------------------------------------------------------------------------]");
			}

		}catch (Exception e) {
			logger.error("[IntroBatch4] auditStatisticsMigrationJobScheduler Excetion" + e.getMessage(), e);
		}
	} // end method auditStatisticsJobScheduler*/

	@Scheduled(cron = "${snet.schedule.intro.sec}")
	public void introSecCategoryJobScheduler() throws Exception {
		try{
			if(this.introUse) {
				log.info("*[Intro] introSecCategoryJobScheduler start");

				// watch start
				StopWatch watch = new StopWatch("test4");
				watch.start();

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -1);
				String createDate = dateFormat.format(cal.getTime());

				log.info("*[createDate] : {}", createDate);

				statisticsService.secCategoryBatch(createDate);

				watch.stop();
				// watch end

				log.info("\n{}", watch.prettyPrint());

				long millis = watch.getTotalTimeMillis();

				String pattern = "mm:ss";
				SimpleDateFormat format = new SimpleDateFormat(pattern);
				String date = format.format(new Timestamp(millis));
				log.info("\nTotal time : {} sec", date);

				log.info("*[Intro] introSecCategoryJobScheduler end");
			}
		}catch (Exception e) {
			logger.error("[Intro] introSecCategoryJobScheduler Excetion" + e.getMessage(), e);
		}
	}

	/**
	 * 보고서 EXCEL JOB 테이블 삭제
	 */
	@Scheduled(cron = "${snet.schedule.excel.job.delete.data}")
	public void deleteAuditExcelJobScheduler(){
		try{
			if(this.excelJobDeleteUse) {
				logger.info("*[deleteAuditExcelJobScheduler] start");

				resourceMonitoringService.deleteAuditExcelJobScheduler();

				logger.info("*[deleteAuditExcelJobScheduler] end");
			}
		}catch(Exception e){
			logger.error("Delete deleteAuditExcelJobScheduler data Excetion :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * 에이전트 리소트 데이터 삭제
	 */
	@Scheduled(cron = "${snet.schedule.resource.job.delete.data}")
	public void deleteAgentResource() {
		try {
			if (this.agentJobDeleteUse) {
				logger.info("*[deleteAgentResource] start");

				resourceMonitoringService.deleteAgentResource();

				logger.info("*[deleteAgentResource] end");
			}
		} catch (Exception e) {
			logger.error("Delete agentResource Excetion :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * 부서별 준수율 주간 데이터 생성 (국민연금 전용)
	 */
	@Scheduled(cron = "${snet.schedule.branchReport.week}")
	public void branchWeeklyReportSchdueler() {
		try {
			if (this.branchWeeklyReportUse) {
				logger.info("*[branchWeeklyReportSchdueler] start");

				makeReportService.branchWeeklyReport();

				logger.info("*[branchWeeklyReportSchdueler] end");
			}
		} catch (Exception e) {
			logger.error("branch weekly Report Excetion :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * 부서별 준수율 월간 데이터 생성 (국민연금 전용)
	 */
	@Scheduled(cron = "${snet.schedule.branchReport.month}")
	public void branchMonthlyReportSchdueler() {
		try {
			if (this.branchMonthlyReportUse) {
				logger.info("*[branchMonthlyReportSchdueler] start");

				makeReportService.branchMonthlyReport();

				logger.info("*[branchMonthlyReportSchdueler] end");
			}
		} catch (Exception e) {
			logger.error("branch monthly Report Excetion :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * 부서별 준수율 데이터 삭제(작년 12월 기준 전 데이터 삭제)
	 */
	@Scheduled(cron = "${snet.schedule.branchReport.job.delete.data}")
	public void deleteBranchReport() {
		try {
			if (this.branchReportJobDeleteUse) {
				logger.info("*[deleteBranchReport] start");

				makeReportService.deleteBranchReport();

				logger.info("*[deleteBranchReport] end");
			}
		} catch (Exception e) {
			logger.error("Delete branch Report Excetion :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}
}
