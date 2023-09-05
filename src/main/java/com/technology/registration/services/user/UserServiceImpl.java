package com.technology.registration.services.user;

import com.technology.registration.dto.display.DisplayUserDto;
import com.technology.registration.errors.RoleNotFoundException;
import com.technology.registration.errors.UserAlreadyExistsException;
import com.technology.registration.models.Role;
import com.technology.registration.models.User;
import com.technology.registration.registration.requests.AddressRegistrationRequest;
import com.technology.registration.registration.requests.UserRegistrationRequest;
import com.technology.registration.repositories.RoleRepository;
import com.technology.registration.repositories.UserRepository;
import com.technology.registration.services.address.AddressService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
                .patronymic(userRegistrationRequest.patronymic())
                .email(email)
                .phoneNumber(userRegistrationRequest.phoneNumber())
                .password(passwordEncoder.encode(userRegistrationRequest.password()))
                .isEnabled(true)
                .roles(roleOptional.get())
                .build();
        //.addresses(addressService.registerUserAddress(addressRegistrationRequest))
        userRepository.save(user);
    }

    @Override
    @Transactional
    public List<DisplayUserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new DisplayUserDto(
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getEmail(),
                user.getPhoneNumber()
        )).collect(Collectors.toList());
    }
}
