package com.ftnteam11_2025.pki.pki_system.email.service;

import com.ftnteam11_2025.pki.pki_system.email.dto.ActivationEmailBodyDTO;
import com.ftnteam11_2025.pki.pki_system.email.dto.EmailDTO;
import com.ftnteam11_2025.pki.pki_system.user.model.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailGeneratorService {
    private final TemplateProcessorService templateProcessorService;

    @Value("${frontend-url}")
    private String frontendUrl;

    public EmailDTO getAccountActivationEmail(RegistrationRequest registrationRequest) {
        final String email = registrationRequest.getEmail();

        final String activationUrl = frontendUrl + "/user/activate?code=" + registrationRequest.getVerificationCode();
        final ActivationEmailBodyDTO bodyDTO = new ActivationEmailBodyDTO(
                registrationRequest.getUser().getFirstName(),
                registrationRequest.getUser().getLastName(),
                activationUrl
        );
        final String body = templateProcessorService.getAccountActivationEmailBody(bodyDTO);

        return new EmailDTO(email, "Activate your Account at Event Planner", body);
    }

    public EmailDTO getAccountActivationEmailCA(RegistrationRequest registrationRequest) {
        final String email = registrationRequest.getEmail();
        final String activationUrl = frontendUrl + "/user/activate/ca?code=" + registrationRequest.getVerificationCode();
        final ActivationEmailBodyDTO bodyDTO = new ActivationEmailBodyDTO(
                registrationRequest.getUser().getFirstName(),
                registrationRequest.getUser().getLastName(),
                activationUrl
        );
        final String body = templateProcessorService.getAccountActivationEmailCABody(bodyDTO);

        return new EmailDTO(email, "Activate your Account at Event Planner", body);
    }
}
