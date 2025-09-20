package com.ftnteam11_2025.pki.pki_system.organization.controller;

import com.ftnteam11_2025.pki.pki_system.organization.dto.CreateOrganizationRequestDTO;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationHierarchy;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationResponseDTO;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationResponseMinDTO;
import com.ftnteam11_2025.pki.pki_system.organization.service.interfaces.IOrganizationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final IOrganizationService organizationService;


    @GetMapping
    public ResponseEntity<List<OrganizationResponseMinDTO>> getAllOrganization(){
        return ResponseEntity.ok(organizationService.getAll());
    }

    // CA
    @Secured({"ROLE_ADMIN", "ROLE_CA"})
    @GetMapping("/hierarchy")
    public ResponseEntity<List<OrganizationHierarchy>> getOrganizationHierarchy(){
        return ResponseEntity.ok(organizationService.getOrganizationHierarchy());
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public ResponseEntity<OrganizationResponseDTO> createOrganization(@ModelAttribute CreateOrganizationRequestDTO dto){
        return ResponseEntity.ok(organizationService.create(dto));
    }

}
