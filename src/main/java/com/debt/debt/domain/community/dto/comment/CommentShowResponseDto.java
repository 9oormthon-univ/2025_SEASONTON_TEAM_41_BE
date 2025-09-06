package com.debt.debt.domain.community.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Schema(description = "댓글 열람 응답 DTO")
public class CommentShowResponseDto{
    @Schema(description = "내용", example="저도요.")
    private String content;

    @Schema(description = "작성자", example="유저")
    private String nickname;

    @Schema(description = "프로필 이미지 경로")
    private String profileImagePath;

    @Schema(description = "생성일", example="2025-05-28T18:30:00")
    private LocalDateTime createdAt;
}