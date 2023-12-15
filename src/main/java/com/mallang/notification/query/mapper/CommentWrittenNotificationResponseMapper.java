package com.mallang.notification.query.mapper;

import static com.mallang.notification.domain.type.CommentWrittenNotification.COMMENT_WRITTEN_NOTIFICATION_TYPE;

import com.mallang.notification.domain.Notification;
import com.mallang.notification.domain.type.CommentWrittenNotification;
import com.mallang.notification.query.response.CommentWrittenNotificationListResponse;
import com.mallang.notification.query.response.NotificationListResponse;
import org.springframework.stereotype.Component;

@Component
public class CommentWrittenNotificationResponseMapper implements NotificationResponseMapper {

    @Override
    public boolean support(Notification notification) {
        return notification instanceof CommentWrittenNotification;
    }

    @Override
    public NotificationListResponse mapToResponse(Notification notification) {
        CommentWrittenNotification commentWrittenNotification = (CommentWrittenNotification) notification;
        return new CommentWrittenNotificationListResponse(
                COMMENT_WRITTEN_NOTIFICATION_TYPE,
                commentWrittenNotification.getId(),
                commentWrittenNotification.isRead(),
                commentWrittenNotification.getTargetMemberId(),
                commentWrittenNotification.getType(),
                commentWrittenNotification.getPostId(),
                commentWrittenNotification.getPostOwnerBlogId(),
                commentWrittenNotification.getParentCommentId(),
                commentWrittenNotification.getParentCommentWriterId(),
                commentWrittenNotification.getParentCommentWriterName(),
                commentWrittenNotification.getCommentId(),
                commentWrittenNotification.getCommentWriterId(),
                commentWrittenNotification.getCommentWriterName(),
                commentWrittenNotification.getCommentWriterImageUrl(),
                commentWrittenNotification.getMessage()
        );
    }
}
