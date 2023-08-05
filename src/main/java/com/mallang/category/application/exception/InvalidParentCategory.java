package com.mallang.category.application.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class InvalidParentCategory extends MallangLogException {

    public InvalidParentCategory() {
        super(new ErrorCode(BAD_REQUEST, "부모 카테고리 Id가 유효하지 않습니다."));
    }
}
