/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.utils.SMSClient.java
 * company : Mobigen
 * @author : Je Joong Lee
 * created at : 2016. 3. 16.
 * description : 
 */

package com.mobigen.snet.supportagent.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mobigen.snet.supportagent.concurrents.OneTimeThread;

import jodd.util.StringUtil;

@Component
public class SmsSendClient {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${snet.sms.ip}")
	private String smsIp;
	@Value("${snet.sms.port}")
	private String smsPort;
	@Value("${snet.sms.cmd}")
	private String smsCmd;
	@Value("${snet.sms.txnum}")
	private String smsTxnum;
	@Value("${snet.sms.prjname}")
	private String smsPrjname;
	@Value("${snet.sms.kind}")
	private String smsKind;
	@Value("${snet.sms.type}")
	private String smsType;

	private String command;

	public String sendSMS(String personMs, String msg) throws Exception {

		String result = "SUCCESS";

		try {
			if(smsType.toUpperCase().equals("DB")){

				// make Message
				// SMS|smsTxnum|personMs|title|msg
				StringBuilder message = new StringBuilder();
				message.append("SMS|")
						.append(smsTxnum+"|")
						.append(personMs+"|")
						.append("|") // title NONE
						.append(msg);

				OneTimeThread worker = new OneTimeThread() {
					@Override
					public void task() throws Exception {
						SocketClientUtil.sendSocketString(smsIp, Integer.parseInt(smsPort), message.toString());
					}
				};
				worker.start();
			}else if(smsType.toUpperCase().equals("ETC")){

				OneTimeThread worker = new OneTimeThread() {
					@Override
					public void task() throws Exception {
						setMessage(personMs, msg);
						StringBuffer output = new StringBuffer();
						Process p;
						String line;
						try {
							logger.info("[ShellCommandHandler] cmd = " + command);
							p = Runtime.getRuntime().exec(command);
							BufferedReader br =new BufferedReader(new InputStreamReader(p.getInputStream()));
							while((line=br.readLine()) != null){
								output.append(line);
								logger.info("[ShellCommandHandler] result = " + line);
							}
							p.waitFor();

							if(br != null){
								br.close();
							}

						} catch (Exception e) {
							throw new Exception();
						}
					}
				};
				worker.start();
			}
		} catch (Exception e) {
			result ="FAILED";
			logger.error("sendSMS Exception :: {}", e.getMessage(), e.fillInStackTrace());
		}

		return result;
	}

	public String packString(String data, int len){
		String rtn = "";
		data = data.replaceAll("-", "");
		if(data.length() < len){
			int dataLen = data.length();
			for(int i=dataLen;i<len;i++){
				data += " ";
			}
			rtn = data;
		}
		return rtn;
	}

	private void setMessage(String personMs, String msg){
		String[] cmd = {smsCmd, personMs, smsTxnum, smsPrjname, smsKind, smsType, addSlashes(msg)};
		this.command = StringUtil.join(cmd, " ");
		logger.debug("SMS Command :: {}", command);
	}

	private String addSlashes(String str){
		String res = str.replace("\\", "\\\\")
				.replace("(", "\\(")
				.replace(")", "\\)")
				.replace("{", "\\{")
				.replace("}", "\\}")
				.replace("^", "\\^");
		return res;
	}
}
