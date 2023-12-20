package com.mobigen.snet.supportagent.dao.table.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.mobigen.snet.supportagent.dao.table.model.SnetAssetUserEntity;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_ASSET_USER
 *  - PK :   asset_cd, user_id, user_type
 *  - generated in : 2017-03-14 11:17
 * </pre>
 */
public interface SnetAssetUserDao {
    public int insert(SnetAssetUserEntity table);
    public int update(SnetAssetUserEntity table);
    public int updateIncludingNulls(SnetAssetUserEntity table);
    public int delete(   @Param("assetCd")String assetCd , @Param("userId")String userId , @Param("userType")String userType  );
    public SnetAssetUserEntity select(   @Param("assetCd")String assetCd , @Param("userId")String userId , @Param("userType")String userType  );


}
