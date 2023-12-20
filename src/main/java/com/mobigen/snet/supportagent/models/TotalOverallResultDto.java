package com.mobigen.snet.supportagent.models;

public class TotalOverallResultDto extends Entity {

    private String key; // 점검군
    private int value1; // 점검수량
    private int value2; // 점검항목수
    private int value3; // 전체점검항목수
    private int value4; // 양호
    private int value5; // 취약
    private int value6; // NA
    private int value7; // 위험수용(불가)
    private int value8; // 인터뷰필요
    private double auditRate; // 보안준수율
    private String minAuditDay; // 보안점검 최초 진단일
    private String maxAuditDay; // 이행정검 최종 진단일
    private String[] assetCdIn; // assetCdIn
    private String swNm;
    private String assetCd;
    private String etcSwType;

    public String getEtcSwType() {
        return etcSwType;
    }

    public String getAssetCd() {
        return assetCd;
    }

    public void setAssetCd(String assetCd) {
        this.assetCd = assetCd;
    }

    public void setEtcSwType(String etcSwType) {
        this.etcSwType = etcSwType;
    }

    public String getSwNm() {
        return swNm;
    }

    public void setSwNm(String swNm) {
        this.swNm = swNm;
    }

    public String[] getAssetCdIn() {
        return assetCdIn;
    }

    public void setAssetCdIn(String[] assetCdIn) {
        this.assetCdIn = assetCdIn;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMinAuditDay() {
        return minAuditDay;
    }

    public int getValue4() {
        return value4;
    }

    public void setValue4(int value4) {
        this.value4 = value4;
    }

    public int getValue5() {
        return value5;
    }

    public void setValue5(int value5) {
        this.value5 = value5;
    }

    public int getValue6() {
        return value6;
    }

    public void setValue6(int value6) {
        this.value6 = value6;
    }

    public int getValue7() {
        return value7;
    }

    public String getMaxAuditDay() {
        return maxAuditDay;
    }

    public void setMaxAuditDay(String maxAuditDay) {
        this.maxAuditDay = maxAuditDay;
    }

    public void setValue7(int value7) {
        this.value7 = value7;
    }

    public void setMinAuditDay(String minAuditDay) {
        this.minAuditDay = minAuditDay;
    }

    public int getValue1() {
        return value1;
    }

    public void setValue1(int value1) {
        this.value1 = value1;
    }

    public int getValue2() {
        return value2;
    }

    public void setValue2(int value2) {
        this.value2 = value2;
    }

    public int getValue3() {
        return value3;
    }

    public void setValue3(int value3) {
        this.value3 = value3;
    }

    public int getValue8() {
        return value8;
    }

    public void setValue8(int value8) {
        this.value8 = value8;
    }

    public double getAuditRate() {
        return auditRate;
    }

    public void setAuditRate(double auditRate) {
        this.auditRate = auditRate;
    }
}
