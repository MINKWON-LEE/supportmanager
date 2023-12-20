package com.mobigen.snet.supportagent.service;

import com.mobigen.snet.supportagent.dao.DaoMapper;
import com.mobigen.snet.supportagent.entity.FireWallEntity;
import com.mobigen.snet.supportagent.entity.FireWallRateEntity;
import com.mobigen.snet.supportagent.entity.ITGOEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by osujin12 on 2017. 1. 12..
 */

@Service
public class FireWallConfigServiceImpl  extends AbstractService implements FireWallConfigService {

    @Autowired
    private DaoMapper daoMapper;

    @Override
    public void dataInsert() throws CloneNotSupportedException {
        List<FireWallEntity> list = daoMapper.selectFireWallConfig();
        HashMap<String,String> srcPara = new HashMap<>();
        HashMap<String,String> dstPara = new HashMap<>();

        HashMap<String,String> chkIpList = new HashMap<>();

        for(FireWallEntity fireWallEntity : list){

            FireWallRateEntity entity = new FireWallRateEntity();
            entity.setFirewallReqNum(fireWallEntity.getFirewallReqNum());

            if (!chkIpList.containsKey(fireWallEntity.getSrcFwIpAddress())){
                /**
                 * Src 처리
                 */

                chkIpList.put(fireWallEntity.getSrcFwIpAddress(),"");

                entity.setIpType("SRC");
                srcPara.put("hostNm",fireWallEntity.getSrcHostNm());
                srcPara.put("ipAddr",fireWallEntity.getSrcFwIpAddress());

                fireWallRateDataMake(srcPara,entity,fireWallEntity);
            }

            if (!chkIpList.containsKey(fireWallEntity.getDestFwIpAddress())){
                /**
                 * Dst 처리
                 */

                chkIpList.put(fireWallEntity.getDestFwIpAddress(),"");

                entity.setIpType("DEST");
                dstPara.put("hostNm",fireWallEntity.getDestHostNm());
                dstPara.put("ipAddr",fireWallEntity.getDestFwIpAddress());

                fireWallRateDataMake(dstPara,entity,fireWallEntity);
            }

        }

        //ITGO 데이터 추가
        List<ITGOEntity> itgoList = daoMapper.selectITGOconfig();

        list.size();

        for(ITGOEntity itgoEntity : itgoList){
            daoMapper.updateServiceMaster(itgoEntity);
            daoMapper.updateITGOconfig(itgoEntity);
        }

    }


    /**
     * Src , Dst 공통 method
     * @param fireWallPara
     * @param entity
     * @param fireWallEntity
     * @return
     * @throws CloneNotSupportedException
     */
    private void fireWallRateDataMake(@SuppressWarnings("rawtypes") HashMap fireWallPara, FireWallRateEntity entity , FireWallEntity fireWallEntity) throws CloneNotSupportedException {


        List<FireWallRateEntity> fireWallRateEntities = new ArrayList<>();

//          1. host,ip로 조회
        FireWallRateEntity entityIpHost = (FireWallRateEntity) entity.clone();
        entityIpHost.setHostNm(fireWallPara.get("hostNm").toString());
        entityIpHost.setIpAddress(fireWallPara.get("ipAddr").toString());

        List<FireWallRateEntity> fireWallRateEntitiesHostIp = daoMapper.selectHostIpAsset(entityIpHost);

        List<FireWallRateEntity> fireWallAuditRateList_HOST = null;
        List<FireWallRateEntity> fireWallAuditRateList_IP = null;

        if (fireWallRateEntitiesHostIp.size() == 0) {
            entity.setIpAddress(entityIpHost.getIpAddress());
            entity.setHostNm(entityIpHost.getHostNm());
            entity.setSvcNm("");
            entity.setAuditRate("0");
            entity.setAuditDate("");
            entity.setReqTeam(fireWallEntity.getReqTeam());
            entity.setReqUser(fireWallEntity.getReqUser());
            entity.setBranchNm("-");
            entity.setTeamNm("-");
            entity.setUserNm("-");
            entity.setCheckType("요청정보");

            fireWallRateEntities.add(entity);

//              2. ip로 조회
            FireWallRateEntity entityIp = (FireWallRateEntity) entity.clone();

            entityIp.setIpAddress(fireWallPara.get("ipAddr").toString());
            entityIp.setCheckType("IP주소 일치");
            fireWallAuditRateList_IP = daoMapper.selectIpAsset(entityIp);


            for (FireWallRateEntity list_ip : fireWallAuditRateList_IP) {
                fireWallRateEntities.add(list_ip);
            }


//              3. host로 조회
            FireWallRateEntity entityHost = (FireWallRateEntity) entity.clone();

            entityHost.setHostNm(fireWallPara.get("hostNm").toString());
            entityHost.setCheckType("Host 정보 일치");
            if (!fireWallPara.get("hostNm").toString().equals("-")){
                fireWallAuditRateList_HOST = daoMapper.selectHostAsset(entityHost);

                for (FireWallRateEntity list_host : fireWallAuditRateList_HOST) {
                    fireWallRateEntities.add(list_host);
                }
            }
        }else{
            //host , ip 로 일치하는 정보가 있는경우

            for(FireWallRateEntity list : fireWallRateEntitiesHostIp){
                list.setReqTeam(fireWallEntity.getReqTeam());
                list.setReqUser(fireWallEntity.getReqUser());
                list.setCheckType("요청정보");
                fireWallRateEntities.add(list);
            }
        }

        for(FireWallRateEntity firewallRateSet : fireWallRateEntities){
            daoMapper.insertFireWallRateData(firewallRateSet);
        }

    }
}
