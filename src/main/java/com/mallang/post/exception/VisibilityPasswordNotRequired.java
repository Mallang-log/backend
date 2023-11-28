package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class VisibilityPasswordNotRequired extends MallangLogException {

    public VisibilityPasswordNotRequired() {
        super(new ErrorCode(BAD_REQUEST, "보호가 아닌 포스트에는 비밀번호가 필요하지 않습니다."));
    }
}
