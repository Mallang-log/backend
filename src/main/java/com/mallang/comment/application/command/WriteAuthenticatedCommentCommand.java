package com.mallang.comment.application.command;

import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.writer.AuthenticatedWriter;
import com.mallang.post.domain.Post;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record WriteAuthenticatedCommentCommand(
        Long postId,
        String content,
        boolean secret,
        Long memberId,
        @Nullable Long parentCommentId
) {
    public Comment toComment(Post post, AuthenticatedWriter writer, @Nullable Comment parent) {
        return Comment.builder()
                .post(post)
                .commentWriter(writer)
                .content(content)
                .secret(secret)
                .parent(parent)
                .build();
    }
}
