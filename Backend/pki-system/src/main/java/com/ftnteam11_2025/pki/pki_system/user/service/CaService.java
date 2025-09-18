package com.ftnteam11_2025.pki.pki_system.user.service;

import com.ftnteam11_2025.pki.pki_system.user.dto.RegisterResponseDTO;
import com.ftnteam11_2025.pki.pki_system.user.dto.AccountStatusUpdateDTO;
import com.ftnteam11_2025.pki.pki_system.user.mapper.RegisterMapper;
import com.ftnteam11_2025.pki.pki_system.user.model.Account;
import com.ftnteam11_2025.pki.pki_system.user.model.AccountStatus;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.repository.AccountRepository;
import com.ftnteam11_2025.pki.pki_system.user.repository.UserRepository;
import com.ftnteam11_2025.pki.pki_system.user.specification.UserSpecification;
import com.ftnteam11_2025.pki.pki_system.util.exception.NotFoundError;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaService {

    private final RegisterMapper registerMapper;
    private final CurrentUserService currentUserService;
    private final UserRepository  userRepository;
    private final UserSpecification userSpecification;
    private final AccountRepository accountRepository;

    public Page<RegisterResponseDTO> getAllPendingUsers(int page, int size) {
        User user = userRepository.findById(currentUserService.getCurrentUserId()).orElseThrow(() -> new NotFoundError("User not found"));
        Pageable pageable = PageRequest.of(page, size);
        Specification<User> specification = userSpecification.createSpecification(user.getOrganization().getId());
        return userRepository.findAll(specification, pageable).map(registerMapper::toResponseDTO);
    }

    public boolean rejectUser(AccountStatusUpdateDTO dto) {
        Account account = accountRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new NotFoundError("Account not found"));
        account.setStatus(AccountStatus.DEACTIVATED);
        accountRepository.save(account);
        return true;
    }

    public boolean acceptUser(AccountStatusUpdateDTO dto) {
        Account account = accountRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new NotFoundError("Account not found"));
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
        return true;
    }
}
