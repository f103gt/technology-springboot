package com.technology.validation.otp.services;

import com.technology.cart.exceptions.UserNotFoundException;
import com.technology.user.models.User;
import com.technology.user.repositories.UserRepository;
import com.technology.validation.otp.exceptions.OtpExpiredException;
import com.technology.validation.otp.generators.OTPGenerator;
import com.technology.validation.otp.models.Otp;
import com.technology.validation.otp.repositories.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpService {
    @Value("${spring.otp.expiration}")
    private String otpExpiration;

    private final OtpRepository otpRepository;
    private final OTPGenerator otpGenerator;
    private final UserRepository userRepository;

    public Otp generateAndSaveOtp(String email) {
        String otpValue = otpGenerator.generateOTP(8);
        //TODO review the token type setup for the entity (string or token type)
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User " + email + " not found"));
        LocalDateTime currentTime = LocalDateTime.now();
        Otp otp = Otp.builder()
                .otp(otpValue)
                .user(user)
                .issuedTime(currentTime)
                .expirationTime(currentTime.plusMinutes(Long.parseLong(otpExpiration)))
                .expired(false)
                .revoked(false)
                .build();
        otpRepository.save(otp);
        return otp;
    }

    //TODO CHECK WHAT EXCEPTIONS DO NOT EXTEND THE NECESSARY GENERAL EXCEPTIONS

    public User otpConfirmation(String otp) {
        Optional<User> result = Optional.empty();
        Otp presentOtp = otpRepository.findOtpByOtp(otp)
                .orElseThrow(() -> new OtpExpiredException("Incorrect otp"));
        if(LocalDateTime.now().isAfter(presentOtp.getExpirationTime())){
            presentOtp.setExpired(true);
            otpRepository.save(presentOtp);
            throw new OtpExpiredException("Otp " + presentOtp + " is expired");
        }
        if (presentOtp.isExpired()) {
            throw new OtpExpiredException("Otp " + presentOtp + " is expired");
        }
        if (presentOtp.isRevoked()) {
            throw new OtpExpiredException("Otp " + presentOtp + " is revoked");
        }

        User user = presentOtp.getUser();
        user.setIsEnabled(true);
        return userRepository.saveAndFlush(user);
    }

    public String updateOtp(String email){
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User "+ email +" not found"));
        user.getOtps().stream()
                .filter(otp -> !otp.isExpired() && !otp.isRevoked())
                .forEach(otp -> {
                    otp.setRevoked(true);
                    otp.setExpired(true);
                    otpRepository.save(otp);
                });
        Otp otp = generateAndSaveOtp(email);
        user.getOtps().add(otp);
        userRepository.save(user);
        return otp.getOtp();
    }
}

