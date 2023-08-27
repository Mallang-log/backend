package com.mallang.comment.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class CannotWriteSecretCommentException extends MallangLogException {

    public CannotWriteSecretCommentException() {
        super(new ErrorCode(BAD_REQUEST, "비밀 댓글은 로그인을 하셔야 작성할 수 있습니다."));
    }
}
