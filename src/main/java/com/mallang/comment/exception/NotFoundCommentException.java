package com.mallang.comment.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundCommentException extends MallangLogException {

    public NotFoundCommentException() {
        super(new ErrorCode(NOT_FOUND, "존재하지 않는 댓글입니다."));
    }
}
