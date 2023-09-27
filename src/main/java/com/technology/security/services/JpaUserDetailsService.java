package com.technology.security.services;

import com.technology.user.registration.models.User;
import com.technology.user.registration.repositories.UserRepository;
import com.technology.security.adapters.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findUserByEmail(username);
        return userOptional
                .map(SecurityUser::new)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Username" + username + "not found"));
    }
}
