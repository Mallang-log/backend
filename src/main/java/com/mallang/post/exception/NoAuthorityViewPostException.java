package com.mallang.post.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class NoAuthorityViewPostException extends MallangLogException {

    public NoAuthorityViewPostException() {
        super(new ErrorCode(HttpStatus.FORBIDDEN, "조회 권한이 없는 게시물입니다."));
    }
}
