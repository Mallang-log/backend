package com.mallang.category.application.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityUseCategory extends MallangLogException {

    public NoAuthorityUseCategory() {
        super(new ErrorCode(FORBIDDEN, "다른 사용자의 카테고리를 선택했습니다."));
    }
}
