package com.debt.debt.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Schema(description = "응답 DTO")
public class MyPageEditResponseDto{
    @Schema(description = "메세지", example="수정이 완료되었습니다.")
    private String message;
}