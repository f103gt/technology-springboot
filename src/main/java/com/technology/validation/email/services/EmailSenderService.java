package com.technology.validation.email.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService{
    private final JavaMailSender mailSender;

    public void sendMessage(String receiver,
                            String body,
                            String subject){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("technology.com");
        message.setTo(receiver);
        message.setText(body);
        message.setSubject(subject);
        mailSender.send(message);
    }

    public void sendMessageWithAttachment(
            String receiverEmail,
            String htmlBody,
            String subject
    ) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        helper.setFrom("technology.com");
        helper.setTo(receiverEmail);
        helper.setSubject(subject);
        helper.setText(htmlBody,true);
        mailSender.send(message);
    }
}
