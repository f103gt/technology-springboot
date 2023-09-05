package com.technology.registration.services.user;

import com.technology.registration.dto.display.DisplayUserDto;
import com.technology.registration.dto.input.UserDto;
import com.technology.registration.errors.RoleNotFoundException;
import com.technology.registration.errors.UserAlreadyExistsException;
import com.technology.registration.registration.requests.AddressRegistrationRequest;
import com.technology.registration.registration.requests.UserRegistrationRequest;

import java.util.List;

public interface UserService {
    void registerUser(UserRegistrationRequest userRegistrationRequest,
                      AddressRegistrationRequest addressRegistrationRequest)
            throws RoleNotFoundException, UserAlreadyExistsException;

    List<DisplayUserDto> getAllUsers();
}
