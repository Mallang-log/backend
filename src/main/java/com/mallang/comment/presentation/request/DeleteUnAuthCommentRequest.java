package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.DeleteUnAuthCommentCommand;
import jakarta.annotation.Nullable;

public record DeleteUnAuthCommentRequest(
        String password
) {
    public DeleteUnAuthCommentCommand toCommand(
            @Nullable Long memberId,
            Long commentId,
            @Nullable String postPassword
    ) {
        return DeleteUnAuthCommentCommand.builder()
                .postPassword(postPassword)
                .password(password)
                .commentId(commentId)
                .memberId(memberId)
                .build();
    }
}
