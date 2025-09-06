package com.debt.debt.domain.community.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Schema(description = "게시글 작성 응답 DTO")
public class ArticleResponseDto{
    @Schema(description = "ID", example="1")
    private Long articleId;

    @Schema(description = "메세지", example="게시글이 작성되었습니다.")
    private String message;
}