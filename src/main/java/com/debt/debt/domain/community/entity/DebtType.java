package com.debt.debt.domain.community.entity;

import com.debt.debt.global.exception.CustomException;
import com.debt.debt.global.exception.type.ErrorCode;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum DebtType {
    학자금대출("학자금대출"),
    신용대출("신용대출"),
    전세대출("전세대출"),
    기타("기타");

    private final String koreanName;

    DebtType(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public static DebtType fromKoreanName(String name) {
        for (DebtType type : values()) {
            if (type.getKoreanName().equals(name)) {
                return type;
            }
        }
        throw new CustomException(ErrorCode.INVALID_DEBT_TYPE);
    }
}