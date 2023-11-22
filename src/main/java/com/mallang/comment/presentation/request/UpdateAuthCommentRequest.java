package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.UpdateAuthenticatedCommentCommand;
import jakarta.annotation.Nullable;

public record UpdateAuthCommentRequest(
        String content,
        boolean secret
) {
    public UpdateAuthenticatedCommentCommand toCommand(Long commentId, Long memberId, @Nullable String postPassword) {
        return UpdateAuthenticatedCommentCommand.builder()
                .postPassword(postPassword)
                .memberId(memberId)
                .commentId(commentId)
                .content(content)
                .secret(secret)
                .build();
    }
}
