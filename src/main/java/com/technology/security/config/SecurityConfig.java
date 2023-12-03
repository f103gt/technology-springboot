package com.technology.security.config;

import com.technology.exception.filters.AnonymousFilter;
import com.technology.exception.filters.ClearContextFilter;
import com.technology.exception.handler.authentication.AccessDeniedExceptionHandler;
import com.technology.exception.handler.authentication.AuthenticationErrorHandler;
import com.technology.exception.handler.authentication.CustomAuthenticationEntryPoint;
import com.technology.security.csrf.CsrfCookieFilter;
import com.technology.security.csrf.CustomCsrfTokenRequestHandler;
import com.technology.security.jwt.filters.JwtAuthenticationFilter;
import com.technology.security.jwt.services.LogoutService;
import com.technology.security.services.JpaUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JpaUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutService logoutService;
    private final AuthenticationErrorHandler authenticationErrorHandler;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http

                .addFilterBefore(new ClearContextFilter(), AnonymousAuthenticationFilter.class)
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer
                                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                                .accessDeniedHandler(new AccessDeniedExceptionHandler()))
                .securityMatcher(request -> !request.getRequestURI().equals("/error"))

                /*.anonymous(anonymousConfigurer -> anonymousConfigurer.authenticationFilter())*/

                .requiresChannel(channel -> channel
                                .requestMatchers(request -> !request.getRequestURI().startsWith("/subscribe")).requiresSecure()
                        /*.requestMatchers("/**").requiresSecure()*/
                )
                .csrf(csrfConfigurer ->
                        csrfConfigurer
                                .csrfTokenRepository(
                                        CookieCsrfTokenRepository.withHttpOnlyFalse())
                                .csrfTokenRequestHandler(new CustomCsrfTokenRequestHandler())
                                /*.ignoringRequestMatchers("/notification/subscribe")*/
                                .ignoringRequestMatchers("/subscribe/**")

                )
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        /*.requestMatchers("/notification/subscribe").permitAll()*/
                                        /*TODO .requestMatcher("/place-order").hasAuthority("ROLE_USER")*/
                                       /* .requestMatchers("/error").permitAll()*/
                                        .requestMatchers("/subscribe/**").permitAll()
                                        .requestMatchers("/place-order").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/trigger-notification").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/authenticate").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/generate-otp").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/otp-verification").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/update-otp").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v1/all-categories").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v1/category-products").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/v1/specific-product").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/csrf/api/v1").permitAll()
                                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                                        .requestMatchers("/manager/**").hasAuthority("ROLE_MANAGER")
                                        .requestMatchers("/staff/**").hasAuthority("ROLE_STAFF")
                                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(new CsrfCookieFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthFilter, CsrfCookieFilter.class)
                .httpBasic(Customizer.withDefaults())
                .logout(httpLogoutConfigurer -> httpLogoutConfigurer
                        .logoutUrl("api/v1/auth/logout")
                        .addLogoutHandler(logoutService)
                        .logoutSuccessHandler((request, response, authentication) ->
                                SecurityContextHolder.clearContext()))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("https://localhost:3000"));
        corsConfiguration.setAllowedMethods(List.of("GET", "PUT", "PATCH", "POST", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT,
                "X-XSRF-TOKEN"));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}


