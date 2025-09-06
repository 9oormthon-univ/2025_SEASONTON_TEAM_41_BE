package com.debt.debt.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Schema(description = "마이페이지 열람 응답 DTO")
public class MyPageResponseDto{
    @Schema(description = "닉네임", example="유저")
    private String nickname;

    @Schema(description = "이메일", example="user@example.com")
    private String email;


//    @Schema(description = "부채 유형", example = "학자금대출")
//    private String debtType;
//
//    @Schema(description = "부채 금액(만)", example = "600")
//    private Integer debtAmount;

    @Schema(description = "프로필 이미지 경로")
    private String profileImagePath;
}