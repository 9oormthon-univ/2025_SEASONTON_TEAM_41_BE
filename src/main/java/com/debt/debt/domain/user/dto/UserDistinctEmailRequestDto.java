package com.debt.debt.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Schema(description = "이메일 중복 요청 DTO")
public class UserDistinctEmailRequestDto {
    @Schema(description = "이메일", example="user@example.com")
    @NotBlank(message = "필수입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;
}