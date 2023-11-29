package com.mallang.category.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityCategoryException extends MallangLogException {

    public NoAuthorityCategoryException() {
        super(new ErrorCode(FORBIDDEN, "카테고리에 대한 권한이 없습니다."));
    }
}
