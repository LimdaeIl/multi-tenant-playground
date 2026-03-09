package com.arctic.backend.user.repository;


import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserTokenRepository {

    private final StringRedisTemplate template;

    private String buildKey(Long userId) {
        return "refreshToken:" + userId;
    }

    private String blacklistKey(String accessToken) {
        return "auth:blacklist:access:" + accessToken;
    }

    private String blacklistRefreshKey(String refreshToken) {
        return "auth:blacklist:refresh:" + refreshToken;
    }

    public void saveRefreshToken(Long userId, String refreshToken, long ttlMillis) {
        String key = buildKey(userId);
        template.opsForValue().set(key, refreshToken, Duration.ofMillis(ttlMillis));
    }

    public String findRt(Long userId) {
        return template.opsForValue().get(buildKey(userId));
    }

    public void deleteRt(Long userId) {
        template.delete(buildKey(userId));
    }


    public void blacklistAt(String accessToken, long ttlMillis) {
        template.opsForValue()
                .set(blacklistKey(accessToken), "blacklisted", Duration.ofMillis(ttlMillis));
    }

    public boolean isAtBlacklisted(String accessToken) {
        return template.hasKey(blacklistKey(accessToken));
    }

    public void blacklistRt(String refreshToken, long ttlMillis) {
        template.opsForValue()
                .set(blacklistRefreshKey(refreshToken), "blacklisted", Duration.ofMillis(ttlMillis));
    }

    public boolean isRtBlacklisted(String refreshToken) {
        return template.hasKey(blacklistRefreshKey(refreshToken));
    }
}
