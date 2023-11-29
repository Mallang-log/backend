package com.mallang.blog.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityBlogException extends MallangLogException {

    public NoAuthorityBlogException() {
        super(new ErrorCode(FORBIDDEN, "블로그에 대한 권한이 없습니다."));
    }
}
