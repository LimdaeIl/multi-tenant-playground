package com.arctic.backend.common.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String issuer,
        long atTtlSeconds,
        long rtTtlSeconds,
        String secret,
        long clockSkewSeconds
) {

}
