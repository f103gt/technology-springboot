package com.technology.security.csrf;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/csrf/api/v1")
public class CsrfController {
    @GetMapping
    public CsrfToken csrfToken(CsrfToken csrfToken){
        return csrfToken;
    }
}
