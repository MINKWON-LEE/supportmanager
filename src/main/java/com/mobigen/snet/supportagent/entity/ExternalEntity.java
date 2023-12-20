/**
 * project : SupportAgent
 * package : com.mobigen.snet.supportagent.entity
 * company : Mobigen
 * 
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 9.
 * Description : 
 * 
 */
package com.mobigen.snet.supportagent.entity;

import java.util.List;
import java.util.Map;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.entity
 * Company : Mobigen
 * File    : ExternalEntity.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 9.
 * Description : 
 * 
 */
public class ExternalEntity {

	/**
	 * SFTP, FTP 
	 */
	private String connectType;
	
	/**
	 * 외부 접속 host or ip
	 */
	private String host;
	/**
	 * port 
	 */
	private int port;
	/**
	 * 외부 접속 아이디
	 */
	private String userId;
	/**
	 * 외부 접속 패스워드
	 */
	private String passwd;
	
	/**
	 * 외부 접속 root path
	 */
	private String srcPath;
	/**
	 * download file
	 */
	private String dstPath;
	
	private List<String> downloadFileList;
	
	/**
	 * download dir
	 */
	private List<String> downloadDirList;

	private List<Map<String, String>> downloadFileInfoList; 
	
	public String getConnectType() {
		return connectType;
	}

	public void setConnectType(String connectType) {
		this.connectType = connectType;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getSrcPath() {
		return srcPath;
	}

	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	public String getDstPath() {
		return dstPath;
	}

	public void setDstPath(String dstPath) {
		this.dstPath = dstPath;
	}

	public List<String> getDownloadFileList() {
		return downloadFileList;
	}

	public void setDownloadFileList(List<String> downloadFileList) {
		this.downloadFileList = downloadFileList;
	}

	public List<String> getDownloadDirList() {
		return downloadDirList;
	}

	public void setDownloadDirList(List<String> downloadDirList) {
		this.downloadDirList = downloadDirList;
	}

	public List<Map<String, String>> getDownloadFileInfoList() {
		return downloadFileInfoList;
	}

	public void setDownloadFileInfoList(List<Map<String, String>> downloadFileInfoList) {
		this.downloadFileInfoList = downloadFileInfoList;
	}
}
