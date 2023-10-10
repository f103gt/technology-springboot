package com.technology.user.controllers;

import com.technology.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    /*@PostMapping("/registration")
    public ResponseEntity<String> processRegistrationUser(@RequestBody RegistrationRequest registrationRequest){
        userService.registerUser(registrationRequest.getUserRegistrationRequest(),
                registrationRequest.getAddressRegistrationRequest());
        return ResponseEntity.ok("User was registered successfully");
    }*/

    //TODO implement:
    // address registration controller
    // payment registration controller


    @GetMapping("/test")
    public String testMethod(){return "test";}
}
