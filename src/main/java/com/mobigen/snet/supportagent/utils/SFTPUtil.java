/**
 * project : SupportAgent
 * package : com.mobigen.snet.supportagent.utils
 * company : Mobigen
 * 
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 9.
 * Description : 
 * 
 */
package com.mobigen.snet.supportagent.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.utils
 * Company : Mobigen
 * File    : SFTPUtil.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 9.
 * Description : 
 * 
 */
public class SFTPUtil {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Session session = null;

	private Channel channel = null;

	private ChannelSftp channelSftp = null;

	/**
	 * 서버와 연결에 필요한 값들을 가져와 초기화 시킴
	 *
	 * @param host
	 *            서버 주소
	 * @param userName
	 *            접속에 사용될 아이디
	 * @param password
	 *            비밀번호
	 * @param port
	 *            포트번호
	 * @throws Exception 
	 */
	public void init(String host, int port, String userName, String password) throws Exception {
		JSch jsch = new JSch();
		try {
			session = jsch.getSession(userName, host, port);
			session.setPassword(password);

			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();

			channel = session.openChannel("sftp");
			channel.connect();
		} catch (JSchException e) {
			e.printStackTrace();
			throw new Exception(e);
		}

		channelSftp = (ChannelSftp) channel;

	}

	/**
	 * 인자로 받은 경로의 파일 리스트를 리턴한다.
	 * 
	 * @param path
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Vector<ChannelSftp.LsEntry> getFileList(String path) {

		Vector<ChannelSftp.LsEntry> list = null;
		try {
			channelSftp.cd(path);
			list = channelSftp.ls(".");
		} catch (SftpException e) {
			e.printStackTrace();
			return null;
		}

		return list;
	}

	/**
	 * 하나의 파일을 업로드 한다.
	 *
	 * @param dir
	 *            저장시킬 주소(서버)
	 * @param file
	 *            저장할 파일
	 */
	public void upload(String dir, File file) {

		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			channelSftp.cd(dir);
			channelSftp.put(in, file.getName());
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 하나의 파일을 다운로드 한다.
	 *
	 * @param dir
	 *            저장할 경로(서버)
	 * @param downloadFileName
	 *            다운로드할 파일
	 * @param path
	 *            저장될 공간
	 */
	public void downloadFile(String dir, String downloadFileName, String path) {
		InputStream in = null;
		FileOutputStream out = null;
		try {
			CommonUtils.mkdir(path);
			
			channelSftp.cd(dir);
			in = channelSftp.get(downloadFileName);
		} catch (SftpException e) {
			e.printStackTrace();
		}

		try {
			out = new FileOutputStream(new File(path+File.separator+downloadFileName));
			int i;

			while ((i = in.read()) != -1) {
				out.write(i);
			}
			
			Runtime.getRuntime().exec("chown -R sgweb:sgweb " + path);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	public void downloadDir(String srcDir, String path) {
		Vector<ChannelSftp.LsEntry> list = getFileList(srcDir);
		for(ChannelSftp.LsEntry entry : list){
			if(entry.getAttrs().isDir()){
				if(!entry.getFilename().equals(".") && !entry.getFilename().equals("..")){
					logger.debug("Recursive :: {}", srcDir + "/" + entry.getFilename());
					downloadDir(srcDir+ File.separator + entry.getFilename(), path + File.separator + entry.getFilename());
				}else{
					logger.debug(entry.getFilename());
				}
			}else{
				logger.debug("SRC :: {}, File :: {}, destPath :: {}", srcDir, entry.getFilename(), path);
				downloadFile(srcDir, entry.getFilename(), path);
			}
		}
	}

	/**
	 * 서버와의 연결을 끊는다.
	 */
	public void disconnection() {
		channelSftp.quit();

	}
}
