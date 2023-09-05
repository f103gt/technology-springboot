package com.technology.registration.registration.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@NotNull
@NotBlank
@NotEmpty
public record UserRegistrationRequest(
        String firstName,
        String lastName,
        String patronymic,
        String email,
        String phoneNumber,
        String password) {
}
