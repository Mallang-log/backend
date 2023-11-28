package com.mallang.blog.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundBlogSubscribeException extends MallangLogException {

    public NotFoundBlogSubscribeException(Long id) {
        super(new ErrorCode(NOT_FOUND, "id가 %d인 블로그 구독을 찾을 수 없습니다.".formatted(id)));
    }
}
