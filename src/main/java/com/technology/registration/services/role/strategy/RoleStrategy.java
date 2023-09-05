package com.technology.registration.services.role.strategy;

import com.technology.registration.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public interface RoleStrategy {
    default String getCurrenUserName(){
       return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    void addRole(UserRepository userRepository, String username);
    void deleteRole(UserRepository userRepository, String username);
}
