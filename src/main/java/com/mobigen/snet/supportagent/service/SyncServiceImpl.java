package com.mobigen.snet.supportagent.service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mobigen.snet.supportagent.memory.GLOBALVAR;
import com.mobigen.snet.supportagent.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Project : SupportAgent
 * Package : com.mobigen.snet.supportagent.service
 * Company : Igloosec
 * File    : SyncServiceImpl.java
 *
 * @author ChangMyung Lee
 * @Date   2018. 2. 1.
 * Description :
 *
 */

@Service
public class SyncServiceImpl extends AbstractService implements SynService {

    @Override
    public void itgoSync() {

        String savepath = "/usr/local/snetManager/conf";

        logger.info("SyncService Start!!");

        String[] runCmd  = { "/bin/sh", "-c", GLOBALVAR.CONF_DIR+"/ITGO_SYNC.sh "+ savepath};

        ArrayList<String> managerResult = CommonUtils.getRunProcess(runCmd);
        for(String msg: managerResult){
            logger.debug("RUN SCRIPT RESULT :: {}", msg);
        }
        logger.info("SyncService End!!");
    }
}
