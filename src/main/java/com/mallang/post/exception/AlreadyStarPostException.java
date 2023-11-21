package com.mallang.post.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class AlreadyStarPostException extends MallangLogException {

    public AlreadyStarPostException() {
        super(new ErrorCode(HttpStatus.CONFLICT, "이미 즐겨찾기한 게시물입니다."));
    }
}
