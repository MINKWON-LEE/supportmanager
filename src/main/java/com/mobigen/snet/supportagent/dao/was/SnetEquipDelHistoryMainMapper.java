package com.mobigen.snet.supportagent.dao.was;

import com.mobigen.snet.supportagent.models.was.SnetEquipDelHistoryMainModel;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository("snetEquipDelHistoryMainMapper")
public interface SnetEquipDelHistoryMainMapper {

    public List<SnetEquipDelHistoryMainModel> selectSnetEquipDelHistoryUserList (HashMap< String , Object > param ) throws Exception;
    public List < SnetEquipDelHistoryMainModel > selectSnetEquipDelHistoryMainList ( HashMap < String , Object > param ) throws Exception;
}
