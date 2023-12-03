package com.mallang.reference.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundReferenceLinkException extends MallangLogException {

    public NotFoundReferenceLinkException() {
        super(new ErrorCode(NOT_FOUND, "참고 링크가 존재하지 않습니다."));
    }
}
