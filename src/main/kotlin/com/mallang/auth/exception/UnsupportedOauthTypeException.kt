package com.mallang.auth.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class UnsupportedOauthTypeException extends MallangLogException {

    public UnsupportedOauthTypeException() {
        super(new ErrorCode(INTERNAL_SERVER_ERROR, "Oauth 에 문제가 있습니다."));
    }
}
