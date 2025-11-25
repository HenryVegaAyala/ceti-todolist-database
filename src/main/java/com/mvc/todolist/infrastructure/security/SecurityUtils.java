package com.mvc.todolist.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtils {

    public static Optional<String> getCurrentUsername() {
        return getCurrentAuthentication()
                .map(auth -> extractUsername(auth.getPrincipal()));
    }

    public static Optional<Authentication> getCurrentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isInvalidAuthentication(authentication)) {
            return Optional.empty();
        }

        return Optional.of(authentication);
    }

    public static Optional<UserDetails> getCurrentUserDetails() {
        return getCurrentAuthentication()
                .map(Authentication::getPrincipal)
                .filter(UserDetails.class::isInstance)
                .map(UserDetails.class::cast);
    }

    public static boolean isAuthenticated() {
        return getCurrentAuthentication().isPresent();
    }

    public static boolean hasRole(String role) {
        String roleToCheck = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return getCurrentAuthentication()
                .map(auth -> auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals(roleToCheck)))
                .orElse(false);
    }

    private static boolean isInvalidAuthentication(Authentication authentication) {
        return authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal());
    }

    private static String extractUsername(Object principal) {
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof String username) {
            return username;
        }
        return null;
    }
}
