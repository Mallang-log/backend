package com.mallang.blog.exception;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import org.springframework.http.HttpStatus;

public class NotFoundBlogSubscribeException extends MallangLogException {

    public NotFoundBlogSubscribeException(Long id) {
        super(new ErrorCode(HttpStatus.NOT_FOUND, "id가 %d인 블로그 구독을 찾을 수 없습니다.".formatted(id)));
    }
}
