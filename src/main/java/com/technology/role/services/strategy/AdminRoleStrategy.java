package com.technology.role.services.strategy;

import org.springframework.stereotype.Service;

@Service
public class AdminRoleStrategy extends AbstractRoleStrategy {
    @Override
    public String getRoleName() {
        return "ADMIN";
    }
}
