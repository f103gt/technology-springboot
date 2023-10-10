package com.technology.user.services;

import com.technology.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();
}
