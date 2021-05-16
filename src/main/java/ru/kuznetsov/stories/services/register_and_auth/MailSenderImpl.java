package ru.kuznetsov.stories.services.register_and_auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailSenderImpl implements MailSender {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String hostname;

    @Autowired
    public MailSenderImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    @Async
    public void send(String emailTo, String subject, String messageText) {
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setSubject(subject);
            MimeMessageHelper helper;
            helper = new MimeMessageHelper(message, true,"UTF-8");
            helper.setFrom(hostname);
            helper.setTo(emailTo);
            helper.setText(messageText, true);
            javaMailSender.send(message);
        } catch (MessagingException ex) {
            System.out.println(ex.getCause());
        }

    }
}
