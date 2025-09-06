package com.debt.debt.domain.community.controller;

import com.debt.debt.domain.community.dto.article.ArticleIndexResponseDto;
import com.debt.debt.domain.community.dto.article.ArticleRequestDto;
import com.debt.debt.domain.community.dto.article.ArticleResponseDto;
import com.debt.debt.domain.community.dto.article.ArticleShowResponseDto;
import com.debt.debt.domain.community.service.ArticleService;
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

import java.util.List;

@Tag(name="Article")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class ArticleController{
    @Autowired
    private ArticleService service;

    @Operation(summary = "게시글 생성")
    @PostMapping("/articles")
    public ResponseEntity<ArticleResponseDto> create(@Valid @RequestBody ArticleRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getUser().getId();
        ArticleResponseDto responseDto = service.create(requestDto,userId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "모든 게시글 표시")
    @GetMapping("")
    public ResponseEntity<List<ArticleIndexResponseDto>> getAll(@RequestParam(required = false, defaultValue = "LATEST") String sort,// LATEST, LIKE
                                                                @RequestParam(required = false) String debtType,
                                                                @RequestParam(required = false) String search) {
        List<ArticleIndexResponseDto> responseDtos = service.index(sort,debtType,search);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @Operation(summary = "게시글 열람")
    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleShowResponseDto> get(@PathVariable Long articleId) {
        ArticleShowResponseDto responseDto= service.show(articleId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}