package com.technology.user.services;

import com.technology.cart.exceptions.UserNotFoundException;
import com.technology.employee.repositories.EmployeeRepository;
import com.technology.role.enums.Role;
import com.technology.security.adapters.SecurityUser;
import com.technology.security.jwt.models.Token;
import com.technology.security.jwt.models.TokenType;
import com.technology.security.jwt.repositores.TokenRepository;
import com.technology.security.jwt.services.JwtService;
import com.technology.user.models.User;
import com.technology.user.repositories.UserRepository;
import com.technology.user.requests.AuthenticationRequest;
import com.technology.user.requests.RegistrationRequest;
import com.technology.user.response.AuthenticationResponse;
import com.technology.validation.email.services.EmailSenderService;
import com.technology.validation.otp.models.Otp;
import com.technology.validation.otp.repositories.OtpRepository;
import com.technology.validation.otp.services.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${rabbitmq.employee.topic.error}")
    private String errorTopic;

    @Value("${rabbitmq.binding.key.error}")
    private String orderBindingKey;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final OtpService otpService;
    private final EmailSenderService emailSenderService;
    private final EmployeeRepository employeeRepository;
    private final OtpRepository otpRepository;
    private final RabbitTemplate rabbitTemplate;
    private String uuid = "";

    //TODO EXTRACT THE USER INSERTION INTO THE SECURITY CONTEXT AFTER THE OTP IS VERIFIED

    //TODO WHAT IF THE USER HAS REGISTERED FIRST AND ONLY THEN GOT ACCESS TO EMPLOYEE ACCOUNT
    private String generateUniqueIdentifier() {
        return UUID.randomUUID().toString();
    }

    //TODO ON THE CLIENT SIDE IMPLEMENT REDIRECTION TO OTP CONFIRMATION
    @Transactional
    public void register(RegistrationRequest registrationRequest) {
        String email = registrationRequest.getEmail();
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            if (user.getIsEnabled()) {
                authenticate(new AuthenticationRequest(registrationRequest.getEmail()
                        , registrationRequest.getPassword()));
                return;
            }
            revokeOtps(user.getOtps());
            generateEmailValidationOtp(email);
            return;

        }

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

    /*throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "User already exist.");*/
    //TODO CHECK

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String email = request.getEmail();
         try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (Exception e) {
             User user = userRepository.findUserByEmail(request.getEmail())
                     .orElseThrow(() -> new UserNotFoundException("User " + email + " not found"));
             if(!user.getIsEnabled()){
                 rabbitTemplate.convertAndSend(
                         errorTopic,
                         orderBindingKey,
                         email);
                 throw new ResponseStatusException(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS,
                         "User is not enabled.");
             }
             throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                     "The incorrect username or password were provided");
         }

        User user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User " + email + " not found"));
        return saveTokensAndCreateResponse(user);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @RabbitListener(queues = "${rabbitmq.message.queue.error}")
    public void handleException(String email) {
        userRepository.findUserByEmail(email)
                .ifPresent(user ->{
                    revokeOtps(user.getOtps());
                    generateEmailValidationOtp(email);
                });
    }

    public void revokeOtps(List<Otp> otps){
        if (!otps.isEmpty()) {
            otps.forEach(otp -> {
                otp.setExpired(true);
                otp.setRevoked(true);
            });
            otpRepository.saveAll(otps);
        }
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
