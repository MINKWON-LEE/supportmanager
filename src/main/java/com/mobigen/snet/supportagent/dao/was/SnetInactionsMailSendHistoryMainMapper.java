package com.mobigen.snet.supportagent.dao.was;

import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository("snetInactionsMailSendHistoryMainMapper")
public interface SnetInactionsMailSendHistoryMainMapper {

    public void insertInactionsMailHis(HashMap<String, Object> param) throws Exception;
}
