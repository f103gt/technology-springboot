package com.technology.security.services;

import com.technology.user.models.User;
import com.technology.user.repositories.UserRepository;
import com.technology.security.adapters.SecurityUser;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Getter
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username)
                .orElseThrow(()->new UsernameNotFoundException("Username" + username + "not found"));
        return new SecurityUser(user);
    }

    public UserDetails loadSecurityUserByUserEntity(User user){
        return new SecurityUser(user);
    }
}
