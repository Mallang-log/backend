package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityDeletePostException extends MallangLogException {

    public NoAuthorityDeletePostException() {
        super(new ErrorCode(FORBIDDEN, "포스트를 삭제할 권한이 없습니다."));
    }
}
