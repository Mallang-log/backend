package com.mallang.comment.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class CommentDepthConstraintViolationException extends MallangLogException {

    public CommentDepthConstraintViolationException() {
        super(new ErrorCode(BAD_REQUEST, "대댓글에 대한 댓글은 작성할 수 없습니다."));
    }
}
