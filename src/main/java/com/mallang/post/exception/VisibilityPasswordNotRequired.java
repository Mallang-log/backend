package com.mallang.post.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class VisibilityPasswordNotRequired extends MallangLogException {

    public VisibilityPasswordNotRequired() {
        super(new ErrorCode(HttpStatus.BAD_REQUEST, "보호가 아닌 포스트에는 비밀번호가 필요하지 않습니다."));
    }
}
