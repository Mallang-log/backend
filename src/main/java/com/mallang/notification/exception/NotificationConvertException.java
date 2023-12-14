package com.mallang.notification.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.mallang.common.domain.DomainEvent;
import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotificationConvertException extends MallangLogException {

    public NotificationConvertException(DomainEvent<?> event) {
        super(new ErrorCode(
                INTERNAL_SERVER_ERROR,
                "주어진 이벤드 (%s) 를 알림으로 변환하는데 실패했습니다.".formatted(event.getClass().getSimpleName())
        ));
    }
}
