package com.mallang.category.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityDeleteCategoryException extends MallangLogException {

    public NoAuthorityDeleteCategoryException() {
        super(new ErrorCode(FORBIDDEN, "카테고리를 제거할 권한이 없습니다."));
    }
}
