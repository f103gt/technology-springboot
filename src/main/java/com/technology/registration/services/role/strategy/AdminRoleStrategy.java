package com.technology.registration.services.role.strategy;

import com.technology.registration.models.Role;
import com.technology.registration.models.User;
import com.technology.registration.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

public class AdminRoleStrategy implements RoleStrategy {
    @Override
    public void addRole(UserRepository userRepository, String username) {
        Optional<User> userOptional = userRepository.findUserByEmail(username);
        userOptional.ifPresentOrElse(user -> {
                    Role role = new Role();
                    role.setRoleName("ADMIN");
                    user.getRoles().add(role);
                    userRepository.save(user);
                },
                () -> {
                    throw new UsernameNotFoundException("User with email " + username + "does not exist");
                });
    }

    @Override
    public void deleteRole(UserRepository userRepository, String username) {
        Optional<User> userOptional = userRepository.findUserByEmail(username);
        userOptional.ifPresentOrElse(user -> {
                    String currenUserName = getCurrenUserName();
                    if (!user.getEmail().equals(currenUserName)) {
                        Set<Role> roles = user.getRoles();
                        roles.removeIf(role -> role.getRoleName().equals("ADMIN"));
                        userRepository.save(user);
                    }
                },
                () -> {
                    throw new UsernameNotFoundException("User with email " + username + "does not exist");
                });
    }
}
