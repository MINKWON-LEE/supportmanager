package com.mobigen.snet.supportagent.service;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobigen.snet.supportagent.dao.DaoMapper;
import com.mobigen.snet.supportagent.memory.GLOBALVAR;
import com.mobigen.snet.supportagent.utils.CommonUtils;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Igloosec
 * File    : EtcServiceImpl.java
 *
 * @author Seonju Ma
 * @Date   2018. 1. 12.
 * Description : 
 * 
 */
@Service
public class EtcServiceImpl extends AbstractService implements EtcService {

	@Autowired
	private DaoMapper daoMapper;

	@Override
	public void backupDB() {
		//DB에서 백업 실행 시간 및 백업 파일 저장 주기를 GET
		String backupTime = daoMapper.BackupTime();

		//DB 백업 전, 백업 디렉토리의 파일 확인 후, 보관 기간이 지난 파일 있는 경우 삭제
		logger.info("Backup File Check Start...");
		//현재 시간 GET
		SimpleDateFormat csdf = new SimpleDateFormat("HH");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		long ct = System.currentTimeMillis();
		String strCurrent = sdf.format(ct);

		String saveTerm = daoMapper.SaveTerm();
		String backupPath = daoMapper.BackupPath();

		File file = new File(backupPath);

		if(file.exists()){ //디렉토리가 있는 경우에만 파일 정리
			for(File f:file.listFiles()){
				if(f.isFile()){ //파일인 경우만 체크
					String fname = f.getName();

					try {
						int cPoint = fname.indexOf(".");
						String fdate = fname.substring(cPoint-12, cPoint);

						Date fileDate = sdf.parse(fdate);
						Date CurrentDate = sdf.parse(strCurrent);

						long termTime = CurrentDate.getTime() - fileDate.getTime();

						long cmp = Long.parseLong(saveTerm)*24*60*60*1000;
						logger.info("fdate : " + fdate + ", termTime : " + termTime + ", cmp : " + cmp);

						if(termTime >= cmp){
							logger.info(fname + " Backup File delete Success.");
							f.delete();
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.error(fname + " Backup File delete Failed.");
						logger.error(e.getMessage());
					}
				}
			}
		}

		//관리자가 WEB에 입력 시 한자리 숫자 입력시 문제 가능성이 있어 수정
		int currentT = Integer.parseInt(csdf.format(ct));
		int backupT = Integer.parseInt(backupTime);

		if(currentT != backupT){
			logger.info("BackupDB Skip => Current Time : " + currentT + " BackupTime : " + backupT);
			return ;
		}

		//properties에서 DB 사용자 계정을 READ 후, 복호화하여 DB Backup 실행 명령에 적용
		try {
			String externalContextProperties = GLOBALVAR.CONF_DIR + File.separator + GLOBALVAR.CONF_PROPERTIES_FILE;
			FileInputStream fi;

			fi = new FileInputStream(externalContextProperties);

			Properties proConf = new Properties();

			proConf.load(fi);

			String DBID = decrypt(proConf.getProperty("jdbc.user"));
			String DBPW = decrypt(proConf.getProperty("jdbc.passwd"));

			logger.info("Backup maria db start...");

			String[] runCmd  = { "/bin/sh", "-c", GLOBALVAR.CONF_DIR+"/MARIADB_BACKUP.sh '" + backupPath + "' '" + DBID + "' '" + DBPW + "'"};
			ArrayList<String> managerResult = CommonUtils.getRunProcess(runCmd);
			for(String msg: managerResult){
				logger.debug("RUN SCRIPT RESULT :: {}", msg);
			}
			logger.info("Backup maria db end...");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public String decrypt(String beforeStr) {
		String encryptData = beforeStr.substring(beforeStr.indexOf("(")+1, beforeStr.indexOf(")"));
		
		StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
		standardPBEStringEncryptor.setAlgorithm("PBEWITHMD5ANDDES");
		standardPBEStringEncryptor.setPassword("igloosec");
		String des = standardPBEStringEncryptor.decrypt(encryptData);
		return des;
	}
}
