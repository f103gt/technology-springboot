package com.technology.security.jwt.services;

import com.technology.security.jwt.models.Token;
import com.technology.security.jwt.repositores.TokenRepository;
import com.technology.security.jwt.utilities.CookieUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;
    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        Cookie[] cookies = request.getCookies();
        Map<String,String> tokens = CookieUtility.getTokens(cookies);
        String jwtToken = tokens.get("jwtToken");
        String refreshToken = tokens.get("refreshToken");
        deactivateToken(jwtToken);
        deactivateToken(refreshToken);
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                Cookie deleteCookie = new Cookie(cookie.getName(),null);
                deleteCookie.setMaxAge(0);
                deleteCookie.setPath("/");
                response.addCookie(deleteCookie);
            }
        }
    }

    private void deactivateToken(String jwtToken) {
        Token storedToken = tokenRepository.findTokenByToken(jwtToken)
                .orElse(null);
        if(storedToken != null){
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }
    }
}
