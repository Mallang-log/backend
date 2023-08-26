package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class BadPostSearchCondException extends MallangLogException {

    public BadPostSearchCondException(String message) {
        super(new ErrorCode(BAD_REQUEST, message));
    }
}
