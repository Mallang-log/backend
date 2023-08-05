package com.mallang.category.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityUpdateCategoryException extends MallangLogException {

    public NoAuthorityUpdateCategoryException() {
        super(new ErrorCode(FORBIDDEN, "카테고리를 업데이트할 권한이 없습니다."));
    }
}
