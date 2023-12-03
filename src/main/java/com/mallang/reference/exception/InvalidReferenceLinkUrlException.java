package com.mallang.reference.exception;


import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class InvalidReferenceLinkUrlException extends MallangLogException {

    public InvalidReferenceLinkUrlException(String message) {
        super(new ErrorCode(BAD_REQUEST, message));
    }
}
