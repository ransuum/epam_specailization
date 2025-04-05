package org.epam.utils.mappers;

import org.epam.models.dto.UserDto;
import org.epam.models.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDto(Users users);
}
