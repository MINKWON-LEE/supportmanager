package com.mobigen.snet.supportagent.entity;

/**
 * Created by osujin12 on 2017. 1. 12..
 */
public class FireWallRateEntity implements Cloneable{

    String firewallReqNum;
    String ipType;
    String ipAddress;
    String hostNm;
    String svcNm;
    String auditRate;
    String auditDate;
    String reqTeam;
    String reqUser;
    String branchNm;
    String teamNm;
    String userNm;
    String checkType;

    @Override
    public Object clone() throws CloneNotSupportedException {
        Object obj = null;
        try {
            obj = super.clone();
        } catch (Exception e) {
        }
        return obj;
    }

    public String getFirewallReqNum() {
        return firewallReqNum;
    }

    public void setFirewallReqNum(String firewallReqNum) {
        this.firewallReqNum = firewallReqNum;
    }

    public String getIpType() {
        return ipType;
    }

    public void setIpType(String ipType) {
        this.ipType = ipType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getHostNm() {
        return hostNm;
    }

    public void setHostNm(String hostNm) {
        this.hostNm = hostNm;
    }

    public String getSvcNm() {
        return svcNm;
    }

    public void setSvcNm(String svcNm) {
        this.svcNm = svcNm;
    }

    public String getAuditRate() {
        return auditRate;
    }

    public void setAuditRate(String auditRate) {
        this.auditRate = auditRate;
    }

    public String getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(String auditDate) {
        this.auditDate = auditDate;
    }

    public String getReqTeam() {
        return reqTeam;
    }

    public void setReqTeam(String reqTeam) {
        this.reqTeam = reqTeam;
    }

    public String getReqUser() {
        return reqUser;
    }

    public void setReqUser(String reqUser) {
        this.reqUser = reqUser;
    }

    public String getBranchNm() {
        return branchNm;
    }

    public void setBranchNm(String branchNm) {
        this.branchNm = branchNm;
    }

    public String getTeamNm() {
        return teamNm;
    }

    public void setTeamNm(String teamNm) {
        this.teamNm = teamNm;
    }

    public String getUserNm() {
        return userNm;
    }

    public void setUserNm(String userNm) {
        this.userNm = userNm;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }
}
