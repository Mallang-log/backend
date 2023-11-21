package com.mallang.post.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class NotFoundPostStarException extends MallangLogException {

    public NotFoundPostStarException() {
        super(new ErrorCode(HttpStatus.NOT_FOUND, "즐겨찾기한 포스트를 찾을 수 없습니다."));
    }
}
