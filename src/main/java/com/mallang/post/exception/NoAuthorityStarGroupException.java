package com.mallang.post.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityStarGroupException extends MallangLogException {

    public NoAuthorityStarGroupException() {
        super(new ErrorCode(FORBIDDEN, "즐겨찾기 그룹에 대한 권한이 없습니다."));
    }
}
