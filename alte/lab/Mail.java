package alte.lab;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Mail {

    public static void mail(String destination, String subject, String content) {
        final String sendrmailid = "ananas@yuran.us";
        final String pwd = "dmVyeSB0b3AgcGFzc3dvcmQ=";
        String smtphost = "smtp.yandex.com";

        Properties propvls = new Properties();
        propvls.put("Mail.smtp.host", smtphost);
        propvls.put("Mail.smtp.auth", "true");
        propvls.put("Mail.smtp.port", "465");
        propvls.put("Mail.smtp.socketFactory.port", "465");
        propvls.put("Mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session sessionobj = Session.getInstance(propvls,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sendrmailid, pwd);
                    }
                });

        try {
            Message messageobj = new MimeMessage(sessionobj);
            messageobj.setFrom(new InternetAddress(sendrmailid));
            messageobj.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destination));
            messageobj.setSubject(subject);
            messageobj.setText(content);
            Transport.send(messageobj);
        } catch (MessagingException exp) {
            throw new RuntimeException(exp);
        }
    }
}
