package com.library.security;

import com.library.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // لو مفيش Authorization header
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // لو مش Bearer token
        if (!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // استخراج الـ token
        String token = authHeader.substring(7);

        // validate token
        if (!jwtService.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

       // extract data from token
        String role = jwtService.extractRole(token);
        Long id= jwtService.extractId(token);

      // create authenticated user object
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        id,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

       // store authenticated user in Spring Security
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

       // continue request
        filterChain.doFilter(request, response);
    }
}