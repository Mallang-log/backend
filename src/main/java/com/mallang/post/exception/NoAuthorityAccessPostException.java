package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityAccessPostException extends MallangLogException {

    public NoAuthorityAccessPostException() {
        super(new ErrorCode(FORBIDDEN, "접근 권한이 없는 포스트입니다."));
    }
}
