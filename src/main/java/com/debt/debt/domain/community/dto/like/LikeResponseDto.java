package com.debt.debt.domain.community.dto.like;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Schema(description = "좋아요 응답 DTO")
public class LikeResponseDto {
    @Schema(description = "좋아요수", example="4")
    private Integer likes;

    @Schema(description = "메세지", example="좋아요가 반영되었습니다.")
    private String message;
}
