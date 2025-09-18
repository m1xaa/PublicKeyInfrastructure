package com.ftnteam11_2025.pki.pki_system.user.service;

import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import com.ftnteam11_2025.pki.pki_system.organization.repository.OrganizationRepository;
import com.ftnteam11_2025.pki.pki_system.user.dto.CARegisterRequestDTO;
import com.ftnteam11_2025.pki.pki_system.user.dto.RegisterRequestDTO;
import com.ftnteam11_2025.pki.pki_system.user.dto.UserResponseDTO;
import com.ftnteam11_2025.pki.pki_system.user.mapper.UserMapper;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.repository.UserRepository;
import com.ftnteam11_2025.pki.pki_system.util.exception.BadRequestError;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    public User createUser(@Valid RegisterRequestDTO registerRequestDTO) {
        User user = userMapper.toUser(registerRequestDTO);
        Optional<Organization> organization  = organizationRepository.findById(registerRequestDTO.getOrganizationId());
        user.setOrganization(organization.get());
        userRepository.save(user);
        return user;
    }

    @Transactional
    public User createUser(@Valid CARegisterRequestDTO registerRequestDTO) {
        User user = userMapper.toUser(registerRequestDTO);
        Optional<Organization> organization  = organizationRepository.findById(registerRequestDTO.getOrganizationId());
        if(organization.isPresent()){
            user.setOrganization(organization.get());
        }else {
            throw new BadRequestError("Invalid organization");
        }
        userRepository.save(user);
        return user;
    }
}
