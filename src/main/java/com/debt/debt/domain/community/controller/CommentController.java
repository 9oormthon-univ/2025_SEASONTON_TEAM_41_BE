package com.debt.debt.domain.community.controller;

import com.debt.debt.domain.community.dto.comment.CommentRequestDto;
import com.debt.debt.domain.community.dto.comment.CommentResponseDto;
import com.debt.debt.domain.community.service.CommentService;
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

@Tag(name="Comment")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommentController {
    @Autowired
    private CommentService service;

    @Operation(summary = "게시글 생성")
    @PostMapping("/{articleId}/comments")
    public ResponseEntity<CommentResponseDto> create(@Valid @RequestBody CommentRequestDto requestDto, @PathVariable Long articleId, @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getUser().getId();
        CommentResponseDto responseDto = service.create(requestDto,articleId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
