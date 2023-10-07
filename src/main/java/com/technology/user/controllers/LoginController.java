package com.technology.user.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(origins = "http://localhost:3000")
@Controller
@RequestMapping("/login")
public class LoginController {
    @GetMapping
    public String getLoginPage(){
        return "login";
    }
}
