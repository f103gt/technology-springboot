package com.technology.user.controllers;

import com.technology.security.jwt.services.LogoutService;
import com.technology.user.requests.AuthenticationRequest;
import com.technology.user.requests.RegistrationRequest;
import com.technology.user.response.AuthenticationResponse;
import com.technology.user.response.JsonAuthResponse;
import com.technology.user.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService service;
    private final LogoutService logoutService;

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Authentication authentication){
        logoutService.logout(request,response,authentication);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody RegistrationRequest request) {
        service.register(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(
            @RequestParam("email") String email
    ) {
        service.generateEmailValidationOtp(email);
        return ResponseEntity.ok().build();
    }

    /*TODO DO I REALLY NEED TO USE CONTROLLER FOR OTP GENERATION
        OR RABBIT MQ WILL BE MORE SUITABLE IN THIS CONTEXT
    *  */
    @GetMapping("/update-otp")
    public ResponseEntity<String> updateOtp(
            @RequestParam("email") String email) {
        service.regenerateEmailValidationOtp(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/otp-verification")
    private ResponseEntity<JsonAuthResponse> otpVerify(
            @RequestBody String otp
    ) {
        return configureResponseEntity(service.verifyOtp(otp));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JsonAuthResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        return configureResponseEntity(service.authenticate(request));
    }

    private ResponseEntity<JsonAuthResponse> configureResponseEntity(AuthenticationResponse response) {
        String tokenCookie =
                ResponseCookie.from("jwtToken", response.getToken())
                        .secure(true)
                        .httpOnly(true)
                        .sameSite("Strict")
                        .path("/")
                        .build()
                        .toString();
        //TODO instead of email return user first name and last name
        String refreshTokenCookie =
                ResponseCookie.from("refreshToken", response.getRefreshToken())
                        .secure(true)
                        .httpOnly(true)
                        .sameSite("Strict")
                        .path("/")
                        .build()
                        .toString();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenCookie)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie)
                .body(new JsonAuthResponse(response.getRole().toLowerCase(),response.getUuid()));
    }
}
