package com.technology.user.registration.registration.requests.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@NotNull
@NotBlank
@NotEmpty
public record UserRegistrationRequest(
        String firstName,
        String lastName,
        String email,
        String password) {
}
