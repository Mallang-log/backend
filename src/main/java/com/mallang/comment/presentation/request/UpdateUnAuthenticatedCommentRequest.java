package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.UpdateUnAuthenticatedCommentCommand;
import jakarta.annotation.Nullable;

public record UpdateUnAuthenticatedCommentRequest(
        String content,
        String password
) {
    public UpdateUnAuthenticatedCommentCommand toCommand(Long commentId, @Nullable String postPassword) {
        return UpdateUnAuthenticatedCommentCommand.builder()
                .postPassword(postPassword)
                .password(password)
                .commentId(commentId)
                .content(content)
                .build();
    }
}
