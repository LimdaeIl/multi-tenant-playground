package com.arctic.backend.user.dto.response;

import com.arctic.backend.user.domain.User;

public record SignInResponse(
        Long userId,
        String email,
        String nickName,
        String accessToken,
        String refreshToken
) {

    public static SignInResponse of(User user, String accessToken, String refreshToken) {
        return new SignInResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                accessToken,
                refreshToken
        );
    }
}