package com.ftnteam11_2025.pki.pki_system.certificates.controller;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRequestDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateAuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/ca")
@RequiredArgsConstructor
public class CertificateAuthorityController {
    private final ICertificateAuthorityService certificateAuthorityService;
    private final com.ftnteam11_2025.pki.pki_system.certificates.service.impl.CertificateAuthority certificateAuthority;

    @PostMapping
    public ResponseEntity<CertificateAuthority> create(@RequestBody CertificateRequestDTO req) throws Exception {
        return ResponseEntity.ok(certificateAuthorityService.createCA(req));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        certificateAuthority.readJKSFIle();
        return null;
    }
}
