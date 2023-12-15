package com.mallang.notification.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NoAuthorityNotificationException extends MallangLogException {

    public NoAuthorityNotificationException() {
        super(new ErrorCode(FORBIDDEN, "해당 알림에 대한 권한이 없습니다."));
    }
}
