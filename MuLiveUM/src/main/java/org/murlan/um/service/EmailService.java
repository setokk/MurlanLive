package org.murlan.um.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class EmailService {
    @Value("${spring.mail.username}")
    private String email;

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String to, String subject, String html, MimeMessageHelperConsumer customMimeAttributes) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setFrom(email);
        helper.setSubject(subject);
        helper.setText(html, true);
        customMimeAttributes.accept(helper);

        mailSender.send(mimeMessage);
    }

    @FunctionalInterface
    public interface MimeMessageHelperConsumer {
        void accept(MimeMessageHelper helper) throws MessagingException;
    }
}