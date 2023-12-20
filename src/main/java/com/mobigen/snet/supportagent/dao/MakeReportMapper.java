/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.dao
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 7. 1.
 * description : 
 */
package com.mobigen.snet.supportagent.dao;

import java.util.ArrayList;

import org.mybatis.spring.annotation.MapperScan;

import com.mobigen.snet.supportagent.entity.MakeReportEntity;

/**
 * @author Hyeon-sik Jung
 *
 */
@MapperScan
public interface MakeReportMapper {

	/*
	 * Delete Report
	 * CDATE 기준
	 * ex)20160701
	 */
	int deleteReportHostIp();
	int deleteReportTeam();
	int deleteReportBranch();
	int deleteReportSwWeak();
	int deleteReportBranchSwWeak();
	int deleteReportSwNm();
	int deleteReportSwType();
	int deleteReportTeamSwType();
	int deleteReportBranchSwType();
	int deleteReportHostIpSwType();
	int deleteReportBranchAgent();
	int deleteReportBranchAgentStatus();

	/*
	 * MakeReport 
	 * 기준 CDATE
	 * ex)20160701
	 */
	int insertReportHostIp();
	int insertReportTeam();
	int insertReportBranch();
	int insertReportSwWeak();
	int insertReportBranchSwWeak();
	int insertReportSwNm();
	int insertReportSwType();
	int insertReportTeamSwType();
	int insertReportBranchSwType();
	int insertReportHostIpSwType();
	int insertReportBranchAgent();
	int insertReportBranchAgentStatus();

	ArrayList<MakeReportEntity> selectReportBranch();
	
	/**
	 * selectReportBranch ->  updateReportBranchCnt
	 * @param entity
	 */
	int updateReportBranchCnt(MakeReportEntity entity);
	
	ArrayList<MakeReportEntity> selectReportBranchSwType();
	/**
	 * selectReportBranchSwType -> updateReportBranchSwType
	 * @param entity
	 */
	int updateReportBranchSwType(MakeReportEntity entity);
	
	ArrayList<MakeReportEntity> selectReportHostIp();
	/**
	 * selectReportHostIp -> updateReportHostIp
	 * @param entity
	 */
	int updateReportHostIp(MakeReportEntity entity);
	
	ArrayList<MakeReportEntity> selectReportSwNm();
	/**
	 * selectReportSwNm -> updateReportSwNm
	 * @param entity
	 */
	int updateReportSwNm(MakeReportEntity entity);
	
	ArrayList<MakeReportEntity> selectReportSwType();
	/**
	 * selectReportSwType -> updateReportSwType
	 * @param entity
	 */
	int updateReportSwType(MakeReportEntity entity);
	
	ArrayList<MakeReportEntity> selectReportTeam();
	/**
	 * selectReportTeam -> updateReportTeam
	 * @param entity
	 */
	int updateReportTeam(MakeReportEntity entity);
	
	ArrayList<MakeReportEntity> selectReportTeamSwType();
	/**
	 * selectReportTeamSwType -> updateReportTeamSwType
	 * @param entity
	 */
	int updateReportTeamSwType(MakeReportEntity entity);
	
	int deleteAgentReport();
	
	int makeAgentReport();

	void insertBranchWeeklyReport();

	void insertBranchMonthlyReport();

	void deleteBranchReport();
}
