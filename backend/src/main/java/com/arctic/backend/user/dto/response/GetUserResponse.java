package com.arctic.backend.user.dto.response;

import com.arctic.backend.tenant.domain.UserTenantMembership;
import com.arctic.backend.user.domain.User;
import java.util.List;

public record GetUserResponse(
        Long id,
        String email,
        String nickname,
        String phone,
        String role,
        List<UserMembershipResponse> memberships
) {
    public static GetUserResponse of(User user, List<UserTenantMembership> memberships) {
        return new GetUserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPhone(),
                user.getRole().name(),
                memberships.stream()
                        .map(UserMembershipResponse::from)
                        .toList()
        );
    }
}