package com.mallang.blog.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class IsNotBlogOwnerException extends MallangLogException {

    public IsNotBlogOwnerException() {
        super(new ErrorCode(FORBIDDEN, "블로그 주인이 아닙니다."));
    }
}
