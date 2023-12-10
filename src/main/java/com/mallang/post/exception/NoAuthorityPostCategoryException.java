package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityPostCategoryException extends MallangLogException {

    public NoAuthorityPostCategoryException() {
        super(new ErrorCode(FORBIDDEN, "카테고리에 대한 권한이 없습니다."));
    }
}
