package com.mallang.common.execption;

import static com.mallang.common.execption.ErrorCode.INTERNAL_SERVER_ERROR_CODE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import lombok.Getter;

@Getter
public class MallangLogException extends RuntimeException {

    private final ErrorCode errorCode;

    public MallangLogException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public MallangLogException(String message) {
        super(message);
        this.errorCode = new ErrorCode(INTERNAL_SERVER_ERROR, message);
    }

    public MallangLogException() {
        this.errorCode = INTERNAL_SERVER_ERROR_CODE;
    }
}
