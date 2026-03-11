package com.arctic.backend.user.dto.response;

import com.arctic.backend.tenant.domain.UserTenantMembership;
import com.arctic.backend.user.domain.User;
import java.util.List;

public record SignInResponse(
        Long userId,
        String email,
        String nickName,
        Long primaryTenantId,
        String accessToken,
        String refreshToken,
        List<UserMembershipResponse> memberships
) {
    public static SignInResponse of(
            User user,
            String accessToken,
            String refreshToken,
            List<UserTenantMembership> memberships
    ) {
        return new SignInResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPrimaryTenantId(),
                accessToken,
                refreshToken,
                memberships.stream()
                        .map(membership -> UserMembershipResponse.from(
                                membership,
                                user.getPrimaryTenantId()
                        ))
                        .toList()
        );
    }
}