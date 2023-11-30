package com.mallang.comment.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityCommentException extends MallangLogException {

    public NoAuthorityCommentException() {
        super(new ErrorCode(FORBIDDEN, "댓글에 대한 권한이 없습니다."));
    }
}
