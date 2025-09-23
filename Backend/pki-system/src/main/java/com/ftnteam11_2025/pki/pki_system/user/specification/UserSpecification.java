package com.ftnteam11_2025.pki.pki_system.user.specification;

import com.ftnteam11_2025.pki.pki_system.user.model.Account;
import com.ftnteam11_2025.pki.pki_system.user.model.RegistrationRequest;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserSpecification {
    public Specification<User> createSpecification(long organizationId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("organization").get("id"), organizationId));

            Join<User, Account> accountJoin = root.join("account", JoinType.INNER);
            predicates.add(cb.equal(accountJoin.get("status"), "PENDING"));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
