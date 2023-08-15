package com.mallang.category.exception;

import static org.springframework.http.HttpStatus.CONFLICT;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class DuplicateCategoryNameException extends MallangLogException {

    public DuplicateCategoryNameException() {
        super(new ErrorCode(CONFLICT, "이미 사용중인 카테고리 이름입니다."));
    }
}
