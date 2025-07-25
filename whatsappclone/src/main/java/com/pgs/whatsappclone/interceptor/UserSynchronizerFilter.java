package com.pgs.whatsappclone.interceptor;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pgs.whatsappclone.user.UserSynchronizer;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// Synchronizes model users with keycloak
@Component
@RequiredArgsConstructor
public class UserSynchronizerFilter extends OncePerRequestFilter {
	
	private final UserSynchronizer userSynchronizer;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
    	// Authenticated
       if(!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
    	   JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    	   userSynchronizer.synchronizeWithIdp(token.getToken());
       }
        filterChain.doFilter(request, response);
    }
}
