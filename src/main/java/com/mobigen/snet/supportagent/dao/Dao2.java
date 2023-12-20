package com.mobigen.snet.supportagent.dao;

import java.util.List;

import com.mobigen.snet.supportagent.dao.table.model.SnetConfigWifiFirmwareEntity;
import org.apache.ibatis.annotations.Param;
import com.mobigen.snet.supportagent.entity.UserInfo;
import com.mobigen.snet.supportagent.dao.table.model.SnetConfigFemtoFirmwareEntity;

public interface Dao2 {

	public List<SnetConfigWifiFirmwareEntity> selectWifiFirmwareList();

	public String selectCountOfAssetMaster(@Param("hostNm")String hostNm, @Param("ipAddress")String ipAddress);

	public UserInfo selectUserInfo(  @Param("userId")String userId );

	public int deleteAllConfigFemtoAsset();

	public int insertIntoFemtoFirmware();

	/** SNET_CONFIG_FEMTO_FIRMWARE 의 use_yn을 Y로 update */
	public int updateUseYnInFemtoFirmware();

	/** SNET_CONFIG_FEMTO_FIRMWARE 의 use_yn을 N로 update */
	public int updateUseYnInFemtoFirmware2( );

	public int deleteAllConfigFemtoEnb();

	public List<SnetConfigFemtoFirmwareEntity> selectFemtoFirmwareList();
	
	
	
}
