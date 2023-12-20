package com.mobigen.snet.supportagent.dao.manager;

import com.igloosec.smartguard.next.agentmanager.entity.AssetMasterDBEntity;
import com.igloosec.smartguard.next.agentmanager.exception.SnetCommonErrCode;
import com.mobigen.snet.supportagent.exception.SnetException;
import com.mobigen.snet.supportagent.utils.CommonUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AssetMasterSgwDao {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SqlSessionTemplate sqlSession;

    public int updateAssetMasterSgwRegiIn() throws SnetException {
        int cnt = 0;
        try {
            cnt = sqlSession.update("updateAssetMasterSgwRegiIn");
        } catch (Exception e) {
            logger.error(CommonUtils.printError(e));
            throw new SnetException(SnetCommonErrCode.ERR_0026.getMessage());
        }
        return cnt;
    } // end method updateAssetMasterSgwRegiIn

    public int updateAssetMasterSgwRegiNotIn() throws SnetException {
        int cnt = 0;
        try {
            cnt = sqlSession.update("updateAssetMasterSgwRegiNotIn");
        } catch (Exception e) {
            logger.error(CommonUtils.printError(e));
            throw new SnetException(SnetCommonErrCode.ERR_0026.getMessage());
        }
        return cnt;
    } // end method updateAssetMasterSgwRegiNotIn

    public List<?> selectAssetMasterAll() throws SnetException {
        try {
            return sqlSession.selectList("selectAssetMasterAll");
        } catch (Exception e) {
            logger.error(CommonUtils.printError(e));
            throw new SnetException(SnetCommonErrCode.ERR_0026.getMessage());
        }
    } // end method selectAssetMasterAll

    public int updateAssetMasterAliveChkNotIn() throws SnetException {
        int cnt = 0;
        try {
            cnt = sqlSession.update("updateAssetMasterAliveChkNotIn");

        } catch (Exception e) {
            logger.error(CommonUtils.printError(e));
            throw new SnetException(SnetCommonErrCode.ERR_0026.getMessage());
        }
        return cnt;
    } // end method updateAssetMasterAliveChkNotIn

    public void updateAssetMasterAliveChk(List<AssetMasterDBEntity> assetMasterDBEntityList) throws SnetException {
        try {
            for (AssetMasterDBEntity assetMasterDBEntity : assetMasterDBEntityList)
                sqlSession.update("updateAssetMasterAliveChk", assetMasterDBEntity);
        } catch (Exception e) {
            logger.error(CommonUtils.printError(e));
            throw new SnetException(SnetCommonErrCode.ERR_0026.getMessage());
        }
    } // end method updateAssetMasterAliveChk

    public List<?> selectDiagnosisJob() throws SnetException {
        try {
            return sqlSession.selectList("selectDiagnosisJob");
        } catch (Exception e) {
            logger.error(CommonUtils.printError(e));
            throw new SnetException(SnetCommonErrCode.ERR_0026.getMessage());
        }
    } // end method selectDiagnosisJob

    public List<String> selectAssetMasterSgwRegiIn() throws SnetException {

        try {
            return sqlSession.selectList("selectAssetMasterSgwRegiIn");
        } catch (Exception e) {
            logger.error(CommonUtils.printError(e));
            throw new SnetException(SnetCommonErrCode.ERR_0026.getMessage());
        }
    }
}
