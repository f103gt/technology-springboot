package com.technology.user.mappers;

import com.technology.user.dto.UserDto;
import com.technology.user.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userToUserDto(User user);
}
