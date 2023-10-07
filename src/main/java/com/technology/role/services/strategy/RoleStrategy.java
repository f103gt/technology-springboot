package com.technology.role.services.strategy;

import com.technology.user.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

public interface RoleStrategy {
    default String getCurrenUserName(){
       return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    void addRole(UserRepository userRepository, String username);
    void deleteRole(UserRepository userRepository, String username);
}
