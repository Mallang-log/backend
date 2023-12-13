package com.mallang.reference.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundLabelException extends MallangLogException {

    public NotFoundLabelException() {
        super(new ErrorCode(NOT_FOUND, "존재하지 않는 라벨입니다."));
    }
}
