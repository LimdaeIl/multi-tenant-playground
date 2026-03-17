package com.arctic.backend.user.controller;

import com.arctic.backend.common.response.ApiResponse;
import com.arctic.backend.user.dto.request.LogoutRequest;
import com.arctic.backend.user.dto.request.SignInRequest;
import com.arctic.backend.user.dto.request.SignupRequest;
import com.arctic.backend.user.dto.request.SignupWithInvitationRequest;
import com.arctic.backend.user.dto.request.TokenReissueRequest;
import com.arctic.backend.user.dto.response.SignInResponse;
import com.arctic.backend.user.dto.response.SignupResponse;
import com.arctic.backend.user.dto.response.TokenReissueResponse;
import com.arctic.backend.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @RequestBody @Valid SignupRequest request
    ) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @PostMapping("/signup/invitation")
    public ResponseEntity<ApiResponse<SignupResponse>> signupWithInvitation(
            @RequestBody @Valid SignupWithInvitationRequest request
    ) {
        SignupResponse response = authService.signupWithInvitation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<SignInResponse>> signIn(
            @RequestBody @Valid SignInRequest request
    ) {
        SignInResponse response = authService.signIn(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenReissueResponse>> reissue(
            @RequestHeader(name = "Authorization", required = false) String at,
            @RequestBody @Valid TokenReissueRequest request
    ) {
        TokenReissueResponse response = authService.reissue(at, request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(name = "Authorization") String at,
            @RequestBody @Valid LogoutRequest request
    ) {
        authService.logout(at, request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}