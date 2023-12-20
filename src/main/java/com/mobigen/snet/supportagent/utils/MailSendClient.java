package com.mobigen.snet.supportagent.utils;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailSendClient {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${snet.mail.port}")
    private String mailPort;

    @Value("${snet.mail.host}")
    private String mailHost;

    @Value("${snet.mail.user}")
    private String mailUser;

    @Value("${snet.mail.passwd}")
    private String mailPasswd;


    public void sendMailMessage(String toMailAddr, String fromMailAddr, String ccMailAddr, String subject, String content, String fileList) throws Exception {

        // Recipient's email ID needs to be mentioned.
        String to = toMailAddr;
        String cc = ccMailAddr;

        // Sender's email ID needs to be mentioned
        String from = fromMailAddr;

        // Get system properties
        Properties properties = System.getProperties();

        properties.setProperty("mail.smtp.port", mailPort);
        properties.setProperty("mail.smtp.host", mailHost);

        Session session = null;

        // Setup mail server
        if (mailPasswd != null && !mailPasswd.isEmpty()) {
            properties.put("mail.smtp.auth", "true");

            //Starttls를 이용한 명시적 보안(SSL/TLS 참조)
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.ssl.trust", "*");

            session = Session.getInstance(properties,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(mailUser, mailPasswd);
                        }
                    });
        } else {
            session = Session.getDefaultInstance(properties);
        }

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            if (from == null || from.equals("")) {
                from = mailUser;
            }

            logger.info("From List : " + from);
            //	        message.setFrom(new InternetAddress(from));
            //언어셋 추가(한글깨짐 현상)
            message.setFrom(new InternetAddress(new String(from.getBytes("UTF-8"))));
            String fromname = "";
            String frommail = "";
            if (from.indexOf("<") > -1) {
                String[] temp = from.split("<");
                fromname = temp[0];
                frommail = temp[1].replace("<", "").replace(">", "");
                message.setFrom(new InternetAddress(frommail, fromname, "UTF-8"));
            } else {
                message.setFrom(new InternetAddress(new String(from.getBytes("UTF-8"))));
            }
            logger.info("To list : " + to);

            // Set To: header field of the header.
            if (to == null || to.equals("")) {
                //받는 사람이 설정되어 있지 않은 경우, 메일전송이 실패 되도록 설정.
                logger.error(">>> Do not set Receiver.");
                return;
            }

            String[] tolist = to.split(",");

            InternetAddress[] toAddr = new InternetAddress[tolist.length];

            for (int i = 0; i < tolist.length; i++) {
                //toAddr[i] = new InternetAddress(new String(tolist[i].getBytes("UTF-8")));

                if (tolist[i].indexOf("<") > -1) {
                    String[] temp = tolist[i].split("<");
                    String name = tolist[i];
                    String mail = temp[1].replace("<", "").replace(">", "");
                    toAddr[i] = new InternetAddress(mail, name, "UTF-8");
                } else {
                    toAddr[i] = new InternetAddress(new String(tolist[i].getBytes("UTF-8")));
                }
            }

            message.addRecipients(Message.RecipientType.TO, toAddr);

            logger.info("CC List : " + cc);

            if (cc != null) {
                if (!cc.equals("")) {
                    String[] cclist = cc.split(",");
                    InternetAddress[] ccAddr = new InternetAddress[cclist.length];

                    for (int i = 0; i < cclist.length; i++) {
                        if (cclist[i].indexOf("<") > -1) {
                            String[] temp = cclist[i].split("<");
                            String name = temp[0];
                            String mail = temp[1].replace("<", "").replace(">", "");
                            ccAddr[i] = new InternetAddress(mail, cclist[i], "UTF-8");
                        } else {
                            ccAddr[i] = new InternetAddress(new String(cclist[i].getBytes("UTF-8")));
                        }
                    }
                    message.addRecipients(Message.RecipientType.CC, ccAddr);
                }
            }

            // Set Subject: header field
            message.setSubject(subject, "UTF-8");

            //메일 본문 내용 설정
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setContent(content, "text/html; charset=UTF-8");

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);

            if (fileList != null) {
                if (!fileList.equals("")) {
                    String[] fList = fileList.split(",");

                    for (int i = 0; i < fList.length; i++) {
                        MimeBodyPart mbp2 = new MimeBodyPart();

                        FileDataSource fds = new FileDataSource(fList[i]);
                        mbp2.setDataHandler(new DataHandler(fds));
                        mbp2.setFileName(new String(fds.getName().getBytes("UTF-8")));

                        mp.addBodyPart(mbp2);
                    }
                }
            }

            message.setContent(mp, "text/html; charset=UTF-8");
            message.setSentDate(new Date());
            // Send message
            Transport.send(message);

            logger.info("Sent message successfully....");

        } catch (MessagingException mex) {
            logger.error("Send Mail Exception :: {}", mex.getMessage(), mex.fillInStackTrace());
            throw new MessagingException("Send Mail Exception :: " + mex.getMessage());
        }
    }
}
