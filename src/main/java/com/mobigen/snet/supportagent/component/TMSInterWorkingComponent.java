/**
 * project : SupportAgent
 * package : com.mobigen.snet.supportagent.component
 * company : Mobigen
 * 
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 9.
 * Description : 
 * 
 */
package com.mobigen.snet.supportagent.component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mobigen.snet.supportagent.dao.ExternalInterWorkingMapper;
import com.mobigen.snet.supportagent.entity.Asset;
import com.mobigen.snet.supportagent.entity.Company;
import com.mobigen.snet.supportagent.entity.ExternalEntity;
import com.mobigen.snet.supportagent.entity.FirmWare;
import com.mobigen.snet.supportagent.entity.Group;
import com.mobigen.snet.supportagent.entity.TMSInterworkingEntity;
import com.mobigen.snet.supportagent.memory.GLOBALVAR.CONNECT_TYPE;
import com.mobigen.snet.supportagent.utils.CommonUtils;
import com.mobigen.snet.supportagent.utils.DateUtil;

import jodd.util.StringUtil;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.component
 * Company : Mobigen
 * File    : SftpComponent.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 9.
 * Description : 
 * 
 */
@Component
public class TMSInterWorkingComponent extends AbstractComponent{

	@Autowired
	private ExternalInterWorkingComponent externalComponent;
	
	@Autowired
	private ExternalInterWorkingMapper externalInterWorkingMapper;
	
	/*
	 *	TMS SERVER INFO
	 *  - IP : 60.11.8.172, 90.90.100.182, 150.23.27.214
	 *  - alien / sksna100
	 *  - port 22
	 *  - Directory : /data/offer_data/smartguard
	 *  firmware, asset, group, company
	 */
	private String IP					= "60.11.8.172";
	private String userId				= "alien";
	private String passwd				= "sksna100";
	private String[] PREFIX_FILENAME 	= {"firmware", "asset", "group", "company"};
	private String DELIMETER			= "_";
	private String SUFFIX_FILENAME		= DateUtil.getFormatString("yyyyMMdd")+".csv";
	private String ROOT_DIRECTORY		= "/data/offer_data/smartguard";
	private String EXTERNAL_DATA_PATH 	= "/usr/local/snetManager/txFiles/inbound/tms";
	private String ENC					= "EUC-KR";
	
	
	public boolean TMSInterworkingSyncronizing(){
		boolean result = true;
		CommonUtils.mkdir(EXTERNAL_DATA_PATH);
		
		// 외부 연동 파일 처리
		ExternalEntity entity = new ExternalEntity();
		entity.setHost(IP);
		entity.setPort(CONNECT_TYPE.SFTP.PORT);
		entity.setUserId(userId);
		entity.setPasswd(passwd);
		entity.setSrcPath(ROOT_DIRECTORY);
		entity.setDstPath(EXTERNAL_DATA_PATH);
		entity.setConnectType(CONNECT_TYPE.SFTP.TYPE);
		
		List<String> downLoadSrcList = new ArrayList<>();
		Map<String, String> destFileMap = new HashMap<>();
		for(String prefix : PREFIX_FILENAME){
			StringBuilder dw = new StringBuilder();
			dw.append(prefix)
			.append(DELIMETER)
			.append(SUFFIX_FILENAME);
			downLoadSrcList.add(dw.toString());
			destFileMap.put(prefix, dw.toString());
		}
		
		entity.setDownloadFileList(downLoadSrcList);
		
		// TMS 외부 연동 data 다운로드 
		externalComponent.ExternalDownloadFile(entity);
		
		// CSV --> DATA 연동
		for(Map.Entry<String, String> elem : destFileMap.entrySet()){
			logger.debug("KEY :: {} , VALUE :: {}", elem.getKey(), elem.getValue());
			
			String dataPath = EXTERNAL_DATA_PATH + File.separator + elem.getValue();
			List<String> datas = externalComponent.getReadCsvFile(dataPath, ENC);
			
			if(datas.size()>0){
				switch (elem.getKey()) {
				case "firmware":
					syncronizingFirmWare(datas);
					break;
				case "asset":
					syncronizingAsset(datas);
					break;
				case "group":
					syncronizingGroup(datas);
					break;
				case "company":
					syncronizingCompany(datas);
					break;
					
				default:
					break;
				}
			}else{
				logger.info("No data :: {}", elem.getKey());
			}
		}
		
		return result;
	}
	
	
	public boolean syncronizingFirmWare(List<String> datas){
		boolean result = true;
		
		try {
			List<FirmWare> firmwareList = new ArrayList<FirmWare>();
				
			for(String data: datas){
				FirmWare firmware = new FirmWare();
				String[] firmwareInfo = StringUtil.split(data, "|");
				firmware.setCompanyId(firmwareInfo[0]);
				firmware.setCompanyNm(firmwareInfo[1]);
				firmware.setFirmwareVer(firmwareInfo[2]);
				firmwareList.add(firmware);
				externalInterWorkingMapper.TMSFirmwareInsert(firmware);
			}
			
			TMSInterworkingEntity entity = new TMSInterworkingEntity();
			entity.setFirmwareList(firmwareList);

			// 데이터 연동
			externalInterWorkingMapper.TMSFirmwareUpdate(entity);
			
		} catch (Exception e) {
			logger.error("syncronizingFirmWare Exception ::", e);
			result = false;
		}
		
		return result;
	}
	
	public boolean syncronizingCompany(List<String> datas){
		boolean result = true;
		
		try {
			List<Company> companyList = new ArrayList<Company>();
			for(String data : datas){
				String[] dataArray = StringUtil.split(data, "|");
				Company company = new Company();
				company.setCompanyId(dataArray[0]);
				company.setCompanyNm(dataArray[1]);
				companyList.add(company);
				externalInterWorkingMapper.TMSCompanyInsert(company);
			}
			TMSInterworkingEntity entity = new TMSInterworkingEntity();
			entity.setCompanyList(companyList);
			// 데이터 연동
			externalInterWorkingMapper.TMSCompanyUpdate(entity);
		} catch (Exception e) {
			logger.error("syncronizingFirmWare Exception :: {}", e.getMessage(), e.fillInStackTrace());
			result = false;
		}
		
		return result;
	}
	
	public boolean syncronizingGroup(List<String> datas){
		boolean result = true;
		
		try {
			List<Group> groupList = new ArrayList<Group>();
			for(String data : datas){
				String[] dataArray = StringUtil.split(data, "|");
				Group group = new Group();
				group.setBranchId(dataArray[0]);
				group.setBranchNm(dataArray[1]);
				group.setTeamId(dataArray[2]);
				group.setTeamNm(dataArray[3]);
				groupList.add(group);
				externalInterWorkingMapper.TMSGroupInsert(group);
			}
			
			TMSInterworkingEntity entity = new TMSInterworkingEntity();
			entity.setGroupList(groupList);
			
			// 데이터 연동
			externalInterWorkingMapper.TMSGroupUpdate(entity);
		} catch (Exception e) {
			logger.error("syncronizingFirmWare Exception :: {}", e.getMessage(), e.fillInStackTrace());
			result = false;
		}
		
		return result;
	}
	
	public boolean syncronizingAsset(List<String> datas){
		boolean result = true;
		
		try {
			List<Asset> assetList = new ArrayList<Asset>();
			for(String data : datas){
				String[] dataArray = StringUtil.split(data, "|");
				Asset asset = new Asset();
				asset.setMacAddress(dataArray[0]);
				asset.setCompanyId(dataArray[1]);
				asset.setAssetLocation(dataArray[2]);
				asset.setIpAddress(dataArray[3]);
				asset.setTeamId(dataArray[4]);
				asset.setFirmwareVer(dataArray[5]);
				
				assetList.add(asset);
				externalInterWorkingMapper.TMSAssetInsert(asset);
			}
			TMSInterworkingEntity entity = new TMSInterworkingEntity();
			entity.setAssetList(assetList);
			externalInterWorkingMapper.TMSAssetUpdate(entity);
		} catch (Exception e) {
			logger.error("syncronizingFirmWare Exception :: {}", e.getMessage(), e.fillInStackTrace());
			result = false;
		}
		
		return result;
	}
}
