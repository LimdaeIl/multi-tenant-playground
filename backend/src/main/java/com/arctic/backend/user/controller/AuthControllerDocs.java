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
import com.arctic.backend.user.dto.response.docs.SignInApiResponseDoc;
import com.arctic.backend.user.dto.response.docs.SignupApiResponseDoc;
import com.arctic.backend.user.dto.response.docs.TokenReissueApiResponseDoc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(
        name = "인증/인가 API",
        description = "회원가입, 로그인, 토큰 재발급, 로그아웃 등 인증/인가 관련 API"
)
public interface AuthControllerDocs {

    @Operation(
            summary = "회원가입",
            description = "일반 회원가입을 수행합니다. 성공 시 응답 본문은 ApiResponse(status, success, data) 래퍼 구조로 반환됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SignupApiResponseDoc.class),
                            examples = @ExampleObject(
                                    name = "회원가입 성공 예시",
                                    value = """
                                            {
                                              "status": 201,
                                              "success": true,
                                              "data": {
                                                "id": 1,
                                                "email": "user@example.com",
                                                "name": "홍길동"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 값 또는 유효성 검증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 사용 중인 식별자 또는 중복 회원"
            )
    })
    ResponseEntity<ApiResponse<SignupResponse>> signup(
            @RequestBody(
                    description = "회원가입 요청 정보",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SignupRequest.class)
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody @Valid SignupRequest request
    );

    @Operation(
            summary = "초대 기반 회원가입",
            description = "초대 정보를 사용해 회원가입을 수행합니다. 성공 시 응답 본문은 ApiResponse(status, success, data) 래퍼 구조로 반환됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "초대 기반 회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SignupApiResponseDoc.class),
                            examples = @ExampleObject(
                                    name = "초대 기반 회원가입 성공 예시",
                                    value = """
                                            {
                                              "status": 201,
                                              "success": true,
                                              "data": {
                                                "id": 2,
                                                "email": "invitee@example.com",
                                                "name": "초대사용자"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 초대 정보 또는 유효성 검증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "초대 정보를 찾을 수 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 가입된 사용자 또는 사용할 수 없는 초대"
            )
    })
    ResponseEntity<ApiResponse<SignupResponse>> signupWithInvitation(
            @RequestBody(
                    description = "초대 기반 회원가입 요청 정보",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SignupWithInvitationRequest.class)
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody @Valid SignupWithInvitationRequest request
    );

    @Operation(
            summary = "로그인",
            description = "아이디와 비밀번호로 로그인하고 액세스 토큰 및 리프레시 토큰을 발급합니다. 성공 시 응답 본문은 ApiResponse(status, success, data) 래퍼 구조로 반환됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SignInApiResponseDoc.class),
                            examples = @ExampleObject(
                                    name = "로그인 성공 예시",
                                    value = """
                                            {
                                              "status": 200,
                                              "success": true,
                                              "data": {
                                                "accessToken": "Bearer access-token",
                                                "refreshToken": "refresh-token"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 값"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            )
    })
    ResponseEntity<ApiResponse<SignInResponse>> signIn(
            @RequestBody(
                    description = "로그인 요청 정보",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SignInRequest.class)
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody @Valid SignInRequest request
    );

    @Operation(
            summary = "토큰 재발급",
            description = "기존 액세스 토큰과 리프레시 토큰을 바탕으로 새로운 토큰을 재발급합니다. 성공 시 응답 본문은 ApiResponse(status, success, data) 래퍼 구조로 반환됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenReissueApiResponseDoc.class),
                            examples = @ExampleObject(
                                    name = "토큰 재발급 성공 예시",
                                    value = """
                                            {
                                              "status": 200,
                                              "success": true,
                                              "data": {
                                                "accessToken": "Bearer new-access-token",
                                                "refreshToken": "new-refresh-token"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 값"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 토큰 또는 재발급 불가"
            )
    })
    ResponseEntity<ApiResponse<TokenReissueResponse>> reissue(
            @Parameter(
                    name = "Authorization",
                    description = "기존 액세스 토큰. 일반적으로 `Bearer {accessToken}` 형식입니다.",
                    required = false
            )
            @RequestHeader(name = "Authorization", required = false) String at,
            @RequestBody(
                    description = "토큰 재발급 요청 정보(리프레시 토큰 포함)",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TokenReissueRequest.class)
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody @Valid TokenReissueRequest request
    );

    @Operation(
            summary = "로그아웃",
            description = "현재 액세스 토큰과 리프레시 토큰을 기준으로 로그아웃 처리합니다. 성공 시 HTTP 204 No Content를 반환하며 응답 본문은 없습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "로그아웃 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 값"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 토큰"
            )
    })
    ResponseEntity<Void> logout(
            @Parameter(
                    name = "Authorization",
                    description = "액세스 토큰. 일반적으로 `Bearer {accessToken}` 형식입니다.",
                    required = true
            )
            @RequestHeader(name = "Authorization") String at,
            @RequestBody(
                    description = "로그아웃 요청 정보(리프레시 토큰 포함)",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LogoutRequest.class)
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody @Valid LogoutRequest request
    );
}