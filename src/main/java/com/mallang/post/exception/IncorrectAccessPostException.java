package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class IncorrectAccessPostException extends MallangLogException {

    public IncorrectAccessPostException() {
        super(new ErrorCode(BAD_REQUEST, "포스트에 대한 비정상적인 접근입니다."));
    }
}
