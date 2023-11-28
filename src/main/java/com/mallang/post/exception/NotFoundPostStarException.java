package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundPostStarException extends MallangLogException {

    public NotFoundPostStarException() {
        super(new ErrorCode(NOT_FOUND, "즐겨찾기한 포스트를 찾을 수 없습니다."));
    }
}
