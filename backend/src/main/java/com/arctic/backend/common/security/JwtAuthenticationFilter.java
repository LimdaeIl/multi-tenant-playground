package com.arctic.backend.common.security;

import com.arctic.backend.common.jwt.JwtTokenProvider;
import com.arctic.backend.user.domain.UserRole;
import com.arctic.backend.user.repository.UserTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JwtAuthenticationFilter")
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserTokenRepository userTokenRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && !authHeader.isBlank()) {
            try {
                if (jwtTokenProvider.isValid(authHeader)) {
                    if (userTokenRepository.isAtBlacklisted(authHeader)) {
                        filterChain.doFilter(request, response);
                        return;
                    }

                    Long userId = jwtTokenProvider.getUserId(authHeader);
                    String email = jwtTokenProvider.getEmail(authHeader);
                    UserRole role = jwtTokenProvider.getRole(authHeader);

                    CustomUserDetails principal =
                            new CustomUserDetails(userId, email, role);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    principal,
                                    null,
                                    principal.getAuthorities()
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                log.debug("JWT authentication skipped: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}