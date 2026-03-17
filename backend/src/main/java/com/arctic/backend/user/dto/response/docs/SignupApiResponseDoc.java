package com.arctic.backend.user.dto.response.docs;

import com.arctic.backend.user.dto.response.SignupResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SignupApiResponse")
public record SignupApiResponseDoc(
        @Schema(example = "201")
        int status,

        @Schema(example = "true")
        boolean success,

        SignupResponse data
) {
}