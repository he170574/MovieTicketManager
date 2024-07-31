package com.movie_theater.service.fogotPassWord.javamail;

import com.movie_theater.service.fogotPassWord.MailConfig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SentMailToEmpCreate {
    public static void sendAccountToEmailEmployee(String pass,String acc,String email) {


        // 1) Lấy đối tượng session
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", MailConfig.HOST_NAME);
        props.put("mail.smtp.socketFactory.port", MailConfig.SSL_PORT);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.port", MailConfig.SSL_PORT);

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MailConfig.APP_EMAIL, MailConfig.APP_PASSWORD);
            }
        });

        // 2) Tạo nội dung email
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MailConfig.APP_EMAIL));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            // 3) Tạo nội dung HTML
            message.setSubject("HTML Message");
            String htmlContent = "<!doctype html>\n" +
                    "<html lang=\"en\">\n" +
                    "  <head>\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "    <title>Bootstrap demo</title>\n" +
                    "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH\" crossorigin=\"anonymous\">\n" +
                    "  </head>\n" +
                    "  <body>\n" +
                    "    <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-collapse: collapse;\">\n" +
                    "        \n" +
                    "        <tr>\n" +
                    "            <td style=\"background-color: #000000; padding: 20px; text-align: center;\">\n" +
                    "                <!-- Email Content -->\n" +
                    "                <img src=\"https://res.cloudinary.com/de6o7wax8/image/upload/v1710136865/%E1%BA%A2nh_ch%E1%BB%A5p_m%C3%A0n_h%C3%ACnh_2024-01-11_230903_edjqtz.png\" alt=\"Black Friday Sales\" style=\"width: 100%; height: auto; border: 0;\">\n" +
                    "                <h3 style=\"color: #c1b37e;\">Wellcome to Netflex</h3>\n" +
                    "                <h1 style=\"color: #ececeb;\">The Account and PassWord</h1>\n" +
                    "                <h3 style=\"color: #d00701;\">Pass: "+pass+" and account: "+acc+"</h3>\n" +
                    "                <a href=\"http://localhost:8080/home\" class=\"btn btn-danger mt-4\">Go to home page</a>\n" +
                    "            </td>\n" +
                    "        </tr>\n" +
                    "        <tr>\n" +
                    "            <td style=\"padding: 20px; background-color: #EFEFEF; text-align: center;\">\n" +
                    "                <!-- Social Icons -->\n" +
                    "                <a href=\"https://www.facebook.com/profile.php?id=100040323076309\" target=\"_blank\"><img src=\"https://res.cloudinary.com/de6o7wax8/image/upload/v1709621744/p0ratsgwcy6pjhsuqwyp.jpg\" alt=\"Twitter\" style=\"width: 32px; margin-right: 10px;\"></a>\n" +
                    "                <a href=\"https://www.facebook.com/profile.php?id=100039906993007\" target=\"_blank\"><img src=\"https://res.cloudinary.com/de6o7wax8/image/upload/v1708861815/y8d81aqonbnyddf4fton.jpg\" alt=\"Facebook\" style=\"width: 32px; margin-right: 10px;\"></a>\n" +
                    "                <!-- Add more social icons as needed -->\n" +
                    "            </td>\n" +
                    "        </tr>\n" +
                    "    </table>\n" +
                    "    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js\" integrity=\"sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz\" crossorigin=\"anonymous\"></script>\n" +
                    "  </body>\n" +
                    "</html>";
            message.setContent(htmlContent, "text/html");

            // 4) Gửi email
            Transport.send(message);

            System.out.println("Message sent successfully");
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }
}
