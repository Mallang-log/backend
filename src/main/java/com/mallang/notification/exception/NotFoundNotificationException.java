package com.mallang.notification.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundNotificationException extends MallangLogException {

    public NotFoundNotificationException() {
        super(new ErrorCode(NOT_FOUND, "찾으시는 알림이 없습니다."));
    }
}
