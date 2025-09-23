package com.ftnteam11_2025.pki.pki_system.user.validation;

import com.ftnteam11_2025.pki.pki_system.user.model.UserRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AllowedRolesValidator implements ConstraintValidator<AllowedRoles, UserRole> {
    private UserRole[] allowedRoles;

    @Override
    public void initialize(AllowedRoles annotation) {
        this.allowedRoles = annotation.value();
    }

    @Override
    public boolean isValid(UserRole userRole, ConstraintValidatorContext context) {
        if (userRole == null) {
            return false; // Null is invalid
        }
        for (UserRole allowedRole : allowedRoles) {
            if (userRole == allowedRole) {
                return true;
            }
        }
        return false;
    }
}
