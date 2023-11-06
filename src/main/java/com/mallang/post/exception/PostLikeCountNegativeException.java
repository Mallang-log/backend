package com.mallang.post.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class PostLikeCountNegativeException extends MallangLogException {

    public PostLikeCountNegativeException() {
        super(new ErrorCode(HttpStatus.BAD_REQUEST, "좋아요는 음수가 될 수 없습니다."));
    }
}
