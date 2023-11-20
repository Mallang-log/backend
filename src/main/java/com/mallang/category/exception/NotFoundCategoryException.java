package com.mallang.category.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundCategoryException extends MallangLogException {

    public NotFoundCategoryException(String message) {
        super(new ErrorCode(NOT_FOUND, message));
    }

    public NotFoundCategoryException() {
        super(new ErrorCode(NOT_FOUND, "존재하지 않는 카테고리입니다."));
    }
}
