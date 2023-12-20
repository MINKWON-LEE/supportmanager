package com.mobigen.snet.supportagent.service;

import com.mobigen.snet.supportagent.dao.was.SnetInactionsMailSendHistoryMainMapper;
import com.sktelecom.framework.result.ResultSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("snetInactionsMailSendHistoryMainService")
@Slf4j
public class SnetInactionsMailSendHistoryMainService {

    @Autowired
    SnetInactionsMailSendHistoryMainMapper snetInactionsMailSendHistoryMainMapper;

    /**
     * 미조히메일발송 이력등록
     *
     * @param param
     * @return
     * @throws Exception
     */
    public ResultSet insertInactionsMailHis (HashMap< String , Object > param ) throws Exception {
        ResultSet rs = new ResultSet();
        try{
            snetInactionsMailSendHistoryMainMapper.insertInactionsMailHis ( param );
            log.info("*[sendServiceSendMail] : {}", "insertInactionsMailHis");
            rs.setData(1);
        }
        catch ( Exception e) {
            log.error("Error" , e);
        }

        return rs;
    }
}
