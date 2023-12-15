package com.mallang.notification.query.mapper;

import static com.mallang.notification.domain.type.BlogSubscribedNotification.BLOG_SUBSCRIBED_NOTIFICATION_TYPE;

import com.mallang.notification.domain.Notification;
import com.mallang.notification.domain.type.BlogSubscribedNotification;
import com.mallang.notification.query.response.BlogSubscribedNotificationListResponse;
import com.mallang.notification.query.response.NotificationListResponse;
import org.springframework.stereotype.Component;

@Component
public class BlogSubscribedNotificationResponseMapper implements NotificationResponseMapper {

    @Override
    public boolean support(Notification notification) {
        return notification instanceof BlogSubscribedNotification;
    }

    @Override
    public NotificationListResponse mapToResponse(Notification notification) {
        BlogSubscribedNotification blogSubscribedNotification = (BlogSubscribedNotification) notification;
        return new BlogSubscribedNotificationListResponse(
                BLOG_SUBSCRIBED_NOTIFICATION_TYPE,
                blogSubscribedNotification.getId(),
                blogSubscribedNotification.isRead(),
                blogSubscribedNotification.getTargetMemberId(),
                blogSubscribedNotification.getBlogId(),
                blogSubscribedNotification.getSubscriberId()
        );
    }
}
