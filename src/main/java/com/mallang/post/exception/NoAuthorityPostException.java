package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityPostException extends MallangLogException {

    public NoAuthorityPostException() {
        super(new ErrorCode(FORBIDDEN, "포스트에 대한 권한이 없습니다."));
    }
}
