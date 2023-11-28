package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.CONFLICT;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class AlreadyStarPostException extends MallangLogException {

    public AlreadyStarPostException() {
        super(new ErrorCode(CONFLICT, "이미 즐겨찾기한 게시물입니다."));
    }
}
