package com.mallang.reference.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class BadReferenceLinkUrlException extends MallangLogException {

    public BadReferenceLinkUrlException() {
        super(new ErrorCode(BAD_REQUEST, "링크가 입력되지 않았거나, 공백으로만 이루어져 있습니다."));
    }
}
