package com.mobigen.snet.supportagent.service.scheduler;

import com.google.common.collect.Lists;
import com.igloosec.smartguard.next.agentmanager.entity.AssetMasterDBEntity;
import com.igloosec.smartguard.next.agentmanager.entity.AssetMasterStatusDBEntity;
import com.igloosec.smartguard.next.agentmanager.services.AbstractManager;
import com.mobigen.snet.supportagent.dao.manager.AssetMasterSgwDao;
import com.mobigen.snet.supportagent.exception.SnetException;
import com.mobigen.snet.supportagent.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("updateAssetMasterManager")
public class UpdateAssetMasterManager {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired (required = false)
    private AssetMasterSgwDao assetMasterSgwDao;

    @Autowired (required = false)
    private ThreadPoolTaskExecutor taskExecutor;

    @Transactional
    public void updateSgwStatus() throws SnetException {
        // AUDIT_CONFIG_SG in --> sgw_regi = 1

        List<String> updateAssetMasterSgwRegiInList = assetMasterSgwDao.selectAssetMasterSgwRegiIn();
        logger.info("*[updateSgw] updateAssetMasterSgwRegiInList size : {}", updateAssetMasterSgwRegiInList.size());

        if (updateAssetMasterSgwRegiInList.size() > 0) {
            assetMasterSgwDao.updateAssetMasterSgwRegiIn();
            logger.info("*[updateSgw] updateAssetMasterSgwRegiIn");

            // AUDIT_CONFIG_SG not in --> sgw_regi =0
            assetMasterSgwDao.updateAssetMasterSgwRegiNotIn();
            logger.info("*[updateSgw] updateAssetMasterSgwRegiNotIn");
        }

        logger.info("*[updateSgw] updateSgwStatus finished!!");
    } // end method updateSgwStatus

    @SuppressWarnings("unchecked")
    @Transactional
    public void taskUpdateAliveStatus() throws SnetException {

        List<AssetMasterStatusDBEntity> assetMasterList = (List<AssetMasterStatusDBEntity>) assetMasterSgwDao.selectAssetMasterAll();
        logger.info("*[updateAlive] assetMasterList: {}", assetMasterList);

        if(assetMasterList.size() > 0){
            int size = (int) Math.ceil(assetMasterList.size() / (double) 5);

            List<List<AssetMasterStatusDBEntity>> partitionList = Lists.partition(assetMasterList, size);

            List<String> updateAssetMasterSgwRegiInList = assetMasterSgwDao.selectAssetMasterSgwRegiIn();
            logger.info("*[updateAlive] updateAssetMasterSgwRegiInList: {}", updateAssetMasterSgwRegiInList);

            if (updateAssetMasterSgwRegiInList.size() > 0) {
                // AUDIT_CONFIG_SG에 없을 경우 alive_chk = 0
                int updCnt = assetMasterSgwDao.updateAssetMasterAliveChkNotIn();
                logger.info("*[updateAlive] updateAssetMasterAliveChkNotIn : {}", updCnt);

                // Multiple thread
                for (List<AssetMasterStatusDBEntity> entity : partitionList) {
                    // statusTaskManager.setAssetMasterList(entity);
                    taskExecutor.execute(new AliveCheckTask(entity));
                }

                  // asyc-timeout 이 default 100 (0.1 sec) 인데, 테스트할때 asyc-timeout 이 처리시간보다 짧으면 예외가 발생함
//                for (; ; ) {
//                    int count = taskExecutor.getActiveCount();
//                    // System.out.println("Active Threads : " + count);
//                    try {
//
////                        Thread.sleep(10);
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if (count == 0) {
//                        taskExecutor.shutdown();
//                        break;
//                    }
//                }
            }
            logger.info("*[taskUpdateAliveStatus] taskExecutor.execute success");
        }
    } // end method taskUpdateAliveStatus

    private class AliveCheckTask implements Runnable {

        private List<AssetMasterStatusDBEntity> assetMasterList;

        public AliveCheckTask(List<AssetMasterStatusDBEntity> assetMasterList) {
            this.assetMasterList = assetMasterList;
        }

        public void run() {
            List<AssetMasterDBEntity> updateList = new ArrayList<>();
            // AUDIT_CONFIG_SG에 있는 경우만 alive check
            for(AssetMasterStatusDBEntity entity : assetMasterList){
                AssetMasterDBEntity updateEntity = new AssetMasterDBEntity();

                boolean isAlive = false;
                updateEntity.setAssetCd(entity.getAssetCd());

                String ip = entity.getMasterIp();
                List<Integer> ports = new ArrayList<>();
                if(entity.getSsh()!=null)
                    ports.add(Integer.parseInt(entity.getSsh()));
                if(entity.getTelnet()!=null)
                    ports.add(Integer.parseInt(entity.getTelnet()));
                if(entity.getFtp()!=null)
                    ports.add(Integer.parseInt(entity.getFtp()));
                if(entity.getSftp()!=null)
                    ports.add(Integer.parseInt(entity.getSftp()));
                if(entity.getWindow()!=null)
                    ports.add(Integer.parseInt(entity.getWindow()));
                for(Integer port : ports){
                    if(CommonUtils.isOpenPort(ip, port)){
                        isAlive=true;
                        break;
                    }
                }

                if(isAlive){
                    if(entity.getAliveChk()!= 1){
                        updateEntity.setAliveChk(1);
                        updateList.add(updateEntity);
                        logger.info("is Alive Check asset_cd :: {} , check :: {}", entity.getAssetCd(), isAlive);
                    }
                }
                else{
                    if(entity.getAliveChk()!=0){
                        updateEntity.setAliveChk(0);
                        updateList.add(updateEntity);
                        logger.info("is Alive Check asset_cd :: {} , check :: {}", entity.getAssetCd(), isAlive);
                    }
                }
            }
            try {
                assetMasterSgwDao.updateAssetMasterAliveChk(updateList);
            } catch (SnetException e) {
                e.printStackTrace();
            }
        }
    } // end method AliveCheckTask
}
