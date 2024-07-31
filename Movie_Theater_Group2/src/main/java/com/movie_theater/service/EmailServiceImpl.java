package com.movie_theater.service;

import com.movie_theater.service.fogotPassWord.MailConfig;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@Log
public class EmailServiceImpl implements EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";

    private static final int SMTP_PORT = 465;

    private static final String SMTP_APPNAME = "SMTP_SWP391_MOVIETHEATER";

    private static final String SMTP_USERNAME = "hieulmhe176232@fpt.edu.vn"; // your email

    private static final String SMTP_PASSWORD = "mdca asju bezb xysp"; // your password

    @Override
    @Async
    public void sendEmail(String email, String subject, String body) throws MessagingException {
        Properties props = getMailProperties();
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });
        MimeMessage message = createMessage(session, email, subject, body);
        Transport.send(message);
        log.info("Send Mail To %s Successfully!".formatted(email));
    }

    @Override
    @Async
    public void sendActivateEmail(String email, String username, String secret, String password) throws MessagingException {
        String body = """
                <!doctype html>
                <html>
                    <body>
                        <h1>Activate your account</h1>
                        <p>You have register account <code>%s</code> with password <code>%s</code></p>
                        <a href="http://localhost:8080/activate-account?secret=%s">Activate</a>
                    </body>
                </html>
                """.formatted(username, password, secret);
        sendEmail(email, "Activate your account", body);
    }

    private Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.socketFactory.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.port", SMTP_PORT);
        return props;
    }

    private MimeMessage createMessage(Session session, String email, String subject, String body) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(MailConfig.APP_EMAIL));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setSubject(subject);
        message.setContent(body, "text/html");
        return message;
    }
}
