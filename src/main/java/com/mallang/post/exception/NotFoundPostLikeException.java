package com.mallang.post.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class NotFoundPostLikeException extends MallangLogException {

    public NotFoundPostLikeException() {
        super(new ErrorCode(HttpStatus.NOT_FOUND, "눌린 좋아요가 없습니다."));
    }
}
