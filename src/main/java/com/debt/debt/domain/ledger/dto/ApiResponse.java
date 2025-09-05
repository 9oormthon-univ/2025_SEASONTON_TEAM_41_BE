package com.debt.debt.domain.ledger.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse {
    private String message;
    private Object data;

    public static ApiResponse success(Object data) {
        return new ApiResponse("요청이 성공적으로 처리되었습니다.", data);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(message, null);
    }
}
