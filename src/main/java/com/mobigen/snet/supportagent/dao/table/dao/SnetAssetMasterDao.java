package com.mobigen.snet.supportagent.dao.table.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.mobigen.snet.supportagent.dao.table.model.SnetAssetMasterEntity;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_ASSET_MASTER
 *  - PK :   asset_cd
 *  - generated in : 2017-03-14 11:17
 * </pre>
 */
public interface SnetAssetMasterDao {
    public int insert(SnetAssetMasterEntity table);
    public int update(SnetAssetMasterEntity table);
    public int updateIncludingNulls(SnetAssetMasterEntity table);
    public int delete(   @Param("assetCd")String assetCd  );
    public SnetAssetMasterEntity select(   @Param("assetCd")String assetCd  );


}
