package com.technology.user.registration.services.role.strategy;

import org.springframework.stereotype.Service;

@Service
public class AdminRoleStrategy extends AbstractRoleStrategy {
    @Override
    public String getRoleName() {
        return "ADMIN";
    }
}
