package com.debt.debt.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Schema(description = "닉네임 중복 요청 DTO")
public class UserDistinctNicknameRequestDto {
    @Schema(description = "닉네임", example="유저")
    @NotBlank(message = "필수입니다.")
    private String nickname;
}