package uz.medsu.sevice.serviceImpl;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uz.medsu.sevice.EmailService;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender emailSender;

    @Override
    public void sendSimpleMessage(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            emailSender.send(message);
        } catch (MailException e) {
            // log error
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void sendCodeMessage(String to, String subject, String content) {
        try {
            String text = String.format("""
                    <h2 style="padding: 0">Your confirmation code:</h2><h1 style="padding: 0" id="myContent" onclick="myFunction()">%s</h1><h2 style="padding: 0">do not give the code to strangers!</h2>
                    """, content);
            MimeMessage message = emailSender.createMimeMessage();
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setContent(text, "text/html");
            emailSender.send(message);
        } catch (MailException e) {
            // log error
            e.printStackTrace();
        }
    }
}