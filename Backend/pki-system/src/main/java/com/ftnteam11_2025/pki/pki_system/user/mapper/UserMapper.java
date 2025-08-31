package com.ftnteam11_2025.pki.pki_system.user.mapper;

import com.ftnteam11_2025.pki.pki_system.user.dto.RegisterRequestDTO;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userRole", target = "role")
    User toUser(RegisterRequestDTO registerRequestDTO);
}
