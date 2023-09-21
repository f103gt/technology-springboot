package com.technology.registration.services.user;

import com.technology.registration.dto.UserDto;
import com.technology.registration.errors.RoleNotFoundException;
import com.technology.registration.errors.UserAlreadyExistsException;
import com.technology.registration.registration.requests.address.AddressRegistrationRequest;
import com.technology.registration.registration.requests.user.UserRegistrationRequest;

import java.util.List;

public interface UserService {
    void registerUser(UserRegistrationRequest userRegistrationRequest,
                      AddressRegistrationRequest addressRegistrationRequest)
            throws RoleNotFoundException, UserAlreadyExistsException;

    List<UserDto> getAllUsers();
}
