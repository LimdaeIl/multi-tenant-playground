package com.arctic.backend.user.service;


import static com.arctic.backend.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.arctic.backend.common.jwt.JwtErrorCode;
import com.arctic.backend.common.jwt.JwtTokenProvider;
import com.arctic.backend.common.jwt.TokenException;
import com.arctic.backend.user.domain.User;
import com.arctic.backend.user.dto.request.SignInRequest;
import com.arctic.backend.user.dto.request.SignupRequest;
import com.arctic.backend.user.dto.response.SignInResponse;
import com.arctic.backend.user.dto.response.SignupResponse;
import com.arctic.backend.user.dto.response.TokenReissueResponse;
import com.arctic.backend.user.exception.AuthErrorCode;
import com.arctic.backend.user.exception.AuthException;
import com.arctic.backend.user.repository.UserRepository;
import com.arctic.backend.user.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException(AuthErrorCode.EMAIL_EXISTS);
        }

        User user = User.create(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.nickname(),
                request.phone()
        );

        userRepository.save(user);

        return SignupResponse.from(user);
    }

    @Transactional
    public SignInResponse signIn(SignInRequest request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.email())
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND_BY_EMAIL,
                        request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthException(AuthErrorCode.USER_PASSWORD_INCORRECT);
        }

        String at = jwtTokenProvider.generateAt(user.getId(), user.getEmail(), user.getRole());
        String rt = jwtTokenProvider.generateRt(user.getId(), user.getEmail(), user.getRole());

        long refreshTtlMs = jwtTokenProvider.getRtTtlSeconds(rt);

        userTokenRepository.saveRefreshToken(user.getId(), rt, refreshTtlMs);

        return SignInResponse.of(user, at, rt);
    }

    @Transactional
    public TokenReissueResponse reissue(String at, String rt) {

        Long userId = jwtTokenProvider.getUserId(rt);

        if (userTokenRepository.isRtBlacklisted(rt)) {
            throw new TokenException(JwtErrorCode.INVALID_REFRESH_TOKEN);
        }

        String storedRt = userTokenRepository.findRt(userId);
        if (storedRt == null) {
            throw new TokenException(JwtErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        if (!storedRt.equals(rt)) {
            throw new TokenException(JwtErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new AuthException(USER_NOT_FOUND));

        long rtTtl = jwtTokenProvider.getRtTtlSeconds(rt);
        if (rtTtl > 0) {
            userTokenRepository.blacklistRt(rt, rtTtl);
        }

        if (at != null && !at.isBlank()) {
            long atTtl = jwtTokenProvider.getAtTtlSeconds(at);
            if (atTtl > 0) {
                userTokenRepository.blacklistAt(at, atTtl);
            }
        }

        String newAt = jwtTokenProvider.generateAt(userId, user.getEmail(), user.getRole());
        String newRt = jwtTokenProvider.generateRt(userId, user.getEmail(), user.getRole());
        long newRtTtl = jwtTokenProvider.getRtTtlSeconds(newRt);

        // 새 RT를 Redis에 저장 (사용 가능한 RT는 항상 '한 개'만 유지)
        userTokenRepository.saveRefreshToken(userId, newRt, newRtTtl);

        return TokenReissueResponse.of(user.getId(), newAt, newRt);
    }

    @Transactional
    public void logout(String at, String rt) {
        Long userIdByRt = jwtTokenProvider.getUserId(rt);
        Long userIdByAt = jwtTokenProvider.getUserId(at);

        if (!userIdByRt.equals(userIdByAt)) {
            throw new TokenException(JwtErrorCode.INVALID_BEARER_TOKEN);
        }

        if (userTokenRepository.isRtBlacklisted(rt)) {
            throw new TokenException(JwtErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (userTokenRepository.isAtBlacklisted(at)) {
            throw new TokenException(JwtErrorCode.INVALID_ACCESS_TOKEN);
        }

        String storedRt = userTokenRepository.findRt(userIdByRt);
        if (storedRt == null) {
            throw new TokenException(JwtErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        if (!storedRt.equals(rt)) {
            throw new TokenException(JwtErrorCode.INVALID_REFRESH_TOKEN);
        }

        long rtTtl = jwtTokenProvider.getRtTtlSeconds(rt);
        if (rtTtl > 0) {
            userTokenRepository.blacklistRt(rt, rtTtl);
        }

        long atTtl = jwtTokenProvider.getAtTtlSeconds(at);
        if (atTtl > 0) {
            userTokenRepository.blacklistAt(at, atTtl);
        }

        userTokenRepository.deleteRt(userIdByRt);

    }
}
