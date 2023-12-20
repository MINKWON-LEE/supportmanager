package com.mobigen.snet.supportagent.models.was;

import lombok.Data;

@Data
public class SnetConfigMailSettingModel {
    private String diagnosisPartOrderSeq = "";
    private String diagnosisPartNm = "";
    private String diagnosisPartId = "";

    private String managerId = "";
    private String managerNm = "";
    private String managerMail = "";
    private String managerTeamId = "";
    private String managerTeamNm = "";
    private String managerMailInfo = "";

    private String plId = "";
    private String plNm = "";
    private String plMail = "";
    private String plTeamId = "";
    private String plTeamNm = "";
    private String plMailInfo = "";

    private String sendYn = "";

    private String templateCd = "";
    private String templateNm = "";
    private String mailTitle = "";
    private String sendMsg = "";

    private String data;
    private String label;

}