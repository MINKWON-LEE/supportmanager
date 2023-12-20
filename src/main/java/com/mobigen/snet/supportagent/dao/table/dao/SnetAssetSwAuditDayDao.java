package com.mobigen.snet.supportagent.dao.table.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.mobigen.snet.supportagent.dao.table.model.SnetAssetSwAuditDayEntity;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_ASSET_SW_AUDIT_DAY
 *  - PK :   asset_cd, sw_type, sw_nm, sw_info, sw_dir, sw_user, sw_etc
 *  - generated in : 2017-03-14 11:17
 * </pre>
 */
public interface SnetAssetSwAuditDayDao {
    public int insert(SnetAssetSwAuditDayEntity table);
    public int update(SnetAssetSwAuditDayEntity table);
    public int updateIncludingNulls(SnetAssetSwAuditDayEntity table);
    public int delete(   @Param("assetCd")String assetCd , @Param("swType")String swType , @Param("swNm")String swNm , @Param("swInfo")String swInfo , @Param("swDir")String swDir , @Param("swUser")String swUser , @Param("swEtc")String swEtc  );
    public SnetAssetSwAuditDayEntity select(   @Param("assetCd")String assetCd , @Param("swType")String swType , @Param("swNm")String swNm , @Param("swInfo")String swInfo , @Param("swDir")String swDir , @Param("swUser")String swUser , @Param("swEtc")String swEtc  );


}
