package com.technology.role.services.strategy;

import com.technology.role.enums.Role;
import org.springframework.stereotype.Service;

@Service
public class AdminRoleStrategy extends AbstractRoleStrategy {
    @Override
    public String getRoleName() {
        return Role.ADMIN.name();
    }
}
