package com.arctic.backend.asset.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAssetRequest(

        @NotBlank(message = "자산 코드는 필수입니다.")
        @Size(max = 50, message = "자산 코드는 50자 이하여야 합니다.")
        String code,

        @NotBlank(message = "자산 이름은 필수입니다.")
        @Size(max = 100, message = "자산 이름은 100자 이하여야 합니다.")
        String name,

        @Size(max = 100, message = "외부 키는 100자 이하여야 합니다.")
        String externalKey,

        @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
        String description
) {
}