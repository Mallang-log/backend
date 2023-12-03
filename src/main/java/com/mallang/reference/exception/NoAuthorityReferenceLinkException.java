package com.mallang.reference.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityReferenceLinkException extends MallangLogException {

    public NoAuthorityReferenceLinkException() {
        super(new ErrorCode(FORBIDDEN, "참고 링크에 대한 권한이 없습니다."));
    }
}
