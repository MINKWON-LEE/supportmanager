package com.mobigen.snet.supportagent.dao.table.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.mobigen.snet.supportagent.dao.table.model.SnetConfigWifiFirmwareEntity;

/**
 * <pre>
 *  - 테이블 설명 : 
 *  - 테이블 명 : SNET_CONFIG_WIFI_FIRMWARE
 *  - PK :   company_id, firmware_ver
 *  - generated in : 2017-03-14 11:44
 * </pre>
 */
public interface SnetConfigWifiFirmwareDao {
    public int insert(SnetConfigWifiFirmwareEntity table);
    public int update(SnetConfigWifiFirmwareEntity table);
    public int updateIncludingNulls(SnetConfigWifiFirmwareEntity table);
    public int delete(   @Param("companyId")String companyId , @Param("firmwareVer")String firmwareVer  );
    public SnetConfigWifiFirmwareEntity select(   @Param("companyId")String companyId , @Param("firmwareVer")String firmwareVer  );


}
