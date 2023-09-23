package com.technology.security.config;

import com.technology.security.services.JpaUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JpaUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(JpaUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> {
                            auth
                                    .requestMatchers("/manager/**").hasRole("MANAGER")
                                    .requestMatchers("/staff/**").hasRole("STAFF")
                                    .requestMatchers("/admin/**").hasRole("ADMIN")
                                    .requestMatchers("/user/**").hasRole("USER")
                                    .requestMatchers("/**").permitAll()
                                    .anyRequest().authenticated();
                        })
                .userDetailsService(userDetailsService)
                .httpBasic(Customizer.withDefaults())
                .formLogin(loginConfig -> loginConfig
                        .loginPage("/login")
                        .defaultSuccessUrl("/all-products"))
                .build();
    }

    //auth.requestMatchers("/admin/**").hasRole("ADMIN");
    //auth.requestMatchers("/manager/**").hasRole("MANAGER");
    //TODO fix the issue with the hierarchy

    /*return http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/").permitAll()
                .and()
                .authorizeHttpRequests().requestMatchers("/manager").authenticated()
                .and().formLogin()
                .and().build();*/
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_MANAGER\n" +
                "ROLE_MANAGER > ROLE_STAFF"+
                "ROLE_STAFF > ROLE_USER");
        return roleHierarchy;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
