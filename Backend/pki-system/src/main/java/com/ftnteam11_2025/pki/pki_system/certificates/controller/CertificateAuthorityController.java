package com.ftnteam11_2025.pki.pki_system.certificates.controller;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateRequestDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.dto.CertificateResponseDTO;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateAuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable("id") UUID id) throws Exception {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"keystore.jks\"")
                .body(certificateAuthorityService.downloadCertificateAuthority(id));
    }
}
