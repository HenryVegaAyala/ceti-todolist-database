package com.mvc.todolist.infrastructure.security;

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

import static com.mvc.todolist.infrastructure.constant.SecurityConstants.PUBLIC_ENDPOINTS;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getServletPath();

        if (shouldNotFilter(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldNotFilter(String path) {

        return Arrays.stream(PUBLIC_ENDPOINTS).anyMatch(pattern -> {

            ///  Normalizar rutas con o sin barra final
            String normalizedPath = path.endsWith("/") ? pattern : pattern + "/";
            String normalizedPattern = pattern.endsWith("/") ? pattern : pattern + "/";

            // Manejar patrones con **
            if (pattern.endsWith("/**")) {
                String basePattern = pattern.substring(0, pattern.length() - 3);
                return path.equals(basePattern) || path.startsWith(basePattern + "/");
            }

            // Manejar patrones con *
            if (pattern.contains("*")) {
                String regex = pattern.replace("**", ".*").replace("*", "[^/]*");
                return path.matches(regex);
            }

            return path.equals(pattern) || normalizedPath.equals(normalizedPattern);
        });
    }
}
