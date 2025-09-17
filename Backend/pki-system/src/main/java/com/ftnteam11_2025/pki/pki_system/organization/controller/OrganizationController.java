package com.ftnteam11_2025.pki.pki_system.organization.controller;

import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationHierarchy;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationResponseDTO;
import com.ftnteam11_2025.pki.pki_system.organization.service.interfaces.IOrganizationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final IOrganizationService organizationService;

    @Secured("ROLE_ADMIN")
    @GetMapping
    public ResponseEntity<List<OrganizationResponseDTO>> getAllOrganization(){
        return ResponseEntity.ok(organizationService.getAllOrganization());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/hierarchy")
    public ResponseEntity<List<OrganizationHierarchy>> getOrganizationHierarchy(){
        return ResponseEntity.ok(organizationService.getOrganizationHierarchy());
    }
}
