package com.mallang.blog.exception;

import static org.springframework.http.HttpStatus.CONFLICT;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class AlreadySubscribedException extends MallangLogException {

    public AlreadySubscribedException() {
        super(new ErrorCode(CONFLICT, "이미 구독한 블로그입니다."));
    }
}
