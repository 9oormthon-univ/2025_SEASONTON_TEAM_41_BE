package com.debt.debt.global.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 BAD_REQUEST
    INVALID_REQUEST(400, "C001", "잘못된 요청입니다."),
    DUPLICATE_USER_ID(400, "C002", "이미 존재하는 아이디입니다."),
    INVALID_INPUT_VALUE(400, "C003", "입력 값이 올바르지 않습니다."),
    INVALID_PASSWORD(400, "U003", "비밀번호가 올바르지 않습니다."),

    // 401 UNAUTHORIZED
    UNAUTHORIZED(401, "A001", "인증되지 않은 사용자입니다."),
    INVALID_TOKEN(401, "A002", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(401, "A003", "토큰이 만료되었습니다."),

    // 404 NOT_FOUND
    USER_NOT_FOUND(404, "U001", "사용자를 찾을 수 없습니다."),
    Entity_NOT_FOUND(404, "U002", "객체를 찾을 수 없습니다."),

    // 500 INTERNAL_SERVER_ERROR
    INTERNAL_SERVER_ERROR(500, "S001", "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;
}
