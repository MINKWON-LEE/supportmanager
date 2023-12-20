package com.mobigen.snet.supportagent.dao.table.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.mobigen.snet.supportagent.dao.table.model.SnetConnectMasterEntity;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_CONNECT_MASTER
 *  - PK :   asset_cd
 *  - generated in : 2017-03-14 11:17
 * </pre>
 */
public interface SnetConnectMasterDao {
    public int insert(SnetConnectMasterEntity table);
    public int update(SnetConnectMasterEntity table);
    public int updateIncludingNulls(SnetConnectMasterEntity table);
    public int delete(   @Param("assetCd")String assetCd  );
    public SnetConnectMasterEntity select(   @Param("assetCd")String assetCd  );


}
