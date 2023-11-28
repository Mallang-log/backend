package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundPostLikeException extends MallangLogException {

    public NotFoundPostLikeException() {
        super(new ErrorCode(NOT_FOUND, "눌린 좋아요가 없습니다."));
    }
}
