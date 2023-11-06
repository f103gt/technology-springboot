package com.technology.user.services;

import com.technology.cart.exceptions.UserNotFoundException;
import com.technology.role.enums.Role;
import com.technology.security.adapters.SecurityUser;
import com.technology.security.jwt.models.Token;
import com.technology.security.jwt.models.TokenType;
import com.technology.security.jwt.repositores.TokenRepository;
import com.technology.security.jwt.services.JwtService;
import com.technology.user.errors.UserAlreadyExistsException;
import com.technology.user.models.NewEmployee;
import com.technology.user.models.User;
import com.technology.user.repositories.NewEmployeeRepository;
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

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final NewEmployeeRepository newEmployeeRepository;

    @Transactional
    public AuthenticationResponse register(RegistrationRequest registrationRequest)
            throws UserAlreadyExistsException {
        String email = registrationRequest.getEmail();
        User user = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(email)
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .isEnabled(true)
                .build();
        newEmployeeRepository.findNewEmployeeByEmail(email)
                .ifPresentOrElse(
                        (employee) -> {
                            user.setRole(employee.getRole());
                            employee.setRegistered(true);
                            newEmployeeRepository.save(employee);
                        },
                        () -> user.setRole(Role.USER)
                );

        userRepository.save(user);
        String role = user.getRole().name();
        SecurityUser securityUser = new SecurityUser(user);
        String jwtToken = jwtService.generateToken(Map.of("role", role), securityUser);
        String refreshToken = jwtService.generateRefreshToken();
        return new AuthenticationResponse(jwtToken, refreshToken, user.getFirstName(), user.getLastName(), role);
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String email = request.getEmail();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );
        User user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User " + email + " not found"));
        //TODO replace oneToMany mapping with oneToOne
        String role = user.getRole().name();
        Map<String, Object> claims = Map.of("role", role);
        SecurityUser securityUser = new SecurityUser(user);
        String jwtToken = jwtService.generateToken(claims, securityUser);
        String refreshToken = jwtService.generateRefreshToken();
        tokenRepository.save(Token.builder()
                .token(jwtToken)
                .type(TokenType.BEARER)
                .user(user)
                .expired(false)
                .revoked(false)
                .build());
        tokenRepository.save(Token.builder()
                .token(refreshToken)
                .user(user)
                .type(TokenType.REFRESH)
                .expired(false)
                .revoked(false)
                .build());
        return new AuthenticationResponse(jwtToken, refreshToken, user.getFirstName(), user.getLastName(), role);
    }
}
