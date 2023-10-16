package com.technology.user.dto;

import com.technology.user.response.AuthenticationResponse;

public record UserDto(
        String fistName,
        String lastName,
        String email) {

}
