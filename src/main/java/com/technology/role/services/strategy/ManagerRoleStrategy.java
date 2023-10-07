package com.technology.role.services.strategy;

import org.springframework.stereotype.Service;

@Service
public class ManagerRoleStrategy extends AbstractRoleStrategy {
    @Override
    public String getRoleName() {
        return "MANAGER";
    }
}
