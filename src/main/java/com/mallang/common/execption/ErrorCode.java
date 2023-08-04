package com.mallang.common.execption;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.http.HttpStatus;

public record ErrorCode(
        HttpStatus status,
        String message
) {

    static ErrorCode INTERNAL_SERVER_ERROR_CODE =
            new ErrorCode(INTERNAL_SERVER_ERROR, "서버에서 알 수 없는 오류가 발생했습니다.");

}
