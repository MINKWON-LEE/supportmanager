/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 5. 27.
 * description : 
 */
package com.mobigen.snet.supportagent.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mobigen.snet.supportagent.dao.DaoMapper;
import com.mobigen.snet.supportagent.entity.NmapData;
import com.mobigen.snet.supportagent.entity.NmapJob;
import com.mobigen.snet.supportagent.entity.NpsNmapJob;
import com.mobigen.snet.supportagent.entity.OpenPortInfo;
import com.mobigen.snet.supportagent.exception.DBException;
import com.mobigen.snet.supportagent.exception.DBException.NoData;
import com.mobigen.snet.supportagent.exception.XMLtoDataException;
import com.mobigen.snet.supportagent.utils.CommonUtils;
import com.mobigen.snet.supportagent.utils.NmapUtil;


/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : PortScanServiceImpl.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 1. 20.
 * Description : 
 * 
 */
@Service
public class PortScanServiceImpl extends AbstractService implements PortScanService {

	@Autowired
	private DaoMapper daoMapper;

	@Value("${snet.support.xml.path}")
	private String xmlPath;

	@Value("${snet.support.nmap.cmd}")
	private String cmd;

	@Value("${snet.support.manual.xml.path}")
	private String manualPath;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mobigen.snet.supportagent.service.PortScanService#portScan(java.lang.
	 * String)
	 */
	@Override
	public void portScan(String key) {

		NmapJob job = new NmapJob();
		job.setJobKey(key);

		try {
			// 1. job 검색
			NmapJob jobData = daoMapper.selectNmapJob(job);

			if (jobData != null && jobData.getJobStatus().equals("1")) {
				jobData.setJobStatus("2");
				daoMapper.updateNmapJob(jobData);
			} else if (jobData == null) {
				throw new DBException.NoData("Nmap Job not found Exception");
			} else if (jobData != null && !jobData.getJobStatus().equals("1")) {
				throw new DBException.MoreData("Nmap Status Exception :: " + jobData.getJobStatus());
			}

			// 2-1 Nmap scan & export xml
			String fileName = CommonUtils.makeFileName() + ".xml";
			CommonUtils.mkdir(xmlPath);

			String filePath = xmlPath + File.separator + fileName;

			String[] cmdArray = NmapUtil.convertCmd(cmd.split(" "), jobData.getRangeIp(), jobData.getRangePort(),
					filePath);
			File file = NmapUtil.makeXml(cmdArray, filePath);

			if (file.isFile()) {
				// 2-2 Xml parsing -> NmapData
				List<NmapData> dataList = null;
				try {
					dataList = NmapUtil.getXMLtoData(jobData.getJobKey(), filePath);
				} catch (Exception e) {
					throw new XMLtoDataException("XML TO Data Exception !!");
				}

				// 3. raw data insert
				// 4. data result (delete -> insert) or insert
				for (NmapData data : dataList) {
					daoMapper.insertNmapRawData(data);
					daoMapper.deleteNmapResult(data);
					daoMapper.insertNmapResult(data);
				}
			} else {
				throw new IOException("Nmap Xml File not found Exception");
			}

			jobData.setJobStatus("3");
			jobData.setJobFileNm(fileName);
			daoMapper.updateNmapJob(jobData);

		} catch (DBException.NoData e) {
			logger.error("No Data Exception :: {}", e.getMessage(), e.getCause());
		} catch (DBException.MoreData e) {
			logger.error("Nmap Job Status Exception :: {}", e.getMessage(), e.getCause());
		} catch (IOException ioe) {
			job.setJobStatus("4");
			daoMapper.updateNmapJob(job);
			logger.error("Port Scan Exception :: {}", ioe.getMessage(), ioe.getCause());
		} catch (XMLtoDataException ioe) {
			job.setJobStatus("4");
			daoMapper.updateNmapJob(job);
			logger.error("Port Scan Exception :: {}", ioe.getMessage(), ioe.getCause());
		}
	}

	
	@Override
	public void nmapXmlInsert(String key, String fileName) {
		NpsNmapJob job = new NpsNmapJob();
		job.setJobId(key);

		try {
			// 1. job 검색
			List<NpsNmapJob> jobList = daoMapper.selectTempletJob(key);
			
			if(jobList.size()==0){
				throw new DBException.NoData("Nmap Job not found Exception");
			}
			job.setJobResult("2");
			daoMapper.updateJobStart(job);
			
			List<NmapData> dataList = new ArrayList<>();
			
			for (NpsNmapJob jobData : jobList) {
				String filePath = manualPath + File.separator + fileName;
				logger.debug("File path :: {}", filePath);
				File file = new File(filePath);

				if (file.isFile()) {
					// 2-2 Xml parsing -> NmapData

					try {
						List<NmapData> data = NmapUtil.getXMLtoData(jobData.getJobId(), filePath);
						if(data.size()>0)
							dataList.addAll(data);
					} catch (Exception e) {
						throw new XMLtoDataException("XML TO Data Exception !!");
					}

				} else {
					throw new IOException("Nmap Xml File not found Exception");
				}
			}
			
			String ip = "";
			int ipCount = 0;
			List<OpenPortInfo> openPort = new ArrayList<>();
			
			
			int count = 0;
			
			for(NmapData data :dataList){
				//create data Key
				data.setDataKey(key+count);
				
				if(!ip.equals(data.getIpAddress())){
					ipCount  = daoMapper.selectAssetMasterByIpAddress(data.getIpAddress());
					openPort = daoMapper.selectOpenPortByIpAddress(data.getIpAddress());
				}
				
					// case 1. 중복 IP
				if( ipCount> 1){
					data.setHostNm("Duplicated Ip address");
					daoMapper.insertComparedOpenPort(data);
				}else if(ipCount ==1 ){
					// case 2. matching IP
					NmapData compareData = compareNmapData(openPort, data);
					if(compareData!=null)
						daoMapper.insertComparedOpenPort(compareData);
				}else{
					// case 3. no matching ip address
					daoMapper.insertComparedOpenPort(data);
				}
				count++;
			}
			
			job.setJobResult("3");
			daoMapper.updateJobResult(job);
			
		} catch (IOException ioe) {
			job.setJobResult("4");
			job.setJobDesc("포트스캔 결과 파일 생성에 실패하였습니다. [포트스캔 템플릿을 확인해 주십시오.]");
			daoMapper.updateJobResult(job);
			logger.error("Port Scan Exception :: {}", ioe.getMessage(), ioe.getCause());
		} catch (XMLtoDataException ioe) {
			job.setJobResult("4");
			job.setJobDesc("포트스캔 결과 파일 데이터 변환에 실패하였습니다.[어플리케이션이 포트스캔 결과 파일을 변환하지 못하였습니다. 관리자에 문의해 주십시오.]");
			daoMapper.updateJobResult(job);
			logger.error("Port Scan Exception :: {}", ioe.getMessage(), ioe.getCause());
		} catch (NoData e) {
			logger.error("Port Scan Exception :: {}", e.getMessage(), e.getCause());
		} catch (Exception e){
			job.setJobResult("4");
			job.setJobDesc("포트스캔중 알수 없는 오류가 발생하였습니다. 관리자에 문의해 주십시오.");
			daoMapper.updateJobResult(job);
			logger.error("Port Scan Exception :: {}", e.getMessage(), e.fillInStackTrace());
		}
		
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mobigen.snet.supportagent.service.PortScanService#multiplePortScan(
	 * java.lang.String)
	 */
	@Override
	public void multiplePortScan(String key) {
		NpsNmapJob job = new NpsNmapJob();
		job.setJobId(key);

		try {
			// 1. job 검색
			List<NpsNmapJob> jobList = daoMapper.selectTempletJob(key);

			if(jobList.size()==0){
				throw new DBException.NoData("Nmap Job not found Exception");
			}
			job.setJobResult("2");
			daoMapper.updateJobStart(job);

			List<NmapData> dataList = new ArrayList<>();

			for (NpsNmapJob jobData : jobList) {
				// 2-1 Nmap scan & export xml
				String fileName = CommonUtils.makeFileName() + ".xml";
				CommonUtils.mkdir(xmlPath);

				String filePath = xmlPath + File.separator + fileName;

				String[] cmdArray = NmapUtil.convertCmd(cmd.split(" "), jobData.getIpAddress(), jobData.getPort(),
						filePath);
				File file = NmapUtil.makeXml(cmdArray, filePath);

				if (file.isFile()) {
					// 2-2 Xml parsing -> NmapData

					try {
						List<NmapData> data = NmapUtil.getXMLtoData(jobData.getJobId(), filePath);
						if(data.size()>0)
							dataList.addAll(data);
					} catch (Exception e) {
						throw new XMLtoDataException("XML TO Data Exception !!");
					}

				} else {
					throw new IOException("Nmap Xml File not found Exception");
				}
			}

			String ip = "";
			int ipCount = 0;
			List<OpenPortInfo> openPort = new ArrayList<>();


			int count = 0;

			for(NmapData data :dataList){
				//create data Key
				data.setDataKey(key+count);

				if(!ip.equals(data.getIpAddress())){
					ipCount  = daoMapper.selectAssetMasterByIpAddress(data.getIpAddress());
					openPort = daoMapper.selectOpenPortByIpAddress(data.getIpAddress());
				}

					// case 1. 중복 IP
				if( ipCount> 1){
					data.setHostNm("Duplicated Ip address");
					daoMapper.insertComparedOpenPort(data);
				}else if(ipCount ==1 ){
					// case 2. matching IP
					NmapData compareData = compareNmapData(openPort, data);
					if(compareData!=null)
						daoMapper.insertComparedOpenPort(compareData);
				}else{
					// case 3. no matching ip address
					daoMapper.insertComparedOpenPort(data);
				}
				count++;
			}

			job.setJobResult("3");
			daoMapper.updateJobResult(job);

		} catch (IOException ioe) {
			job.setJobResult("4");
			job.setJobDesc("포트스캔 결과 파일 생성에 실패하였습니다. [포트스캔 템플릿을 확인해 주십시오.]");
			daoMapper.updateJobResult(job);
			logger.error("Port Scan Exception :: {}", ioe.getMessage(), ioe.getCause());
		} catch (XMLtoDataException ioe) {
			job.setJobResult("4");
			job.setJobDesc("포트스캔 결과 파일 데이터 변환에 실패하였습니다.[어플리케이션이 포트스캔 결과 파일을 변환하지 못하였습니다. 관리자에 문의해 주십시오.]");
			daoMapper.updateJobResult(job);
			logger.error("Port Scan Exception :: {}", ioe.getMessage(), ioe.getCause());
		} catch (NoData e) {
			logger.error("Port Scan Exception :: {}", e.getMessage(), e.getCause());
		} catch (Exception e){
			job.setJobResult("4");
			job.setJobDesc("포트스캔중 알수 없는 오류가 발생하였습니다. 관리자에 문의해 주십시오.");
			daoMapper.updateJobResult(job);
			logger.error("Port Scan Exception :: {}", e.getMessage(), e.fillInStackTrace());
		}
	}
	
	
	private NmapData compareNmapData(List<OpenPortInfo> openport, NmapData data){
		try{
			if(openport!=null){
				for(OpenPortInfo port : openport){
					if(port.getIpAddress().equals(data.getIpAddress())){
						data.setHostNm(port.getHostNm());
						if(port.getOpenPort() !=null
						&& port.getOpenPort().equals(data.getPortid())){
							data.setAuditDay(port.getAuditDay());
							data.setOpenPort(port.getOpenPort());
							data.setProcessNm(port.getProcessNm());
							data.setOpenType(port.getOpenType());
						}
						
					}
				}
			}
		}catch(Exception e){
			logger.error("compareNmapData Exception :: {} ", e.getMessage(), e.fillInStackTrace());
		}
		return data;
	}
}
