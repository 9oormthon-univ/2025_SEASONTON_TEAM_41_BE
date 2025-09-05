package com.debt.debt.domain.community.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Schema(description = "댓글 작성 응답 DTO")
public class CommentResponseDto{
    @Schema(description = "ID", example="1")
    private Long commentId;

    @Schema(description = "메세지", example="댓글이 작성되었습니다.")
    private String message;
}