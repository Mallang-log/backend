package com.mallang.reference.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundReferenceLinkMetaTitleException extends MallangLogException {

    public NotFoundReferenceLinkMetaTitleException() {
        super(new ErrorCode(BAD_REQUEST, "주어진 Url에 title 혹은 og:title 정보가 없습니다."));
    }
}
