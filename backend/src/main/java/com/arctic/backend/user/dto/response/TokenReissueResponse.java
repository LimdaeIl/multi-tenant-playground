package com.arctic.backend.user.dto.response;

public record TokenReissueResponse(
        Long userId,
        String accessToken,
        String refreshToken
) {

    public static TokenReissueResponse of(Long userId, String newAt, String newRt) {
        return new TokenReissueResponse(userId, newAt, newRt);
    }
}
