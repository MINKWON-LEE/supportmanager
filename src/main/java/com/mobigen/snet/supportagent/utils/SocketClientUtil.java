/**
 * project : SupportAgent
 * package : com.mobigen.snet.supportagent.utils
 * company : Mobigen
 * 
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 7.
 * Description : 
 * 
 */
package com.mobigen.snet.supportagent.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.utils
 * Company : Mobigen
 * File    : SocketClientUtil.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 7.
 * Description : 
 * 
 */
public class SocketClientUtil {
	
	private static Logger logger = LoggerFactory.getLogger(SocketClientUtil.class);
	
	public static boolean sendSocketString(String serverIp, int port, String message){
		boolean result = true;
		OutputStreamWriter osw = null;
		Socket socket = null;
		try {
			socket = new Socket(serverIp, port);
			osw = new OutputStreamWriter(socket.getOutputStream());
			osw.write(message, 0, message.length());
			osw.flush();
			osw.close();
		} catch (Exception e) {
			result = false;
			logger.debug("tcp socket client Exception :: " +  e.getMessage(), e);
		} finally {
			try {
				if(socket!=null){
					socket.close();
				}
			} catch (IOException e) {
				logger.error("== [ignore] socket close error : " + e.getMessage(), e);
			}
		}
		
		return result;
	}
}
