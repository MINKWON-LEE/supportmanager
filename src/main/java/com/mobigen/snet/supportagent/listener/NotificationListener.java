/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.concurrents.NoSleepThread.java
 * @author : Je-Joong Lee
 * created at : 2016. 1. 5.
 * description : 
 */
package com.mobigen.snet.supportagent.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mobigen.snet.supportagent.concurrents.NoSleepThread;
import com.mobigen.snet.supportagent.concurrents.OneTimeThread;
import com.mobigen.snet.supportagent.service.AbstractService;
import com.mobigen.snet.supportagent.service.ManageMentJobService;
import com.mobigen.snet.supportagent.utils.CommonUtils;

@Component("notificationListener")
public class NotificationListener extends AbstractService {

	ServerSocket serverSocket = null;
	Socket socket = null;
	
	@Autowired (required = false)
	private ManageMentJobService manageMentJobService;
	
	@Value("${snet.listener.port}")
	private String port;
	
	public void initNotificationListener() {

		NoSleepThread serverIntiator = new NoSleepThread() {
			@Override
			public void task() throws Exception {
				loadServerSocket();
			}
		};

		serverIntiator.start();
	}

	private void loadServerSocket(){

		try {

			serverSocket = new ServerSocket(Integer.parseInt(port));

			logger.info("*[loadServerSocket]");
			logger.info("{}", serverSocket);
			logger.info("{}", serverSocket.getInetAddress());
			logger.info("{}", serverSocket.getLocalPort());
			logger.info("{}", serverSocket.getLocalSocketAddress());

		} catch (IOException e) {
			logger.error(CommonUtils.printError(e));

		}
		while (true) {

			logger.info("*[serverSocket.accept]");

			try {
				
				socket = serverSocket.accept();
				createWorker(socket);
			} catch (IOException e) {
				System.out.println("I/O error: " + e);
				logger.error("I/O error: {}",e);
			} 
		}

	}

	public void createWorker(final Socket socket) {

		OneTimeThread worker = new OneTimeThread() {
			@Override
			public void task() throws Exception {
				doHandleStreamMessage(socket);
			}
		};
		worker.start();
	}

	public void doHandleStreamMessage(Socket socket)
			throws Exception {
		try {
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String recvMsg = inFromClient.readLine();

			logger.info("*[header] : " + recvMsg);

			manageMentJobService.jobManager(recvMsg);
			
			socket.close();

			if(inFromClient != null){
				inFromClient.close();
			}
		} catch (IOException e) {
			logger.error(CommonUtils.printError(e));

		}

	}

}
