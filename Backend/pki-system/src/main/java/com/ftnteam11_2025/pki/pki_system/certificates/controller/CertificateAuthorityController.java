package com.ftnteam11_2025.pki.pki_system.certificates.controller;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRequestDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateResponseDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateAuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/certificate")
@RequiredArgsConstructor
public class CertificateAuthorityController {
    private final ICertificateAuthorityService certificateAuthorityService;

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<CertificateResponseDTO> create(@RequestBody CertificateRequestDTO req) throws Exception {
        return ResponseEntity.ok(certificateAuthorityService.createCertificateAuthority(req));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/parent")
    public ResponseEntity<List<CertificateResponseDTO>> getAllParent() {
        return ResponseEntity.ok(certificateAuthorityService.getParentCertificate());
    }
}
