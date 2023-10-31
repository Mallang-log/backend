package com.mallang.blog.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class IsNotBlogOwnerException extends MallangLogException {

    public IsNotBlogOwnerException() {
        super(new ErrorCode(HttpStatus.FORBIDDEN, "블로그 주인이 아닙니다."));
    }
}
