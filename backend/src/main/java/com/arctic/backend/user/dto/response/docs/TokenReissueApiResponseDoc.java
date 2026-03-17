package com.arctic.backend.user.dto.response.docs;

import com.arctic.backend.user.dto.response.TokenReissueResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TokenReissueApiResponse")
public record TokenReissueApiResponseDoc(
        @Schema(example = "200")
        int status,

        @Schema(example = "true")
        boolean success,

        TokenReissueResponse data
) {
}