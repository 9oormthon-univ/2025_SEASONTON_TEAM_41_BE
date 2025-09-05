package com.debt.debt.domain.mypage.service;

import com.debt.debt.domain.mypage.dto.MyPageEditRequestDto;
import com.debt.debt.domain.mypage.dto.MyPageEditResponseDto;
import com.debt.debt.domain.mypage.dto.MyPageResponseDto;
import com.debt.debt.domain.user.entity.User;
import com.debt.debt.domain.user.repository.UserRepository;
import com.debt.debt.global.exception.CustomException;
import com.debt.debt.global.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {
    @Autowired
    private UserRepository userRepository;

    public MyPageResponseDto show(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new MyPageResponseDto(user.getNickname(), user.getAge(), user.getDebtType(), user.getDebtAmount());
    }

    @Transactional
    public MyPageEditResponseDto update(Long id, MyPageEditRequestDto requestDto) {
        User target = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        target.setNickname(requestDto.getNickname());
        target.setAge(requestDto.getAge());
        target.setDebtType(requestDto.getDebtType());
        target.setDebtAmount(requestDto.getDebtAmount());

        userRepository.save(target);
        return new MyPageEditResponseDto("수정이 완료되었습니다.");

    }
}