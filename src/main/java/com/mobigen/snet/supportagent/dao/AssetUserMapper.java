package com.mobigen.snet.supportagent.dao;

import com.mobigen.snet.supportagent.models.SnetAssetUserDto;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 'sg_supprotmanager 프로젝트 - 스케줄러'
 */
@Repository
public interface AssetUserMapper {

    List<SnetAssetUserDto> getAssetUserFirstList(SnetAssetUserDto paramMap);

    List<SnetAssetUserDto> getAssetUserList(SnetAssetUserDto userParamDto);

    List<SnetAssetUserDto> getAssetUserAllList(Map userParamDto);
}
