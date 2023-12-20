/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.utils
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 5. 26.
 * description : 
 */
package com.mobigen.snet.supportagent.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.google.gson.JsonElement;
import com.mobigen.snet.supportagent.entity.NmapData;

/**
 * @author Hyeon-sik Jung
 *
 */
public class NmapUtil {

	static Logger logger = LoggerFactory.getLogger(NmapUtil.class);

	/**
	 * Nmap Result -> XML file
	 * 
	 * nmap -sS -n -O --max-rate 128 --max-rtt-timeout 1 -T5 -p T:1-1024 -oX 192.168.10.0.log 192.168.10.0/24
	 * nmap -sS -T4 -v -oN xxx.xxx.xxx.xxx --open > D:Scanning_ResultSyn_Stealth_Scan(PC).txt
	 * @param fileName
	 * @param portRange
	 * @param ipRange
	 * @return
	 */
	public static File makeXml(String[] cmd, String fileName) {
		CommonUtils.runProcess(StringUtils.arrayToDelimitedString(cmd, " "));
		File file = new File(fileName);
		return file;
	}
	
	public static List<NmapData> getXMLtoData(String jobKey, String path){
		List<NmapData> listData = new ArrayList<NmapData>();
		String cdate = DateUtil.getCurrDate();
		JsonElement json = XmlToObjectConverter.xmlToJsonOjbect(path);
		JsonElement host = json.getAsJsonObject().get("host");

		if(host!=null){
			if(host.isJsonArray()){
				
				for(int i=0; i< host.getAsJsonArray().size();i++){
					JsonElement element = host.getAsJsonArray().get(i);
					
//					String ipAddress = element.getAsJsonObject().get("address").getAsJsonObject().get("addr").getAsString();
					String ipAddress = "";
					JsonElement address = element.getAsJsonObject().get("address");
					
					if(address.isJsonArray()){
						for(int a=0; a< address.getAsJsonArray().size();a++){
							JsonElement addressElement = address.getAsJsonArray().get(a);
							if(!addressElement.getAsJsonObject().get("addrtype").getAsString().equals("mac"))
								ipAddress = addressElement.getAsJsonObject().get("addr").getAsString();
						}
					}else{
						ipAddress = element.getAsJsonObject().get("address").getAsJsonObject().get("addr").getAsString();
					}
					
					
					JsonElement ports  = element.getAsJsonObject().get("ports");
					
					// case 1. ports is JsonArray ?
					if(ports!=null && ports.isJsonArray()){
						for(int ja =0; ja < ports.getAsJsonArray().size(); ja ++){
							NmapData data = new NmapData();
							JsonElement portElement = ports.getAsJsonArray().get(ja);
							String portid	= portElement.getAsJsonObject().get("portid").getAsString();
							String protocol = portElement.getAsJsonObject().get("protocol").getAsString();
							
							String state	= portElement.getAsJsonObject().get("state") != null ? portElement.getAsJsonObject().get("state").getAsJsonObject().get("state").getAsString(): null;
							String service  = portElement.getAsJsonObject().get("service") !=null ? portElement.getAsJsonObject().get("service").getAsJsonObject().get("name").getAsString():null;
							
							data.setCdate(cdate);
							data.setJobKey(jobKey);
							data.setProtocol(protocol);
							data.setIpAddress(ipAddress);
							data.setPortid(portid);
							data.setStateState(state);
							data.setServiceNm(service);
							
							listData.add(data);
							
						}
					}else if(ports!=null && !ports.isJsonArray()){
						// case 2. ports is JsonObject
						JsonElement port = ports.getAsJsonObject().get("port");
						
						if(port!=null && !port.isJsonNull()){
							
							// port is array (?)
							if(port.isJsonArray()){
								for( int p=0; p < port.getAsJsonArray().size(); p++){
									
									NmapData data = new NmapData();
									
									JsonElement portEntity = port.getAsJsonArray().get(p);
									
									String portid	= portEntity.getAsJsonObject().get("portid").getAsString();
									String protocol = portEntity.getAsJsonObject().get("protocol").getAsString();
									
									String state	= portEntity.getAsJsonObject().get("state") != null ? portEntity.getAsJsonObject().get("state").getAsJsonObject().get("state").getAsString(): null;
									String service  = portEntity.getAsJsonObject().get("service") !=null ? portEntity.getAsJsonObject().get("service").getAsJsonObject().get("name").getAsString():null;
									
									data.setCdate(cdate);
									data.setJobKey(jobKey);
									data.setProtocol(protocol);
									data.setIpAddress(ipAddress);
									data.setPortid(portid);
									data.setStateState(state);
									data.setServiceNm(service);
									
									listData.add(data);
								}
							}else{ 
								// port is one
								NmapData data = new NmapData();
								String portid	= port.getAsJsonObject().get("portid").getAsString();
								String protocol = port.getAsJsonObject().get("protocol").getAsString();
								
								String state	= port.getAsJsonObject().get("state") != null ? port.getAsJsonObject().get("state").getAsJsonObject().get("state").getAsString():null;
								String service  = port.getAsJsonObject().get("service") !=null ?port.getAsJsonObject().get("service").getAsJsonObject().get("name").getAsString():null;
								
								data.setCdate(cdate);
								data.setJobKey(jobKey);
								data.setProtocol(protocol);
								data.setIpAddress(ipAddress);
								data.setPortid(portid);
								data.setStateState(state);
								data.setServiceNm(service);
								
								listData.add(data);
							}
						}else{
							
							// open port 없음
							NmapData data = new NmapData();
							data.setCdate(cdate);
							data.setJobKey(jobKey);
							data.setIpAddress(ipAddress);
							listData.add(data);
						}
						
					}else{
						// port is null
						NmapData data = new NmapData();
						data.setCdate(cdate);
						data.setJobKey(jobKey);
						data.setIpAddress(ipAddress);
						listData.add(data);
					}
					
					
				}
				
			}else{
//				String ipAddress  = host.getAsJsonObject().get("address").getAsJsonObject().get("addr").getAsString();
				String ipAddress = "";
				JsonElement address = host.getAsJsonObject().get("address");
				
				if(address.isJsonArray()){
					for(int a=0; a< address.getAsJsonArray().size();a++){
						JsonElement addressElement = address.getAsJsonArray().get(a);
						if(!addressElement.getAsJsonObject().get("addrtype").getAsString().equals("mac"))
							ipAddress = addressElement.getAsJsonObject().get("addr").getAsString();
					}
				}else{
					ipAddress = host.getAsJsonObject().get("address").getAsJsonObject().get("addr").getAsString();
				}
				
				JsonElement ports = host.getAsJsonObject().get("ports");
				
				if(ports != null && ports.isJsonArray()){
					for(int ja =0; ja < ports.getAsJsonArray().size(); ja ++){
						NmapData data = new NmapData();
						JsonElement portElement = ports.getAsJsonArray().get(ja);
						String portid	= portElement.getAsJsonObject().get("portid").getAsString();
						String protocol = portElement.getAsJsonObject().get("protocol").getAsString();
						
						String state	= portElement.getAsJsonObject().get("state") !=null? portElement.getAsJsonObject().get("state").getAsJsonObject().get("state").getAsString():null;
						String service  = portElement.getAsJsonObject().get("service")!=null? portElement.getAsJsonObject().get("service").getAsJsonObject().get("name").getAsString():null;
						
						data.setCdate(cdate);
						data.setJobKey(jobKey);
						data.setProtocol(protocol);
						data.setIpAddress(ipAddress);
						data.setPortid(portid);
						data.setStateState(state);
						data.setServiceNm(service);
						
						listData.add(data);
						
					}					
				}else if(ports != null && !ports.isJsonArray()){
					JsonElement port  = ports.getAsJsonObject().get("port");
					if(!port.isJsonNull()){
						// port is array (?)
						if(port.isJsonArray()){
							for( int p=0; p < port.getAsJsonArray().size(); p++){
								NmapData data = new NmapData();
								
								JsonElement portEntity = port.getAsJsonArray().get(p);
								
								String portid	= portEntity.getAsJsonObject().get("portid").getAsString();
								String protocol = portEntity.getAsJsonObject().get("protocol").getAsString();
								
								String state	= portEntity.getAsJsonObject().get("state") !=null ?portEntity.getAsJsonObject().get("state").getAsJsonObject().get("state").getAsString():null;
								String service  = portEntity.getAsJsonObject().get("service")!=null ? portEntity.getAsJsonObject().get("service").getAsJsonObject().get("name").getAsString():null;
								
								data.setCdate(cdate);
								data.setJobKey(jobKey);
								data.setProtocol(protocol);
								data.setIpAddress(ipAddress);
								data.setPortid(portid);
								data.setStateState(state);
								data.setServiceNm(service);
								
								listData.add(data);
							}
						}else{
							// port is one
							NmapData data = new NmapData();
							String portid	= port.getAsJsonObject().get("portid").getAsString();
							String protocol = port.getAsJsonObject().get("protocol").getAsString();
							
							String state	= port.getAsJsonObject().get("state")!=null ?port.getAsJsonObject().get("state").getAsJsonObject().get("state").getAsString():null;
							String service  = port.getAsJsonObject().get("service")!=null?port.getAsJsonObject().get("service").getAsJsonObject().get("name").getAsString():null;
							
							data.setCdate(cdate);
							data.setJobKey(jobKey);
							data.setProtocol(protocol);
							data.setIpAddress(ipAddress);
							data.setPortid(portid);
							data.setStateState(state);
							data.setServiceNm(service);
							
							listData.add(data);
						}
					}else{
						// open port 없음
						NmapData data = new NmapData();
						data.setCdate(cdate);
						data.setJobKey(jobKey);
						data.setIpAddress(ipAddress);
						listData.add(data);
					}
				}else{
					// port is null
					NmapData data = new NmapData();
					data.setCdate(cdate);
					data.setJobKey(jobKey);
					data.setIpAddress(ipAddress);
					listData.add(data);
				}
			}
		}
		return listData;
	}
	
	/**
	 * @param arrayString
	 * @param ipRange
	 * @param portRange
	 * @param fileName
	 * @return
	 */
	public static String[] convertCmd(String[] arrayString, String ipRange, String portRange, String fileName){
		
		List<String> cmdLine = new ArrayList<String>();
		for(String s : arrayString){
			logger.debug(s);
			if(s.equals("ipRange"))
				cmdLine.add(ipRange);
			else if(s.equals("T:"))
				cmdLine.add("T:"+portRange);
			else if(s.equals("fileName"))
				cmdLine.add(fileName);
			else
				cmdLine.add(s);
		}
		
		logger.debug("convertCmd :: {}", cmdLine.toString());
		
		String[] result = cmdLine.toArray(new String[cmdLine.size()]);
		
		return result;
	}
}
