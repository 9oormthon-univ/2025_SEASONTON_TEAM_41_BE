package com.debt.debt.domain.community.dto.article;

import com.debt.debt.domain.community.entity.DebtType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Schema(description = "게시글 작성 요청 DTO")
public class ArticleRequestDto {
    @Schema(description = "제목", example="게시글")
    @NotBlank(message = "필수입니다.")
    @Pattern(regexp = "^.{0,100}$", message = "100자 이하로 입력해주세요.")
    private String title;

    @Schema(description = "내용", example="빚이 너무 많아요.")
    @NotBlank(message = "필수입니다.")
    @Pattern(regexp = "^.{0,1000}$", message = "1000자 이하로 입력해주세요.")
    private String content;

    @Schema(description = "부채 유형", example="학자금")
    private DebtType debtType;
}
