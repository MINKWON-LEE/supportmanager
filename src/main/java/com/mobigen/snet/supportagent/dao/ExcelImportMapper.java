/**
 * project : SupportAgent
 * program name : com.mobigen.snet.supportagent.dao
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 7. 20.
 * description : 
 */
package com.mobigen.snet.supportagent.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.annotation.MapperScan;

/**
 * @author Hyeon-sik Jung
 *
 */
@MapperScan
@SuppressWarnings("rawtypes")
public interface ExcelImportMapper {

	List<Map> selectAuditAssetIp() throws SQLException;
	Map selectAssetCd(Map params) throws SQLException;
	
	int insertAssetCdToMaster(Map params) throws SQLException;
	
	/**
	 * insertAssetCdToMaster == 0 
	 * @param params
	 * @return
	 */
	int insertAssetCdToIp(Map params) throws SQLException;
	
	int insertAuditSwReport(Map params) throws SQLException;
	int insertAuditSwHistory(Map params) throws SQLException;
	List<Map> selectUploadFileList() throws SQLException;
	List<Map> selectZipFileList() throws SQLException;
	int insertZipFileInfo(Map params) throws SQLException;
	int updateUploadFileStart(Map params) throws SQLException;
	int updateUploadFileEnd(Map params) throws SQLException;
	int updateZipFileStart(Map params) throws SQLException;
	int updateZipFileEnd(Map params) throws SQLException;
	Map selectAssetExcelResult(Map params)throws SQLException;

	int insertAssetExcelResult(Map params) throws SQLException;
	int updateAssetExcelResult(Map params) throws SQLException;
	Map selectAuditSwDay(Map params)throws SQLException;

	int insertAuditSwDay(Map params) throws SQLException;
	int updateAuditSwDay(Map params)throws SQLException;

	int deleteAssetSwReport(Map params) throws SQLException;
	int deleteAssetSwHistory(Map params) throws SQLException;
	int deleteAssetSwDay(Map params) throws SQLException;
	List<Map> selectSwZipFileList(Map params) throws SQLException;
	int deleteSwZipFileList(Map params) throws SQLException;
	int deleteAssetExcelResult(Map params) throws SQLException;
	List<Map> selectAssetExcelResultByFile(Map params) throws SQLException;
	int updateAssetExcelResultByFile(Map params)throws SQLException;

	Map selectAssetRateData(Map params) throws SQLException;
	int updateAssetRateData(Map params)throws SQLException;

	Map selectSnetAssetMasterTot(String assetCd) throws SQLException;
	int updateAssetMaster(Map params) throws SQLException;
	List<Map> selectAuditItems(Map params) throws SQLException;

}
