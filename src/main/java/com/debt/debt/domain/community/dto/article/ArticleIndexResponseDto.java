package com.debt.debt.domain.community.dto.article;

import com.debt.debt.domain.community.entity.DebtType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Schema(description = "게시글 목록 응답 DTO")
public class ArticleIndexResponseDto{
    @Schema(description = "ID", example="1")
    private Long articleId;

    @Schema(description = "제목", example="게시글")
    private String title;

    @Schema(description = "내용", example="빚이 너무 많아요")
    private String contet;

    @Schema(description = "부채 유형", example="학자금")
    private DebtType debtType;

    @Schema(description = "작성자", example="유저")
    private String nickname;

    @Schema(description = "프로필 이미지 경로")
    private String profileImagePath;

    @Schema(description = "생성일", example="2025-05-27T10:15:00")
    private LocalDateTime createdAt;

    @Schema(description = "좋아요수", example="3")
    private Integer likes;

    @Schema(description = "댓글수", example="2")
    private Integer commentCount;
}