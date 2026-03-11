package com.arctic.backend.tenant.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TenantInvitationTokenRepository {

    private final StringRedisTemplate template;

    private String tokenKey(String token) {
        return "tenant:invitation:token:" + token;
    }

    public void save(String token, Long invitationId, long ttlMillis) {
        template.opsForValue().set(
                tokenKey(token),
                String.valueOf(invitationId),
                Duration.ofMillis(ttlMillis)
        );
    }

    public Long findInvitationId(String token) {
        String value = template.opsForValue().get(tokenKey(token));

        if (value == null || value.isBlank()) {
            return null;
        }

        return Long.valueOf(value);
    }

    public boolean exists(String token) {
        return Boolean.TRUE.equals(template.hasKey(tokenKey(token)));
    }

    public void delete(String token) {
        template.delete(tokenKey(token));
    }
}