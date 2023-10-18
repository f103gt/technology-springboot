package com.technology.user.controllers;

import com.technology.user.requests.AuthenticationRequest;
import com.technology.user.requests.RegistrationRequest;
import com.technology.user.response.AuthenticationResponse;
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
    public ResponseEntity<Void> register(
            @RequestBody RegistrationRequest request) {
        return configureResponseEntity(service.register(request));
    }

    @PostMapping("/authenticate")
        public ResponseEntity<Void> authenticate(
            @RequestBody AuthenticationRequest request) {
        return configureResponseEntity(service.authenticate(request));
    }

    private ResponseEntity<Void> configureResponseEntity(AuthenticationResponse response){
        String tokenCookie =
                ResponseCookie.from("token", response.getToken())
                        .secure(true)
                        .httpOnly(true)
                        .path("/")
                        .build()
                        .toString();
        String emailCookie = ResponseCookie.from("email", response.getEmail())
                .secure(true)
                .path("/")
                .build()
                .toString();
        String roleCookie = ResponseCookie.from("role",response.getRole())
                .secure(true)
                .path("/")
                .build()
                .toString();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,tokenCookie)
                .header(HttpHeaders.SET_COOKIE,emailCookie)
                .header(HttpHeaders.SET_COOKIE,roleCookie)
                .build();
    }
}
