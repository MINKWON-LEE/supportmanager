package com.mobigen.snet.supportagent.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FtpClientHelper {
	private FTPUtil ftpUtil;
	public FtpClientHelper(String url, int port, String username, String password){
		ftpUtil = new FTPUtil(url, port, username, password);
	}
	
	public void login(boolean isPassiveMode) throws IOException{
		boolean login = ftpUtil.login(isPassiveMode);
		if(!login){
			throw new IOException("== login failed");
		}
	}
	
	public void close(){
		if(ftpUtil!=null){
			ftpUtil.logout();
		}
	}
	
	public FTPUtil getFtpUtil() {
		return ftpUtil;
	}
	
	

	
}
