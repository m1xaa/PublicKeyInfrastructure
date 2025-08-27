package com.ftnteam11_2025.pki.pki_system.user.service;

import com.ftnteam11_2025.pki.pki_system.user.dto.RegisterRequestDTO;
import com.ftnteam11_2025.pki.pki_system.user.mapper.UserMapper;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Transactional
    public User createUser(@Valid RegisterRequestDTO registerRequestDTO) {
        User user = userMapper.toUser(registerRequestDTO);
        userRepository.save(user);
        return user;
    }
}
