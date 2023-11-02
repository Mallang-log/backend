package com.mallang.subscribe.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class AlreadySubscribedException extends MallangLogException {

    public AlreadySubscribedException() {
        super(new ErrorCode(HttpStatus.CONFLICT, "이미 구독한 블로그입니다."));
    }
}
