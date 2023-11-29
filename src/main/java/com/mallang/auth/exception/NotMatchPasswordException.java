package com.mallang.auth.exception;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotMatchPasswordException extends MallangLogException {

    public NotMatchPasswordException() {
        super(new ErrorCode(UNAUTHORIZED, "비밀번호가 일치하지 않습니다."));
    }
}
