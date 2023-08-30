package com.mallang.comment.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class DifferentPostFromParentCommentException extends MallangLogException {

    public DifferentPostFromParentCommentException() {
        super(new ErrorCode(BAD_REQUEST, "댓글의 포스트와 대댓글의 포스트가 다릅니다."));
    }
}
