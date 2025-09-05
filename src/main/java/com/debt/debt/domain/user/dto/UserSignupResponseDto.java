package com.debt.debt.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "회원가입 응답 DTO")
public class UserSignupResponseDto {
    @Schema(description = "유저 고유 ID", example = "2")
    private Long userId;

    @Schema(description = "닉네임", example = "유저")
    private String nickname;

    @Schema(description = "응답 메시지", example = "회원가입이 완료되었습니다.")
    private String message;
}