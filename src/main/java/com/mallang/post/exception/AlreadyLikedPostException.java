package com.mallang.post.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class AlreadyLikedPostException extends MallangLogException {

    public AlreadyLikedPostException() {
        super(new ErrorCode(HttpStatus.CONFLICT, "이미 좋아요를 누른 게시물입니다."));
    }
}
