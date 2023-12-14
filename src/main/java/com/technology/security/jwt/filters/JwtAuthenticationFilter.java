package com.technology.security.jwt.filters;

import com.technology.security.adapters.SecurityUser;
import com.technology.security.jwt.exceptions.TokenNotFoundException;
import com.technology.security.jwt.models.Token;
import com.technology.security.jwt.models.TokenType;
import com.technology.security.jwt.repositores.TokenRepository;
import com.technology.security.jwt.services.JwtService;
import com.technology.security.jwt.services.LogoutService;
import com.technology.security.jwt.utilities.CookieUtility;
import com.technology.security.services.JpaUserDetailsService;
import com.technology.user.models.User;
import com.technology.user.repositories.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final JpaUserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final LogoutService logoutService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        Map<String, String> tokens = CookieUtility.getTokens(cookies);
        String jwtToken = tokens.get("jwtToken");
        String refreshToken = tokens.get("refreshToken");
        boolean refreshIsExpired = isTokenExpired(refreshToken);

        if (jwtToken != null && !isTokenExpired(jwtToken)) {
            final String username = jwtService.extractUsername(jwtToken);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findUserByEmail(username)
                        .orElseThrow(()->new UsernameNotFoundException("Username" + username + "not found"));
                UserDetails userDetails = userDetailsService.loadSecurityUserByUserEntity(user);
                if (jwtService.isJwtTokenValid(jwtToken, userDetails) && isTokenValid(jwtToken)) {
                    setAuthenticationForUser(request, userDetails);
                } else if (refreshIsExpired) {
                    setStoredTokenExpired(refreshToken);
                    refreshToken = jwtService.generateRefreshToken();
                    tokenRepository.save(Token.builder()
                            .token(jwtToken)
                            .type(TokenType.BEARER)
                            .user(user)
                            .expired(false)
                            .revoked(false)
                            .build());
                    createTokenCookie(response, "refreshToken", refreshToken);
                }
            }
        }
        else if (refreshToken != null && !refreshIsExpired) {

            boolean isRefreshTokenValid = isTokenValid(refreshToken);
            if (isRefreshTokenValid) {
                Token storedRefresh = null;
                try {
                    storedRefresh = tokenRepository.findTokenByToken(refreshToken)
                            .orElseThrow(() -> new TokenNotFoundException("Refresh token not found"));
                } catch (TokenNotFoundException e) {
                    logger.error("REFRESH TOKEN NOT FOUND", e);
                }
                if (storedRefresh != null) {
                    setStoredTokenExpired(jwtToken);
                    User user = storedRefresh.getUser();
                    String userRole = user.getRole().name();
                    SecurityUser userDetails = new SecurityUser(user);
                    String newJwtToken = jwtService.generateToken(Map.of("role", userRole,"timestamp", Instant.now().toString()), userDetails);
                    tokenRepository.save(Token.builder()
                            .token(newJwtToken)
                            .type(TokenType.BEARER)
                            .user(user)
                            .expired(false)
                            .revoked(false)
                            .build());
                    setAuthenticationForUser(request, userDetails);
                    createTokenCookie(response, "jwtToken", newJwtToken);

                }
            }
        } else if (jwtToken != null && refreshToken != null) {
            logoutService.logout(request, response, null);
        }
        filterChain.doFilter(request, response);

    }

    private void setStoredTokenExpired(String jwtToken) {
        Token storedJwtToken = tokenRepository.findTokenByToken(jwtToken)
                .orElse(null);
        if (storedJwtToken != null) {
            storedJwtToken.setExpired(true);
            tokenRepository.save(storedJwtToken);
        }
    }

    private Boolean isTokenValid(String jwtToken) {
        return tokenRepository.findTokenByToken(jwtToken)
                .map(token -> !token.isExpired() && !token.isRevoked())
                .orElse(false);
    }

    private void createTokenCookie(HttpServletResponse response, String tokenType, String token) {
        Cookie tokenCookie = new Cookie(tokenType, token);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setSecure(true);
        response.addCookie(tokenCookie);
    }

    private void setAuthenticationForUser(HttpServletRequest request, UserDetails userDetails) {
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

    private boolean isTokenExpired(String jwtToken) {
        if (jwtToken == null) {
            return true;
        }
        try {
            if (!jwtService.isTokenExpired(jwtToken)) {
                return false;
            }
        } catch (ExpiredJwtException e) {
            return true;
        }
        return false;
    }

}
