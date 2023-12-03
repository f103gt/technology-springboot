package com.technology.exception.filters;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

public class AnonymousFilter extends AnonymousAuthenticationFilter {
    public AnonymousFilter(String key) {
        super(key);
    }

    @Override
    protected Authentication createAuthentication(HttpServletRequest request) {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        if (currentAuth == null || currentAuth instanceof AnonymousAuthenticationToken) {
            return super.createAuthentication(request);
        }

        return currentAuth;
    }

}
