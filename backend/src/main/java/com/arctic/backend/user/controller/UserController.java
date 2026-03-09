package com.arctic.backend.user.controller;

import com.arctic.backend.common.response.ApiResponse;
import com.arctic.backend.common.security.CustomUserDetails;
import com.arctic.backend.user.dto.response.GetUserResponse;
import com.arctic.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "UserController")
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<GetUserResponse>> me(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        GetUserResponse response = userService.me(principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}