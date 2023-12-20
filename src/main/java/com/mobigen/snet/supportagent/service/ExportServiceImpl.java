/**
 * project : SupportAgent
 * package : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * 
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 17.
 * Description : 
 * 
 */
package com.mobigen.snet.supportagent.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.common.collect.Lists;
import com.mobigen.snet.supportagent.component.ExcelExportTotalComponent;
import com.mobigen.snet.supportagent.dao.DaoMapper;
import com.mobigen.snet.supportagent.entity.ExcelJobEntity;
import com.mobigen.snet.supportagent.models.ReportRequestDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mobigen.snet.supportagent.component.ExcelExportComponent;
import com.mobigen.snet.supportagent.dao.ExcelExportMapper;
import com.mobigen.snet.supportagent.memory.SafeThread;
import com.mobigen.snet.supportagent.memory.SyncQueue;
import org.springframework.util.StopWatch;
import org.zeroturnaround.zip.ZipUtil;

/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : ExportServiceImpl.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 2. 17.
 * Description : 
 *
 * 'sg_supprotmanager 프로젝트 - 상세보고서'
 */
@Service
public class ExportServiceImpl extends AbstractService implements ExportService {
	
	@Autowired
	private ExcelExportMapper excelExportMapper;
	
	@Autowired
	private ExcelExportComponent excelExportComponent;

	@Autowired
	private ExcelExportTotalComponent excelExportTotalComponent;

	@Autowired (required = false)
	private DaoMapper daoMapper;

	@Value("${snet.support.zip.path}")
	private String excelPath;

	// to do test : 서버에 올릴때 주석 변경할 것
//	private static final String tempReportFilesPath = "D:/report/";
	private static final String tempReportFilesPath = "/usr/local/snetManager/data/excel/tempReportFiles/";

	private static final String seperator = "/";


	/**
	 * 보고서 생성 요청 수신 처리
	 */
	@Override
	public void excelExportRequest(ReportRequestDto reportRequestDto) {
		// watch start
		StopWatch watch = new StopWatch("excelExport");
		watch.start();

		try {

			Map params = new HashMap();
			params.put("REQ_CD", reportRequestDto.getReqCd());

			// Job 테이블 상태 변경 (처리중)
			updateJobStatus("", "2", params);

			List<Map> jobList = excelExportMapper.selectAssetSwExportJobListbyReqcd(params);

			// 종합보고서 생성 완료 확인
			int totalResultCnt = 0;

			// 상세보고서 생성 완료 확인
			int detailResultCnt = 0;

			for(@SuppressWarnings("rawtypes") Map job:jobList){

				reportRequestDto.setReqUser(job.get("REQ_USER").toString());
				totalResultCnt = excelExportTotalComponent.createTotalReport(job, reportRequestDto);
				detailResultCnt = excelExportComponent.createDetailReport(job, reportRequestDto);
			} // end for jobList

			// 종합보고서, 상세보고서 완료 확인하여 압축파일 생성
			if (totalResultCnt == 1 && detailResultCnt == 1) {

				updateJobStatusFinish("temp_" + reportRequestDto.getReqCd(), "2", params);

				ExcelJobEntity entity = new ExcelJobEntity();
				entity.setReqCd(reportRequestDto.getReqCd());
				ExcelJobEntity getExcelJob = daoMapper.getExcelJob(entity);

				// -> to do test : 서버에 올릴때 주석 할 것
//				String excelPath = "D:/zip";

				if (StringUtils.equals("temp_" + reportRequestDto.getReqCd(), getExcelJob.getJobFileNm())) {

					// write zip file
					String zipFileNm = reportRequestDto.getReqCd();
					compressZip(tempReportFilesPath + "/" + reportRequestDto.getReqCd(), excelPath + seperator, zipFileNm);
//
					File files = new File(tempReportFilesPath + "/" + reportRequestDto.getReqCd());
					deleteFolder(files);
				} // end if

				// Job 테이블 상태 변경 (완료)
				updateJobStatusFinish(reportRequestDto.getReqCd() + ".zip", "1", params);
				watch.stop();

				logger.info("\n{}", watch.prettyPrint());

				long millis = watch.getTotalTimeMillis();

				String pattern = "mm:ss";
				SimpleDateFormat format = new SimpleDateFormat(pattern);
				String date = format.format(new Timestamp(millis));
				logger.info("\n스마트가드 진단 결과 보고서 생성 최종 완료, Total time : {} sec", date);
			} else {
				// Job 테이블 상태 변경 (실패)
				updateJobStatusFinish(reportRequestDto.getReqCd() + ".zip", "3", params);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e.fillInStackTrace());
		}
	} // end method excelExportRequest

	/**
	 * 하위 폴더 삭제
	 */
	private void deleteFolder(File file) {

		File[] files = file.listFiles();

		for (int i = 0; i < files.length; i++) {

			if (files[i].isFile()) {

				files[i].delete();
				continue;
			}

			if (files[i].isDirectory()) {

				deleteFolder(files[i]);
			}

			files[i].delete();
		}
	} // end method deleteFolder

	/**
	 * zip 파일로 압축 (폴더)
	 */
	public void compressZip(String excelPath, String zipFileNmPath, String zipFileNm) {

		File tempReportFolder = new File(tempReportFilesPath + "/" + zipFileNm);
		if (!tempReportFolder.exists()) {

			tempReportFolder.mkdirs();
		}

		ZipUtil.pack(new File(excelPath), new File(zipFileNmPath + zipFileNm + ".zip"));
	}

	@Override
	public void excelExport(String key) {

//		logger.info("ExcelExportService Start..!!");
//		excelType = key;
//		this.exportQueue = new SyncQueue();
//		try {
//
//			@SuppressWarnings("rawtypes")
//			List<Map> jobList = excelExportMapper.selectAssetSwExportJobList();
//
//			for(@SuppressWarnings("rawtypes") Map job:jobList){
//
//				exportQueue.push(job);
//
//				Worker worker = new Worker();
//				worker.startup();
//			}
//
//		}catch (SQLException e) {
//			logger.error(e.getMessage(), e.fillInStackTrace());
//		}
	}

	private void updateJobStatus(String jobFileNm, String jobFlag, Map gAssetSwJob) throws Exception {

		String reqCd = gAssetSwJob.get("REQ_CD").toString();

		gAssetSwJob.put("JOB_FILE_NM", reqCd + ".zip");
		gAssetSwJob.put("JOB_FLAG", jobFlag);
		excelExportMapper.updateAssetSwExportJob(gAssetSwJob);
	}

	private void updateJobStatusFinish(String jobFileNm, String jobFlag, Map gAssetSwJob) throws Exception {

		gAssetSwJob.put("JOB_FILE_NM", jobFileNm);
		gAssetSwJob.put("JOB_FLAG", jobFlag);
		excelExportMapper.updateAssetSwExportJobFinish(gAssetSwJob);
	}
}
