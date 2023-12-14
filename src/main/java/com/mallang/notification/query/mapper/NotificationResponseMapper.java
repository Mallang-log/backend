package com.mallang.notification.query.mapper;

import com.mallang.notification.domain.Notification;
import com.mallang.notification.query.response.NotificationListResponse;

public interface NotificationResponseMapper {

    boolean support(Notification notification);

    NotificationListResponse mapToResponse(Notification notification);
}
