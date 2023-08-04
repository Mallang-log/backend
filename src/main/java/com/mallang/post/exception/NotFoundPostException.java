package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundPostException extends MallangLogException {

    public NotFoundPostException() {
        super(new ErrorCode(NOT_FOUND, "존재하지 않는 게시글입니다."));
    }
}
