package com.technology.role.services.strategy;

import com.technology.role.enums.Role;
import com.technology.user.models.User;
import com.technology.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

public abstract class AbstractRoleStrategy implements RoleStrategy{
    public abstract String getRoleName();
    @Transactional
    @Override
    public void addRole(UserRepository userRepository, String username) {

        Optional<User> userOptional = userRepository.findUserByEmail(username);
        userOptional.ifPresentOrElse(user -> {
                    user.setRole(Role.valueOf(getRoleName()));
                    userRepository.save(user);
                },
                () -> {
                    throw new UsernameNotFoundException("User with email " + username + "does not exist");
                });
    }

    @Transactional
    @Override
    public void deleteRole(UserRepository userRepository, String username) {

        Optional<User> userOptional = userRepository.findUserByEmail(username);
        userOptional.ifPresentOrElse(user -> {
                    String currenUserName = getCurrenUserName();
                    if (!user.getEmail().equals(currenUserName)) {
                       user.setRole(null);
                        userRepository.save(user);
                    }
                },
                () -> {
                    throw new UsernameNotFoundException("User with email " + username + "does not exist");
                });
    }
}
