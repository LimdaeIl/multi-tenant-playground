package com.arctic.backend.common.jwt;


import com.arctic.backend.user.domain.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j(topic = "JwtTokenProvider")
@Getter
@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    private static final String PREFIX_BEARER = "Bearer ";
    private static final String CLAIM_UID = "uid";
    private static final String CLAIM_ROLE = "role";


    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAt(Long userId, String email, UserRole role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(jwtProperties.atTtlSeconds());

        return Jwts.builder()
                .header().type("at").and()
                .subject(email)
                .claim(CLAIM_UID, userId)
                .claim(CLAIM_ROLE, role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(secretKey)
                .compact();
    }

    public String generateRt(Long userId, String email, UserRole role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(jwtProperties.rtTtlSeconds());

        return Jwts.builder()
                .header().type("rt").and()
                .subject(email)
                .claim(CLAIM_UID, userId)
                .claim(CLAIM_ROLE, role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(secretKey)
                .compact();
    }

    public Long getUserId(String token) {
        return parseClaims(token).get(CLAIM_UID, Long.class);
    }

    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public UserRole getRole(String token) {
        String role = parseClaims(token).get(CLAIM_ROLE, String.class);
        return UserRole.valueOf(role);
    }

    public long getRtTtlSeconds(String rt) {
        Claims claims = parseClaims(rt);
        return Math.max(claims.getExpiration().getTime() - System.currentTimeMillis(), 0);
    }

    public long getAtTtlSeconds(String at) {
        Claims claims = parseClaims(at);
        return Math.max(claims.getExpiration().getTime() - System.currentTimeMillis(), 0);
    }


    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return getClaims(token, secretKey);
    }

    private Claims getClaims(String token, SecretKey key) {
        String stripped = stripBearer(token);

        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .clockSkewSeconds(jwtProperties.clockSkewSeconds())
                    .build()
                    .parseSignedClaims(stripped)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new TokenException(JwtErrorCode.EXPIRED_TOKEN);
        } catch (PrematureJwtException e) {
            throw new TokenException(JwtErrorCode.PREMATURE_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new TokenException(JwtErrorCode.UNSUPPORTED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new TokenException(JwtErrorCode.MALFORMED_TOKEN);
        } catch (SecurityException e) {
            throw new TokenException(JwtErrorCode.TAMPERED_TOKEN);
        } catch (MissingClaimException | IncorrectClaimException e) {
            throw new TokenException(JwtErrorCode.INVALID_CLAIMS);
        } catch (JwtException e) {
            log.debug("JWT parse error: {}", e.toString());
            throw new TokenException(JwtErrorCode.INVALID_BEARER_TOKEN);
        }
    }

    private String stripBearer(String token) {
        if (token == null || token.isBlank()) {
            throw new TokenException(JwtErrorCode.TOKEN_IS_NULL);
        }

        String trimmed = token.trim();
        if (trimmed.regionMatches(true, 0, PREFIX_BEARER, 0, PREFIX_BEARER.length())) {
            trimmed = trimmed.substring(PREFIX_BEARER.length()).trim();
            if (trimmed.isEmpty()) {
                throw new TokenException(JwtErrorCode.TOKEN_IS_NULL);
            }
        }
        return trimmed;
    }


}
