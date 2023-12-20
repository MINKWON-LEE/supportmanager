/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.dao
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 7. 12.
 * description : 
 */
package com.mobigen.snet.supportagent.dao;

import java.util.List;
import java.util.Map;

import com.mobigen.snet.supportagent.entity.*;
import org.mybatis.spring.annotation.MapperScan;

/**
 * @author Hyeon-sik Jung
 *
 */
@MapperScan
public interface ResourceMonitoringMapper {

	Integer insertProcess(ProcessEntity process)throws Exception;
	
	Integer insertCpu(CpuEntity cpu)throws Exception;
	
	Integer insertDisk(DiskEntity disk)throws Exception;
	
	Integer insertMemory(MemoryEntity memory)throws Exception;
	
	Threshold selectThreshold(Threshold threshold) throws Exception;

	List<AlarmUser> selectAlarmUser(AlarmUser alarmUser)throws Exception;
	
	/**
	 * 최근5분간 프로세스별 평균 CPU 사용률
	 * @param processNm
	 * @return CPU usage
	 */
	double checkProcess(String processNm)throws Exception;
	
	/**
	 * 최근 5분간 서버 평균 CPU 사용률 
	 * @param serverName
	 * @return CPU usage
	 */
	double checkCpu(String serverName)throws Exception;
	
	/**
	 * Current Disk Usage
	 * @param serverName
	 * @return 
	 */
	List<DiskEntity> checkDisk(String serverName)throws Exception;
	
	/**
	 * 최근 5분간 서버 평균 Memory usage
	 * @param serverName
	 * @return
	 */
	double checkMemory(String serverName)throws Exception;
	
	/**
	 * 리소스 모니터링 14일 이전 데이터 삭제
	 * @param map
	 * @return
	 * @throws Exception
	 */
	int deleteReportTerm(Map<String, String> map) throws Exception;

	/**
	 * 'sg_supprotmanager 프로젝트 - 스케줄러'
	 * 보고서 EXCEL JOB 테이블 삭제
	 *   -> 자료실 excel_job 테이블 기준으로 데이터, 파일 삭제 - 1일 1회, 대상은 60일이 지난 row 에 대함
	 */
    List<ExcelJobEntity> getAuditExcelJob(String jobDeleteDay);


	/**
	 * 에이전트 리소스 모니터링 데이터 삭제
	 *
	 * @param resourceDeleteDay
	 * @return
	 * @throws Exception
	 */
	int deleteAgentResource(String resourceDeleteDay) throws Exception;
}
