package com.mallang.reference.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class BadReferenceLinkMemoException extends MallangLogException {

    public BadReferenceLinkMemoException(int maxLength) {
        super(new ErrorCode(BAD_REQUEST, "링크의 메모는 최대 " + maxLength + "글자입니다."));
    }
}
