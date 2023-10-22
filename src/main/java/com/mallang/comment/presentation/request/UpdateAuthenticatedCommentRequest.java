package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.UpdateAuthenticatedCommentCommand;

public record UpdateAuthenticatedCommentRequest(
        String content,
        boolean secret
) {
    public UpdateAuthenticatedCommentCommand toCommand(Long commentId, Long memberId) {
        return UpdateAuthenticatedCommentCommand.builder()
                .memberId(memberId)
                .commentId(commentId)
                .content(content)
                .secret(secret)
                .build();
    }
}
