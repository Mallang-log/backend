package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class ProtectVisibilityPasswordMustRequired extends MallangLogException {

    public ProtectVisibilityPasswordMustRequired() {
        super(new ErrorCode(BAD_REQUEST, "보호인 포스트는 비밀번호가 필요합니다."));
    }
}
