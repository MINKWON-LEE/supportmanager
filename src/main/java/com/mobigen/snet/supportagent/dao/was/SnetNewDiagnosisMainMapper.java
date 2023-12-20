package com.mobigen.snet.supportagent.dao.was;

import org.springframework.stereotype.Repository;

@Repository("snetNewDiagnosisMainMapper")
public interface SnetNewDiagnosisMainMapper {

    public String makeMailCd ( ) throws Exception;
}
