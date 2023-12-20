/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.dao
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 9. 12.
 * description : 
 */
package com.mobigen.snet.supportagent.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author Hyeon-sik Jung
 *
 */
@SuppressWarnings("rawtypes")
public interface ExcelExportMapper {
	
	public List<Map> selectAssetSwExportJobList() throws SQLException;
	
	public int updateAssetSwExportJob(Map params)throws SQLException;

	public int updateAssetSwExportJobFinish(Map params)throws SQLException;
	
	public List<Map> selectAssetSwExportList(Map params) throws SQLException;
	
	public List<Map> selectAssetSwAuditReport(Map params) throws SQLException;

	//SvcAssetSwExportService
	public List<Map> selectSvcAssetSwExportList() throws SQLException;
	
	public int updateSvcAssetSwExport(Map params)throws SQLException;

	public int updateSvcAssetSwExportFinish(Map params)throws SQLException;
	
	public List<Map> selectSvcAssetSwInfoList(Map params) throws SQLException;
	
	public List<Map> selectSvcAssetSwList(Map params) throws SQLException;


	/**
	 * 'sg_supprotmanager 프로젝트 - 상세보고서'
	 */
    List<Map> selectAssetSwAuditReportNewMap(Map assetSwInfo);

	List<Map> selectAssetSwExportJobListbyReqcd(Map params);

    void deleteAssetSwExportJob(Map gAssetSwJob);
}
