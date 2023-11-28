package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.CONFLICT;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class AlreadyLikedPostException extends MallangLogException {

    public AlreadyLikedPostException() {
        super(new ErrorCode(CONFLICT, "이미 좋아요를 누른 게시물입니다."));
    }
}
