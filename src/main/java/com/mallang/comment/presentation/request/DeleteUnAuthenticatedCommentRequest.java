package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.DeleteUnAuthenticatedCommentCommand;
import jakarta.annotation.Nullable;

public record DeleteUnAuthenticatedCommentRequest(
        @Nullable Long memberId,
        String password
) {
    public DeleteUnAuthenticatedCommentCommand toCommand(@Nullable Long memberId,
                                                         Long commentId,
                                                         @Nullable String postPassword) {
        return DeleteUnAuthenticatedCommentCommand.builder()
                .postPassword(postPassword)
                .password(password)
                .commentId(commentId)
                .memberId(memberId)
                .build();
    }
}
