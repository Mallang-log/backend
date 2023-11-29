package com.mallang.blog.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityAboutException extends MallangLogException {

    public NoAuthorityAboutException() {
        super(new ErrorCode(FORBIDDEN, "소개에 대한 권한이 없습니다."));
    }
}
