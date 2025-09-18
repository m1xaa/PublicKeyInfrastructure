package com.ftnteam11_2025.pki.pki_system.organization.service.impl;

import com.ftnteam11_2025.pki.pki_system.certificates.model.CertificateAuthority;
import com.ftnteam11_2025.pki.pki_system.certificates.repository.CertificateAuthorityRepository;
import com.ftnteam11_2025.pki.pki_system.certificates.service.impl.CryptoUtils;
import com.ftnteam11_2025.pki.pki_system.certificates.service.impl.PasswordUtils;
import com.ftnteam11_2025.pki.pki_system.organization.dto.*;
import com.ftnteam11_2025.pki.pki_system.organization.mapper.HierarchyMapper;
import com.ftnteam11_2025.pki.pki_system.organization.mapper.OrganizationMapper;
import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import com.ftnteam11_2025.pki.pki_system.organization.repository.OrganizationRepository;
import com.ftnteam11_2025.pki.pki_system.organization.service.interfaces.IOrganizationService;
import com.ftnteam11_2025.pki.pki_system.util.exception.BadRequestError;
import com.ftnteam11_2025.pki.pki_system.util.exception.InvalidRequestError;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationService implements IOrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;
    private final CertificateAuthorityRepository certificateAuthorityRepository;

    @Value("${security.master-key}")
    private String masterKey;

    @Override
    public Organization saveOrganization(Organization org) {


        if(org.getName().length() < 2){
            throw new BadRequestError("Name must be at least 2 characters");
        }

        return organizationRepository.save(org);
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

    @Override
    public OrganizationResponseDTO create(CreateOrganizationRequestDTO dto) {
        try {
            String keyStorePassword = PasswordUtils.generateRandomPassword(24);
            String privateKeyPassword = PasswordUtils.generateRandomPassword(24);
            String encryptedKeyStorePassword = CryptoUtils.encrypt(keyStorePassword, masterKey);
            String encryptedPrivateKeyPassword = CryptoUtils.encrypt(privateKeyPassword, masterKey);

            Organization organization = new Organization();
            organization.setName(dto.getName());
            organization.setEncryptedKeyStorePassword(encryptedKeyStorePassword);
            organization.setEncryptedPrivateKeyPassword(encryptedPrivateKeyPassword);
            this.saveOrganization(organization);
            return organizationMapper.toOrganizationResponseDTO(organization);
        }
        catch (Exception e) {
            return new OrganizationResponseDTO();
        }

    }

}
