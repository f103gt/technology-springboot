package com.technology.user.services;

import com.technology.cart.exceptions.UserNotFoundException;
import com.technology.role.errors.RoleNotFoundException;
import com.technology.role.models.Role;
import com.technology.role.repositories.RoleRepository;
import com.technology.security.adapters.SecurityUser;
import com.technology.security.jwt.JwtService;
import com.technology.user.errors.UserAlreadyExistsException;
import com.technology.user.models.User;
import com.technology.user.repositories.UserRepository;
import com.technology.user.requests.AuthenticationRequest;
import com.technology.user.requests.RegistrationRequest;
import com.technology.user.response.AuthenticationResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(RegistrationRequest registrationRequest)
            throws RoleNotFoundException, UserAlreadyExistsException {
        String email = registrationRequest.getEmail();
        userRepository.findUserByEmail(email)
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("User " + email + " already exists");
                });
        Role role = roleRepository.findRoleByRoleName("USER")
                .orElseThrow(() -> new RoleNotFoundException("Role USER is not found"));
        User user = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(email)
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .isEnabled(true)
                .roles(Set.of(role))
                .build();
        userRepository.save(user);
        String jwtToken = jwtService.generateToken(Map.of("role",role.getRoleName()),new SecurityUser(user));
        return new AuthenticationResponse(jwtToken);
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String email = request.getEmail();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );
        User user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User " + email + " not found"));
        Map<String,Object> claims = Map.of("role",user.getRoles().iterator().next().getRoleName());
        String jwtToken = jwtService.generateToken(claims,new SecurityUser(user));
        return new AuthenticationResponse(jwtToken);
    }
}
