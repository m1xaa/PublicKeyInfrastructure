package com.ftnteam11_2025.pki.pki_system.email.service;

import com.ftnteam11_2025.pki.pki_system.email.dto.ActivationEmailBodyDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;

@Service
public class ThymeleafTemplateProcessorService  implements TemplateProcessorService {
    private final String resourceUrl;

    private final SpringTemplateEngine templateEngine;

    public ThymeleafTemplateProcessorService(
            @Value("${email.static.resource-url}") String resourceUrl,
            SpringTemplateEngine templateEngine
    ) {
        this.resourceUrl = resourceUrl;
        this.templateEngine = templateEngine;
    }

    private Context getEmailTemplateContext() {
        Context context = new Context();
        context.setVariable("resourceUrl", resourceUrl);
        context.setVariable("currentYear", LocalDate.now().getYear());
        return context;
    }


    @Override
    public String getAccountActivationEmailBody(ActivationEmailBodyDTO dto) {
        Context context = getEmailTemplateContext();
        context.setVariable("name", dto.getFirstName() + " " + dto.getLastName());
        context.setVariable("activationUrl", dto.getActivationUrl());

        return templateEngine.process("activate", context);
    }

    @Override
    public String getAccountActivationEmailCABody(ActivationEmailBodyDTO dto) {
        Context context = getEmailTemplateContext();
        context.setVariable("name", dto.getFirstName() + " " + dto.getLastName());
        context.setVariable("activationUrl", dto.getActivationUrl());

        return templateEngine.process("activate", context);
    }


}

