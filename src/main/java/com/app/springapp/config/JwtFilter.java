package com.app.springapp.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import com.app.springapp.service.CustomUserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import io.jsonwebtoken.Claims;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // ✅ Bypass filter for public endpoints like /api/auth/**
        String path = request.getRequestURI();
        System.out.println("[JwtFilter] Incoming request path: " + path);
        if (path.startsWith("/api/auth/")) {
            System.out.println("[JwtFilter] Bypassing auth endpoint: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        System.out.println("[JwtFilter] Auth header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            System.out.println("[JwtFilter] Extracted username: " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("[JwtFilter] UserDetails found: " + (userDetails != null));

                if (jwtUtil.validateToken(token, userDetails)) {
                    System.out.println("[JwtFilter] Token is valid for user: " + username);
                    // Extract roles from JWT and map to authorities
                    Claims claims = jwtUtil.extractAllClaims(token);
                    List<String> roles = claims.get("roles", List.class);
                    System.out.println("[JwtFilter] Roles from JWT: " + roles);
                    List<GrantedAuthority> authorities = roles != null ?
                        roles.stream().map(role -> role.startsWith("ROLE_") ? new SimpleGrantedAuthority(role) : new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList()) :
                        (List<GrantedAuthority>) userDetails.getAuthorities();
                    System.out.println("[JwtFilter] Authorities mapped: " + authorities);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    authorities
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("[JwtFilter] Token is INVALID for user: " + username);
                }
            } else {
                System.out.println("[JwtFilter] Username is null or already authenticated.");
            }
        } else {
            System.out.println("[JwtFilter] No valid Authorization header found.");
        }

        filterChain.doFilter(request, response);
    }
}
