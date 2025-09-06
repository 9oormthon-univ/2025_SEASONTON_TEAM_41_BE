package com.debt.debt.domain.community.controller;

import com.debt.debt.domain.community.dto.article.ArticleRequestDto;
import com.debt.debt.domain.community.dto.article.ArticleResponseDto;
import com.debt.debt.domain.community.dto.like.LikeRequestDto;
import com.debt.debt.domain.community.dto.like.LikeResponseDto;
import com.debt.debt.domain.community.service.ArticleService;
import com.debt.debt.domain.community.service.LikeService;
import com.debt.debt.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name="Like")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class LikeController {
    @Autowired
    private LikeService service;

    @Operation(summary = "좋아요 누르기")
    @PostMapping("/{articleId}/likes")
    public ResponseEntity<LikeResponseDto> create(@Valid @RequestBody LikeRequestDto requestDto, @PathVariable Long articleId, @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getUser().getId();
        LikeResponseDto responseDto = service.create(requestDto,articleId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "좋아요 취소")
    @DeleteMapping("/{articleId}/likes")
    public ResponseEntity<LikeResponseDto> delete(@PathVariable Long articleId, @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getUser().getId();
        LikeResponseDto responseDto = service.delete(articleId,userId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
