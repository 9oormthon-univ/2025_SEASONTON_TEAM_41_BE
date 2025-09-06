package com.debt.debt.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Schema(description = "닉네임 중복 응답 DTO")
public class UserDistinctNicknameResponseDto {
    @Schema(description = "메세지", example="사용 가능")
    private String message;
}