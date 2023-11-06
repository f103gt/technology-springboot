package com.technology.role.enums;

import java.util.Optional;

public enum Role {
    USER,
    STAFF,
    MANAGER,
    ADMIN;

    public static Optional<Role> contains(String roleValue) {
        for (Role role : Role.values()) {
            if (role.name().equals(roleValue.toUpperCase())) {
                return Optional.of(role);
            }
        }
        return Optional.empty();
    }
    }
