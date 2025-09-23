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
import com.ftnteam11_2025.pki.pki_system.user.repository.UserRepository;
import com.ftnteam11_2025.pki.pki_system.user.service.CurrentUserService;
import com.ftnteam11_2025.pki.pki_system.util.exception.BadRequestError;
import com.ftnteam11_2025.pki.pki_system.util.exception.InvalidRequestError;
import com.ftnteam11_2025.pki.pki_system.util.exception.NotFoundError;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrganizationService implements IOrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;
    private final CertificateAuthorityRepository certificateAuthorityRepository;
    private final CurrentUserService currentUserService;

    @Value("${security.master-key}")
    private String masterKey;

    public OrganizationService(OrganizationRepository organizationRepository, OrganizationMapper organizationMapper, CertificateAuthorityRepository certificateAuthorityRepository, CurrentUserService currentUserService) {
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
        this.certificateAuthorityRepository = certificateAuthorityRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public Organization saveOrganization(Organization org) {


        if(org.getName().length() < 2){
            throw new BadRequestError("Name must be at least 2 characters");
        }

        return organizationRepository.save(org);
    }

    @Override
    public List<OrganizationResponseMinDTO> getAll() {
        return organizationRepository.findAll().stream().map(organizationMapper::toOrganizationResponseMinDTO).collect(Collectors.toList());
    }



    @Override
    public List<OrganizationHierarchy> getOrganizationHierarchy() {
        List<Organization> organizations = organizationRepository.findAll();
        List<OrganizationHierarchy> result = new ArrayList<>();

        for (Organization org : organizations) {
            List<OrganizationNode> roots = getChild(org);

            result.add(OrganizationHierarchy.builder()
                    .organizationId(org.getId())
                    .organizationName(org.getName())
                    .rootCertificates(roots)
                    .build());
        }

        return result;
    }

    private List<OrganizationNode> getChild(Organization org){
        List<CertificateAuthority> certs = certificateAuthorityRepository.findAllByOrganization(org);
        Map<UUID, OrganizationNode> nodeMap = new HashMap<>();
        for (CertificateAuthority c : certs) {
            nodeMap.put(c.getId(), HierarchyMapper.toNode(c));
        }
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
        return roots;
    }

    @Override
    public OrganizationHierarchy getOrganizationHierarchyByOrganization() {
        Organization org = organizationRepository.findById(currentUserService.getCurrentUser().getOrganization().getId()).orElseThrow(()-> new NotFoundError("Organization not found"));
        return OrganizationHierarchy.builder()
                .organizationId(org.getId())
                .organizationName(org.getName())
                .rootCertificates(getChild(org))
                .build();
    }

    @Override
    public OrganizationResponseDTO create(CreateOrganizationRequestDTO dto) {
        try {
            if(dto.getName().length() < 2){
                throw new BadRequestError("Organization name must be at least 2 characters");
            }
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
