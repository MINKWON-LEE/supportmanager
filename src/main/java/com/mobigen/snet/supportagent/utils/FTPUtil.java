/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.utils.FTPHandler.java
 * company : Mobigen
 * @author : Je-Joong Lee
 * created at : 2016. 1. 8.
 * description : 
 */

package com.mobigen.snet.supportagent.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPUtil {

	static Logger logger = LoggerFactory.getLogger(FTPUtil.class);
	FTPClient ftpClient;

	private String server;
	private int port = 21;
	private String username = "";
	private String password = "";

	static final long RemoteFileCheckInterval = 1000; // 1 = 1 millisecond

	public FTPUtil() {
		this.ftpClient = new FTPClient();
		this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
	}

	public FTPUtil(String rURL, int rPort, String username, String password) {
		this.ftpClient = new FTPClient();
		this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		this.server = rURL;
		this.port = rPort;
		this.username = username;
		this.password = password;

		// logger.debug(this.username+ " : "+this.password);
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUser(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public boolean login(boolean isPassiveMode) throws IOException {

		logger.debug("FTP Connect. ip = " + this.server + " port = " + this.port);
		ftpClient.connect(this.server, this.port);
		showServerReply(ftpClient);
		logger.debug("FTP login.");
		boolean isLogin = ftpClient.login(this.username, this.password);
		showServerReply(ftpClient);
		if (isPassiveMode) {
			ftpClient.enterLocalPassiveMode();
		} else {
			ftpClient.enterLocalActiveMode();
		}
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

		return isLogin;
	}

	public boolean logout() {
		try {
			if(ftpClient==null){
				return true;
			}
			CommonUtils.runSafely(ftpClient::logout, true);
			CommonUtils.runSafely(ftpClient::disconnect, true);
			return true;
			
		} catch (Exception ex) {
			logger.error("FTP LOGOUT ERROR : " + ex.getMessage(), ex);
			return false;
		}

	}

	public boolean fileCheck(String serverDir, String fileName) {
		boolean result = false;
		try {
			String[] rFileNames = ftpClient.listNames(serverDir);
			for (String fname : rFileNames) {
				if (fileName.equalsIgnoreCase(fname)) {
					result = true;
					break;
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return result;

	}

	public String uploadFile(String remoteFileWPath, File localFile) {

		InputStream inputStream = null;
		try {
			String scriptFullPath = "";
			String homeDir = ftpClient.printWorkingDirectory();
			if ("".equals(homeDir)) {
				homeDir = "./";
			}

			showServerReply(ftpClient);
			if (homeDir != null && homeDir.contains(remoteFileWPath)) {
				scriptFullPath = homeDir + "/";
			} else {
				ftpClient.mkd(remoteFileWPath);
				scriptFullPath = homeDir + "/" + remoteFileWPath + "/";
			}
			logger.debug("printWorking Dir : " + homeDir);
			logger.debug("Upload file to remoteFilePath:" + scriptFullPath + localFile.getName() + " , localFile:"
					+ localFile.getAbsolutePath());

			inputStream = new FileInputStream(localFile);

			logger.debug("SENDING " + localFile.getName() + " FILE .....");

			ftpClient.changeWorkingDirectory(scriptFullPath);

			ftpClient.storeFile(scriptFullPath + localFile.getName(), inputStream);
			// ftpClient.completePendingCommand();
			logger.debug(localFile.getName() + " FTP FILE UPLOAD COMPLETED.!");
			ftpClient.sendSiteCommand("chmod " + "755 " + scriptFullPath + localFile.getName());
			showServerReply(ftpClient);

			String workingDir = ftpClient.printWorkingDirectory();
			logger.debug("returning workingDir : " + workingDir);
			// return workingDir;

			if(inputStream != null){
				inputStream.close();
			}

			return scriptFullPath;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "**ERROR WHILE FTP FILE UPLOAD - FILE NOT FOUND" + e.getMessage();
		} catch (IOException e2) {
			return "**ERROR WHILE FTP FILE UPLOAD - IO EXCEPTION" + e2.getMessage();
		}
	}

	private static void showServerReply(FTPClient ftpClient) {
		String[] replies = ftpClient.getReplyStrings();
		if (replies != null && replies.length > 0) {
			for (String aReply : replies) {
				logger.debug("SERVER: " + aReply);
			}
		}
	}

	/**
	 * using retrieveFile(String, OutputStream)
	 **/
	public boolean downloadFile(String remotePath, String fileName, String destPath) {
		String remoteFile = remotePath + "/" + fileName;
		String destFile = destPath + "/" + fileName;

		boolean success = false;
		OutputStream outputStream = null;
		try {
			CommonUtils.mkdir(destPath);

			File downloadFile = new File(destFile);
			logger.debug("REMOTE FILE :: {}, destPath :: {}", remotePath, fileName, destFile);
			outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
			success = ftpClient.retrieveFile(remoteFile, outputStream);
			outputStream.close();
			if (success) {
				logger.info(remoteFile + " has been downloaded successfully.");
			}
		} catch (IOException e) {
			success = false;
			logger.error("FTP File Download IO ERROR = " + e.getMessage());
			e.printStackTrace();
		} finally {
			if(outputStream!=null)
				try {
					outputStream.close();
				} catch (IOException e) {
				}
		}
		return success;

	}
	
	public boolean downloadDirs(String srcPath, String destPath){
		boolean result = true;
		cd(srcPath);
		for(FTPFile ftpFile : list()){
			if(ftpFile.isDirectory())
				downloadDirs(pwd()+ File.separator + ftpFile.getName(), destPath+ File.separator + ftpFile.getName());
			else
				downloadFile(pwd(), ftpFile.getName(), destPath);
		}
		return result;
	}

	/**
	 * using InputStream retrieveFileStream(String)
	 **/
	public boolean getRemoteFile2(String remoteFilePath, String localFilePath, String batchFileName, String exeTime) {

		String remoteFile = remoteFilePath + "/" + batchFileName;
		File downloadFile = new File(localFilePath + "/" + batchFileName);


		boolean success = false;
		try {

			OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(downloadFile));
			InputStream inputStream = ftpClient.retrieveFileStream(remoteFile);
			byte[] bytesArray = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(bytesArray)) != -1) {
				outputStream2.write(bytesArray, 0, bytesRead);
			}

			success = ftpClient.completePendingCommand();
			if (success) {
				logger.info(batchFileName + " has been downloaded successfully.");
			}
			outputStream2.close();
			inputStream.close();

		} catch (IOException ex) {
			success = false;
			logger.error("Error: " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return success;
	}

	// FTP의 ls 명령, 모든 파일 리스트를 가져온다
	public FTPFile[] list() {

		FTPFile[] files = null;
		try {
			files = this.ftpClient.listFiles();
			return files;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	

	public String pwd() {

		String path = "";
		try {
			path = this.ftpClient.printWorkingDirectory();
			return path;
		} catch (IOException e) {
			CommonUtils.printError(e);
		}
		return null;
	}

	/**
	 * Change Directory
	 * 
	 * @param path
	 */
	public boolean cd(String path) {
		boolean result = true;
		try {
			return ftpClient.changeWorkingDirectory(path);
		} catch (Exception e) {
			logger.error("== ftp cd", e);
			result =false;
		}
		return result;
	}
	
	public boolean isDirExists(String dir) throws Exception{
		try {
			ftpClient.changeWorkingDirectory(dir);
		    int returnCode = ftpClient.getReplyCode();
		    if (returnCode == 550) {
		        return false;
		    }
		    return true;			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	
	public FileDownloadResult downloadAFile(FTPFile srcFile, String targetDirPath) {
		String remoteCurrDir = pwd();
		String fileName = srcFile.getName();
		String remoteFile =   remoteCurrDir + "/" + fileName;
		String destFile = targetDirPath + "/" + fileName;
		FileDownloadResult result = new FileDownloadResult();
		result.setFileName(fileName).setRemoteFilePath(remoteCurrDir + "/" + fileName);
		
		boolean success = false;
		OutputStream outputStream = null;
		try {
			CommonUtils.mkdir(targetDirPath);
			File localFile = new File(destFile);
			CommonUtils.deleteFile(localFile);
			outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
			success = ftpClient.retrieveFile(remoteFile, outputStream);
			result.setLocalFile(localFile);
			result.setSucceeded(success);
			if (success) {
				logger.info("== ftp downloaded : {}", localFile.getAbsolutePath());
			}else{
				logger.warn("== ftp download failed : {}", localFile.getAbsolutePath());
			}
		} catch (Exception e) {
			logger.error("== ftp download : " + remoteFile, e);
			result.setSucceeded(false);
		} finally {
			CommonUtils.close(outputStream);
		}
		return result;
	}
	
	
	public List<FTPFile> list(Pattern pattern){
		FTPFile[] origList = list();
		if(origList==null){
			return null;
		}
		List<FTPFile> resultList = Arrays.stream(origList).filter(f->pattern.matcher(f.getName()).find()).collect(Collectors.toList());
		return resultList;
	}
	
	public Optional<FTPFile> getLastFile(Pattern pattern){
		List<FTPFile> list = list(pattern);
		return getLastFile(list);
	}
	
	public Optional<FTPFile> getLastFile(List<FTPFile> list){
		if(list==null || list.size()==0){
			return Optional.empty();
		}
		
		Optional<FTPFile> result = list.stream().max((f, f2)-> f.getName().compareTo(f2.getName()));
		return result;
	}
	
	public static class FileDownloadResult{
		private String remoteFilePath;
		private String fileName;
		private File localFile;
		private boolean succeeded;
		
		public FileDownloadResult(String remoteFilePath, String fileName, File localFile) {
			this.remoteFilePath = remoteFilePath;
			this.fileName = fileName;
			this.localFile = localFile;
		}
		public FileDownloadResult(){
		}
		public String getRemoteFilePath() {
			return remoteFilePath;
		}
		public FileDownloadResult setRemoteFilePath(String remoteFilePath) {
			this.remoteFilePath = remoteFilePath;
			return this;
		}
		public String getFileName() {
			return fileName;
		}
		public FileDownloadResult setFileName(String fileName) {
			this.fileName = fileName;
			return this;
		}
		public File getLocalFile() {
			return localFile;
		}
		public FileDownloadResult setLocalFile(File localFile) {
			this.localFile = localFile;
			return this;
		}
		public boolean isSucceeded() {
			return succeeded;
		}
		public FileDownloadResult setSucceeded(boolean succeeded) {
			this.succeeded = succeeded;
			return this;
		}
		public boolean isFailed(){
			return !isSucceeded();
		}
	}
	
}