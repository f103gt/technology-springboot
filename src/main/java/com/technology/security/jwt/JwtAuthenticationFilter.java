package com.technology.security.jwt;

import com.technology.cart.exceptions.UserNotFoundException;
import com.technology.security.adapters.SecurityUser;
import com.technology.security.services.JpaUserDetailsService;
import com.technology.user.models.User;
import com.technology.user.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final JpaUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String jwtToken = null;
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    jwtToken = cookie.getValue();
                }
                else if(cookie.getName().equals("refreshToken")){
                    refreshToken = cookie.getValue();
                }
            }
        }
        if (jwtToken != null) {
            if (jwtService.isTokenExpired(jwtToken)) {
                if (refreshToken != null) {
                    if (jwtService.isTokenExpired(refreshToken)) {
                        filterChain.doFilter(request, response);
                        return;
                    }
                    final String username = jwtService.extractUsername(refreshToken);
                    User user = userRepository.findUserByEmail(username)
                            .orElseThrow(
                                    () -> new UserNotFoundException("User not found"));
                    String userRole = user.getRole().getRoleName();
                    jwtToken = jwtService.generateToken(Map.of("role",userRole),new SecurityUser(user));

                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("token")) {
                            cookie.setValue(jwtToken);
                            response.addCookie(cookie);
                        }
                    }

                }
                filterChain.doFilter(request, response);
                return;
            }
            final String username = jwtService.extractUsername(jwtToken);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isJwtTokenValid(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
/*final String authenticationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String jwtToken = authenticationHeader.substring(7);*/