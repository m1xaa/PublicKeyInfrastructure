package com.ftnteam11_2025.pki.pki_system.organization.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.repository.CertificateAuthorityRepository;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationHierarchy;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationNode;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationRequestDTO;
import com.ftnteam11_2025.pki.pki_system.organization.dto.OrganizationResponseDTO;
import com.ftnteam11_2025.pki.pki_system.organization.mapper.HierarchyMapper;
import com.ftnteam11_2025.pki.pki_system.organization.mapper.OrganizationMapper;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import com.ftnteam11_2025.pki.pki_system.organization.repository.OrganizationRepository;
import com.ftnteam11_2025.pki.pki_system.organization.service.interfaces.IOrganizationService;
import com.ftnteam11_2025.pki.pki_system.util.exception.BadRequestError;
import com.ftnteam11_2025.pki.pki_system.util.exception.InvalidRequestError;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrganizationService implements IOrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;
    private final CertificateAuthorityRepository certificateAuthorityRepository;

    @Override
    public Organization createOrganization(OrganizationRequestDTO req) {
        Optional<Organization> organization = organizationRepository.findByName(req.getName());
        if(organization.isPresent()) {
            return organization.get();
        }

        if(req.getName().length() < 2){
            throw new BadRequestError("Name must be at least 2 characters");
        }

        return organizationRepository.save(organizationMapper.toOrganization(req));
    }

    @Override
    public List<OrganizationResponseDTO> getAllOrganization() {
        return organizationRepository.findAll().stream().map(organizationMapper::toOrganizationResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrganizationHierarchy> getOrganizationHierarchy() {
        List<Organization> organizations = organizationRepository.findAll();
        List<OrganizationHierarchy> result = new ArrayList<>();

        for (Organization org : organizations) {
            List<CertificateAuthority> certs = certificateAuthorityRepository.findAllByOrganization(org);

            // Mapiranje po ID-u
            Map<UUID, OrganizationNode> nodeMap = new HashMap<>();
            for (CertificateAuthority c : certs) {
                nodeMap.put(c.getId(), HierarchyMapper.toNode(c));
            }

            // Povezivanje children-a
            List<OrganizationNode> roots = new ArrayList<>();
            for (CertificateAuthority c : certs) {
                OrganizationNode node = nodeMap.get(c.getId());
                if (c.getIssuer() != null) {
                    OrganizationNode parentNode = nodeMap.get(c.getIssuer().getId());
                    if (parentNode != null) {
                        parentNode.getChildren().add(node);
                    }
                } else {
                    // RootCA
                    roots.add(node);
                }
            }

            result.add(OrganizationHierarchy.builder()
                    .organizationId(org.getId())
                    .organizationName(org.getName())
                    .rootCertificates(roots)
                    .build());
        }

        return result;
    }

}
