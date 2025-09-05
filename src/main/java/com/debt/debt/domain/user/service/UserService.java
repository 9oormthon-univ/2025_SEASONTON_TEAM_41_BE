package com.debt.debt.domain.user.service;

import com.debt.debt.domain.user.dto.UserLoginRequestDto;
import com.debt.debt.domain.user.dto.UserLoginResponseDto;
import com.debt.debt.domain.user.dto.UserSignupRequestDto;
import com.debt.debt.domain.user.dto.UserSignupResponseDto;
import com.debt.debt.domain.user.entity.User;
import com.debt.debt.domain.user.repository.UserRepository;
import com.debt.debt.global.exception.CustomException;
import com.debt.debt.global.exception.type.ErrorCode;
import com.debt.debt.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserSignupResponseDto signup(UserSignupRequestDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE_USER_ID);

        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .age(dto.getAge())
                .debtType(dto.getDebtType())
                .debtAmount(dto.getDebtAmount())
                .build();

        userRepository.save(user);
        return new UserSignupResponseDto(user.getId(), user.getNickname(), "회원가입이 완료되었습니다.");
    }

    public UserLoginResponseDto login(UserLoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new CustomException(ErrorCode.INVALID_PASSWORD);

        String token = jwtUtil.generateToken(user.getEmail());

        return new UserLoginResponseDto(user.getId(), user.getNickname(), token);
    }
}