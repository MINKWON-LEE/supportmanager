package com.mobigen.snet.supportagent.task;

import com.mobigen.snet.supportagent.service.scheduler.UpdateAssetMasterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Manager → Support Manager 기능 이관
 * UpdateAssetMasterManager.updateSgw
 *
 * Manager → Support Manager 기능 이관
 * UpdateAssetMasterManager.updateAlive
 */
@Component
public class UpdateAssetUpdateScheduler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired (required = false)
    private UpdateAssetMasterManager updateAssetMasterManager;
    @Value("${snet.schedule.updatesgw.use}")
    private boolean updateSgwUse;

    /**
     * AUDIT_CONFIG_SG 테이블에 등록된 ASSET_CD 대상으로
     * SNET_ASSET_MASER 테이블 SGW_REGI=1 OR 0 으로 업데이트
     */
    @Scheduled(cron = "${snet.schedule.updatesgw}")
    public void updateSgw(){
        try {
            if(this.updateSgwUse) {
                logger.info("*[updateSgw] start");

                updateAssetMasterManager.updateSgwStatus();

                logger.info("*[updateSgw] end");
            }
        } catch (Exception e) {
            logger.error("Update SGW Exception :: {}", e.getMessage(), e.fillInStackTrace());
        }
    } // end method updateSgw

    /**
     * AUDIT_CONFIG_SG 테이블에 등록되지 않은 ASSET_CD 대상으로 alive_chk = 0
     * 이후 등록정보로 장비 접속 체크후 alive_chk = 1 로 업데이트
     */
    /*@Scheduled(cron = "${snet.schedule.updatealive}")
    public void updateAlive(){
        try {
            if(this.updateAliveUse) {
                logger.info("*[updateAlive] start");

                updateAssetMasterManager.taskUpdateAliveStatus();

                logger.info("*[updateAlive] end");
            }
        } catch (Exception e) {
            logger.error("Update Alive check Exception :: {}", e.getMessage(), e.fillInStackTrace());
        }
    } // end method updateAlive*/
}
