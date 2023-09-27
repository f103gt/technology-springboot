package com.technology.address.regitration.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@NotNull
@NotBlank
@NotEmpty
public record AddressRegistrationRequest(
        String phoneNumber,
        String region,
        String district,
        //city town
        String locality,
        String street,
        String premise,
        String zipcode) {
}
