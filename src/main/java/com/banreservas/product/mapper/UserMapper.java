package com.banreservas.product.mapper;

import com.banreservas.openapi.models.UserRegistrationRequestDto;
import com.banreservas.openapi.models.UserResponseDto;
import com.banreservas.product.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

    User toUser(UserRegistrationRequestDto userRegistrationRequestDto);

    UserResponseDto toDto(User user);
}
