package com.technology.user.controllers;

import com.technology.user.requests.AuthenticationRequest;
import com.technology.user.requests.RegistrationRequest;
import com.technology.user.response.AuthenticationResponse;
import com.technology.user.response.JsonAuthResponse;
import com.technology.user.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<JsonAuthResponse> register(
            @RequestBody RegistrationRequest request) {
        return configureResponseEntity(service.register(request));
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
                .body(new JsonAuthResponse(response.getRole().toLowerCase(),
                        response.getFirstName(),
                        response.getLastName()));
    }
}
