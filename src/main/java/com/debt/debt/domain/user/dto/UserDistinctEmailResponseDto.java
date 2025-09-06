package com.debt.debt.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Schema(description = "이메일 중복 응답 DTO")
public class UserDistinctEmailResponseDto {
    @Schema(description = "메세지", example="사용 가능")
    private String message;
}