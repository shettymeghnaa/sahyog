package org.hartford.mutualinsurance.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (!request.getServletPath().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (request.getServletPath().startsWith("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (request.getServletPath().contains("/api/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);
            try {
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractClaims(token).get("role", String.class);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singleton(new SimpleGrantedAuthority(role))
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            catch(ExpiredJwtException e){
                System.out.println("JWT expired, ignoring request authentication");
            }
        }

        filterChain.doFilter(request, response);
    }
}
