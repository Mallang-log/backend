package com.mallang.blog.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class SelfSubscribeException extends MallangLogException {

    public SelfSubscribeException() {
        super(new ErrorCode(BAD_REQUEST, "자신의 블로그를 구독하실 수 없습니다."));
    }
}
