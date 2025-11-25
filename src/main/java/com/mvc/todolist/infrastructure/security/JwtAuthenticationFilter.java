package com.mvc.todolist.infrastructure.security;

import com.mvc.todolist.infrastructure.constant.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (shouldNotFilter(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        if (jwt.trim().isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtService.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al procesar el token JWT: " + e.getMessage());
            // Continuar sin autenticar al usuario
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldNotFilter(String path) {
        return Arrays.stream(SecurityConstants.PUBLIC_ENDPOINTS)
                .anyMatch(pattern -> matchesPattern(path, pattern));
    }

    private boolean matchesPattern(String path, String pattern) {
        if (pattern.endsWith("/**")) {
            String basePattern = pattern.substring(0, pattern.length() - 3);
            return path.equals(basePattern) || path.startsWith(basePattern + "/");
        }

        if (pattern.contains("*")) {
            String regex = pattern.replace("**", ".*").replace("*", "[^/]*");
            return path.matches(regex);
        }

        String normalizedPath = path.endsWith("/") ? path : path + "/";
        String normalizedPattern = pattern.endsWith("/") ? pattern : pattern + "/";
        return path.equals(pattern) || normalizedPath.equals(normalizedPattern);
    }
}

