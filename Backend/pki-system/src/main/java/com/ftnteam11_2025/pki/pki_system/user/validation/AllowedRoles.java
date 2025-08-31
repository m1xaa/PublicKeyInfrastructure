package com.ftnteam11_2025.pki.pki_system.user.validation;

import com.ftnteam11_2025.pki.pki_system.user.model.UserRole;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AllowedRolesValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedRoles {
    String message() default "Role is not allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    UserRole[] value();
}
