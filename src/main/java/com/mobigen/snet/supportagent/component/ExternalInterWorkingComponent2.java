/**
 * project : SupportAgent
 * package : com.mobigen.snet.supportagent.component
 * company : Mobigen
 * 
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 9.
 * Description : 
 * 
 */
package com.mobigen.snet.supportagent.component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.ChannelSftp;
import com.mobigen.snet.supportagent.entity.ExternalEntity;
import com.mobigen.snet.supportagent.memory.GLOBALVAR.CONNECT_TYPE;
import com.mobigen.snet.supportagent.utils.CommonUtils;
import com.mobigen.snet.supportagent.utils.FTPUtil;
import com.mobigen.snet.supportagent.utils.SFTPUtil;

import jodd.util.StringUtil;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.component
 * Company : Mobigen
 * File    : SftpComponent.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 9.
 * Description : 
 * 
 */
@Component
public class ExternalInterWorkingComponent2 extends AbstractComponent{

	/**
	 * 외부 서버에서 sftp로 파일 다운로드
	 * @param entity
	 */
	public void ExternalDownloadFile(ExternalEntity entity) {
		
		if(CONNECT_TYPE.SFTP.TYPE.equals(entity.getConnectType())){
			SFTPUtil util = new SFTPUtil();
			try {
				util.init(entity.getHost(), entity.getPort(), entity.getUserId(), entity.getPasswd());
				Vector<ChannelSftp.LsEntry> list = util.getFileList(entity.getSrcPath());
				
				List<String> downloadList = new ArrayList<>();
				for (ChannelSftp.LsEntry oListItem : list) {
					if (!oListItem.getAttrs().isDir()) {
						if(entity.getDownloadFileList() != null){
							if(entity.getDownloadFileList().contains(oListItem.getFilename())){
								util.downloadFile(entity.getSrcPath(), oListItem.getFilename(), entity.getDstPath());
								downloadList.add(oListItem.getFilename());
							}
						}else{
							util.downloadFile(entity.getSrcPath(), oListItem.getFilename(), entity.getDstPath());
							downloadList.add(oListItem.getFilename());
						}
					} else {
						logger.debug(oListItem.toString() + " ... is Directory");
					}
				}
				logger.debug("Download Total :: {}, File :: [ {} ]", downloadList.size(), StringUtil.join(downloadList, ","));
			} catch (Exception e) {
				logger.error("SFTP Exception :: {}", e.getMessage(), e.fillInStackTrace());
			} finally{
				util.disconnection();
			}
			
		}else if(CONNECT_TYPE.FTP.TYPE.equals(entity.getConnectType())){
			
			FTPUtil ftp = new FTPUtil(entity.getHost(), entity.getPort(), entity.getUserId(), entity.getPasswd());
			
			try {
				if(ftp.login(true)){
					ftp.cd(entity.getSrcPath());
					if(entity.getDownloadFileList()!=null && entity.getDownloadFileList().size() > 0){
						for(FTPFile file : ftp.list()){
							if(entity.getDownloadFileList().contains(file.getName()))
								ftp.downloadFile(entity.getSrcPath(), file.getName(), entity.getDstPath());
						}
					}else{
						for(FTPFile file : ftp.list()){
							ftp.downloadFile(entity.getSrcPath(), file.getName(), entity.getDstPath());
						}
					}
				}else{
					logger.error("FTP Login Failed !");
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				
			} finally {
				ftp.logout();
			}
		}
	}
	
	

	/**
	 * 다운로드 대상 디렉토리 목록에 있는 모든 파일
	 * 하위 디렉토리도 포함하여 다운로드 
	 * 하위 디렉토리가 있을 경우 다운로드 되는 서버에 디렉토리를 생성한다.
	 * @param entity
	 */
	public ExternalEntity ExternalDownloadDir(ExternalEntity entity) throws Exception{
		
		try {
			List<Map<String, String>> downloadFileInfoList = new ArrayList<Map<String, String>>();
			
			if(entity.getConnectType().equals(CONNECT_TYPE.SFTP.TYPE)){
				SFTPUtil sftp = new SFTPUtil();
				sftp.init(entity.getHost(), entity.getPort(), entity.getUserId(), entity.getPasswd());
				
				Vector<ChannelSftp.LsEntry> list = sftp.getFileList(entity.getSrcPath());
				
				for (ChannelSftp.LsEntry oListItem : list) {
					if (oListItem.getAttrs().isDir()) {
						if(entity.getDownloadDirList() != null){
							if(entity.getDownloadDirList().contains(oListItem.getFilename())){
								sftp.downloadDir(entity.getSrcPath() + File.separator + oListItem.getFilename(), entity.getDstPath());
							}
						}
					} 
				}
				sftp.disconnection();
			}else{
				
				FTPUtil ftp = new FTPUtil(entity.getHost(), entity.getPort(), entity.getUserId(), entity.getPasswd());
				if(ftp.login(true)){
					ftp.cd(entity.getSrcPath());
					
					if(entity.getDownloadDirList() !=null && entity.getDownloadDirList().size()>0){
						for(FTPFile ftpEntity : ftp.list()){
							if(ftpEntity.isDirectory() && entity.getDownloadDirList().contains(ftpEntity.getName())){
								if(ftpEntity.isDirectory()){
									downloadFileInfoList.addAll(getDownloadFiles(ftp.pwd() + File.separator + ftpEntity.getName(), entity.getDstPath()+ File.separator + ftpEntity.getName(), ftp));
								}else{
									downloadFileInfoList.addAll(getDownloadFiles(ftp.pwd(), entity.getDstPath(), ftp));
								}
							}
						}
					}else{
						for(FTPFile ftpEntity : ftp.list()){
							if(ftpEntity.isDirectory()){
								downloadFileInfoList.addAll(getDownloadFiles(entity.getSrcPath() + File.separator + ftpEntity.getName(), entity.getDstPath()+ File.separator + ftpEntity.getName(), ftp));
							}else{
								downloadFileInfoList.addAll(getDownloadFile(ftpEntity.getName(), entity.getDstPath(), ftp));
							}
						}
					}
				}else{
					logger.error("FTP Login Failed !");
				}
			}
			if(downloadFileInfoList.size()>0)
				entity.setDownloadFileInfoList(downloadFileInfoList);
		} catch (Exception e) {
			logger.error("ExternalDownloadDir Exception :: {}", e.getMessage(), e.fillInStackTrace());
			throw new Exception(e.getMessage());
		}
		
		return entity;
	}
	
	private List<Map<String, String>> getDownloadFiles(String srcPath, String destPath, FTPUtil ftp){
        File destDir = new File(destPath);

        if(!destDir.exists()){
            //없다면 생성
        	destDir.mkdirs();
        }else{
            //있다면 현재 디렉토리 파일을 삭제
            File[] destroy = destDir.listFiles();
            for(File des : destroy){
            	if(des.isFile()){
            		des.delete();
            	}
            }
        }
        
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		ftp.cd(srcPath);
		for(FTPFile ftpFile : ftp.list()){
			if(ftpFile.isFile()){
				Map<String, String> map = new HashMap<String, String>();
				ftp.downloadFile(ftp.pwd(), ftpFile.getName(), destPath);
				map.put("PATH", destPath);
				map.put("FILE_NAME", ftpFile.getName());
				result.add(map);
			}else if(ftpFile.isDirectory()){
				result= getDownloadFiles(srcPath + File.separator + ftpFile.getName(), destPath + File.separator + ftpFile.getName(), ftp);
			}
		}
		return result;
	}
	
	private List<Map<String, String>> getDownloadFile(String srcFilename, String destDirPath, FTPUtil ftp){
		File destDir = new File(destDirPath);
		
		if(!destDir.exists()){
			//없다면 생성
			destDir.mkdirs();
		}else{
			CommonUtils.deleteFile(new File(destDirPath, srcFilename));
		}
		
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		ftp.downloadFile(ftp.pwd(), srcFilename, destDirPath);
		map.put("PATH", destDirPath);
		map.put("FILE_NAME", srcFilename);
		result.add(map);
		
		return result;
	}
	
	public List<String> getReadCsvFile(String path, String encoding) {
        BufferedReader br = null;
        String line;
        List<String> list = new ArrayList<String>();
        
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path), encoding));
            while ((line = br.readLine()) != null) {
            	list.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
	

	
	
}
