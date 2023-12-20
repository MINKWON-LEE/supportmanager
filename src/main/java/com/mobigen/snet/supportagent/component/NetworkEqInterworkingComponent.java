/**
 * project : SupportAgent
 * package : com.mobigen.snet.supportagent.component
 * company : Mobigen
 * 
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 15.
 * Description : 
 * 
 */
package com.mobigen.snet.supportagent.component;

import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mobigen.snet.supportagent.entity.ExternalEntity;
import com.mobigen.snet.supportagent.memory.GLOBALVAR.CONNECT_TYPE;
import com.mobigen.snet.supportagent.utils.CommonUtils;
import com.mobigen.snet.supportagent.utils.DateUtil;
import com.mobigen.snet.supportagent.utils.SocketClientUtil;

import jodd.util.StringUtil;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.component
 * Company : Mobigen
 * File    : NetworkEquipDiagInterworkingComponent.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 15.
 * Description : 
 * 
 */
@Component
public class NetworkEqInterworkingComponent extends AbstractComponent{

	@Autowired
	private ExternalInterWorkingComponent externalComponent;
	
	private String NW_MANAGER_CD 		= "T1359935510887";
	private String IP					= "60.11.8.37";
	private String userId				= "sdncfgftp";
	private String passwd				= "!sdncfgftp";
	private String ROOT_DIRECTORY		= "/data";
	private String EXTERNAL_DATA_PATH 	= "/usr/local/snetManager/txFiles/inbound/nwResults";
	
	public boolean networkEquipInterworking(){
		boolean result = true;
		CommonUtils.mkdir(EXTERNAL_DATA_PATH);
		
		// 외부 연동 파일 처리
		ExternalEntity entity = new ExternalEntity();
		entity.setConnectType(CONNECT_TYPE.FTP.TYPE);
		entity.setHost(IP);
		entity.setPort(CONNECT_TYPE.FTP.PORT);
		entity.setUserId(userId);
		entity.setPasswd(passwd);
		entity.setSrcPath(ROOT_DIRECTORY);
		entity.setDstPath(EXTERNAL_DATA_PATH);
		
		try {
			// Network 진단 파일 다운로드
			entity = externalComponent.ExternalDownloadDir(entity);
			
			// Send AgentManager 
			sendAgentManger(entity);
            
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result = false;
		}
		
		return result;
	}
	
	
	public boolean sendAgentManger(ExternalEntity entity){
		boolean result = true;
		
		// send_socket AgentManager 
		// MULTISRVMANUALGSCRPTFIN|Asset_CD|Manager_CD|Vendor_Cd|FilePath|FileName
		for(Map<String, String> downloadFile : entity.getDownloadFileInfoList()){
			String filePath = downloadFile.get("PATH");
			String fileName = downloadFile.get("FILE_NAME");
			String vendorCd = getVendorCode(filePath);
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
			
			logger.info("Send Messaage :: {}", sb.toString());
			SocketClientUtil.sendSocketString("127.0.0.1", 10225, sb.toString());
		}
		
		return result;
	}
	
	private String getVendorCode(String filePath){
		String[] splitPath = StringUtil.split(filePath, "/");
		String vendorName = splitPath[splitPath.length-1];
		String vendorCd = "";
        switch (vendorName){
            case "CISCO":
                vendorCd = "Cisco";
                break;
            case "CISCO_NX-OS":
                vendorCd = "Cisco_NX_OS";
                break;
            case "CISCO_IOS-XR":
                vendorCd = "Cisco_IOS_XR";
                break;
            case "BROCADE":
                vendorCd = "Brocade";
                break;
            case "EXTREME":
                vendorCd = "Extreme";
                break;
            case "FORTINET":
                vendorCd = "FortiOS";
                break;
            case "HUAWEI":
                vendorCd = "Huawei";
                break;
            case "JUNIPER":
                vendorCd = "Juniper";
                break;
            case "ALCATEL":
                vendorCd = "Alcatel_Lucent";
                break;
            default:
                vendorCd = null;
                break;
        }
		
		return vendorCd;
	}
}
