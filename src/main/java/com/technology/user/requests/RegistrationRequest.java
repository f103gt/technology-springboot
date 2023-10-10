package com.technology.user.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@NotNull
@NotBlank
@NotEmpty
public class RegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
