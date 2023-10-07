package com.technology.user.services;

import com.technology.user.dto.UserDto;
import com.technology.role.errors.RoleNotFoundException;
import com.technology.user.errors.UserAlreadyExistsException;
import com.technology.role.models.Role;
import com.technology.user.models.User;
import com.technology.address.regitration.request.AddressRegistrationRequest;
import com.technology.user.requests.user.UserRegistrationRequest;
import com.technology.role.repositories.RoleRepository;
import com.technology.user.repositories.UserRepository;
import com.technology.address.services.AddressService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AddressService addressService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           AddressService addressService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.addressService = addressService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void registerUser(UserRegistrationRequest userRegistrationRequest,
                             AddressRegistrationRequest addressRegistrationRequest)
            throws RoleNotFoundException,UserAlreadyExistsException {
        String email = userRegistrationRequest.email();
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("User " + email + " already exists");
        }
        Optional<Role> roleOptional = roleRepository.findRoleByRoleName("USER");
        if(roleOptional.isEmpty()){
            throw new RoleNotFoundException("Role USER is not found");
        }
        User user = User.builder()
                .firstName(userRegistrationRequest.firstName())
                .lastName(userRegistrationRequest.lastName())
                .email(email)
                .password(passwordEncoder.encode(userRegistrationRequest.password()))
                .isEnabled(true)
                .roles(Set.of(roleOptional.get()))
                .build();
        //.addresses(addressService.registerUserAddress(addressRegistrationRequest))
        userRepository.save(user);
    }

    @Override
    @Transactional
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        )).collect(Collectors.toList());
    }
}
