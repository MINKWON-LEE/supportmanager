/*
 * Copyright ⓒ 2012 sktelecom Co.Ltd. All rights reserved
 *
 */
package com.sktelecom.mf.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 업무 그룹명 : gov.ktx.tnms.util
 * 서브 업무명 : TnmsPropertis.java
 *
 * </pre>
 * 작성자 : dark
 * 작성일 : 2013. 9. 6. 오전 10:51:11
 * 설명 : tnms propertis
 */
public class AuditPropertis {
    private static AuditPropertis inst = null;
    private static Properties pro;

    /**
     * @methodName : getPro
     * @description : get property
     *
     * @return
     */
    public Properties getPro(){
        return pro;
    }

    /**
     * @methodName : TnmsPropertis
     * @description : TnmsPropertis
     *
     */
    private AuditPropertis(){
        InputStream is = getClass().getResourceAsStream("/properties/audit.properties");
        pro = new Properties();
        try {
            pro.load(is);
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error(String.valueOf(e));
        }
    }

    /**
     * @methodName : getInstance
     * @description : TnmsPropertis getinstance (singleton)
     *
     * @return instance
     */
    public static AuditPropertis getInstance(){
        if(inst == null){
            inst = new AuditPropertis();
        }
        return inst;
    }
}
