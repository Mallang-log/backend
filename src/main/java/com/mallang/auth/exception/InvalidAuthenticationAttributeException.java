package com.mallang.auth.exception;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class InvalidAuthenticationAttributeException extends MallangLogException {

    public InvalidAuthenticationAttributeException() {
        super(new ErrorCode(UNAUTHORIZED, "세션에 담긴 값이 잘못되었습니다."));
    }
}
