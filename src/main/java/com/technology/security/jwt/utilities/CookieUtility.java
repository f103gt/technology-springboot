package com.technology.security.jwt.utilities;

import jakarta.servlet.http.Cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CookieUtility {
    public static Map<String, String> getTokens(Cookie[] cookies){
        Map<String, String> tokens = new HashMap<>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwtToken")) {
                    tokens.put("jwtToken", cookie.getValue());
                } else if (cookie.getName().equals("refreshToken")) {
                    tokens.put("refreshToken", cookie.getValue());
                }
            }
        }
        return tokens;
    }


}
