package com.arctic.backend.user.dto.request;

import jakarta.validation.constraints.NotNull;

public record TokenReissueRequest(
        @NotNull(message = "리프레시 토큰: 리프레시 토큰은 필수입니다.")
        String refreshToken
) {

}
