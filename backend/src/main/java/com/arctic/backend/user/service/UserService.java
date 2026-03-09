package com.arctic.backend.user.service;

import com.arctic.backend.tenant.domain.UserTenantMembership;
import com.arctic.backend.tenant.repository.UserTenantMembershipRepository;
import com.arctic.backend.user.domain.User;
import com.arctic.backend.user.dto.response.GetUserResponse;
import com.arctic.backend.user.exception.AuthErrorCode;
import com.arctic.backend.user.exception.AuthException;
import com.arctic.backend.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserTenantMembershipRepository membershipRepository;

    @Transactional(readOnly = true)
    public GetUserResponse me(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new AuthException(
                        AuthErrorCode.USER_NOT_FOUND_BY_ID,
                        userId
                ));

        List<UserTenantMembership> memberships = membershipRepository.findAllByUser_Id(userId);

        return GetUserResponse.of(user, memberships);
    }
}