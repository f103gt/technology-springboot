package com.technology.user.services;

import com.technology.cart.exceptions.UserNotFoundException;
import com.technology.employee.repositories.EmployeeRepository;
import com.technology.role.enums.Role;
import com.technology.security.adapters.SecurityUser;
import com.technology.security.jwt.models.Token;
import com.technology.security.jwt.models.TokenType;
import com.technology.security.jwt.repositores.TokenRepository;
import com.technology.security.jwt.services.JwtService;
import com.technology.user.errors.UserAlreadyExistsException;
import com.technology.user.models.User;
import com.technology.user.repositories.UserRepository;
import com.technology.user.requests.AuthenticationRequest;
import com.technology.user.requests.RegistrationRequest;
import com.technology.user.response.AuthenticationResponse;
import com.technology.validation.email.services.EmailSenderService;
import com.technology.validation.otp.services.OtpService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final OtpService otpService;
    private final EmailSenderService emailSenderService;
    private final EmployeeRepository employeeRepository;
    private String uuid = "";

    //TODO EXTRACT THE USER INSERTION INTO THE SECURITY CONTEXT AFTER THE OTP IS VERIFIED

    private String generateUniqueIdentifier() {
        return UUID.randomUUID().toString();
    }

    @Transactional
    public void register(RegistrationRequest registrationRequest)
            throws UserAlreadyExistsException {
        String email = registrationRequest.getEmail();
        User user = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(email)
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .isEnabled(false)
                .build();
        employeeRepository.findEmployeeByEmail(email)
                .ifPresentOrElse(
                        (employee) -> {
                            user.setRole(employee.getRole());
                            employee.setRegistered(true);
                            uuid = generateUniqueIdentifier();
                            employee.setUniqueIdentifier(uuid);
                            employeeRepository.save(employee);
                        },
                        () -> user.setRole(Role.USER)
                );

        userRepository.save(user);
        generateEmailValidationOtp(user.getEmail());
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
        return saveTokensAndCreateResponse(user);
    }

    public void generateEmailValidationOtp(String email) {
        emailSenderService.sendMessage(
                email,
                otpService.generateAndSaveOtp(email).getOtp(),
                "Email confirmation"
        );
    }

    public void regenerateEmailValidationOtp(String email) {
        emailSenderService.sendMessage(
                email,
                otpService.updateOtp(email),
                "Email confirmation"
        );
    }

    public AuthenticationResponse verifyOtp(String otp) {
        User user = otpService.otpConfirmation(otp);
        return saveTokensAndCreateResponse(user);
    }

    private AuthenticationResponse saveTokensAndCreateResponse(User user) {
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
        return new AuthenticationResponse(jwtToken, refreshToken, uuid, role);
    }
}
