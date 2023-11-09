package com.mallang.post.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class NoAuthorityAccessPostException extends MallangLogException {

    public NoAuthorityAccessPostException() {
        super(new ErrorCode(HttpStatus.FORBIDDEN, "접근 권한이 없는 포스트입니다."));
    }
}
