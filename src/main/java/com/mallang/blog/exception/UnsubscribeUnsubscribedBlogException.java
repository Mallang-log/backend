package com.mallang.blog.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class UnsubscribeUnsubscribedBlogException extends MallangLogException {

    public UnsubscribeUnsubscribedBlogException() {
        super(new ErrorCode(BAD_REQUEST, "구독하지 않은 블로그를 구독취소할 수 없습니다."));
    }
}
