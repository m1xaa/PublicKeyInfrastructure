package com.ftnteam11_2025.pki.pki_system.user.service;

import com.ftnteam11_2025.pki.pki_system.user.dto.RegisterRequestDTO;
import com.ftnteam11_2025.pki.pki_system.user.dto.UserResponseDTO;
import com.ftnteam11_2025.pki.pki_system.user.mapper.UserMapper;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    public User createUser(@Valid RegisterRequestDTO registerRequestDTO) {
        User user = userMapper.toUser(registerRequestDTO);
        userRepository.save(user);
        return user;
    }
}
