package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class PostLikeCountNegativeException extends MallangLogException {

    public PostLikeCountNegativeException() {
        super(new ErrorCode(BAD_REQUEST, "좋아요는 음수가 될 수 없습니다."));
    }
}
