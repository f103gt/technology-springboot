package com.technology.registration.controllers;

import com.technology.registration.registration.requests.address.AddressRegistrationRequest;
import com.technology.registration.registration.requests.general.RegistrationRequest;
import com.technology.registration.registration.requests.user.UserRegistrationRequest;
import com.technology.registration.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public ResponseEntity<String> processRegistrationUser(@RequestBody RegistrationRequest registrationRequest){
        userService.registerUser(registrationRequest.getUserRegistrationRequest(),
                registrationRequest.getAddressRegistrationRequest());
        return ResponseEntity.ok("User was registered successfully");
    }


    @GetMapping("/test")
    public String testMethod(){return "test";}
}
