package com.ftnteam11_2025.pki.pki_system.email.service;

import com.ftnteam11_2025.pki.pki_system.email.dto.ActivationEmailBodyDTO;
import org.springframework.stereotype.Service;

public interface TemplateProcessorService {
    String getAccountActivationEmailBody(ActivationEmailBodyDTO dto);
}
