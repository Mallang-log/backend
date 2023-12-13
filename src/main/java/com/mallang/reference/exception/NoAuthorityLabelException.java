package com.mallang.reference.exception;


import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityLabelException extends MallangLogException {

    public NoAuthorityLabelException() {
        super(new ErrorCode(FORBIDDEN, "참고 링크 라벨에 대한 권한이 없습니다."));
    }
}
