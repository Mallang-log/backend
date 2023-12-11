package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundStarGroupException extends MallangLogException {

    public NotFoundStarGroupException() {
        super(new ErrorCode(NOT_FOUND, "존재하지 않는 즐겨찾기 그룹입니다."));
    }
}
