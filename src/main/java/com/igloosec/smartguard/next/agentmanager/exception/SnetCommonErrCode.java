/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.exception.SnetCommonErrCode.java
 * company : Mobigen
 * @author : Oh su jin
 * created at : 2016. 5. 3.
 * description :
 */

package com.igloosec.smartguard.next.agentmanager.exception;

import com.igloosec.smartguard.next.agentmanager.utils.ErrCodeUtil;

public enum SnetCommonErrCode implements ErrCodable{
    ERR_0000("ERR_0000", "알 수 없는 에러."),
    ERR_0001("ERR_0001", "%1의 작업 시간 초과."),
    ERR_0002("ERR_0002", "Agent 기동 여부  확인 및 10226포트 방화벽 확인이 필요합니다."),
    ERR_0003("ERR_0003", "DB Transaction Error."),
    ERR_0004("ERR_0004", "네트워크 세션이 비정상 종료되었습니다. 재실행 필요(방화벽을 확인해 주세요.)"),
    ERR_0005("ERR_0005", "Switch 장비 접속 실패. ID,PASSWORD를 확인해주세요."),
    ERR_0006("ERR_0006", "File Decryption Fail."),
    ERR_0007("ERR_0007", "KEY 암호화  오류가 발생했습니다."),
    ERR_0008("ERR_0008", "파일을 찾을 수 없습니다."),
    ERR_0009("ERR_0009", "파일 스트림 IO ERROR."),
    ERR_0010("ERR_0010", "IO Exception"),
    ERR_0011("ERR_0011", "파일 복사 에러."),
    ERR_0012("ERR_0012", "진단기준이 없습니다."),
    ERR_0013("ERR_0013", "장비정보 수집 결과 파일 오류."),
    ERR_0014("ERR_0014", "장비코드가 없습니다."),
    ERR_0015("ERR_0015", "진단 결과 파싱 오류"),
    ERR_0016("ERR_0016", "진단프로그램 Transfer 시도 실패. Agent 와의 접속이 해제되었습니다. 진단 재시도 해 주세요."),
    //ERR_0017("ERR_0017", "진단프로그램 생성 실패"),
    ERR_0017("ERR_0017", "진단스크립트 파일명이 잘못되었습니다."),
    ERR_0018("ERR_0018", "장비정보 결과 파일 수집 실패"),
    ERR_0019("ERR_0019", "장비정보 결과 파일 Decryption 또는 압축해제 실패"),
    ERR_0020("ERR_0020", "장비정보 결과 파일 Parsing 실패. 장비정보 결과 파일을 확인해 주세요[1]."),
    ERR_0021("ERR_0021", "장비정보 결과 파일 Parsing 후처리 실패 . 파일권한 및 디스크용량 확인해 주세요."),
    ERR_0022("ERR_0022", "장비정보 결과 파일 Parsing 실패. 장비정보 결과 파일을 확인해 주세요[2]."),
    ERR_0023("ERR_0023", "해당 매니저 코드를 확인해 주십시오."),
    ERR_0024("ERR_0024", "SQL 트랜젝션 오류."),
    ERR_0025("ERR_0001", "Agent의 %1 작업의 시간 초과."),
    ERR_0026("ERR_0026", "SQL_ERROR."),
    ERR_0027("ERR_0027", "장비정보 수집 결과 호스트, 아이피 정보가 없습니다."),
    ERR_0028("ERR_0028", "긴급진단 파일이 잘못되었습니다. 진단 파일 유무및 파일이 정상적인지 확인해주세요."),
    ERR_0029("ERR_0029", "긴급진단 XML 파일 파싱에 실패하였습니다. XML의 데이터를 확인해 주세요."),
    ERR_0030("ERR_0030", "긴급진단 데이터 처리를 실패하였습니다."),
    ERR_0031("ERR_0031", "Get Script 정보 파싱에 실패했습니다. : 수집 된 장비정보 없음"),
    ERR_0032("ERR_0032", "복호화 대상파일을 찾을 수 없습니다."),
    ERR_0033("ERR_0033", "자동진단 결과파일(XML) 읽기 실패."),
    ERR_0034("ERR_0034", "수동진단 결과파일 읽기 실패."),
    ERR_0035("ERR_0035", "1회용 진단 결과파일 읽기 실패."),
    ERR_0036("ERR_0036", "장비정보 수집스크립트 파일의 해쉬값이 일치하지 않습니다."),
    ERR_0037("ERR_0037", "진단 스크립트 파일의 해쉬값이 일치하지 않습니다."),
    ERR_0038("ERR_0038", "선택된 장비와 XML 업로드된 장비정보가 다릅니다."),
    ERR_0039("ERR_0039", "진단 실행 권한 변경이 필요한지 확인해주세요. 현재 실행 계정 : %1, 진단 실행 권한 변경 : %2"),
    ERR_0040("ERR_0040", "File Hash List ERROR");



    private String errCode;
    private String msg;

    @Override
    public String getErrCode() {
        return this.errCode;
    }
    @Override
    public String getMessage(String... args) {
        return ErrCodeUtil.parseMessage(this.msg, args);
    }

    SnetCommonErrCode(String errCode, String msg) {
        this.errCode = errCode;
        this.msg = msg;
    }

    SnetCommonErrCode(String errCode) {
        this.errCode = errCode;
    }
}
