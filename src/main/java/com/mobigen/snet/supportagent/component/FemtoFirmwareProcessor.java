package com.mobigen.snet.supportagent.component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mobigen.snet.supportagent.dao.Dao2;
import com.mobigen.snet.supportagent.dao.table.dao.SnetAssetIpDao;
import com.mobigen.snet.supportagent.dao.table.dao.SnetAssetMasterDao;
import com.mobigen.snet.supportagent.dao.table.dao.SnetAssetSwAuditDayDao;
import com.mobigen.snet.supportagent.dao.table.dao.SnetAssetSwAuditHistoryDao;
import com.mobigen.snet.supportagent.dao.table.dao.SnetAssetUserDao;
import com.mobigen.snet.supportagent.dao.table.dao.SnetConfigFemtoAssetDao;
import com.mobigen.snet.supportagent.dao.table.dao.SnetConfigFemtoEnbDao;
import com.mobigen.snet.supportagent.dao.table.dao.SnetConnectMasterDao;
import com.mobigen.snet.supportagent.dao.table.model.SnetAssetIpEntity;
import com.mobigen.snet.supportagent.dao.table.model.SnetAssetMasterEntity;
import com.mobigen.snet.supportagent.dao.table.model.SnetAssetSwAuditDayEntity;
import com.mobigen.snet.supportagent.dao.table.model.SnetAssetSwAuditHistoryEntity;
import com.mobigen.snet.supportagent.dao.table.model.SnetAssetUserEntity;
import com.mobigen.snet.supportagent.dao.table.model.SnetConfigFemtoAssetEntity;
import com.mobigen.snet.supportagent.dao.table.model.SnetConfigFemtoEnbEntity;
import com.mobigen.snet.supportagent.dao.table.model.SnetConfigFemtoFirmwareEntity;
import com.mobigen.snet.supportagent.dao.table.model.SnetConnectMasterEntity;
import com.mobigen.snet.supportagent.entity.UserInfo;
import com.mobigen.snet.supportagent.utils.CommonUtils;
import com.mobigen.snet.supportagent.utils.FTPUtil;
import com.mobigen.snet.supportagent.utils.FTPUtil.FileDownloadResult;
import com.mobigen.snet.supportagent.utils.FtpClientHelper;

import jodd.util.StringUtil;


/**
 * FEMTO firmware 관련 처리
 *   - 테이블 3개 insert(SNET_CONFIG_FEMTO_ASSET, SNET_CONFIG_FEMTO_FIRMWARE, SNET_CONFIG_FEMTO_ENB)
 *   
 * 6개 테이블에 insert
 *   - SNET_ASSET_MASTER          
 *   - SNET_ASSET_USER            
 *   - SNET_ASSET_IP              
 *   - SNET_ASSET_SW_AUDIT_DAY    
 *   - SNET_CONNECT_MASTER        
 *   - SNET_ASSET_SW_AUDIT_HISTORY
 * 
 * @author 홍순풍 (rocklike@gmail.com)
 * @date : 2017. 3. 27.
 */
@Component
public class FemtoFirmwareProcessor {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	// 김주만 manager
	private final String USER_ID = "1109711";
	
	private final String FTP_IP = "60.11.8.76";
	private final String FTP_ID = "etcuser";
	private final String FTP_PW = "rhsl70^^";
	
	//== local 테스트 환경
//	private final String FTP_IP = "192.168.56.103";
//	private final String FTP_ID = "rocklike";
//	private final String FTP_PW = "ghdtnsvnd";
	
	private final String REMOTE_DIR = "/CMS_NAS/data/dumpdata";
	private final String LOCAL_DOWNLOAD_DIR = "/usr/local/snetManager/txFiles/inbound/femtoResult";
	
	@Autowired
	private Dao2 dao2;
	
	@Autowired
	private SnetAssetMasterDao assetMasterDao;
	
	@Autowired
	private SnetAssetUserDao assetUserDao;
	
	@Autowired
	private SnetAssetIpDao assetIpDao;
	
	@Autowired
	private SnetAssetSwAuditDayDao auditDayDao;
	
	@Autowired
	private SnetConnectMasterDao connectMasterDao;
	
	@Autowired
	private SnetAssetSwAuditHistoryDao auditHistoryDao;
	
	@Autowired
	private SnetConfigFemtoAssetDao femtoAssetDao;
	
	@Autowired
	private SnetConfigFemtoEnbDao femtoEnbDao;
	
	@Autowired
	ApplicationContext ctx;
	
	FemtoFirmwareProcessor me;
	
	@PostConstruct
	public void postConstruct(){
		this.me  = ctx.getBean(FemtoFirmwareProcessor.class);
	}
	
	public void mainProcess() throws Exception{
		logger.info("== FemtoFirmwareProcessor 시작.");
		ftpDownloadAndInsertToFemtoTables();
		processFromFemtoFirmware();
		logger.info("== FemtoFirmwareProcessor 끝.");
	}
	
	
	public void ftpDownloadAndInsertToFemtoTables() throws Exception{
		logger.info("== Femto FTP file download 시작");
		CommonUtils.mkdir(LOCAL_DOWNLOAD_DIR);
		FtpClientHelper ftpHelper = null;
		try {
			ftpHelper = new FtpClientHelper(FTP_IP, 21, FTP_ID, FTP_PW);
			ftpHelper.login(false);
			FTPUtil ftp = ftpHelper.getFtpUtil();
			
			
			boolean ftpCdResult = ftp.cd(REMOTE_DIR);
			if(!ftpCdResult){
				throw new IOException("FTP cd failed : " + REMOTE_DIR);
			}
			
			
			FileDownloadResult lteFemtoDeviceInfoDownload = null;
			FileDownloadResult cmEnbAllDownload = null;
			
			//== ftp download : LTE_FEMTO_DEVICEINFO_${연월일}.dat 
			try {
				Optional<FTPFile> lastFileResult = ftp.getLastFile(Pattern.compile("^LTE_FEMTO_DEVICEINFO_"));
				if(!lastFileResult.isPresent()){
					logger.info("== LTE_FEMTO_DEVICEINFO_ 파일 존재하지 않음.");
					lteFemtoDeviceInfoDownload = new FileDownloadResult().setSucceeded(false);
				}else{
					FTPFile ftpFile = lastFileResult.get();
					logger.info("== LTE_FEMTO_DEVICEINFO_ 파일 : {}", ftpFile.getName());
					lteFemtoDeviceInfoDownload = ftp.downloadAFile(ftpFile, LOCAL_DOWNLOAD_DIR);
				}
				
			} catch (Exception e) {
				logger.error("LTE_FEMTO_DEVICEINFO_${연월일}.dat", e);
			}
			
			
			//== ftp download : CM_ENB_ALL_V61_${연월일}.dat
			try {
				Optional<FTPFile> lastFileResult = ftp.getLastFile(Pattern.compile("^CM_ENB_ALL_V61_"));
				if(!lastFileResult.isPresent()){
					logger.info("== CM_ENB_ALL_V61_ 파일 존재하지 않음.");
					cmEnbAllDownload = new FileDownloadResult().setSucceeded(false);
				}else{
					FTPFile ftpFile = lastFileResult.get();
					logger.info("== CM_ENB_ALL_V61_ 파일 : {}", ftpFile.getName());
					cmEnbAllDownload = ftp.downloadAFile(ftpFile, LOCAL_DOWNLOAD_DIR);
				}
			} catch (Exception e) {
				logger.error("CM_ENB_ALL_V61_${연월일}.dat", e);
			}
			
			
			// LTE_FEMTO_DEVICEINFO_${연월일}.dat 처리
			if(lteFemtoDeviceInfoDownload.isSucceeded()){
				//== SNET_CONFIG_FEMTO_ASSET 테이블 전체 삭제후 새로 넣기
				me.deleteAndInsertConfigFemtoAssetTable(lteFemtoDeviceInfoDownload.getLocalFile());
			}
			
			// CM_ENB_ALL_V61_${연월일}.dat 처리
			if(cmEnbAllDownload.isSucceeded()){
				//== SNET_CONFIG_FEMTO_ENB 테이블 전체 삭제후 새로 넣기
				me.deleteAndInsertConfigFemtoEnbTable(cmEnbAllDownload.getLocalFile());
			}
			
		} finally {
			if(ftpHelper!=null){
				ftpHelper.close();
			}
		}
	}
	
	
	/**
	 * SNET_CONFIG_FEMTO_ASSET , SNET_CONFIG_FEMTO_FIRMWARE insert/update
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public void deleteAndInsertConfigFemtoAssetTable(File f) throws IOException{
		logger.info("== SNET_CONFIG_FEMTO_ASSET delete and insert");
		
		// SNET_CONFIG_FEMTO_ASSET 테이블 전체 삭제후 새로 insert
		dao2.deleteAllConfigFemtoAsset();
		
		try (Stream<String> lines = Files.lines(Paths.get(f.getAbsolutePath()), Charset.forName("UTF-8")) ){
			lines.forEach(line->{
				// LTE_FEMTO_DEVICEINFO_ 내용 예)
				//  	FMSID|OPCENTER|LOCID|APID|ECI|MANUFACTURER|MANUFACTUREROUI|MODELNAME|DESCRIPTION|PRODUCTCLASS|SERIALNUMBER|HARDWAREVERSION|SOFTWAREVERSION|MODEMFIRMWAREVERSION|ENABLEDOPTIONS|ADDITIONALSOFTWAREVERSION|SPECVERSION|PROVISIONINGCODE|UPTIME|FIRSTUSEDATE|EQUIPMENT
				//  	7|0|1|0|134217984|SKTS|0017B2|SLN-EN081||HeNB|TSSLNEN1300117|02.01|ST_16.10.00|||1.3|||0|0|SUYU007
				String[] arr = StringUtil.split(line, "|");
				if(arr.length>=21){
					SnetConfigFemtoAssetEntity entity = new SnetConfigFemtoAssetEntity();
					entity.setFmsid( arr[0] );
					entity.setOpcenter( arr[1] );
					entity.setLocid( arr[2] );
					entity.setApid( arr[3] );
					entity.setEci( arr[4] );
					entity.setManufacturer( arr[5] );
					entity.setManufactureroui( arr[6] );
					entity.setModelname( arr[7] );
					entity.setDescription( arr[8] );
					entity.setProductclass( arr[9] );
					entity.setSerialnumber( arr[10] );
					entity.setHardwareversion( arr[11] );
					entity.setSoftwareversion( arr[12] );
					entity.setModemfirmwareversion( arr[13] );
					entity.setEnabledoptions( arr[14] );
					entity.setAdditionalsoftwareversion( arr[15] );
					entity.setSpecversion( arr[16] );
					entity.setProvisioningcode( arr[17] );
					entity.setUptime( arr[18] );
					entity.setFirstusedate( arr[19] );
					entity.setEquipment( arr[20] );
					entity.setUseYn("Y");
					entity.setCreateDate(new Timestamp(System.currentTimeMillis()));
					femtoAssetDao.insert(entity);
					
				}else{
					logger.info("== 데이타 구분자 갯수가 이상해서 통과 : {} : {}", f.getAbsolutePath() , line);
				}
			});
		}
		
		//== SNET_CONFIG_FEMTO_FIRMWARE 테이블 insert
		logger.info("== SNET_CONFIG_FEMTO_FIRMWARE insert/update");
		dao2.insertIntoFemtoFirmware();
		// SNET_CONFIG_FEMTO_ASSET에 있는 것에 대해 use_yn을 Y로
		dao2.updateUseYnInFemtoFirmware();
		// SNET_CONFIG_FEMTO_ASSET에 없는 것에 대해 use_yn을 N로
		dao2.updateUseYnInFemtoFirmware2();
	}
	
	
	/**
	 * SNET_CONFIG_FEMTO_ENB insert
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	public void deleteAndInsertConfigFemtoEnbTable(File f) throws IOException{
		logger.info("== SNET_CONFIG_FEMTO_ENB delete and insert");
		
		// SNET_CONFIG_FEMTO_ASSET 테이블 전체 삭제후 새로 insert
		dao2.deleteAllConfigFemtoEnb();
		
		Charset charset = resolveCharset(f);
		logger.info("== resolved charset : {}", charset);
		
		try (Stream<String> lines = Files.lines(Paths.get(f.getAbsolutePath()), charset) ){
			// CM_ENB_ALL_V61_ 내용 예)
			//   ACT|100430950|1|28867|S성남복정중심국_마천동LDT_02|1001|201620344|25|1000|수도권Infra본부|1023|강남Access Infra팀|1002|성수NOC|1020|1024|강남/송파품질개선팀|2016-08-25 00:10:51|2016-09-10 08:23:04|0|70|3||||131|13|경기|성남시 수정구|복정동|643-1번지 SKT성남복정 3층||||||37:27.58/492|127:7.36/384|134878.492|457656.384|||||||SKT성남복정 3층|3|4||N102082|S성남복정중심국|0028|38.34.15.10||||S612645628||SUB|201624427|S성남복정중심국_특전사B1LDN_00.51.LTE.ENB(내)|||38.34.15.10|성남대로||01528|00000|SKT성남복정||3||복정동||1|38.35.15.10||||37.466248|127.126773|경기성남_DU30대상|S성남복정중심국_마천동LDT_02|RAN_EMS 28|||DU 30||||||||ENB|||||
			lines.forEach(line->{
				String[] arr = StringUtil.split(line, "|");
				if(arr.length>95 && ("ACT".equals(arr[0]) || "FEMTO".equals(arr[95]))  ){
					SnetConfigFemtoEnbEntity entity = new SnetConfigFemtoEnbEntity();
					entity.setEci(arr[3]);
					entity.setFemtoNm(arr[4]); 
					entity.setBranchId(arr[8]); 
					entity.setBranchNm(arr[9]); 
					entity.setTeamId(arr[12]); 
					entity.setTeamNm(arr[13]); 
					entity.setUseYn("Y");
					entity.setCreateDate(new Timestamp(System.currentTimeMillis()));
					femtoEnbDao.insert(entity);
					
				}else{
					logger.info("== 데이타 구분자 갯수가 이상해서 통과 : {} : {}", f.getAbsolutePath() , line);
				}
			});
		}
		
	}
	
	
	
	private void processFromFemtoFirmware(){
		logger.info("== SNET_CONFIG_FEMTO_FIRMWARE 테이블로부터 각 테이블에 insert 시작.");
		//== 아래의 테이블에 집어 넣음.
		// SNET_ASSET_MASTER
		// SNET_ASSET_USER
		// SNET_ASSET_IP
		// SNET_ASSET_SW_AUDIT_DAY
		// SNET_CONNECT_MASTER
		// SNET_ASSET_SW_AUDIT_HISTORY
		
		try {
			//== SNET_CONFIG_FEMTO_FIRMWARE 에서 대상건 select
			List<SnetConfigFemtoFirmwareEntity> firmwareList = dao2.selectFemtoFirmwareList();
			logger.info("== firmware 조회 건수 : {}", firmwareList.size());
			
			UserInfo userInfo = dao2.selectUserInfo(USER_ID);
			if(userInfo==null){
				logger.error("== femto firmware 김주만 manager의 정보가 존재하지 않음.");
				throw new IllegalArgumentException("femto firmware - 김주만 manager의 정보가 존재하지 않음.");
			}
			
			// 건건이 처리
			firmwareList.forEach(firmware->{
				try {
					insertRow(firmware, userInfo);
				} catch (Exception ex) {
					logger.error("== femto firmware처리중 에러 ("+ firmware.getManufacturer() + "," + firmware.getSoftwareversion()+")", ex);
				}
			});
		} catch (Throwable e) {
			logger.error("femto firmware테이블 처리중에 에러", e);
		}
	}
	
	
	
	private void insertRow(SnetConfigFemtoFirmwareEntity firmware, UserInfo userInfo){
		// NPE을 막기 위해서
		if(userInfo==null){
			userInfo = new UserInfo();
		}
		
		// company_nm => host_nm
		// firmware_ver => id_address
		String hostNm = firmware.getManufacturer();
		String ipAddress = firmware.getSoftwareversion();
		
		
		String assetCd = dao2.selectCountOfAssetMaster(hostNm, ipAddress);
		logger.info("== firmware asset 체크 {}, {}, {}", hostNm, ipAddress, assetCd);
		
		if(assetCd==null){
			// assetCd 새로 채번
			assetCd = issueAssetCd();
			logger.info("== asset_cd 새로 채번 : {}", assetCd);
			
			//== SNET_ASSET_MASTER
			SnetAssetMasterEntity assetMaster = new SnetAssetMasterEntity();
			assetMaster.setAssetCd(assetCd);
			assetMaster.setHostNm(hostNm); 
			assetMaster.setIpAddress(ipAddress); 
			assetMaster.setBranchId(userInfo.getBranchId()); 
			assetMaster.setBranchNm(userInfo.getBranchNm()); 
			assetMaster.setTeamId(userInfo.getTeamId()); 
			assetMaster.setTeamNm(userInfo.getTeamNm()); 
			assetMaster.setCreateDate(new Timestamp(System.currentTimeMillis()));
			assetMaster.setUpdateDate(new Timestamp(System.currentTimeMillis()));
			
			logger.info("== AssetMaster에 새로 insert (assetCd : {})", assetCd );
			assetMasterDao.insert(assetMaster);
		}
		
		//== SNET_ASSET_USER
		if(userInfo.getUserId()==null){
			logger.warn("== firmware에서 SNET_ASSET_USER insert 통과");
		}else{
			SnetAssetUserEntity userEntity = assetUserDao.select(assetCd, userInfo.getUserId(), "OP");
			if(userEntity==null){
				userEntity = new SnetAssetUserEntity();
				userEntity.setAssetCd(assetCd);
				userEntity.setUserId(userInfo.getUserId()); 
				userEntity.setUserType("OP"); 
				userEntity.setTeamId(userInfo.getTeamId()); 
				userEntity.setTeamNm(userInfo.getTeamNm()); 
				userEntity.setUserMail(userInfo.getUserMail()); 
				userEntity.setUserMs(userInfo.getUserMs()); 
				userEntity.setUserNm(userInfo.getUserNm()); 
				userEntity.setCreateDate(new Timestamp(System.currentTimeMillis())); 
				assetUserDao.insert(userEntity);
			}
		}
		
		//== SNET_ASSET_IP
		SnetAssetIpEntity ipEntity = assetIpDao.select(assetCd, ipAddress);
		if(ipEntity==null){
			ipEntity = new SnetAssetIpEntity();
			ipEntity.setAssetCd(assetCd);
			ipEntity.setIpAddress(ipAddress); 
			ipEntity.setIpRepresent("1"); 
			assetIpDao.insert(ipEntity);
		}
		
		//== SNET_ASSET_SW_AUDIT_DAY
		SnetAssetSwAuditDayEntity auditDayEntity = auditDayDao.select(assetCd, "OS", "Femto", hostNm + "_" + ipAddress, "-", "-", "-" );
		if(auditDayEntity==null){
			auditDayEntity = new SnetAssetSwAuditDayEntity();
			auditDayEntity.setAssetCd(assetCd);
			auditDayEntity.setSwType("OS"); 
			auditDayEntity.setSwNm("Femto"); 
			auditDayEntity.setSwInfo(hostNm + "_" + ipAddress); 
			auditDayEntity.setSwDir("-"); 
			auditDayEntity.setSwUser("-"); 
			auditDayEntity.setSwEtc("-"); 
			auditDayDao.insert(auditDayEntity);
		}
		
		//== SNET_CONNECT_MASTER
		SnetConnectMasterEntity connectMasterEntity = connectMasterDao.select(assetCd);
		if(connectMasterEntity==null){
			connectMasterEntity = new SnetConnectMasterEntity();
			connectMasterEntity.setAssetCd(assetCd);
			connectMasterEntity.setSwNm("Femto");
			connectMasterDao.insert(connectMasterEntity);
		}
		
		//== SNET_ASSET_SW_AUDIT_HISTORY
		SnetAssetSwAuditHistoryEntity auditHistoryEntity = auditHistoryDao.select(assetCd, "OS", "Femto", hostNm + "_" + ipAddress, "-", "-", "-", "19990101");
		if(auditHistoryEntity==null){
			auditHistoryEntity = new SnetAssetSwAuditHistoryEntity();
			auditHistoryEntity.setAssetCd(assetCd);
			auditHistoryEntity.setSwType("OS");
			auditHistoryEntity.setSwNm("Femto");
			auditHistoryEntity.setSwInfo(hostNm + "_" + ipAddress);
			auditHistoryEntity.setSwDir("-");
			auditHistoryEntity.setSwUser("-");
			auditHistoryEntity.setSwEtc("-");
			auditHistoryEntity.setAuditDay("19990101");
			auditHistoryEntity.setBranchId(userInfo.getBranchId()); 
			auditHistoryEntity.setBranchNm(userInfo.getBranchNm()); 
			auditHistoryEntity.setTeamId(userInfo.getTeamId()); 
			auditHistoryEntity.setTeamNm(userInfo.getTeamNm()); 
			auditHistoryEntity.setHostNm(hostNm); 
			auditHistoryEntity.setIpAddress(ipAddress); 
			auditHistoryDao.insert(auditHistoryEntity);
		}
		
	}
	
	
	
	private String issueAssetCd(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.KOREA);
		return "AC" + sdf.format(new Date());
	}
	
	
	private Charset resolveCharset(File f) throws IOException {
		
		BufferedReader br = null;
		
		try {
			Charset charset = Charset.forName("UTF-8");
			br = Files.newBufferedReader(f.toPath(), charset);
			br.readLine();
			return charset;
		} catch (CharacterCodingException e) {
			logger.info("ignore charset exception : {}", e.getMessage());
		} finally {
			CommonUtils.close(br);
		}
		
		try {
			Charset charset = Charset.forName("EUC-KR");
			br = Files.newBufferedReader(f.toPath(), charset);
			br.readLine();
			return charset;
		} catch (CharacterCodingException e) {
			logger.info("ignore charset exception : {}", e.getMessage());
		} finally {
			CommonUtils.close(br);
		}
		
		return Charset.defaultCharset();
	}
	
}
