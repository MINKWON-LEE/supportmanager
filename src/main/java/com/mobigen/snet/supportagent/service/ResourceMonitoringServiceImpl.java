/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 7. 12.
 * description : 
 */
package com.mobigen.snet.supportagent.service;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import com.google.common.collect.Lists;
import com.mobigen.snet.supportagent.dao.ExcelExportMapper;
import com.mobigen.snet.supportagent.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mobigen.snet.supportagent.dao.ResourceMonitoringMapper;
import com.mobigen.snet.supportagent.memory.GLOBALVAR.PROCESS;
import com.mobigen.snet.supportagent.utils.CommonUtils;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : ResourceMonitoringServiceImpl.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 1. 20.
 * Description : 
 *
 */
@Service
public class ResourceMonitoringServiceImpl extends AbstractService implements ResourceMonitoringService {

	@Autowired
	private ResourceMonitoringMapper resourceMonitoringMapper;
	@Autowired
	private ExcelExportMapper excelExportMapper;

	private String SERVER_NAME ="WEB";
	private String serverIP = "";

	@Value("${snet.support.zip.path}")
	private String tempReportFilesPath;
	@Value("${snet.schedule.excel.job.delete.day}")
	private String jobDeleteDay;
	@Value("${snet.schedule.resource.job.delete.day}")
	private String resourceDeleteDay;

	private String[] serverIPcmd = { "/bin/sh", "-c", "ifconfig -a | grep inet | grep -v inet6 | awk '{print $2}' | sed 's/[^0-9.]*\\([0-9.]*\\).*/\\1/' | grep -v 127.0.0.1 | grep -v 192.168.122.1"};

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public ResourceMonitoringServiceImpl() {
		String[] hostNameCmd = { "/bin/sh", "-c", "hostname" };
//		ArrayList<String> hostNameResult = CommonUtils.getRunProcess(hostNameCmd);
//		this.SERVER_NAME = hostNameResult.get(0);
	}

	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.ResourceMonitoringService#getResource()
	 */
	@Override
	public void getResource() {
		getSystemIP();
		processReport();
		diskReport();
		cpuReport();
		memoryReport();
	}

	public void getSystemIP() {
		try {
			ArrayList<String> serverIpList = CommonUtils.getRunProcess(serverIPcmd);
			logger.info("serverIpList: " + serverIpList);

			if(serverIpList != null  & serverIpList.size() > 0) {
				setServerIP(serverIpList.get(0));
			}
		} catch (Exception e) {
			logger.error("get ip exception :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.ResourceMonitoringService#processReport()
	 */
	@Override
	public void processReport() {
		
		//ps -eo user,pid,pmem,pcpu,cmd --sort -size | head -n 11 |grep java |grep DAgentManager | awk '{print $1","$2","$3","$4}'
		//ps -eo user,pid,pmem,pcpu,cmd --sort -size | head -n 11 |grep java |grep DSupportAgent | awk '{print $1","$2","$3","$4}'
		
		String[] chkManager = { "/bin/sh", "-c", "ps -eo user,pid,pmem,pcpu,cmd --sort -size | head -n 11 |grep java |grep DAgentManager | awk '{print $1\",\"$2\",\"$3\",\"$4}'" };
		String[] chkSupport = { "/bin/sh", "-c", "ps -eo user,pid,pmem,pcpu,cmd --sort -size | head -n 11 |grep java |grep DSupportManager | awk '{print $1\",\"$2\",\"$3\",\"$4}'" };
		String[] chkTomcat  = { "/bin/sh", "-c", "ps -eo user,pid,pmem,pcpu,cmd --sort -size | head -n 11 | grep tomcat | grep -v API | awk '{print $1\",\"$2\",\"$3\",\"$4}'" };
		
		ArrayList<ProcessEntity> processList = new ArrayList<ProcessEntity>();
		
		try {
			ArrayList<String> managerResult = CommonUtils.getRunProcess(chkManager);
			// Check Agent Manager Process Status
			if(managerResult != null & managerResult.size()>0){
				
				// ex) sgweb,24736,4.9,72.8
				String[] result = managerResult.get(0).split(",");
				ProcessEntity process = new ProcessEntity();
				process.setServerNm(SERVER_NAME);
				process.setProcesNm(PROCESS.AGENTMANAGER.NAME);
				process.setUser(result[0]);
				process.setPid(result[1]);
				process.setMem(result[2]);
				process.setCpu(result[3]);
				process.setStatus(1);
				process.setServerIp(serverIP);
				processList.add(process);
			}else{
				ProcessEntity process = new ProcessEntity();
				process.setServerNm(SERVER_NAME);
				process.setProcesNm(PROCESS.AGENTMANAGER.NAME);
				process.setStatus(0);
				process.setServerIp(serverIP);
				processList.add(process);
			}
				
			
			
			
			ArrayList<String> supportResult = CommonUtils.getRunProcess(chkSupport);
			
			if(supportResult != null & supportResult.size()>0){
				
				// ex) sgweb,24736,4.9,72.8
				String[] result = supportResult.get(0).split(",");
				ProcessEntity process = new ProcessEntity();
				process.setServerNm(SERVER_NAME);
				process.setProcesNm(PROCESS.SUPPORTAGENT.NAME);
				process.setUser(result[0]);
				process.setPid(result[1]);
				process.setMem(result[2]);
				process.setCpu(result[3]);
				process.setStatus(1);
				process.setServerIp(serverIP);
				processList.add(process);
			}else{
				ProcessEntity process = new ProcessEntity();
				process.setServerNm(SERVER_NAME);
				process.setProcesNm(PROCESS.SUPPORTAGENT.NAME);
				process.setStatus(0);
				process.setServerIp(serverIP);
				processList.add(process);
			}

			ArrayList<String> tomcatResult = CommonUtils.getRunProcess(chkTomcat);
			
			if(tomcatResult != null & tomcatResult.size()>0){
				
				// ex) sgweb,24736,4.9,72.8
				String[] result = tomcatResult.get(0).split(",");
				ProcessEntity process = new ProcessEntity();
				process.setServerNm(SERVER_NAME);
				process.setProcesNm(PROCESS.TOMCAT.NAME);
				process.setUser(result[0]);
				process.setPid(result[1]);
				process.setMem(result[2]);
				process.setCpu(result[3]);
				process.setStatus(1);
				process.setServerIp(serverIP);
				processList.add(process);
			}else{
				ProcessEntity process = new ProcessEntity();
				process.setServerNm(SERVER_NAME);
				process.setProcesNm(PROCESS.TOMCAT.NAME);
				process.setStatus(0);
				process.setServerIp(serverIP);
				processList.add(process);
			}
			
			for(ProcessEntity process : processList){
				resourceMonitoringMapper.insertProcess(process);
			}
			
		} catch (Exception e) {
			logger.error("Monitoring Process Exception :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.ResourceMonitoringService#diskReport()
	 */
	@Override
	public void diskReport() {
		
		/*
		 *  Linux
		 *  
		 *  Filesystem, Size, Used, Avail, Use%, Mounted
		 */
		try {

			String[] cmd = { "/bin/sh", "-c", "df -HP | awk '{print $1\",\"$2\",\"$3\",\"$4\",\"$5\",\"$6}'" };
			ArrayList<String> result = CommonUtils.getRunProcess(cmd);
			
			List<DiskEntity> diskList = new ArrayList<DiskEntity>();
			for(int i=1; i < result.size(); i++){
				try {
					String[] usage = result.get(i).trim().split(",");
					DiskEntity disk = new DiskEntity();
					disk.setServerNm(SERVER_NAME);
					disk.setFileSystem(usage[0]);
					disk.setSize(usage[1]);
					disk.setUsed(usage[2]);
					disk.setAvail(usage[3]);
					disk.setUseRate(usage[4]);
					disk.setMountedOn(usage[5]);
					disk.setServerIp(serverIP);
					diskList.add(disk);
				} catch (Exception e) {
					logger.error("Disk Report added Exception :: {}", e.getMessage(), e.fillInStackTrace());
				}
			}
			
			// insert disk usage
			for(DiskEntity disk : diskList){
				resourceMonitoringMapper.insertDisk(disk);
			}
			
		} catch (Exception e) {
			logger.error("diskReport Exception :: {}", e.getMessage(), e.fillInStackTrace());
		}
		
	}

	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.ResourceMonitoringService#cpuReport()
	 */
	@Override
	@SuppressWarnings("restriction")
	public void cpuReport() {
		// mpstat | tail -1 | awk '{print 100-$11}'
		try {
			CpuEntity entity = new CpuEntity();
			entity.setServerNm(SERVER_NAME);
			entity.setServerIp(serverIP);
			final OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
		    double load;
		    String pattern = "###.##";
		    DecimalFormat df = new DecimalFormat(pattern);
		    while(true){
		      load = ((com.sun.management.OperatingSystemMXBean) osBean).getSystemCpuLoad();
		      try {
		        Thread.sleep(1000);
		      } catch (InterruptedException e) {
		        e.printStackTrace();
		      }
		      if(load > 0.0){
		    	  entity.setCpu(df.format(load*100d));
		    	  break;
		      }
		    }

			resourceMonitoringMapper.insertCpu(entity);
		} catch (Exception e) {
			logger.error("CPU monitoring Exception :: {}",e.getMessage(), e.fillInStackTrace());
		}
	}

	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.ResourceMonitoringService#memoryReport()
	 */
	@Override
	public void memoryReport() {
		/*
		 	total ==>  free | grep ^Mem | awk '{print $2}'
			used  ==>  free | grep ^-/+ | awk '{print $3}'
			free  ==>  total - used
			사용률  ==>  (used / total) * 100
		 */
		try {
			MemoryEntity memory = new MemoryEntity();
			
			memory.setServerNm(SERVER_NAME);
			String[] totalCmd = { "/bin/sh", "-c", "free | grep ^Mem | awk '{print $2}'" };
			ArrayList<String> totalResult = CommonUtils.getRunProcess(totalCmd);
			memory.setTotal(totalResult.get(0));
			
			String[] usedCmd = { "/bin/sh", "-c", "free | grep ^-/+ | awk '{print $3}'" };
			ArrayList<String> usedResult = CommonUtils.getRunProcess(usedCmd);

			if(usedResult.isEmpty()) {
				usedCmd = new String[]{"/bin/sh", "-c", "free | grep ^Mem | awk '{print $3}'"};
				usedResult = CommonUtils.getRunProcess(usedCmd);
				memory.setUsed(usedResult.get(0));
			} else {
				memory.setUsed(usedResult.get(0));
			}

			String useRate = String.format ("%.4f", (Double.parseDouble(memory.getUsed()) / Double.parseDouble(memory.getTotal())) * (double)100.0);
			Double free = Double.parseDouble(memory.getTotal()) - Double.parseDouble(memory.getUsed());

			memory.setFree(free.toString());
			memory.setUseRate(useRate);
			memory.setServerIp(serverIP);

			resourceMonitoringMapper.insertMemory(memory);
		} catch (Exception e) {
			logger.error("Memory monitoring Exception :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.ResourceMonitoringService#restartService()
	 */
	@Override
	public void restartService() {
		// TODO Auto-generated method stub
//		su - $SNET_WEB -c "cd /usr/local/snetManager/bin/; /usr/local/snetManager/bin/run.sh"
		String[] cmd = { "/bin/sh", "-c", "su - sgweb -c \"cd /usr/local/snetManager/bin/; /usr/local/snetManager/bin/run.sh\"" };
		try {
			CommonUtils.getRunProcess(cmd);
		} catch (Exception e) {
			logger.error("Restart Service Exception :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}

	@Override
	public void deleteResourceMorniotring() {
		
		String[] deleteTable = {
				"SNET_REPORT_CPU",
				"SNET_REPORT_DISK ",
				"SNET_REPORT_MEMORY",
				"SNET_REPORT_PROCESS"
		};
		for(String tableName : deleteTable){
			try {
				Map<String, String> map = new HashMap<String, String>();
				map.put("tableName", tableName);
				resourceMonitoringMapper.deleteReportTerm(map);
			} catch (Exception e) {
				logger.error("Delete Exception :: {}", e.getMessage(), e.fillInStackTrace());
			}
		}
	}

	/**
	 * 'sg_supprotmanager 프로젝트 - 스케줄러'
	 * 보고서 EXCEL JOB 테이블 삭제
	 */
	@Override
	public void deleteAuditExcelJobScheduler() {

		try {

			List<ExcelJobEntity> excelJobEntityList = resourceMonitoringMapper.getAuditExcelJob(jobDeleteDay);

			for (ExcelJobEntity dto : excelJobEntityList) {

				Map gAssetSwJob = new HashMap();
				gAssetSwJob.put("REQ_CD", dto.getReqCd());
				gAssetSwJob.put("JOB_FILE_NM", dto.getJobFileNm());
				gAssetSwJob.put("JOB_FLAG", dto.getJobFlag());

				excelExportMapper.deleteAssetSwExportJob(gAssetSwJob);
				deleteExcelFiles(tempReportFilesPath, dto.getJobFileNm());
			} // end for
		} catch (Exception e) {

			logger.error(e.getMessage(), e);
		}
	} // end method

	/**
	 * report 엑셀 파일 삭제
	 */
	private void deleteExcelFiles(String excelPath, String zipFileNm) {

		ZipOutputStream out = null;

		try {

			File file = new File(excelPath + "/" + zipFileNm);
			logger.info("*[---------------------------------------------------------------------------------------------]");
			logger.info("*[deleteExcelFiles] file : {}", file);

			if (file.exists()) {

				file.delete();
			} else {

				logger.info("*[file] not exists");
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	} // end method deleteExcelFiles

	/**
	 * Agent resource data 삭제
	 */
	public void deleteAgentResource() {
		try {
			resourceMonitoringMapper.deleteAgentResource(resourceDeleteDay);

		} catch (Exception e) {

			logger.error(e.getMessage(), e);
		}
	} // end method deleteAgentResource
}
