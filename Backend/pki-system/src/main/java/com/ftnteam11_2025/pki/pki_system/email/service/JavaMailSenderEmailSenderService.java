package com.ftnteam11_2025.pki.pki_system.email.service;

import com.ftnteam11_2025.pki.pki_system.config.JavaMailSenderConfigProperties;
import com.ftnteam11_2025.pki.pki_system.email.dto.EmailDTO;
import com.ftnteam11_2025.pki.pki_system.email.exception.EmailSendFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Primary
@Service
@RequiredArgsConstructor
public class JavaMailSenderEmailSenderService implements EmailSenderService {
    private final JavaMailSenderConfigProperties config;
    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(EmailDTO email) throws EmailSendFailedException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(config.getFromEmail(), config.getFromName());
            helper.setTo(email.getRecipientEmail());
            helper.setSubject(email.getSubject());
            helper.setText(email.getBody(), true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSendFailedException(e.getMessage());
        }

        mailSender.send(message);
    }
}
