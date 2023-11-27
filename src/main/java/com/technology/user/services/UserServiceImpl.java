package com.technology.user.services;

import com.technology.cart.helpers.CartServiceHelper;
import com.technology.user.dto.UserDto;
import com.technology.user.mappers.UserMapper;
import com.technology.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUser() {
        return userMapper.userToUserDto(
                CartServiceHelper
                        .getSecurityUserFromContext()
                        .getUser());
    }

    @Override
    @Transactional
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }
}
