package com.ftnteam11_2025.pki.pki_system.email.service;

import com.ftnteam11_2025.pki.pki_system.email.dto.EmailDTO;
import com.ftnteam11_2025.pki.pki_system.email.exception.EmailSendFailedException;

public interface EmailSenderService {
    void sendEmail(EmailDTO email) throws EmailSendFailedException;
}
