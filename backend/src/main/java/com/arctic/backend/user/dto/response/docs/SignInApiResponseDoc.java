package com.arctic.backend.user.dto.response.docs;

import com.arctic.backend.user.dto.response.SignInResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SignInApiResponse")
public record SignInApiResponseDoc(
        @Schema(example = "200")
        int status,

        @Schema(example = "true")
        boolean success,

        SignInResponse data
) {
}