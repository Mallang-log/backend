package com.mallang.auth.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundMemberException extends MallangLogException {

    public NotFoundMemberException() {
        super(new ErrorCode(NOT_FOUND, "존재하지 않는 회원입니다."));
    }
}
