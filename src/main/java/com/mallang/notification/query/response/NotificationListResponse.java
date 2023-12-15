package com.mallang.notification.query.response;


import static com.mallang.notification.domain.type.BlogSubscribedNotification.BLOG_SUBSCRIBED_NOTIFICATION_TYPE;
import static com.mallang.notification.domain.type.CommentWrittenNotification.COMMENT_WRITTEN_NOTIFICATION_TYPE;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BlogSubscribedNotificationListResponse.class, name = BLOG_SUBSCRIBED_NOTIFICATION_TYPE),
        @JsonSubTypes.Type(value = CommentWrittenNotificationListResponse.class, name = COMMENT_WRITTEN_NOTIFICATION_TYPE),
})
public interface NotificationListResponse {

    Long id();

    boolean isRead();
}

