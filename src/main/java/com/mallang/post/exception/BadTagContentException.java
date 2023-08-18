package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class BadTagContentException extends MallangLogException {

    public BadTagContentException() {
        super(new ErrorCode(BAD_REQUEST, "태그는 공백이어서는 안되며, 30글자를 초과할 수 없습니다."));
    }
}
