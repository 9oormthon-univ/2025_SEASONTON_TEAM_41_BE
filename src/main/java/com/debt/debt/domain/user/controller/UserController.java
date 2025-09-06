package com.debt.debt.domain.user.controller;

import com.debt.debt.domain.user.dto.*;
import com.debt.debt.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponseDto> signup(@RequestBody @Valid UserSignupRequestDto dto) {
        return ResponseEntity.ok(userService.signup(dto));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody @Valid UserLoginRequestDto dto) {
        return ResponseEntity.ok(userService.login(dto));
    }

    @Operation(summary = "닉네임 중복 확인")
    @PostMapping("/signup/distinctNickname")
    public ResponseEntity<UserDistinctNicknameResponseDto> distinctNickname(@RequestBody @Valid UserDistinctNicknameRequestDto dto) {
        return ResponseEntity.ok(userService.distinctNickname(dto));
    }

    @Operation(summary = "이메일 중복 확인")
    @PostMapping("/signup/distinctEmail")
    public ResponseEntity<UserDistinctEmailResponseDto> distinctNickname(@RequestBody @Valid UserDistinctEmailRequestDto dto) {
        return ResponseEntity.ok(userService.distinctEmail(dto));
    }
}