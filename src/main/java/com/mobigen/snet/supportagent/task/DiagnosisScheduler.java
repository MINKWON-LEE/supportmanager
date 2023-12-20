/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.schedule
 * company : Mobigen
 * @author : Hyeon-sik Jung
 * created at : 2016. 4. 5.
 * description :
 */
package com.mobigen.snet.supportagent.task;

import com.igloosec.smartguard.next.agentmanager.memory.INMEMORYDB;
import com.mobigen.snet.supportagent.dao.manager.AssetMasterSgwDao;
import com.mobigen.snet.supportagent.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

/**
 * Manager → Support Manager 기능 이관
 * DiagnosisScheduler.diagnosisSchedule
 *   -> AgentManager 에서 실행
 */
//@EnableScheduling
//@Component
public class DiagnosisScheduler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired (required = false)
    private AssetMasterSgwDao dao;

    /**
     * 요청 대기인 대상을 Ready 큐로 등록 (미사용)
     */
//	@Scheduled(cron = "${snet.schedule.diagnosis}")
    public void diagnosisSchedule() {

        logger.info("*[diagnosisSchedule] start");

        try {

            List<String> queueJob = (List<String>) dao.selectDiagnosisJob();
            logger.info("*[diagnosisSchedule] queueJob : {}", queueJob);

            String[] queueDG;
            String queue_AssetCd;
            String queue_DGType;

            String[] scheduleDG;
            String schedule_AssetCd;
            String schedule_DGType;

            boolean isQueue = true;

            if(queueJob!=null){
                for(String queue : queueJob){

                    Iterator queueIter = INMEMORYDB.DIAGNOSISQUEUE.iterator();
                    while (queueIter.hasNext()){

                        queueDG = queueIter.next().toString().split("\\|");
                        queue_AssetCd = queueDG[1];
                        queue_DGType = queueDG[2];

                        scheduleDG = queue.split("\\|");
                        schedule_AssetCd = scheduleDG[1];
                        schedule_DGType = scheduleDG[2];

                        if (queue_AssetCd.equalsIgnoreCase(schedule_AssetCd) && queue_DGType.equalsIgnoreCase(schedule_DGType)) {
                            logger.info("*[diagnosisSchedule] : {},{}", schedule_AssetCd, " is Duplicate JOBQUEUE.");
                            isQueue = false;
                            break;
                        }
                    }

                    if (isQueue){
                        INMEMORYDB.DIAGNOSISQUEUE.put(queue);
                        logger.debug("*[diagnosisSchedule] Queueing Message : {}", queue);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("*[diagnosisSchedule] end");

    } // end method diagnosisSchedule
}
