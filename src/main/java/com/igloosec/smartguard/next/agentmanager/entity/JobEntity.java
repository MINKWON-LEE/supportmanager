/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.entity.JobEntity.java
 * company : Mobigen
 * @author : Oh su jin
 * created at : 2016. 2. 19.
 * description :
 */
package com.igloosec.smartguard.next.agentmanager.entity;

import java.io.File;

public class JobEntity extends BaseDBEntity {

    String sOTP; // JOB 고유 아이디로 사용
    String cOTP; // Agent OTP Code

    // Agent_Job_History
    String swType; // OS,DB,WAS,WEB..
    String swNm; // LINUX,WINDOW,ORACLE,JEUS...
    String swInfo; // Version info
    String swDir;
    String swUser;
    String swEtc;
    Integer auditType; // 1:테스트, 2:정식 수행
    String agentJobRdate; // 진단 요청시간
    String auditFileName; // 진단스크립트 경로+파일 명
    String checksumHash; // 진단스크립트 해쉬값
    String infoChecksumHash; // 장비정보 수집스크립트 해쉬값
    Integer agentJobFlag; // 0:진단대기 , 1:진단요청, 2:진단시작, 3:진단 완료, 4:작업실패
    String agentJobSDate; // 진단 시작시간
    String agentJobEDate; // 진단 종료시간
    String auditFileCd; // 진단 파일 명
    String reqType; // getscript,diag,setup,test
    String jobType; // OTP, GSCRPTFILE, DGFILE
    String fileType; // .otp , .jar , .sh
    String sDate; // Manager쪽에서 OTP 발급한 시간.
    String cDate;
    String fileName; // 난수 값으로 생성된 파일 명.
    String hostNm;
    File[] files; //여러 파일을 보내는 JOB 용 파일들 //i.e. Agent Update
    String auditSpeed;
    String globalPwl;
    String globalSts;

    //EVENT
    String prgId; // 이벤트 진단 프로그램 아이디


    // 진단작업 수행관련 필요 설명자료
    String agentJobDesc;


    // 진단 결과에 추가될 Manager Code
    String managerCode;

    //수동업로드 assetCd,ipAddres
    String assetCd;
    String ipAddres;

    //2018.12.19 진단로그
    String statusLog;

    //kill agent
    boolean killAgent = false;

    // 진단 스크립트 수행 전 Get Script 동작 확인값
    boolean getScriptDone = false;

    AgentInfo agentInfo;

    AgentInfo relayAgentInfo;

    AgentInfo relay2AgentInfo;

    AgentInfo relay3AgentInfo;

    //네트워크 장비 config 명령어 호출
    String nwCMD;

    //Agent를 통한 get 프로그램 flag
    boolean isAgentGetprog = false;

    String eventFlag = "N";

    boolean isAgentManualSetup = false;

    String hashTxt;        // 여러 파일을 압축하여 전송할 때 각각의 파일의 해쉬값을 적어놓은 파일 이름.

    public boolean isAgentManualSetup() {
        return isAgentManualSetup;
    }

    public void setAgentManualSetup(boolean agentManualSetup) {
        isAgentManualSetup = agentManualSetup;
    }

    public AgentInfo getRelayAgentInfo() {
        return relayAgentInfo;
    }

    public void setRelayAgentInfo(AgentInfo relayAgentInfo) {
        this.relayAgentInfo = relayAgentInfo;
    }

    public AgentInfo getRelay2AgentInfo() {
        return relay2AgentInfo;
    }

    public void setRelay2AgentInfo(AgentInfo relayAgentInfo) {
        this.relay2AgentInfo = relayAgentInfo;
    }

    public AgentInfo getRelay3AgentInfo() {
        return relay3AgentInfo;
    }

    public void setRelay3AgentInfo(AgentInfo relayAgentInfo) {
        this.relay3AgentInfo = relayAgentInfo;
    }

    public boolean isAgentGetprog() {
        return isAgentGetprog;
    }

    public void setAgentGetprog(boolean agentGetprog) {
        isAgentGetprog = agentGetprog;
    }

    public String getNwCMD() {
        return nwCMD;
    }

    public void setNwCMD(String nwCMD) {
        this.nwCMD = nwCMD;
    }

    public boolean isKillAgent() {
        return killAgent;
    }

    public void setKillAgent(boolean killAgent) {
        this.killAgent = killAgent;
    }

    /**
     * @return the auditFileCd
     */
    public String getAuditFileCd() {
        return auditFileCd;
    }

    /**
     * @param auditFileCd
     *            the auditFileCd to set
     */
    public void setAuditFileCd(String auditFileCd) {
        this.auditFileCd = auditFileCd;
    }

    public boolean isGetScriptDone() {
        return getScriptDone;
    }

    public void setGetScriptDone(boolean getScriptDone) {
        this.getScriptDone = getScriptDone;
    }

    public String getManagerCode() {
        return managerCode;
    }

    public void setManagerCode(String managerCode) {
        this.managerCode = managerCode;
    }

    /**
     * @return the sOTP
     */
    public String getsOTP() {
        return sOTP;
    }

    /**
     * @param sOTP
     *            the sOTP to set
     */
    public void setsOTP(String sOTP) {
        this.sOTP = sOTP;
    }

    /**
     * @return the cOTP
     */
    public String getcOTP() {
        return cOTP;
    }

    /**
     * @param cOTP
     *            the cOTP to set
     */
    public void setcOTP(String cOTP) {
        this.cOTP = cOTP;
    }

    /**
     * @return the swType
     */
    public String getSwType() {
        return swType;
    }

    /**
     * @param swType
     *            the swType to set
     */
    public void setSwType(String swType) {
        this.swType = swType;
    }

    /**
     * @return the swNm
     */
    public String getSwNm() {
        return swNm;
    }

    /**
     * @param swNm
     *            the swNm to set
     */
    public void setSwNm(String swNm) {
        this.swNm = swNm;
    }

    /**
     * @return the swInfo
     */
    public String getSwInfo() {
        return swInfo;
    }

    /**
     * @param swInfo
     *            the swInfo to set
     */
    public void setSwInfo(String swInfo) {
        this.swInfo = swInfo;
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

    /**
     * @return the auditType
     */
    public Integer getAuditType() {
        return auditType;
    }

    /**
     * @param auditType
     *            the auditType to set
     */
    public void setAuditType(Integer auditType) {
        this.auditType = auditType;
    }

    /**
     * @return the agentJobRdate
     */
    public String getAgentJobRdate() {
        return agentJobRdate;
    }

    /**
     * @param agentJobRdate
     *            the agentJobRdate to set
     */
    public void setAgentJobRdate(String agentJobRdate) {
        this.agentJobRdate = agentJobRdate;
    }

    /**
     * @return the auditFileName
     */
    public String getAuditFileName() {
        return auditFileName;
    }

    /**
     * @param auditFileName
     *            the auditFileName to set
     */
    public void setAuditFileName(String auditFileName) {
        this.auditFileName = auditFileName;
    }

    /**
     * @return the checksumHash
     */
    public String getChecksumHash() {
        return checksumHash;
    }

    /**
     * @param checksumHash
     *            the checksumHash to set
     */
    public void setChecksumHash(String checksumHash) {
        this.checksumHash = checksumHash;
    }

    /**
     * @return the infoChecksumHash
     */
    public String getInfoChecksumHash() {
        return infoChecksumHash;
    }

    /**
     * @param infoChecksumHash
     *            the infoChecksumHash to set
     */
    public void setInfoChecksumHash(String infoChecksumHash) {
        this.infoChecksumHash = infoChecksumHash;
    }

    /**
     * @return the agentJobFlag
     */
    public Integer getAgentJobFlag() {
        return agentJobFlag;
    }

    /**
     * @param agentJobFlag
     *            the agentJobFlag to set
     */
    public void setAgentJobFlag(Integer agentJobFlag) {
        this.agentJobFlag = agentJobFlag;
    }

    /**
     * @return the agentJobSDate
     */
    public String getAgentJobSDate() {
        return agentJobSDate;
    }

    /**
     * @param agentJobSDate
     *            the agentJobSDate to set
     */
    public void setAgentJobSDate(String agentJobSDate) {
        this.agentJobSDate = agentJobSDate;
    }

    /**
     * @return the agentJobEDate
     */
    public String getAgentJobEDate() {
        return agentJobEDate;
    }

    /**
     * @param agentJobEDate
     *            the agentJobEDate to set
     */
    public void setAgentJobEDate(String agentJobEDate) {
        this.agentJobEDate = agentJobEDate;
    }

    /**
     * @return the reqType
     */
    public String getReqType() {
        return reqType;
    }

    /**
     * @param reqType
     *            the reqType to set
     */
    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    /**
     * @return the jobType
     */
    public String getJobType() {
        return jobType;
    }

    /**
     * @param jobType
     *            the jobType to set
     */
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    /**
     * @return the fileType
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * @param fileType
     *            the fileType to set
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * @return the sDate
     */
    public String getsDate() {
        return sDate;
    }

    /**
     * @param sDate
     *            the sDate to set
     */
    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    /**
     * @return the cDate
     */
    public String getcDate() {
        return cDate;
    }

    /**
     * @param cDate
     *            the cDate to set
     */
    public void setcDate(String cDate) {
        this.cDate = cDate;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the agentInfo
     */
    public AgentInfo getAgentInfo() {
        return agentInfo;
    }

    /**
     * @param agentInfo
     *            the agentInfo to set
     */
    public void setAgentInfo(AgentInfo agentInfo) {
        this.agentInfo = agentInfo;
    }

    /**
     * @return the agentJobDesc
     */
    public String getAgentJobDesc() {
        return agentJobDesc;
    }

    /**
     * @param agentJobDesc the agentJobDesc to set
     */
    public void setAgentJobDesc(String agentJobDesc) {
        this.agentJobDesc = agentJobDesc;
    }

    public String getHostNm() {
        return hostNm;
    }

    public void setHostNm(String hostNm) {
        this.hostNm = hostNm;
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public String getAuditSpeed() {
        return auditSpeed;
    }

    public void setAuditSpeed(String auditSpeed) {
        this.auditSpeed = auditSpeed;
    }

    public String getEventFlag() {
        return eventFlag;
    }

    public void setEventFlag(String eventFlag) {
        this.eventFlag = eventFlag;
    }

    public String getGlobalPwl() {
        return globalPwl;
    }

    public void setGlobalPwl(String globalPwl) {
        this.globalPwl = globalPwl;
    }

    public String getGlobalSts() {
        return globalSts;
    }

    public void setGlobalSts(String globalSts) {
        this.globalSts = globalSts;
    }

    public String getPrgId() {
        return prgId;
    }

    public void setPrgId(String prgId) {
        this.prgId = prgId;
    }

    @Override
    public String getAssetCd() {
        return assetCd;
    }

    @Override
    public void setAssetCd(String assetCd) {
        this.assetCd = assetCd;
    }

    public String getIpAddres() {
        return ipAddres;
    }

    public void setIpAddres(String ipAddres) {
        this.ipAddres = ipAddres;
    }

    public String getStatusLog() {
        return statusLog;
    }

    public void setStatusLog(String statusLog) {
        this.statusLog = statusLog;
    }

    public String getHashTxt() { return hashTxt; }

    public void setHashTxt(String hashTxt) { this.hashTxt = hashTxt; }
}
