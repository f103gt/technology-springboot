package com.technology.user.registration.services.user;

import com.technology.user.registration.dto.UserDto;
import com.technology.user.registration.errors.RoleNotFoundException;
import com.technology.user.registration.errors.UserAlreadyExistsException;
import com.technology.address.regitration.request.AddressRegistrationRequest;
import com.technology.user.registration.registration.requests.user.UserRegistrationRequest;

import java.util.List;

public interface UserService {
    void registerUser(UserRegistrationRequest userRegistrationRequest,
                      AddressRegistrationRequest addressRegistrationRequest)
            throws RoleNotFoundException, UserAlreadyExistsException;

    List<UserDto> getAllUsers();
}
