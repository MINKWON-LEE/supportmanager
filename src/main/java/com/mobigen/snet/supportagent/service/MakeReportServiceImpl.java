/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.service
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 7. 1.
 * description : 
 */
package com.mobigen.snet.supportagent.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mobigen.snet.supportagent.dao.MakeReportMapper;
import com.mobigen.snet.supportagent.entity.MakeReportEntity;


/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Mobigen
 * File    : MakreReportServiceImpl.java
 *
 * @author Hyeon-sik Jung
 * @Date   2017. 1. 20.
 * Description : 
 * 
 */
@Service
public class MakeReportServiceImpl extends AbstractService implements MakeReportService {

	@Autowired
	private MakeReportMapper makeReportMapper;
	
	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.MakeReportService#makeReport()
	 */
	@Override
	public void makeReport() {
		/*
		 * Report ...
		 * delete all cdate - sysdate
		 * insert all
		 */
		makeReportHostIp();
		makeReportTeam();
		makeReportBranch();
		makeReportSwWeak();
		makeReportBranchSwWeak();
		makeReportSwNm();
		makeReportSwType();
		makeReportTeamSwType();
		makeReportBranchSwType();
		makeReportHostIpSwType();
		makeReportBranchAgent();
		makeReportBranchAgentStatus();
		logger.info("Make Report Finished!!...");
	}
	
	@Transactional
	private void makeReportHostIp(){
		makeReportMapper.deleteReportHostIp();
		makeReportMapper.insertReportHostIp();
	}

	@Transactional
	private void makeReportTeam(){
		makeReportMapper.deleteReportTeam();
		makeReportMapper.insertReportTeam();
	}

	@Transactional
	private void makeReportBranch(){
		makeReportMapper.deleteReportBranch();
		makeReportMapper.insertReportBranch();
	}

	@Transactional
	private void makeReportSwWeak(){
		makeReportMapper.insertReportSwWeak();
		makeReportMapper.deleteReportSwWeak();
	}

	@Transactional
	private void makeReportBranchSwWeak(){
		makeReportMapper.deleteReportBranchSwWeak();
		makeReportMapper.insertReportBranchSwWeak();
	}

	@Transactional
	private void makeReportSwNm(){
		makeReportMapper.deleteReportSwNm();
		makeReportMapper.insertReportSwNm();
	}

	@Transactional
	private void makeReportSwType(){
		makeReportMapper.insertReportSwType();
		makeReportMapper.deleteReportSwType();
	}

	@Transactional
	private void makeReportTeamSwType(){
		makeReportMapper.deleteReportTeamSwType();
		makeReportMapper.insertReportTeamSwType();
	}

	@Transactional
	private void makeReportBranchSwType(){
		makeReportMapper.deleteReportBranchSwType();
		makeReportMapper.insertReportBranchSwType();
	}

	@Transactional
	private void makeReportHostIpSwType(){
		makeReportMapper.deleteReportHostIpSwType();
		makeReportMapper.insertReportHostIpSwType();
	}

	@Transactional
	private void makeReportBranchAgent(){
		makeReportMapper.deleteReportBranchAgent();
		makeReportMapper.insertReportBranchAgent();
	}

	@Transactional
	private void makeReportBranchAgentStatus(){
		makeReportMapper.deleteReportBranchAgentStatus();
		makeReportMapper.insertReportBranchAgentStatus();
	}

	
	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.MakeReportService#updateCount()
	 */
	@Override
	@Transactional
	public void updateCount() {
		/*
		1. selectReportBranch ->  updateReportBranchCnt
		2. selectReportBranchSwType -> updateReportBranchSwType
		3. selectReportHostIp -> updateReportHostIp
		4. selectReportSwNm -> updateReportSwNm
		5. selectReportSwType -> updateReportSwType
		6. selectReportTeam -> updateReportTeam
		7. selectReportTeamSwType -> updateReportTeamSwType
		*/
		
		List<MakeReportEntity> branchList = makeReportMapper.selectReportBranch();
		for(MakeReportEntity entity : branchList){
			makeReportMapper.updateReportBranchCnt(entity);
		}

		List<MakeReportEntity> branchSwTypeList = makeReportMapper.selectReportBranchSwType();
		for(MakeReportEntity entity : branchSwTypeList){
			makeReportMapper.updateReportBranchSwType(entity);
		}
		
		List<MakeReportEntity> hostIpList = makeReportMapper.selectReportHostIp();
		for(MakeReportEntity entity : hostIpList){
			makeReportMapper.updateReportHostIp(entity);
		}
		
		List<MakeReportEntity> swNmList = makeReportMapper.selectReportSwNm();
		for(MakeReportEntity entity : swNmList){
			makeReportMapper.updateReportSwNm(entity);
		}
		
		List<MakeReportEntity> swTypeList = makeReportMapper.selectReportSwType();
		for(MakeReportEntity entity : swTypeList){
			makeReportMapper.updateReportSwType(entity);
		}
		
		List<MakeReportEntity> teamList = makeReportMapper.selectReportTeam();
		for(MakeReportEntity entity : teamList){
			makeReportMapper.updateReportTeam(entity);
		}
		
		List<MakeReportEntity> teamSwTypeList = makeReportMapper.selectReportTeamSwType();
		for(MakeReportEntity entity : teamSwTypeList){
			makeReportMapper.updateReportTeamSwType(entity);
		}
		logger.debug("Make Report update Count...DONE !!...");

	}

	/* (non-Javadoc)
	 * @see com.mobigen.snet.supportagent.service.MakeReportService#makeReportAgent()
	 */
	@Override
	@Transactional
	public void makeAgentReport() {
		makeReportMapper.deleteAgentReport();
		makeReportMapper.makeAgentReport();
	}

	@Override
	@Transactional
	public void branchWeeklyReport() {
		makeReportMapper.insertBranchWeeklyReport();
	}
	@Override
	@Transactional
	public void branchMonthlyReport() {
		makeReportMapper.insertBranchMonthlyReport();
	}

	@Override
	@Transactional
	public void deleteBranchReport() {
		makeReportMapper.deleteBranchReport();
	}
}
