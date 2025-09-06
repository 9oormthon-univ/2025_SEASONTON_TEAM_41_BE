package com.debt.debt.domain.community.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Schema(description = "댓글 작성 요청 DTO")
public class CommentRequestDto {
    @Schema(description = "내용", example="저도요.")
    @NotBlank(message = "필수입니다.")
    @Pattern(regexp = "^.{0,100}$", message = "100자 이하로 입력해주세요.")
    private String content;
}