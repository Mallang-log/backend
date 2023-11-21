package com.mallang.blog.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class AlreadyExistAboutException extends MallangLogException {

    public AlreadyExistAboutException() {
        super(new ErrorCode(HttpStatus.CONFLICT, "이미 작성된 ABOUT이 있습니다."));
    }
}
