package com.technology.user.registration.services.role.strategy;

import org.springframework.stereotype.Service;

@Service
public class ManagerRoleStrategy extends AbstractRoleStrategy {
    @Override
    public String getRoleName() {
        return "MANAGER";
    }
}
