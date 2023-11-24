package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.UpdateUnAuthCommentCommand;
import jakarta.annotation.Nullable;

public record UpdateUnAuthCommentRequest(
        String content,
        String password
) {
    public UpdateUnAuthCommentCommand toCommand(Long commentId, @Nullable String postPassword) {
        return UpdateUnAuthCommentCommand.builder()
                .postPassword(postPassword)
                .password(password)
                .commentId(commentId)
                .content(content)
                .build();
    }
}
