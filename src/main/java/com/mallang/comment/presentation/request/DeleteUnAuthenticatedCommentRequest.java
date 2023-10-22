package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.DeleteUnAuthenticatedCommentCommand;
import jakarta.annotation.Nullable;

public record DeleteUnAuthenticatedCommentRequest(
        @Nullable Long memberId,
        String password
) {
    public DeleteUnAuthenticatedCommentCommand toCommand(@Nullable Long memberId, Long commentId) {
        return DeleteUnAuthenticatedCommentCommand.builder()
                .password(password)
                .commentId(commentId)
                .memberId(memberId)
                .build();
    }
}
