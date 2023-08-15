package com.mallang.category.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class CategoryHierarchyViolationException extends MallangLogException {

    public CategoryHierarchyViolationException() {
        super(new ErrorCode(BAD_REQUEST, "자신보다 하위의 카테고리를 상위 카테고리로 둘 수 없습니다."));
    }
}
