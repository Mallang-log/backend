package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityDraftException extends MallangLogException {

    public NoAuthorityDraftException() {
        super(new ErrorCode(FORBIDDEN, "임시 글 대한 권한이 없습니다."));
    }
}
