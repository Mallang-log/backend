package com.mallang.common.execption;

import static com.mallang.common.execption.ErrorCode.INTERNAL_SERVER_ERROR_CODE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MallangLogException extends RuntimeException {

    private final ErrorCode errorCode;

    public MallangLogException(String message) {
        super(message);
        this.errorCode = new ErrorCode(INTERNAL_SERVER_ERROR, message);
    }

    public MallangLogException() {
        this.errorCode = INTERNAL_SERVER_ERROR_CODE;
    }
}
