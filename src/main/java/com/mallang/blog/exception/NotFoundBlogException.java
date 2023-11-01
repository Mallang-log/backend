package com.mallang.blog.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundBlogException extends MallangLogException {

    public NotFoundBlogException() {
        super(new ErrorCode(NOT_FOUND, "존재하지 않는 블로그입니다."));
    }
}
