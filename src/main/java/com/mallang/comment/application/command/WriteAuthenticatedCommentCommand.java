package com.mallang.comment.application.command;

import com.mallang.auth.domain.Member;
import com.mallang.comment.domain.AuthComment;
import com.mallang.comment.domain.Comment;
import com.mallang.post.domain.Post;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record WriteAuthenticatedCommentCommand(
        Long postId,
        @Nullable String postPassword,
        String content,
        boolean secret,
        Long memberId,
        @Nullable Long parentCommentId
) {
    public AuthComment toComment(Post post, Member writer, Comment parent) {
        return AuthComment.builder()
                .post(post)
                .writer(writer)
                .content(content)
                .secret(secret)
                .parent(parent)
                .build();
    }
}
