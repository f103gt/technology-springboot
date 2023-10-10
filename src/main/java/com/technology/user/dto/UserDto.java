package com.technology.user.dto;

import com.technology.user.response.AuthenticationResponse;

public record UserDto(
        String fistName,
        String lastName,
        String email) {
    public static UserDto getUserDto(AuthenticationResponse response) {
        return new UserDto(response.getFirstName(),
                response.getLastName(),
                response.getEmail());
    }
}
