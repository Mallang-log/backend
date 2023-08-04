package com.mallang.auth.exception;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthenticationSessionException extends MallangLogException {

    public NoAuthenticationSessionException() {
        super(new ErrorCode(UNAUTHORIZED, "인증 정보가 없거나 만료되었습니다."));
    }
}
