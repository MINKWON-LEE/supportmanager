package com.mobigen.snet.supportagent.exception;

/**
 * 'sg_supprotmanager 프로젝트 - 상세보고서'
 */
public class ErrorException extends RuntimeException {

    public void ErrorException() {

    }

    public ErrorException(String message) {

        super(message);
    }

    public ErrorException(String message, Throwable cause) {

        super(message);
    }
}
