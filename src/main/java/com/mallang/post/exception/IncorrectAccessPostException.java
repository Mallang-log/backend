package com.mallang.post.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class IncorrectAccessPostException extends MallangLogException {

    public IncorrectAccessPostException() {
        super(new ErrorCode(HttpStatus.BAD_REQUEST, "포스트에 대한 비정상적인 접근입니다."));
    }
}
