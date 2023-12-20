package com.mobigen.snet.supportagent.task;

import com.mobigen.snet.supportagent.dao.was.SnetConfigMailSettingMapper;
import com.mobigen.snet.supportagent.models.was.SnetConfigMailSettingModel;
import com.mobigen.snet.supportagent.models.was.SnetEquipDelHistoryMainModel;
import com.mobigen.snet.supportagent.service.SnetServiceMailService;
import com.mobigen.snet.supportagent.dao.was.SnetEquipDelHistoryMainMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *  WAS → Support Manager 기능 이관
 *  SnetEquipDelHistorySendMailScheduler.departmentBatchJob
 *  SnetEqipDelHistorySendMailScheduler.equipBatchJob
 */
@Component
public class SnetEquipDelHistorySendMailScheduler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired (required = false)
    SnetEquipDelHistoryMainMapper snetEquipDelHistoryMainMapper;
    @Autowired (required = false)
    SnetServiceMailService snetServiceMailService;
    @Autowired (required = false)
    SnetConfigMailSettingMapper snetConfigMailSettingMapper;

    @Value("${equip.batch.job.cron.use}")
    private boolean equipBatchJobUse;

    @PostConstruct
    public void init() {


        equipBatchJob();
    }

    /**
     * 장비삭제이력 메일 발송 처리
     */
    @Scheduled(cron = "${equip.batch.job.cron}")
    public void equipBatchJob(){

        try {
            if(this.equipBatchJobUse) {
                logger.info("*[equipBatchJob] start");

                List<SnetEquipDelHistoryMainModel> userList = this.selectSnetEquipDelHistoryUserList();

                if (userList != null && userList.size() > 0) {

                    for (SnetEquipDelHistoryMainModel user : userList) {

                        List<SnetEquipDelHistoryMainModel> dataList = this.selectSnetEquipDelHistoryMainList(user.getUserId());

                        this.sendMail(user.getUserMail(), this.makeSendMsg(dataList));

                        Thread.sleep(1000);
                    }
                } else {

                    logger.info("*[equipBatchJob] userList size : 0");
                }

                logger.info("*[equipBatchJob] end");
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    } // end method equipBatchJob

    private void sendMail(String toUserMail, String sendMsg) {
        try {
            HashMap< String , Object > mailSettingParam = new HashMap < String , Object > ( );
            mailSettingParam.put ( "diagnosisPartId" , "EQUIP_DEL_HISTORY" );
            SnetConfigMailSettingModel mailSettingResult = snetConfigMailSettingMapper.selectSnetConfigMailSettingDetail ( mailSettingParam );
            logger.info("*[equipBatchJob] selectSnetConfigMailSettingDetail : {}", mailSettingResult);

            HashMap < String , Object > mailParam = new HashMap < String , Object >();
            mailParam.put ( "svcCd" , 	 	 "");
            mailParam.put ( "toUserMail" ,   toUserMail); // toUserMail
            mailParam.put ( "ccUserMail" , 	 "");
            mailParam.put ( "mailTitle" , 	 "장비삭제이력");
            mailParam.put ( "sendMsg" , 	 sendMsg);
            mailParam.put ( "sendUserNm" ,   mailSettingResult.getPlNm());
            mailParam.put ( "sendUserId" ,   mailSettingResult.getPlId());
            mailParam.put ( "sendUserMail" , mailSettingResult.getPlMailInfo()); //fromMail
            mailParam.put ( "fileList" , "");

            snetServiceMailService.sendServiceSendMail(mailParam, null);
        } catch (Exception e) {

        }
    } // end method sendMail

    private String yesterdayYYYYMMDD() {
        Calendar c1 = new GregorianCalendar();
        c1.add(Calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String d = sdf.format(c1.getTime());

        return d;
    } // end method yesterdayYYYYMMDD

    /** 운용담당자 조회 */
    private List<SnetEquipDelHistoryMainModel> selectSnetEquipDelHistoryUserList() {

        List<SnetEquipDelHistoryMainModel> result = new ArrayList<SnetEquipDelHistoryMainModel>();

        try {

            HashMap<String, Object> param = new HashMap<String, Object>();
            param.put("snetDay", this.yesterdayYYYYMMDD());
            result = snetEquipDelHistoryMainMapper.selectSnetEquipDelHistoryUserList(param);
            logger.info("*[equipBatchJob] selectSnetEquipDelHistoryUserList size : {}", result.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    } // end method selectSnetEquipDelHistoryUserList

    /** 운용담당자의 장비삭제이력 조회 */
    private List<SnetEquipDelHistoryMainModel> selectSnetEquipDelHistoryMainList(String userId) {

        List<SnetEquipDelHistoryMainModel> result = new ArrayList<SnetEquipDelHistoryMainModel>();

        try {
            HashMap<String, Object> param = new HashMap<String, Object>();
            param.put("snetSDay", this.yesterdayYYYYMMDD());
            param.put("snetEDay", this.yesterdayYYYYMMDD());
            param.put("userId", userId);

            result = snetEquipDelHistoryMainMapper.selectSnetEquipDelHistoryMainList(param);
            logger.info("*[equipBatchJob] selectSnetEquipDelHistoryMainList result size : {}", result.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    } // end method selectSnetEquipDelHistoryMainList

    private String makeSendMsg(List<SnetEquipDelHistoryMainModel> dataList) {
        StringBuffer sendMsg = new StringBuffer();

        sendMsg.append("<p><strong>장비삭제이력</strong></p>");
        sendMsg.append("<table");
        sendMsg.append("	<tbody>");
        sendMsg.append("		<tr>");
        sendMsg.append("			<th style='text-align: center; background-color: #999999;'><strong>장비코드</strong></th>");
        sendMsg.append("			<th style='text-align: center; background-color: #999999;'><strong>부서</strong></th>");
        sendMsg.append("			<th style='text-align: center; background-color: #999999;'><strong>운용팀</strong></th>");
        sendMsg.append("			<th style='text-align: center; background-color: #999999;'><strong>운용담당자</strong></th>");
        sendMsg.append("			<th style='text-align: center; background-color: #999999;'><strong>Host명</strong></th>");
        sendMsg.append("			<th style='text-align: center; background-color: #999999;'><strong>IP</strong></th>");
        sendMsg.append("			<th style='text-align: center; background-color: #999999;'><strong>제품</strong></th>");
        sendMsg.append("			<th style='text-align: center; background-color: #999999;'><strong>장비등록</strong></th>");
        sendMsg.append("			<th style='text-align: center; background-color: #999999;'><strong>장비삭제</strong></th>");
        sendMsg.append("			<th style='text-align: center; background-color: #999999;'><strong>삭제자</strong></th>");
        sendMsg.append("			<th style='text-align: center; background-color: #999999;'><strong>삭제사유</strong></th>");
        sendMsg.append("		</tr>");

        for (SnetEquipDelHistoryMainModel data : dataList) {
            sendMsg.append("		<tr>");
            sendMsg.append("			<td style='text-align: center;'>" + data.getAssetCdView() + "</td>");
            sendMsg.append("			<td style='text-align: center;'>" + data.getBranchNm() + "</td>");
            sendMsg.append("			<td style='text-align: center;'>" + data.getTeamNm() + "</td>");
            sendMsg.append("			<td style='text-align: center;'>" + data.getUserNm() + "</td>");
            sendMsg.append("			<td style='text-align: center;'>" + data.getHostNm() + "</td>");
            sendMsg.append("			<td style='text-align: center;'>" + data.getIpAddress() + "</td>");
            sendMsg.append("			<td style='text-align: center;'>" + data.getSwNm() + "</td>");
            sendMsg.append("			<td style='text-align: center;'>" + data.getCreateDateView() + "</td>");
            sendMsg.append("			<td style='text-align: center;'>" + data.getDelDateView() + "</td>");
            sendMsg.append("			<td style='text-align: center;'>" + data.getDelUserNm() + "</td>");
            sendMsg.append("			<td style='text-align: center;word-break:break-all'>" + data.getDelDesc() + "</td>");
            sendMsg.append("		</tr>");
        }
        sendMsg.append("	</tbody>");
        sendMsg.append("</table>");

        return sendMsg.toString();
    } // end method makeSendMsg
}
