package com.mallang.post.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class ProtectVisibilityPasswordMustRequired extends MallangLogException {

    public ProtectVisibilityPasswordMustRequired() {
        super(new ErrorCode(HttpStatus.BAD_REQUEST, "보호인 포스트는 비밀번호가 필요합니다."));
    }
}
