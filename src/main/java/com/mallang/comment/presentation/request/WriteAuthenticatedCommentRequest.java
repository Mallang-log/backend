package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.WriteAuthenticatedCommentCommand;
import jakarta.annotation.Nullable;

public record WriteAuthenticatedCommentRequest(
        Long postId,
        String content,
        boolean secret,
        @Nullable Long parentCommentId
) {

    public WriteAuthenticatedCommentCommand toCommand(Long memberId, @Nullable String postPassword) {
        return WriteAuthenticatedCommentCommand.builder()
                .postId(postId)
                .postPassword(postPassword)
                .content(content)
                .secret(secret)
                .memberId(memberId)
                .parentCommentId(parentCommentId)
                .build();
    }
}
