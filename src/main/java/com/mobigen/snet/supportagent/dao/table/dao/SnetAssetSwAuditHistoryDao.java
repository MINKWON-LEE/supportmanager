package com.mobigen.snet.supportagent.dao.table.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.mobigen.snet.supportagent.dao.table.model.SnetAssetSwAuditHistoryEntity;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_ASSET_SW_AUDIT_HISTORY
 *  - PK :   asset_cd, sw_type, sw_nm, sw_info, sw_dir, sw_user, sw_etc, audit_day
 *  - generated in : 2017-03-14 11:17
 * </pre>
 */
public interface SnetAssetSwAuditHistoryDao {
    public int insert(SnetAssetSwAuditHistoryEntity table);
    public int update(SnetAssetSwAuditHistoryEntity table);
    public int updateIncludingNulls(SnetAssetSwAuditHistoryEntity table);
    public int delete(   @Param("assetCd")String assetCd , @Param("swType")String swType , @Param("swNm")String swNm , @Param("swInfo")String swInfo , @Param("swDir")String swDir , @Param("swUser")String swUser , @Param("swEtc")String swEtc , @Param("auditDay")String auditDay  );
    public SnetAssetSwAuditHistoryEntity select(   @Param("assetCd")String assetCd , @Param("swType")String swType , @Param("swNm")String swNm , @Param("swInfo")String swInfo , @Param("swDir")String swDir , @Param("swUser")String swUser , @Param("swEtc")String swEtc , @Param("auditDay")String auditDay  );


}
