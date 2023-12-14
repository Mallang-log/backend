package com.mallang.notification.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;
import com.mallang.notification.domain.Notification;

public class NotificationResponseMappingException extends MallangLogException {

    public NotificationResponseMappingException(Notification notification) {
        super(new ErrorCode(
                INTERNAL_SERVER_ERROR,
                "주어진 알림(%s)을 지원하는 mapper 가 없습니다..".formatted(notification.getClass().getSimpleName())
        ));
    }
}
