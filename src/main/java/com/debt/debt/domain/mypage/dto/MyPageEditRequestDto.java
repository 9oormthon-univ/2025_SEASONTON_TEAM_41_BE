package com.debt.debt.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Schema(description = "요청 DTO")
public class MyPageEditRequestDto {
    @Schema(description = "닉네임", example="유저")
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @Schema(description = "나이", example = "24")
    @Min(value = 0, message = "나이는 0 이상이어야 합니다.")
    @Max(value = 150, message = "나이는 150 이하이어야 합니다.")
    private Integer age;

    @Schema(description = "부채 유형", example = "학자금대출")
    private String debtType;

    @Schema(description = "부채 금액(만)", example = "600")
    private Integer debtAmount;
}