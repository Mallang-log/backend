package com.mallang.blog.exception;

import static org.springframework.http.HttpStatus.CONFLICT;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class DuplicateBlogNameException extends MallangLogException {

    public DuplicateBlogNameException() {
        super(new ErrorCode(CONFLICT, "이미 존재하는 블로그 이름입나디. 다른 이름을 사용해주세요."));
    }
}
