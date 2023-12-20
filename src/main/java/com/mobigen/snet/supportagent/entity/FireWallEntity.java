package com.mobigen.snet.supportagent.entity;

/**
 * Created by osujin12 on 2017. 1. 12..
 */
public class FireWallEntity {
    String firewallReqNum = "";
    String srcSvcCd = "";
    String srcHostNm = "";
    String srcIpAddress = "";
    String srcFwIpAddress = "";
    String destSvcCd = "";
    String destHostNm = "";
    String destIpAddress = "";
    String destFwIpAddress = "";
    String reqTeam = "";
    String reqUser = "";
    String srcAuditRate = "";
    String destAuditRate = "";
    String srcAuditDate = "";
    String destAuditDate = "";
    String createDate = "";

    public String getFirewallReqNum() {
        return firewallReqNum;
    }

    public void setFirewallReqNum(String firewallReqNum) {
        this.firewallReqNum = firewallReqNum;
    }

    public String getSrcSvcCd() {
        return srcSvcCd;
    }

    public void setSrcSvcCd(String srcSvcCd) {
        this.srcSvcCd = srcSvcCd;
    }

    public String getSrcHostNm() {
        return srcHostNm;
    }

    public void setSrcHostNm(String srcHostNm) {
        this.srcHostNm = srcHostNm;
    }

    public String getSrcIpAddress() {
        return srcIpAddress;
    }

    public void setSrcIpAddress(String srcIpAddress) {
        this.srcIpAddress = srcIpAddress;
    }

    public String getSrcFwIpAddress() {
        return srcFwIpAddress;
    }

    public void setSrcFwIpAddress(String srcFwIpAddress) {
        this.srcFwIpAddress = srcFwIpAddress;
    }

    public String getDestSvcCd() {
        return destSvcCd;
    }

    public void setDestSvcCd(String destSvcCd) {
        this.destSvcCd = destSvcCd;
    }

    public String getDestHostNm() {
        return destHostNm;
    }

    public void setDestHostNm(String destHostNm) {
        this.destHostNm = destHostNm;
    }

    public String getDestIpAddress() {
        return destIpAddress;
    }

    public void setDestIpAddress(String destIpAddress) {
        this.destIpAddress = destIpAddress;
    }

    public String getDestFwIpAddress() {
        return destFwIpAddress;
    }

    public void setDestFwIpAddress(String destFwIpAddress) {
        this.destFwIpAddress = destFwIpAddress;
    }

    public String getSrcAuditDate() {
        return srcAuditDate;
    }

    public void setSrcAuditDate(String srcAuditDate) {
        this.srcAuditDate = srcAuditDate;
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

    public String getSrcAuditRate() {
        return srcAuditRate;
    }

    public void setSrcAuditRate(String srcAuditRate) {
        this.srcAuditRate = srcAuditRate;
    }

    public String getDestAuditRate() {
        return destAuditRate;
    }

    public void setDestAuditRate(String destAuditRate) {
        this.destAuditRate = destAuditRate;
    }

    public String getRcAuditDate() {
        return srcAuditDate;
    }

    public void setRcAuditDate(String rcAuditDate) {
        this.srcAuditDate = rcAuditDate;
    }

    public String getDestAuditDate() {
        return destAuditDate;
    }

    public void setDestAuditDate(String destAuditDate) {
        this.destAuditDate = destAuditDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
