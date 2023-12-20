/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.memory.INMEMORYDB.java
 * company : Mobigen
 * @author : Je Joong Lee
 * created at : 2016. 2. 3.
 * description :
 */

package com.igloosec.smartguard.next.agentmanager.memory;


import com.igloosec.smartguard.next.agentmanager.entity.JobEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class INMEMORYDB {

    Logger logger = LoggerFactory.getLogger(getClass());

    public static String MONITER_PORT = "9876";
    public static String LISTENER_PORT = "10225";
    public static String AGENT_PORT = "10226";
    public static String RELAY_PORT = "10224";
    public static ConcurrentHashMap<String, JobEntity> jobEntityMap;
    public static LinkedBlockingDeque<String> DIAGNOSISQUEUE;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void init(){
        jobEntityMap = new ConcurrentHashMap<String, JobEntity>();
        DIAGNOSISQUEUE = new LinkedBlockingDeque();
    }
}
