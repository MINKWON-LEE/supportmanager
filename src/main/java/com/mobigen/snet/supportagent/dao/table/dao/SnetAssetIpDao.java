package com.mobigen.snet.supportagent.dao.table.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.mobigen.snet.supportagent.dao.table.model.SnetAssetIpEntity;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_ASSET_IP
 *  - PK :   asset_cd, ip_address
 *  - generated in : 2017-03-14 11:17
 * </pre>
 */
public interface SnetAssetIpDao {
    public int insert(SnetAssetIpEntity table);
    public int update(SnetAssetIpEntity table);
    public int updateIncludingNulls(SnetAssetIpEntity table);
    public int delete(   @Param("assetCd")String assetCd , @Param("ipAddress")String ipAddress  );
    public SnetAssetIpEntity select(   @Param("assetCd")String assetCd , @Param("ipAddress")String ipAddress  );


}
