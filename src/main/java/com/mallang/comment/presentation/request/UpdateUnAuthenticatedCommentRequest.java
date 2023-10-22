package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.UpdateUnAuthenticatedCommentCommand;

public record UpdateUnAuthenticatedCommentRequest(
        String content,
        String password
) {
    public UpdateUnAuthenticatedCommentCommand toCommand(Long commentId) {
        return UpdateUnAuthenticatedCommentCommand.builder()
                .password(password)
                .commentId(commentId)
                .content(content)
                .build();
    }
}
