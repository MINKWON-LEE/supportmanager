package com.mobigen.snet.supportagent.dao.was;

import com.mobigen.snet.supportagent.models.was.SnetConfigMailSettingModel;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository("snetConfigMailSettingMapper")
public interface SnetConfigMailSettingMapper {

    public SnetConfigMailSettingModel selectSnetConfigMailSettingDetail(HashMap< String , Object > param ) throws Exception;
}
