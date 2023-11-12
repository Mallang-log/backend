package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class InvalidPostIntroLengthException extends MallangLogException {

    public InvalidPostIntroLengthException() {
        super(new ErrorCode(BAD_REQUEST, "포스트 인트로는 1글자 이상 255글자 이하여야 합니다."));
    }
}
