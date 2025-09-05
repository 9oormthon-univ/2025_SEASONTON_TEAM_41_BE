package com.debt.debt.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "로그인 요청 DTO")
public class UserLoginRequestDto {
    @Schema(description = "아이디 (영문 + 숫자 4~20자)", example = "test1234")
    @NotBlank(message = "아이디는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{4,20}$",
            message = "아이디는 영문과 숫자를 포함한 4~20자여야 하며, 특수문자는 사용할 수 없습니다."
    )
    private String userId;

    @Schema(description = "비밀번호 (영문 + 숫자 8~16자)", example = "pass1234")
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$",
            message = "비밀번호는 영문과 숫자를 포함한 8~16자여야 하며, 특수문자는 사용할 수 없습니다."
    )
    private String password;
}