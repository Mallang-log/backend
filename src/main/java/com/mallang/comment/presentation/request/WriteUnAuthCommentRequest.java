package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.WriteUnAuthenticatedCommentCommand;
import jakarta.annotation.Nullable;

public record WriteUnAuthCommentRequest(
        Long postId,
        String content,
        String nickname,
        String password,
        @Nullable Long parentCommentId
) {

    public WriteUnAuthenticatedCommentCommand toCommand(@Nullable String postPassword) {
        return WriteUnAuthenticatedCommentCommand.builder()
                .postId(postId)
                .postPassword(postPassword)
                .content(content)
                .nickname(nickname)
                .password(password)
                .parentCommentId(parentCommentId)
                .build();
    }
}
