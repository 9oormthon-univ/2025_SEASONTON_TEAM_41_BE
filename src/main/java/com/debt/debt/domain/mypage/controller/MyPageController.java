package com.debt.debt.domain.mypage.controller;

import com.debt.debt.domain.mypage.dto.MyPageEditRequestDto;
import com.debt.debt.domain.mypage.dto.MyPageEditResponseDto;
import com.debt.debt.domain.mypage.dto.MyPageResponseDto;
import com.debt.debt.domain.mypage.service.MyPageService;
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

@Tag(name="card-controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController{
    @Autowired
    private MyPageService service;

    @Operation(summary = "마이페이지 열람")
    @GetMapping("")
    public ResponseEntity<MyPageResponseDto> get(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        MyPageResponseDto responseDto= service.show(userId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "마이페이지 수정")
    @PatchMapping("/edit")
    public ResponseEntity<MyPageEditResponseDto> edit(@Valid @RequestBody MyPageEditRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userId = userDetails.getUser().getId();
        MyPageEditResponseDto responseDto= service.update(userId,requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}