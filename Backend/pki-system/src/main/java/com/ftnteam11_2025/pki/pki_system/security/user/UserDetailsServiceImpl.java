package com.ftnteam11_2025.pki.pki_system.security.user;

import com.ftnteam11_2025.pki.pki_system.user.model.Account;
import com.ftnteam11_2025.pki.pki_system.user.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find user"));
        return new UserDetailsImpl(account);
    }
}
