package com.technology.user.registration.services.role;

import com.technology.user.registration.repositories.UserRepository;
import com.technology.user.registration.services.role.strategy.RoleStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final UserRepository userRepository;
    private RoleStrategy roleStrategy;

    @Autowired
    public RoleServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setRoleStrategy(RoleStrategy roleStrategy) {
        this.roleStrategy = roleStrategy;
    }

    @Override
    public void addRoleManager(String username) {
        roleStrategy.addRole(userRepository, username);
    }

    @Override
    public void addRoleAdmin(String username) {
        roleStrategy.addRole(userRepository, username);
    }

    @Override
    public void deleteRoleManager(String username) {
        roleStrategy.addRole(userRepository, username);
    }

    @Override
    public void deleteRoleAdmin(String username) {
        roleStrategy.addRole(userRepository, username);
    }
}
