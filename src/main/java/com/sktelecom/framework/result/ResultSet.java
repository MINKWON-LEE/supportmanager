package com.sktelecom.framework.result;

import java.sql.SQLException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;

@Slf4j
public class ResultSet {

    /**
     * 결과 실패/성공여부
     */
    private boolean result = true;
    public boolean getResult() {
        return result;
    }
    public void setResult(boolean result) {
        this.result = result;
    }

    /**
     * 결과가 실패일때 오류 메세지
     */
    private String errorMessage = "";
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errMessage) {
        this.errorMessage = errMessage;
        this.result = false;

        log.error(errMessage);
    }
    public void setErrorMessage(Exception e) {
        if (e instanceof DataIntegrityViolationException) {
            this.errorMessage = ((SQLException) e.getCause().getCause()).getMessage();
        } else if (e instanceof SQLException) {
            this.errorMessage = e.getMessage();
        } else {
            this.errorMessage = e.getMessage();
        }

        // log
        log.error(errorMessage);

        // result-Fail
        this.result = false;
    }

    /**
     * 처리 결과
     */
    private Object data = null;
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
}
