package com.mobigen.snet.supportagent.dao;

import com.mobigen.snet.supportagent.entity.*;
import com.mobigen.snet.supportagent.models.HealthChkHistoryDto;
import com.mobigen.snet.supportagent.models.was.SnetNotificationDataModel;
import com.mobigen.snet.supportagent.models.was.SnetNotificationModel;
import org.mybatis.spring.annotation.MapperScan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MapperScan
public interface DaoMapper {

	List<ReportEntity> getSwAuditReport(ExcelListEntity entity);

	List<ReportEntity> getSwTypeAuditReport(ExcelEntity entity);

	void updateExcelJob(ExcelJobEntity entity);

	ExcelJobEntity getExcelJob(ExcelJobEntity entity);

	List<ExcelJobEntity> selectExcelJob(ExcelJobEntity entity);

	List<ExcelListEntity> getExcelAsset(ExcelListEntity entity);

	List<ColumnEntity> getColumnName(String tableName);

	List<ColumnMariaDBEntity> getColumnNameMaria(Map<?,?> map);

	AssetEntity selectAssetInfo(String assetCd);

	AssetEntity selectAssetInfoExcel(ExcelListEntity excelEntity);

	List<ConfigAuditAvailableEntity> selectAvailableConfig();

	void updateConfigAvailable(ConfigAuditAvailableEntity entity);

	List<?> getConfigPkms();

	void insertServiceMaster(ServiceMaster serviceMaster);

	void insertServiceUser(ServiceUser serviceUser);

	Integer selectServiceMaster(Integer seq);

	void updateLoginStatus(Map params);

	void updatePwStatus(Map params);

	//firewall 데이터 이관
	List<FireWallEntity> selectFireWallConfig();
	List<FireWallRateEntity> selectHostIpAsset(FireWallRateEntity entity);
	List<FireWallRateEntity> selectIpAsset(FireWallRateEntity entity);
	List<FireWallRateEntity> selectHostAsset(FireWallRateEntity entity);

	//ITGO data update
	List<ITGOEntity> selectITGOconfig();
	void updateServiceMaster(ITGOEntity entity);
	void updateITGOconfig(ITGOEntity entity);

	void insertFireWallRateData(FireWallRateEntity entity);

	//shadow file decryt data
	void insertCrackId(HashMap<String ,String> map);

	int selectPwChk(String assetCd);
	void deletePwChk(String assetCd);
	/**
	 * <p>Nmap Job 검색</p>
	 * @param job
	 * @return Nmap Job
	 */
	NmapJob selectNmapJob(NmapJob job);

	List<NmapJob> monitoringNmapJob();

	/**
	 * <p>Nmap Job 상태 update</p>
	 * @param job
	 */
	void updateNmapJob(NmapJob job);

	/**
	 * @param data
	 */
	void insertNmapRawData(NmapData data);

	/**
	 * @param data
	 */
	void insertNmapResult(NmapData data);

	/**
	 * <p>Nmap 결과 삭제 </p>
	 * @param cdate
	 */
	void deleteNmapResult(NmapData cdate);

	/**
	 * @param jobId
	 * @return
	 */
	List<NpsNmapJob> selectTempletJob(String jobId);

	/**
	 *
	 */
	void updateJobResult(NpsNmapJob job);

	void updateJobStart(NpsNmapJob job);

	/**
	 * @param jobId
	 * @return
	 */
	NpsNmapJob selectOneJobId(String jobId);

	/**
	 * <p>장비로 등록되어 있는 IP 갯수</p>
	 * @param ip
	 * @return
	 */
	int selectAssetMasterByIpAddress(String ip);

	/**
	 * <p>등록되어 있는 장비중 ip로 오픈되어 있는 포트 정보 검색</p>
	 * @param ip
	 * @return
	 */
	List<OpenPortInfo> selectOpenPortByIpAddress(String ip);

	/**
	 * <p>오픈 포트 정보 저장</p>
	 * @param data
	 */
	void insertComparedOpenPort(NmapData data);

	List<NpsNmapJob> monitoringTempletJob();


	List<SMSEntity> selectSms(SMSEntity entity);

	void updateSmsStatus(String sendKey);

	List<String> selectSendMailUser(String mailCd);

	List<MailEntity> selectMailInfo(String mailCd);

	void updateMailInfo(String mailCd);

	/**
	 * 로그인 90 경과 계정 정보 삭제후 저장
	 * @return
	 */
	int insertConfigUserAuthHistory();

	/**
	 * 로그인 90 경과 계정 삭제
	 * @return
	 */
	int deleteConfigUserAuth();

	// 자동진단 스케쥴 시작
	List<BatchDiagnosis> selectBatchDiagnosis();
	int deleteSnetAgentJobRdate(BatchDiagnosis batch);
	int insertSnetAgentJobRdate(BatchDiagnosis batch);
	int insertSnetAgentJobHistory(BatchDiagnosis batch);
	int deleteSnetAssetSwAuditSchedule();

	/**
	 * 6 시간전의 에이전트 상태 체크 정보 삭제
	 * @return
	 */
	int deleteAgentHealthChkHistory();
	/**
	 * 6 시간전의 릴레이서버 상태 체크 정보 삭제
	 * @return
	 */
	int deleteRelayHealthChkHistory();


	/**
	 * 부서별 서버 현행화율
	 * @return
	 */
	List<?> branchServerActualizingRate();


	/**
	 * 부서 리스트
	 * @return
	 */
	List<?> branchSwAuditRateBranchList();

	/**
	 * SW별 보안 준수율
	 * @return
	 */
	List<?> branchSwAuditRate();


	/**
	 * 담당자별 현행화율
	 * @return
	 */
	List<?> branchActualizingRate(String userId);

	/**
	 * 보고서 메일 대상 리스트
	 * @return
	 */
	/**
	 * @return
	 */
	List<SendMailReport> dailySendMailReport();
	/**
	 * @return
	 */
	List<SendMailReport> weeklySendMailReport();
	/**
	 * @return
	 */
	List<SendMailReport> monthlySendMailReport();

	/**
	 * 즉시 발송 대상 리스트
	 * @return
	 */
	List<SendMailReport> sendMailReportImmediately();

	/**
	 * 메일 발송 결과를 저장한다.
	 * @param sendMailReport
	 * @return
	 */
	int updateSendMail(SendMailReport sendMailReport);

	/**
	 * DB 백업 시간
	 * @return
	 */
	String BackupTime();
	
	/**
	 * DB 백업 파일 보관 주기
	 * @return
	 */
	String SaveTerm();
	
	/**
	 * DB 백업 파일 보관 경로
	 * @return
	 */
	String BackupPath();
	
	/**
	 * DB 백업 사용여부
	 * @return
	 */
	String BackupUse();

	List<Map> selectSnetConfigGlobalList();

	/**
	 * sw_audit_day 테이블에서 CTi 에서의 최종 데이터 업데이트 일자를 구해온다.
	 * @return
	 */
	String selectCTiSyncDate();

	/**
	 * sw_audit_day 테이블에서 CTi 연동을 사용하여 취약점 조회할 대상을 조회한다.
	 * @return
	 */
	List<CTiSyncTarget> selectCTiSyncTartget();

	/**
	 * sw_audit_day 테이블에 cve 값과 업데이트 일자로 업데이트한다.
	 * @return
	 */
	int updateCTiSyncData(CTiSyncTarget target);

	/**
	 * 진단 데이터 백업 파일 보관 주기
	 * @return
	 */
	int selectDateSaveTerm();

	void deleteDateSaveTermAssetSwAuditReport(String dateSaveTramAuditDay);

	void deleteDateSaveTermAssetSwAuditHistory(String dateSaveTramAuditDay);

	// 'sg_supprotmanager 프로젝트 - 스케줄러'
	List<HealthChkHistoryDto> selectAgentHealthChkHistory(Map beforeParamMap);

	List<HealthChkHistoryDto> selectRelayHealthChkHistory(Map afterParamMap);

	int updateAgentHealthChkHistory(Map beforeParamMap);

	void updateRelayHealthChkHistory(Map afterParamMap);

	/**
	 * 'sg_supprotmanager 프로젝트 - 스케줄러'
	 */
	List<AssetEntity> selectAssetInfoExcelList(ExcelListEntity excelEntity);


	/**
	 * 알림관련
	 */
	//==============================
	// 알림 관련
   	long getDuplicateNotiSeq(SnetNotificationModel snetNotificationModel);
   	void insertNotification(SnetNotificationModel snetNotificationModel);
   	void insertNotificationData(SnetNotificationDataModel snetNotificationDataModel);
   	String getNotificationUseYn(String notiType);
   	List<String> selectNotificationUserList(String notiType);
	//=====================================

	/**
	 * CTI 연동 관련(신규)
	 */
	List<CTiSyncTarget> selectCTiSyncTartgetList();

	void updateCTiSyncTartget(CTiSyncTarget target);

	String selectCTICveCode(CTiSyncTarget target);

	void insertCTICveCode(CTiSyncTarget target);

	void updateCTICveCode(CTiSyncTarget target);

	String selectCTICveCodeData(CTiSyncTarget target);

	void insertCTICveCodeData(CTiSyncTarget target);

	void updateCTICveCodeData(CTiSyncTarget target);

	String selectCTICveCodeMapp(CTiSyncTarget target);

	void insertCTICveCodeMapp(CTiSyncTarget target);

	void updateCTICveCodeMapp(CTiSyncTarget target);

	String makeAsserSwCd();
}
