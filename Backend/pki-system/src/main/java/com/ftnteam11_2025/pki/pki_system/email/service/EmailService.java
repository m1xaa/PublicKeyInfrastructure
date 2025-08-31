package com.ftnteam11_2025.pki.pki_system.email.service;

import com.ftnteam11_2025.pki.pki_system.email.dto.EmailDTO;
import com.ftnteam11_2025.pki.pki_system.email.exception.EmailSendFailedException;
import com.ftnteam11_2025.pki.pki_system.user.model.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final EmailGeneratorService emailGeneratorService;
    private final EmailSenderService emailSenderService;

    @Async
    public void sendAccountActivationEmail(RegistrationRequest registrationRequest) {
        EmailDTO email = emailGeneratorService.getAccountActivationEmail(registrationRequest);
        try {
            emailSenderService.sendEmail(email);
        } catch (EmailSendFailedException e) {
            logger.warn("Failed to send account activation email for request: {}", registrationRequest.getId());
        }
    }
}
