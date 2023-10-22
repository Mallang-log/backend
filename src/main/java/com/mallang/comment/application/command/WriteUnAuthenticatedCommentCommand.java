package com.mallang.comment.application.command;

import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.writer.UnAuthenticatedWriter;
import com.mallang.post.domain.Post;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record WriteUnAuthenticatedCommentCommand(
        Long postId,
        String content,
        String nickname,
        String password,
        @Nullable Long parentCommentId
) {
    public Comment toComment(Post post, @Nullable Comment parent) {
        return Comment.builder()
                .post(post)
                .content(content)
                .commentWriter(new UnAuthenticatedWriter(nickname, password))
                .secret(false)
                .parent(parent)
                .build();
    }
}
