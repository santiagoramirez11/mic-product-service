package com.banreservas.product.mapper;

import com.banreservas.openapi.models.AuthenticationResponseDto;
import com.banreservas.product.security.TokenInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserAuthenticationRequestMapper {

    UserAuthenticationRequestMapper AUTHENTICATION_MAPPER = Mappers.getMapper(UserAuthenticationRequestMapper.class);

    AuthenticationResponseDto toDto(TokenInfo tokenInfo);
}
