package ru.kuznetsov.stories.services.register_and_auth;

import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

@Component
public interface MailSender {

    void send(String emailTo, String subject, String message);
}
