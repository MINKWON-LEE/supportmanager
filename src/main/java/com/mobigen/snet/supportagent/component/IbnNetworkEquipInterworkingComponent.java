package com.mobigen.snet.supportagent.component;

import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mobigen.snet.supportagent.entity.ExternalEntity;
import com.mobigen.snet.supportagent.memory.GLOBALVAR.CONNECT_TYPE;
import com.mobigen.snet.supportagent.utils.CommonUtils;
import com.mobigen.snet.supportagent.utils.DateUtil;
import com.mobigen.snet.supportagent.utils.FTPUtil;
import com.mobigen.snet.supportagent.utils.SocketClientUtil;

import jodd.util.StringUtil;

/**
 * ibn 중앙서버용 네트워크 장비 정보 처리.
 * 
 * @author 홍순풍 (rocklike@gmail.com)
 * @date : 2017. 3. 17.
 */
@Component
public class IbnNetworkEquipInterworkingComponent {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ExternalInterWorkingComponent2 externalComponent ;
	
	private final String NW_MANAGER_CD 		= "T1359935510887";
	private final String IP					= "90.90.100.129";
	private final String userId				= "smartguard";
	private final String passwd				= "backhaul@11";
	private final String REMOTE_ROOT_DIR	= "/data4/device_config/SmartGuard";
	private final String LOCAL_ROOT_DIR 	= "/usr/local/snetManager/txFiles/inbound/ibnResults";
	
//	private final String NW_MANAGER_CD 		= "T1359935510887";
//	private final String IP					= "218.233.105.58";
//	private final String userId				= "sgweb";
//	private final String passwd				= "snetweb!2016";
//	private final String REMOTE_ROOT_DIR		= "/home/sgweb/tmp/ibn-test";
//	private final String LOCAL_ROOT_DIR 	= "H:/project/mobigen/tmp-work/ibn-test2";
	
	
	
	public boolean mainProcess(){
		logger.info("== IBN network mainProcess start...");
		long start, end;
		start = System.currentTimeMillis();
		boolean result = true;
		CommonUtils.mkdir(LOCAL_ROOT_DIR);
		
		// 외부 연동 파일 처리
		ExternalEntity entity = new ExternalEntity();
		entity.setConnectType(CONNECT_TYPE.FTP.TYPE);
		entity.setHost(IP);
		entity.setPort(CONNECT_TYPE.FTP.PORT);
		entity.setUserId(userId);
		entity.setPasswd(passwd);
//		entity.setSrcPath(ROOT_DIRECTORY);
//		entity.setDstPath(EXTERNAL_DATA_PATH);
		
		try {
			String vendor = null;
			vendor = "Alcatel-Lucent";
			processEachFolder(entity, vendor);
			// 
			vendor = "CISCO";
			processEachFolder(entity, vendor);
			// 
			vendor = "Dasan";
			processEachFolder(entity, vendor);
			// 
			vendor = "Extreme";
			processEachFolder(entity, vendor);
			// 
			vendor = "HUAWEI";
			processEachFolder(entity, vendor);
			// 
			vendor = "HappyComm";
			processEachFolder(entity, vendor);
			// 
			vendor = "Netgear";
			processEachFolder(entity, vendor);
			// 
			vendor = "Premire_Network";
			processEachFolder(entity, vendor);
			// 
			vendor = "Prosilient_Technologies";
			processEachFolder(entity, vendor);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result = false;
		}
		
		end = System.currentTimeMillis();
		logger.warn("== IBN network mainProcess finished ({})", end-start );
		
		return result;
	}
	
	
	
	private void processEachFolder(ExternalEntity entity, String vendorDir) throws Exception{
		logger.info("== IBN start (vendor:{}", vendorDir);
		
		try {
			FTPUtil ftp = new FTPUtil(entity.getHost(), entity.getPort(), entity.getUserId(), entity.getPasswd());
			if(!ftp.login(true)){
				logger.error("=== failed in ftp login (" + vendorDir + ")");
				return;
			}
			
			// 오늘일자 디렉토리를 체크후에 존재하지 않으면, 어제 일자 디렉토리 체크
			String remoteDir = REMOTE_ROOT_DIR + "/" + vendorDir + "/" + DateUtil.getCurrDate();
			if(!ftp.isDirExists(remoteDir)){
				remoteDir = REMOTE_ROOT_DIR + "/" + vendorDir + "/" + DateUtil.getYesterday();
				if(!ftp.isDirExists(remoteDir)){
					logger.error("== IBN vendor directory does not exist => {}", vendorDir);
					return;
				}
			}
			
			logger.info("== IBN process start : {}", remoteDir);
			entity.setSrcPath(remoteDir);
			entity.setDstPath(LOCAL_ROOT_DIR + "/" + vendorDir);
			CommonUtils.mkdir(LOCAL_ROOT_DIR + "/" + vendorDir);
			
			// 파일 다운로드
			entity = externalComponent.ExternalDownloadDir(entity);
			
			// AgentManager로 전송 
			sendToAgentManger(entity);
			
		} catch (Throwable e) {
			logger.error("=== IBN process failed : " + vendorDir, e);
		}
	}
	
	
	
	private void sendToAgentManger(ExternalEntity entity){
		// send_socket AgentManager 
		// MULTISRVMANUALGSCRPTFIN|Asset_CD|Manager_CD|Vendor_Cd|FilePath|FileName
		for(Map<String, String> downloadFile : entity.getDownloadFileInfoList()){
			String filePath = downloadFile.get("PATH");
			String fileName = downloadFile.get("FILE_NAME");
			String vendorCd = getVendorCode(filePath);
			if(vendorCd==null){
				logger.error("== vendor code is not resolved : {}", filePath);
				continue;
			}
			int otp = new Random().nextInt(900000)+100000;
			String S_OTP = String.valueOf(otp);
			String assetCd = "AC"+ DateUtil.getCurrDateBySecond() + S_OTP;
			
			StringBuilder sb = new StringBuilder();
			sb.append("MULTISRVMANUALGSCRPTFIN")
			.append("|")
			.append(assetCd)
			.append("|")
			.append(NW_MANAGER_CD)
			.append("|")
			.append(vendorCd)
			.append("|")
			.append(filePath)
			.append("|")
			.append(fileName)
			.append("|")
			.append("REGI");
			
			logger.info("== send ibn network info to manager :: {}", sb);
			SocketClientUtil.sendSocketString("127.0.0.1", 10225, sb.toString());
		}
	}
	
	
	private String getVendorCode(String filePath){
		String[] splitPath = StringUtil.split(filePath, "/");
		String vendorName = splitPath[splitPath.length-1];
		String vendorCd = "";
        switch (vendorName){
//            case "CISCO":
//                vendorCd = "Cisco";
//                break;
//            case "CISCO_NX-OS":
//                vendorCd = "Cisco_NX_OS";
//                break;
            case "CISCO":
                vendorCd = "Cisco_IOS_XR";
                break;
//            case "BROCADE":
//                vendorCd = "Brocade";
//                break;
            case "Extreme":
                vendorCd = "Extreme";
                break;
//            case "FORTINET":
//                vendorCd = "FortiOS";
//                break;
            case "HUAWEI":
                vendorCd = "Huawei";
                break;
//            case "JUNIPER":
//                vendorCd = "Juniper";
//                break;
            case "Alcatel-Lucent":
                vendorCd = "Alcatel_Lucent";
                break;
            case "Premire_Network":
            	vendorCd = null;
            	break;
            case "Dasan":
            	vendorCd = null;
            	break;
            case "HappyComm":
            	vendorCd = null;
            	break;
            case "Netgear":
            	vendorCd = null;
            	break;
            case "Prosilient_Technologies":
            	vendorCd = "Accedian";
            	break;
            default:
                vendorCd = null;
                break;
        }
		
		return vendorCd;
	}

	
}
