package com.mallang.comment.application.command;

import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.UnAuthComment;
import com.mallang.post.domain.Post;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record WriteUnAuthenticatedCommentCommand(
        Long postId,
        String content,
        String nickname,
        String password,
        @Nullable Long parentCommentId,
        @Nullable String postPassword
) {
    public UnAuthComment toCommand(Post post, Comment parent) {
        return UnAuthComment.builder()
                .post(post)
                .content(content)
                .nickname(nickname)
                .parent(parent)
                .password(password)
                .build();
    }
}
