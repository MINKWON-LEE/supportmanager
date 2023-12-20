package com.mobigen.snet.supportagent.component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mobigen.snet.supportagent.dao.Dao2;
import com.mobigen.snet.supportagent.dao.table.dao.SnetAssetIpDao;
import com.mobigen.snet.supportagent.dao.table.dao.SnetAssetMasterDao;
import com.mobigen.snet.supportagent.dao.table.dao.SnetAssetSwAuditDayDao;
import com.mobigen.snet.supportagent.dao.table.dao.SnetAssetSwAuditHistoryDao;
import com.mobigen.snet.supportagent.dao.table.dao.SnetAssetUserDao;
import com.mobigen.snet.supportagent.dao.table.dao.SnetConnectMasterDao;
import com.mobigen.snet.supportagent.dao.table.model.SnetAssetIpEntity;
import com.mobigen.snet.supportagent.dao.table.model.SnetAssetMasterEntity;
import com.mobigen.snet.supportagent.dao.table.model.SnetAssetSwAuditDayEntity;
import com.mobigen.snet.supportagent.dao.table.model.SnetAssetSwAuditHistoryEntity;
import com.mobigen.snet.supportagent.dao.table.model.SnetAssetUserEntity;
import com.mobigen.snet.supportagent.dao.table.model.SnetConfigWifiFirmwareEntity;
import com.mobigen.snet.supportagent.dao.table.model.SnetConnectMasterEntity;
import com.mobigen.snet.supportagent.entity.UserInfo;




/**
 * SNET_CONFIG_WIFI_FIRMWARE 테이블에서 레코드를 읽어서, 6개의 기본 테이블에 insert.
 * 
 * @author 홍순풍 (rocklike@gmail.com)
 * @date : 2017. 3. 14.
 */
@Component
public class WifiFirmwareProcessor {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	// 김성진 manager
	private final String USER_ID = "1104351";
	
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
	
	public void processFromWifiFirmware(){
		logger.info("== SNET_CONFIG_WIFI_FIRMWARE 테이블로부터 각 테이블에 insert 시작.");
		//== 아래의 테이블에 집어 넣음.
		// SNET_ASSET_MASTER
		// SNET_ASSET_USER
		// SNET_ASSET_IP
		// SNET_ASSET_SW_AUDIT_DAY
		// SNET_CONNECT_MASTER
		// SNET_ASSET_SW_AUDIT_HISTORY
		
		try {
			//== SNET_CONFIG_WIFI_FIRMWAR 에서 대상건 select
			List<SnetConfigWifiFirmwareEntity> firmwareList = dao2.selectWifiFirmwareList();
			logger.info("== firmware 조회 건수 : {}", firmwareList.size());
			
			UserInfo userInfo = dao2.selectUserInfo(USER_ID);
			if(userInfo==null){
				logger.error("== firmware 김성진 manager의 정보가 존재하지 않음.");
			}
			
			// 건건이 처리
			firmwareList.forEach(firmware->{
				try {
					insertRow(firmware, userInfo);
				} catch (Exception ex) {
					logger.error("== firmware처리중 에러 ("+ firmware.getCompanyNm() + "," + firmware.getFirmwareVer()+")", ex);
				}
			});
		} catch (Throwable e) {
			logger.error("firmware테이블로부터 처리중에 에러", e);
		}
	}
	
	
	
	private void insertRow(SnetConfigWifiFirmwareEntity firmware, UserInfo userInfo){
		// NPE을 막기 위해서
		if(userInfo==null){
			userInfo = new UserInfo();
		}
		
		// company_nm => host_nm
		// firmware_ver => id_address
		String hostNm = firmware.getCompanyNm();
		String ipAddress = firmware.getFirmwareVer();
		
		
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
		SnetAssetSwAuditDayEntity auditDayEntity = auditDayDao.select(assetCd, "OS", "WiFi", hostNm + "_" + ipAddress, "-", "-", "-" );
		if(auditDayEntity==null){
			auditDayEntity = new SnetAssetSwAuditDayEntity();
			auditDayEntity.setAssetCd(assetCd);
			auditDayEntity.setSwType("OS"); 
			auditDayEntity.setSwNm("WiFi"); 
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
			connectMasterEntity.setSwNm("WiFi");
			connectMasterDao.insert(connectMasterEntity);
		}
		
		//== SNET_ASSET_SW_AUDIT_HISTORY
		SnetAssetSwAuditHistoryEntity auditHistoryEntity = auditHistoryDao.select(assetCd, "OS", "WiFi", hostNm + "_" + ipAddress, "-", "-", "-", "19990101");
		if(auditHistoryEntity==null){
			auditHistoryEntity = new SnetAssetSwAuditHistoryEntity();
			auditHistoryEntity.setAssetCd(assetCd);
			auditHistoryEntity.setSwType("OS");
			auditHistoryEntity.setSwNm("WiFi");
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
	
	
	
	
	

}
