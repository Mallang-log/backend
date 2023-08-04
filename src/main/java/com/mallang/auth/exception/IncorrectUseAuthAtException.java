package com.mallang.auth.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class IncorrectUseAuthAtException extends MallangLogException {

    public IncorrectUseAuthAtException() {
        super(new ErrorCode(INTERNAL_SERVER_ERROR, "@Auth 어노테이션을 잘못 사용했습니다."));
    }
}

