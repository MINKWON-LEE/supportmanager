package com.mobigen.snet.supportagent.service;

import com.mobigen.snet.supportagent.dao.was.SnetServiceMailMapper;
import com.sktelecom.framework.result.ResultSet;
import com.sktelecom.mf.common.util.SocketClient;
import com.mobigen.snet.supportagent.dao.was.SnetNewDiagnosisMainMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service("snetServiceMailService")
@Slf4j
public class SnetServiceMailService {

    @Autowired
    SnetServiceMailMapper snetServiceMailMapper;
    @Autowired
    SnetNewDiagnosisMainMapper snetNewDiagnosisMainMapper;
    @Autowired
    SnetInactionsMailSendHistoryMainService snetInactionsMailSendHistoryMainService;

    public ResultSet sendServiceSendMail(HashMap<String, Object> param, List selectedItemArray) throws Exception {

        log.info("*[sendServiceSendMail] param : {}", param);

        ResultSet rs = new ResultSet();

        try {
            String mailCd = snetNewDiagnosisMainMapper.makeMailCd ( );
            log.info("*[equipBatchJob] makeMailCd : {}", mailCd);

            HashMap < String , Object > mailParam = new HashMap < String , Object > ( );

            mailParam.put ( "mailCd" , mailCd );
            mailParam.put ( "svcCd" , 	 	param.get("svcCd"));
            mailParam.put ( "fromMail" , 	param.get("sendUserMail"));
            mailParam.put ( "ccMail" , 	 	param.get("ccUserMail"));
            mailParam.put ( "mailTitle" , 	param.get("mailTitle") );
            mailParam.put ( "sendMsg" , 	param.get("sendMsg") );
            mailParam.put ( "userNm" , 		param.get("sendUserNm") );
            mailParam.put ( "userId" , 		param.get("sendUserId") );
            mailParam.put ( "userMail" , 	param.get("toUserMail") );
            if( param.get("fileList").toString().length() > 0 ) {
                mailParam.put ( "fileList" , 	param.get("fileList") );
            }
            snetServiceMailMapper.insertSnetServiceMailNewReg ( mailParam );
            log.info("*[equipBatchJob] insertSnetServiceMailNewReg");
            snetServiceMailMapper.insertSnetServiceMailUserNewReg ( mailParam );
            log.info("*[equipBatchJob] insertSnetServiceMailUserNewReg");

            //장기 미조치일 경우 이력 생성
            if(param.get("svcCd").equals("LongUnacted")){
                Date date = new Date();
                SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMdd");

                for ( Object object : selectedItemArray ) {
                    HashMap<String, Object> asObject =  (HashMap<String, Object>)object;
                    HashMap < String , Object > swParam = new HashMap < String , Object > ( );

                    swParam.put("assetCd", String.valueOf(asObject.get("assetCd")) );
                    swParam.put("swType", String.valueOf(asObject.get("swType")) );
                    swParam.put("swNm", String.valueOf(asObject.get("swNm")) );
                    swParam.put("swDir", String.valueOf(asObject.get("swDir")) );
                    swParam.put("swUser", String.valueOf(asObject.get("swUser")) );
                    swParam.put("swEtc", String.valueOf(asObject.get("swEtc")) );
                    swParam.put("swInfo", String.valueOf(asObject.get("swInfo")) );
                    swParam.put("mailSendDay" , transFormat.format(date));
                    swParam.put("mailTitle", param.get("mailTitle") );
                    swParam.put("toUserNm", param.get("sendUserNm") );

                    snetInactionsMailSendHistoryMainService.insertInactionsMailHis(swParam);
                    log.info("*[equipBatchJob] insertInactionsMailHis");
                }
            }

            String socketStr = "MAIL|" + mailCd;
            SocketClient socketClient = new SocketClient(socketStr);
            socketClient.setExceptionPort("ServerExcelPort");
            socketClient.setNotify();
            rs.setData(1);
        } catch (Exception e) {
            log.error("Error", e);
        }
        return rs;
    }
}
