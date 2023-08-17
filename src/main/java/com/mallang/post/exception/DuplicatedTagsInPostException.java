package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class DuplicatedTagsInPostException extends MallangLogException {

    public DuplicatedTagsInPostException() {
        super(new ErrorCode(BAD_REQUEST, "한 포스트에 동일한 태그가 붙을 수 없습니다."));
    }
}
