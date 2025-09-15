package com.ftnteam11_2025.pki.pki_system.organization.service.impl;

import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationRequestDTO;
import com.ftnteam11_2025.pki.pki_system.organization.mapper.OrganizationMapper;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import com.ftnteam11_2025.pki.pki_system.organization.repository.OrganizationRepository;
import com.ftnteam11_2025.pki.pki_system.organization.service.interfaces.IOrganizationService;
import com.ftnteam11_2025.pki.pki_system.util.exception.InvalidRequestError;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class OrganizationService implements IOrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    @Override
    public Organization createOrganization(OrganizationRequestDTO req) {
        Optional<Organization> organization = organizationRepository.findByName(req.getName());
        if(organization.isPresent()) {
            throw new InvalidRequestError("Organization with the name " + req.getName() + " already exists");
        }

        if(req.getName().length() < 2){
            throw new InvalidRequestError("Name must be at least 2 characters");
        }
        if(req.getAlias().length() < 2){
            throw new InvalidRequestError("Alias must be at least 2 characters");
        }

        return organizationRepository.save(organizationMapper.toOrganization(req));
    }
}
