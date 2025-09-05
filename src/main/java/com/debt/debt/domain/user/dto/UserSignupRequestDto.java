package com.debt.debt.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원가입 요청 DTO")
public class UserSignupRequestDto {
    @Schema(description = "아이디 (영문 + 숫자 4~20자)", example = "user1234")
    @NotBlank(message = "아이디는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{4,20}$",
            message = "아이디는 영문과 숫자를 포함한 4~20자여야 하며, 다른 문자는 포함될 수 없습니다."
    )
    private String userId;

    @Schema(description = "비밀번호 (영문 + 숫자 8~16자)", example = "pass1234")
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$",
            message = "비밀번호는 영문과 숫자를 포함한 8~16자여야 하며, 특수문자는 사용할 수 없습니다."
    )
    private String password;

    @Schema(description = "닉네임", example = "유저")
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