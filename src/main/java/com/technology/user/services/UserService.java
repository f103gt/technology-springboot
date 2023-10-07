package com.technology.user.services;

import com.technology.user.dto.UserDto;
import com.technology.role.errors.RoleNotFoundException;
import com.technology.user.errors.UserAlreadyExistsException;
import com.technology.address.regitration.request.AddressRegistrationRequest;
import com.technology.user.requests.user.UserRegistrationRequest;

import java.util.List;

public interface UserService {
    void registerUser(UserRegistrationRequest userRegistrationRequest,
                      AddressRegistrationRequest addressRegistrationRequest)
            throws RoleNotFoundException, UserAlreadyExistsException;

    List<UserDto> getAllUsers();
}
