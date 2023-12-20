package com.mobigen.snet.supportagent.dao.was;

import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository("SnetServiceMailMapper")
public interface SnetServiceMailMapper {

    public void insertSnetServiceMailNewReg ( HashMap< String , Object > param ) throws Exception;
    public void insertSnetServiceMailUserNewReg ( HashMap < String , Object > param ) throws Exception;
}
