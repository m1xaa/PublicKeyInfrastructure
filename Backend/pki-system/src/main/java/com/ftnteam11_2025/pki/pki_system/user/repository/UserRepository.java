package com.ftnteam11_2025.pki.pki_system.user.repository;


import com.ftnteam11_2025.pki.pki_system.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
}