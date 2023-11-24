package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.WriteUnAuthCommentCommand;
import jakarta.annotation.Nullable;

public record WriteUnAuthCommentRequest(
        Long postId,
        String content,
        String nickname,
        String password,
        @Nullable Long parentCommentId
) {

    public WriteUnAuthCommentCommand toCommand(@Nullable String postPassword) {
        return WriteUnAuthCommentCommand.builder()
                .postId(postId)
                .postPassword(postPassword)
                .content(content)
                .nickname(nickname)
                .password(password)
                .parentCommentId(parentCommentId)
                .build();
    }
}
