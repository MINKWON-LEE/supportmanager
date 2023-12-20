package com.mobigen.snet.supportagent.entity;

/**
 * Created by osujin12 on 2017. 1. 12..
 */
public class FireWallAuditRateEntity {
    String auditRate;
    String hostNm;


    public String getHostNm() {
        return hostNm;
    }

    public void setHostNm(String hostNm) {
        this.hostNm = hostNm;
    }

    public String getAuditRate() {
        return auditRate;
    }

    public void setAuditRate(String auditRate) {
        this.auditRate = auditRate;
    }
}
