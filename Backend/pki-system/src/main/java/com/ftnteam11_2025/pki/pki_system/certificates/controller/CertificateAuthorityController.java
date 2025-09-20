package com.ftnteam11_2025.pki.pki_system.certificates.controller;

import com.ftnteam11_2025.pki.pki_system.certificates.dto.*;
import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateAuthorityService;
import com.ftnteam11_2025.pki.pki_system.certificates.service.interfaces.ICertificateSigningService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/certificates")
@RequiredArgsConstructor
public class CertificateAuthorityController {
    private final ICertificateAuthorityService certificateAuthorityService;
    private final ICertificateSigningService certificateSigningService;

    // CA
    @Secured({"ROLE_ADMIN", "ROLE_CA"})
    @PostMapping
    public ResponseEntity<CertificateResponseDTO> create(@RequestBody CertificateRequestDTO req) throws Exception {
        return ResponseEntity.ok(certificateAuthorityService.createCertificateAuthority(req));
    }

    // CA
    @Secured({"ROLE_ADMIN", "ROLE_CA"})
    @GetMapping("/parent")
    public ResponseEntity<List<CertificateResponseDTO>> getAllParent() {
        return ResponseEntity.ok(certificateAuthorityService.getParentCertificate());
    }

    @Secured({"ROLE_ADMIN", "ROLE_CA"})
    @GetMapping("/parent/{name}")
    public ResponseEntity<List<CertificateResponseDTO>> getAllParent(@PathVariable String name) {
        return ResponseEntity.ok(certificateAuthorityService.getParentCertificateByOrganization(name));
    }

    // mogu svi download?
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable("id") UUID id) throws Exception {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"keystore.jks\"")
                .body(certificateAuthorityService.downloadCertificateAuthority(id));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public ResponseEntity<List<CertificateResponseCard>> getAll() {
        return ResponseEntity.ok(certificateAuthorityService.getCertificates());
    }


    @Secured({"ROLE_ADMIN", "ROLE_CA"})
    @GetMapping("/{id}")
    public ResponseEntity<CertificateDetailsDTO> getCertificateDetails(@PathVariable("id") UUID id) throws Exception {
        return ResponseEntity.ok(certificateAuthorityService.getCertificateDetails(id));
    }

    @PostMapping("/for-user/{userId}")
    public ResponseEntity<Void> createCSRAutogenerate(
            @RequestBody CertificateSigningRequestDTO request,
            @PathVariable Long userId
    ) throws Exception {
        certificateSigningService.createCSRAutogenerate(userId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<OrganizationCACertificatesResponseDTO> getCACertificatesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(certificateSigningService.getOrganizationCACertificates(userId));
    }
}
