package com.mallang.subscribe.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class SelfSubscribeException extends MallangLogException {

    public SelfSubscribeException() {
        super(new ErrorCode(HttpStatus.BAD_REQUEST, "자신의 블로그를 구독하실 수 없습니다."));
    }
}
