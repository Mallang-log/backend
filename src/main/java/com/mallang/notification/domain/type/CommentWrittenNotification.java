package com.mallang.notification.domain.type;


import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.comment.domain.AuthComment;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.UnAuthComment;
import com.mallang.notification.domain.Notification;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostId;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("CommentWritten")
@Entity
public class CommentWrittenNotification extends Notification {

    public enum Type {
        COMMENT,
        COMMENT_REPLY,
    }

    private Type type;
    private Long postId;
    private Long postOwnerBlogId;
    private Long parentCommentId;
    private Long parentCommentWriterId;
    private String parentCommentWriterName;
    private Long commentId;
    private Long commentWriterId;
    private String commentWriterName;
    private String commentWriterImageUrl;
    private String message;

    public CommentWrittenNotification(
            Long targetMemberId,
            Type type,
            PostId postId,
            Long parentCommentId,
            Long parentCommentWriterId,
            String parentCommentWriterName,
            Long commentId,
            Long commentWriterId,
            String commentWriterName,
            String commentWriterImageUrl,
            String message
    ) {
        super(targetMemberId);
        this.type = type;
        this.postId = postId.getPostId();
        this.postOwnerBlogId = postId.getBlogId();
        this.parentCommentId = parentCommentId;
        this.parentCommentWriterId = parentCommentWriterId;
        this.parentCommentWriterName = parentCommentWriterName;
        this.commentId = commentId;
        this.commentWriterId = commentWriterId;
        this.commentWriterName = commentWriterName;
        this.commentWriterImageUrl = commentWriterImageUrl;
        this.message = message;
    }

    public static CommentWrittenNotification of(
            Member target,
            Post post,
            Comment comment
    ) {
        return new CommentWrittenNotification(
                target.getId(),
                Type.COMMENT,
                post.getId(),
                null,
                null,
                null,
                comment.getId(),
                getWriterId(comment),
                getWriterName(comment),
                getWriterImageUrl(comment),
                comment.getContent()
        );
    }

    public static CommentWrittenNotification replyOf(
            Member target,
            Post post,
            Comment parent,
            Comment reply
    ) {
        return new CommentWrittenNotification(
                target.getId(),
                Type.COMMENT_REPLY,
                post.getId(),
                parent.getId(),
                getWriterId(parent),
                getWriterName(parent),
                reply.getId(),
                getWriterId(reply),
                getWriterName(reply),
                getWriterImageUrl(reply),
                reply.getContent()
        );
    }

    private static Long getWriterId(Comment comment) {
        return switch (comment) {
            case AuthComment authComment -> authComment.getWriter().getId();
            default -> null;
        };
    }

    private static String getWriterName(Comment comment) {
        return switch (comment) {
            case AuthComment authComment -> authComment.getWriter().getNickname();
            case UnAuthComment unAuthComment -> unAuthComment.getNickname();
            default -> throw new IllegalStateException("Unexpected value: " + comment);
        };
    }

    private static String getWriterImageUrl(Comment comment) {
        return switch (comment) {
            case AuthComment authComment -> authComment.getWriter().getProfileImageUrl();
            default -> null;
        };
    }
}
