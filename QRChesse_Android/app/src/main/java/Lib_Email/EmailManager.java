package Lib_Email;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by kimsavinfo on 18/04/15.
 */
public class EmailManager
{
    private String senderAdress;
    private String senderPassword;
    private String senderName;
    private String adminAdress;
    private String subject;
    private String messageBody;

    public EmailManager(String _senderAdress, String _senderPassword)
    {
        senderAdress = _senderAdress;
        senderPassword = _senderPassword;
        senderName = "The Machine";
        adminAdress = "kimsavinfo@gmail.com"; // recipient email address
        subject = "QRCheese";
        messageBody = "QRCode non identifié";
    }

    public Message createMessage(String _messageBody)
    {
        Session session = createSessionObject();
        messageBody = _messageBody;

        Message message = null;
        try {
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderAdress, senderName));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(adminAdress, adminAdress));
            message.setSubject(subject);
            message.setText(messageBody);
        }
        catch (AddressException e)
        {
            Log.e("sendMail - AddressException", e.toString());
        }
        catch (MessagingException e)
        {
            Log.e("sendMail - MessagingException", e.toString());
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e("sendMail - UnsupportedEncodingException", e.toString());
        }

        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(senderAdress, senderPassword);
            }
        });
    }
}

