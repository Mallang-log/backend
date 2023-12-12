package com.mallang.reference.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class InvalidLabelColorException extends MallangLogException {

    public InvalidLabelColorException(String message) {
        super(new ErrorCode(BAD_REQUEST, message));
    }
}
