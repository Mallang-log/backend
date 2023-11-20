package com.mallang.comment.application.command;

import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.UnAuthenticatedComment;
import com.mallang.post.domain.Post;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record WriteUnAuthenticatedCommentCommand(
        Long postId,
        @Nullable String postPassword,
        String content,
        String nickname,
        String password,
        @Nullable Long parentCommentId
) {
    public UnAuthenticatedComment toCommand(Post post, Comment parent) {
        return UnAuthenticatedComment.builder()
                .post(post)
                .content(content)
                .nickname(nickname)
                .parent(parent)
                .password(password)
                .build();
    }
}
