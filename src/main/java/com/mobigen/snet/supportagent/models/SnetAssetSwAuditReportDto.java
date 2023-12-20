package com.mobigen.snet.supportagent.models;

import com.sk.snet.manipulates.EncryptUtil;
import org.apache.ibatis.type.Alias;

@Alias("SnetAssetSwAuditReport")
public class SnetAssetSwAuditReportDto {

    // Ⅳ. 보안점검 결과_총평 sheet
    private String assetCd;
    private String itemGrade;
    private String itemResult;
    private int count;

    private int resultCnt;
    private int WeightTot;

    // Ⅴ. 보안점검 결과_요약 sheet
    private int stadardCount;
    private int weakCount;
    private int itemCount;

    // 기본
    private int rownum;
    private String hostNM;
    private String swNm;
    private String swType;
    private String swInfo;
    private String swDir;
    private String swUser;
    private String swEtc;
    private String ipAddress;
    private String auditDay;
    private String diagnosisCd;
    private String itemGrpNm;
    private String itemNm;
    private String itemNmDesc;
    private String itemGradeView;
    private String itemResultView;
    private String itemStatus;
    private String itemStandard;
    private String itemCountermeasure;
    private String itemCountermeasureDetail;
    private String actionPlanComplateDate;
    private String actionPlanReason;
    private String createDate;

    // 인트로 스케줄러
    private String swTypeNmKey;
    private double auditRateAssetMaster;

    private int weakHighCount;
    private int weakMidCount;
    private int weakLowCount;

    private int itemGrpNmCnt;
    private int itemGrpNmSeq;

    // 보고서 스케줄러
    private int weakCountD;
    private int weakHighCountD;
    private int weakMidCountD;
    private int weakLowCountD;

    // 분류번호
    private String divideNum;
    private int hostCount;
    private String etc;

    // 대시보드
    private int totalCnt;
    private int itemGradeTotal;
    private double auditRate;

    // 보고서 기타
    private String diagnosisCdKey;

    public String getDiagnosisCdKey() {
        return diagnosisCdKey;
    }

    public void setDiagnosisCdKey(String diagnosisCdKey) {
        this.diagnosisCdKey = diagnosisCdKey;
    }

    public double getAuditRate() {
        return auditRate;
    }

    public void setAuditRate(double auditRate) {
        this.auditRate = auditRate;
    }

    public int getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(int totalCnt) {
        this.totalCnt = totalCnt;
    }

    public int getItemGradeTotal() {
        return itemGradeTotal;
    }

    public void setItemGradeTotal(int itemGradeTotal) {
        this.itemGradeTotal = itemGradeTotal;
    }

    public int getWeakCountD() {
        return weakCountD;
    }

    public void setWeakCountD(int weakCountD) {
        this.weakCountD = weakCountD;
    }

    public int getItemGrpNmSeq() {
        return itemGrpNmSeq;
    }

    public int getWeakHighCountD() {
        return weakHighCountD;
    }

    public void setWeakHighCountD(int weakHighCountD) {
        this.weakHighCountD = weakHighCountD;
    }

    public int getWeakMidCountD() {
        return weakMidCountD;
    }

    public void setWeakMidCountD(int weakMidCountD) {
        this.weakMidCountD = weakMidCountD;
    }

    public int getWeakLowCountD() {
        return weakLowCountD;
    }

    public void setWeakLowCountD(int weakLowCountD) {
        this.weakLowCountD = weakLowCountD;
    }

    public void setItemGrpNmSeq(int itemGrpNmSeq) {
        this.itemGrpNmSeq = itemGrpNmSeq;
    }

    public int getItemGrpNmCnt() {
        return itemGrpNmCnt;
    }

    public void setItemGrpNmCnt(int itemGrpNmCnt) {
        this.itemGrpNmCnt = itemGrpNmCnt;
    }

    public int getWeakHighCount() {
        return weakHighCount;
    }

    public void setWeakHighCount(int weakHighCount) {
        this.weakHighCount = weakHighCount;
    }

    public int getWeakCount() {
        return weakCount;
    }

    public void setWeakCount(int weakCount) {
        this.weakCount = weakCount;
    }

    public int getWeakMidCount() {
        return weakMidCount;
    }

    public void setWeakMidCount(int weakMidCount) {
        this.weakMidCount = weakMidCount;
    }

    public int getWeakLowCount() {
        return weakLowCount;
    }

    public void setWeakLowCount(int weakLowCount) {
        this.weakLowCount = weakLowCount;
    }

    public double getAuditRateAssetMaster() {
        return auditRateAssetMaster;
    }

    public void setAuditRateAssetMaster(double auditRateAssetMaster) {
        this.auditRateAssetMaster = auditRateAssetMaster;
    }

    public String getSwTypeNmKey() {
        return swTypeNmKey;
    }

    public void setSwTypeNmKey(String swTypeNmKey) {
        this.swTypeNmKey = swTypeNmKey;
    }

    public int getStadardCount() {
        return stadardCount;
    }

    public void setStadardCount(int stadardCount) {
        this.stadardCount = stadardCount;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getSwInfo() {
        return swInfo;
    }

    public void setSwInfo(String swInfo) {
        this.swInfo = swInfo;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getRownum() {
        return rownum;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public void setRownum(int rownum) {
        this.rownum = rownum;
    }

    public String getAssetCd() {
        return assetCd;
    }

    public String getItemNmDesc() {
        return itemNmDesc;
    }

    public void setItemNmDesc(String itemNmDesc) {
        if(itemNmDesc != null){
           try {
               this.itemNmDesc = EncryptUtil.aes_decrypt(itemNmDesc);
           } catch (Exception e) {
               this.itemNmDesc = itemNmDesc;
           }
       }
    }

    public void setAssetCd(String assetCd) {
        this.assetCd = assetCd;
    }

    public String getItemGrade() {
        return itemGrade;
    }

    public void setItemGrade(String itemGrade) {
        this.itemGrade = itemGrade;
    }

    public String getItemGradeView() {
        return itemGradeView;
    }

    public void setItemGradeView(String itemGradeView) {
        this.itemGradeView = itemGradeView;
    }

    public String getItemResult() {
        return itemResult;
    }

    public void setItemResult(String itemResult) {
        this.itemResult = itemResult;
    }

    public String getItemResultView() {
        return itemResultView;
    }

    public void setItemResultView(String itemResultView) {
        this.itemResultView = itemResultView;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        if(itemStatus != null){
           try {
               this.itemStatus = EncryptUtil.aes_decrypt(itemStatus);
           } catch (Exception e) {
               this.itemStatus = itemStatus;
           }
       }
    }

    public String getItemStandard() {
        return itemStandard;
    }

    public void setItemStandard(String itemStandard) {
        if(itemStandard != null){
           try {
               this.itemStandard = EncryptUtil.aes_decrypt(itemStandard);
           } catch (Exception e) {
               this.itemStandard = itemStandard;
           }
       }
    }

    public String getItemCountermeasure() {
        return itemCountermeasure;
    }

    public void setItemCountermeasure(String itemCountermeasure) {
        if(itemCountermeasure != null){
          try {
              this.itemCountermeasure = EncryptUtil.aes_decrypt(itemCountermeasure);
          } catch (Exception e) {
              this.itemCountermeasure = itemCountermeasure;
          }
        }
    }

    public String getItemCountermeasureDetail() {
        return itemCountermeasureDetail;
    }

    public void setItemCountermeasureDetail(String itemCountermeasureDetail) {
        if(itemCountermeasureDetail != null){
          try {
              this.itemCountermeasureDetail = EncryptUtil.aes_decrypt(itemCountermeasureDetail);
          } catch (Exception e) {
              this.itemCountermeasureDetail = itemCountermeasureDetail;
          }
        }
    }

    public String getActionPlanComplateDate() {
        return actionPlanComplateDate;
    }

    public void setActionPlanComplateDate(String actionPlanComplateDate) {
        this.actionPlanComplateDate = actionPlanComplateDate;
    }

    public String getActionPlanReason() {
        return actionPlanReason;
    }

    public void setActionPlanReason(String actionPlanReason) {
        this.actionPlanReason = actionPlanReason;
    }

    public String getDivideNum() {
        return divideNum;
    }

    public void setDivideNum(String divideNum) {
        this.divideNum = divideNum;
    }

    public int getHostCount() {
        return hostCount;
    }

    public void setHostCount(int hostCount) {
        this.hostCount = hostCount;
    }

    public String getEtc() {
        return etc;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getHostNM() {
        return hostNM;
    }

    public void setHostNM(String hostNM) {
        this.hostNM = hostNM;
    }

    public String getSwNm() {
        return swNm;
    }

    public void setSwNm(String swNm) {
        this.swNm = swNm;
    }

    public String getSwType() {
        return swType;
    }

    public void setSwType(String swType) {
        this.swType = swType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAuditDay() {
        return auditDay;
    }

    public void setAuditDay(String auditDay) {
        this.auditDay = auditDay;
    }

    public String getDiagnosisCd() {
        return diagnosisCd;
    }

    public void setDiagnosisCd(String diagnosisCd) {
        this.diagnosisCd = diagnosisCd;
    }

    public String getItemGrpNm() {
        return itemGrpNm;
    }

    public void setItemGrpNm(String itemGrpNm) {
        this.itemGrpNm = itemGrpNm;
    }

    public String getItemNm() {
        return itemNm;
    }

    public void setItemNm(String itemNm) {
        this.itemNm = itemNm;
    }

    public int getResultCnt() {
        return resultCnt;
    }

    public void setResultCnt(int resultCnt) {
        this.resultCnt = resultCnt;
    }

    public int getWeightTot() {
        return WeightTot;
    }

    public void setWeightTot(int weightTot) {
        WeightTot = weightTot;
    }

    public String getSwDir() {
        return swDir;
    }

    public void setSwDir(String swDir) {
        this.swDir = swDir;
    }

    public String getSwUser() {
        return swUser;
    }

    public void setSwUser(String swUser) {
        this.swUser = swUser;
    }

    public String getSwEtc() {
        return swEtc;
    }

    public void setSwEtc(String swEtc) {
        this.swEtc = swEtc;
    }

    @Override
    public String toString() {
        return "\n" +
                "hostNM                    : " + getHostNM() + "\n" +
                "swNm                      : " + getSwNm() + "\n" +
                "swType                    : " + getSwType() + "\n" +
                "assetCd                   : " + getAssetCd() + "\n" +
                "auditDay                  : " + getAuditDay() + "\n" +
                "auditRate                 : " + getAuditRateAssetMaster() + "\n" +
                "createDate                : " + getCreateDate() + "\n" +
                "swInfo                    : " + getSwInfo() + "\n" +
                "ipAddress                 : " + getIpAddress() + "\n" +
                "diagnosisCd               : " + getDiagnosisCd() + "\n" +
                "itemGrpNm                 : " + getItemGrpNm() + "\n" +
                "itemNmDesc                : " + getItemNmDesc() + "\n" +
                "itemGradeView             : " + getItemGradeView() + "\n" +
                "itemResultView            : " + getItemResultView() + "\n" +
                "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + "\n" +
                "itemStatus                : " + getItemStatus() + "\n" +
                "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + "\n" +
                "itemStandard              : " + getItemStandard() + "\n" +
                "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + "\n" +
                "itemCountermeasure        : " + getItemCountermeasure() + "\n" +
                "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + "\n" +
                "itemCountermeasureDetail  : " + getItemCountermeasureDetail() +
                "\n";
    }
}
