package com.ftnteam11_2025.pki.pki_system.user.repository;


import com.ftnteam11_2025.pki.pki_system.organization.model.Organization;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import com.ftnteam11_2025.pki.pki_system.user.model.UserRole;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.management.relation.Role;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    List<User> findAllByRoleNot(UserRole role);
    List<User> findAllByRoleNotAndOrganization(UserRole role, Organization organization);
    @Query("""
    SELECT u.organization
    FROM User u
    WHERE u.id = :id
""")
    Optional<Organization> findOrganizationByUserId(@Param("id") Long userId);
}