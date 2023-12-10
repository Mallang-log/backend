package com.mallang.category;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class ChildCategoryExistException extends MallangLogException {

    public ChildCategoryExistException() {
        super(new ErrorCode(BAD_REQUEST, "하위 카테고리가 존재하기 때문에 해당 카테고리를 지울 수 없습니다."));
    }
}
