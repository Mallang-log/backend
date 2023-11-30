package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundDraftException extends MallangLogException {

    public NotFoundDraftException() {
        super(new ErrorCode(NOT_FOUND, "존재하지 않는 임시글입니다."));
    }
}
