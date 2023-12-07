package com.mallang.category.exception;

import static org.springframework.http.HttpStatus.CONFLICT;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class DuplicateCategoryNameException extends MallangLogException {

    public DuplicateCategoryNameException(String message) {
        super(new ErrorCode(CONFLICT, message));
    }

    public DuplicateCategoryNameException() {
        super(new ErrorCode(CONFLICT, "형제 카테고리 간 중복되는 이름이 있습니다."));
    }
}
