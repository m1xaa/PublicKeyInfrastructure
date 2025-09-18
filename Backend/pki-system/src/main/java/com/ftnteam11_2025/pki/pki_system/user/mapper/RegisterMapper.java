package com.ftnteam11_2025.pki.pki_system.user.mapper;

import com.ftnteam11_2025.pki.pki_system.user.dto.RegisterResponseDTO;
import com.ftnteam11_2025.pki.pki_system.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegisterMapper {

    @Mapping(target = "email", source = "account.email")
    RegisterResponseDTO toResponseDTO(User user);
}
