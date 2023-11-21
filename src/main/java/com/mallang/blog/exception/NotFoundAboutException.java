package com.mallang.blog.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundAboutException extends MallangLogException {

    public NotFoundAboutException(String message) {
        super(new ErrorCode(NOT_FOUND, message));
    }

    public NotFoundAboutException() {
        super(new ErrorCode(NOT_FOUND, "존재하지 않는 About입니다."));
    }
}
