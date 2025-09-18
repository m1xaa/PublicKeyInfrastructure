package com.ftnteam11_2025.pki.pki_system.security.user;

import com.ftnteam11_2025.pki.pki_system.user.model.Account;
import com.ftnteam11_2025.pki.pki_system.user.model.AccountStatus;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.model.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {
    @Getter
    private final long userId;

    @Getter
    private final long accountId;

    private final String username;

    private final String password;

    @Getter
    private final UserRole userRole;

    private final boolean isEnabled;

    public UserDetailsImpl(long userId, long accountId, String username, UserRole userRole) {
        this.userId = userId;
        this.username = username;
        this.password = null;
        this.userRole = userRole;
        this.isEnabled = true;
        this.accountId = accountId;
    }

    public UserDetailsImpl(Account account) {
        accountId = account.getId();
        userId = account.getUser().getId();
        username = account.getEmail();
        password = account.getPassword();
        userRole = account.getUser().getRole();
        isEnabled = account.getStatus().equals(AccountStatus.ACTIVE);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (Objects.requireNonNull(userRole) == UserRole.ADMINISTRATOR) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (Objects.requireNonNull(userRole) == UserRole.CA) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CA"));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isEnabled() {return isEnabled;}
}
