package com.mallang.blog.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class UnsubscribeUnsubscribedBlogException extends MallangLogException {

    public UnsubscribeUnsubscribedBlogException() {
        super(new ErrorCode(HttpStatus.BAD_REQUEST, "구독하지 않은 블로그를 구독취소할 수 없습니다."));
    }
}
