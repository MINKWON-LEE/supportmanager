package com.sktelecom.mf.common.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

import com.sktelecom.mf.util.AuditPropertis;

@Slf4j
public class SocketClient {
    String sendVal = "";
    public SocketClient ( String _sendVal ) {
        this.sendVal = _sendVal;
    }

    private String exceptionMessage = "";
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    private String port = "ServerPort";
    public void setExceptionPort ( String _port )
    {
        this.port = _port;
    }

    public void setNotify ( )
    {

        log.info("*[equipBatchJob] setNotify");

        exceptionMessage = "";
        try{
            Properties pro = AuditPropertis.getInstance().getPro();
            String serverIp = pro.getProperty ( "ServerIP" ).toString();

            log.info("*[setNotify] serverIp : {}", serverIp);

            int serverPort = Integer.parseInt ( pro.getProperty ( this.port ).toString() );
            log.info("*[equipBatchJob] serverPort = " + serverPort );
            log.info("*[equipBatchJob] >>>>> MESSAGE = " + sendVal);

            Socket socket = new Socket ( serverIp , serverPort );
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bw.write(sendVal);
            bw.close();
            socket.close();
        }
        catch (ConnectException ce) {
            log.error("Error" , ce);
            exceptionMessage = ce.getMessage();
        } catch (IOException ie) {
            log.error("Error" , ie);
            exceptionMessage = ie.getMessage();
        } catch (Exception e) {
            log.error("Error" , e);
            exceptionMessage = e.getMessage();
        } // try - catch
    }

}
