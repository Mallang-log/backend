package com.mallang.blog.exception;

import static org.springframework.http.HttpStatus.CONFLICT;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class AlreadyExistAboutException extends MallangLogException {

    public AlreadyExistAboutException() {
        super(new ErrorCode(CONFLICT, "이미 작성된 ABOUT이 있습니다."));
    }
}
