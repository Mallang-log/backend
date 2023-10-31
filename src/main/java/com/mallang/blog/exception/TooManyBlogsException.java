package com.mallang.blog.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class TooManyBlogsException extends MallangLogException {

    public TooManyBlogsException() {
        super(new ErrorCode(BAD_REQUEST, "만들 수 있는 블로그의 수가 초과되었습니다. 개인당 1개의 블로그만 만들 수 있습니다."));
    }
}
