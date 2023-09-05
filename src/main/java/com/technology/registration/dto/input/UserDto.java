package com.technology.registration.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@NotNull
@NotBlank
@NotEmpty
public record UserDto(
        String firstName,
        String lastName,
        String patronymic,
        String email,
        String phoneNumber,
        String password,
        String region,
        String district,
        //city town
        String locality,
        String street,
        String premise,
        String zipcode) {
}
