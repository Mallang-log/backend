package com.mallang.notification.query.response;

public record BlogSubscribedNotificationListResponse(
        String type,
        Long id,
        boolean isRead,
        Long targetMemberId,
        Long blogId,
        Long subscriberId
) implements NotificationListResponse {
}
