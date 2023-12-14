package com.mallang.notification.query.response;

import com.mallang.notification.domain.type.CommentWrittenNotification.Type;

public record CommentWrittenNotificationListResponse(
        String type,
        Long id,
        boolean isRead,
        Long targetMemberId,
        Type commentType,
        Long postId,
        Long postOwnerBlogId,
        Long parentCommentId,
        Long parentCommentWriterId,
        String parentCommentWriterName,
        Long commentId,
        Long commentWriterId,
        String commentWriterName,
        String commentWriterImageUrl,
        String message
) implements NotificationListResponse {
}
