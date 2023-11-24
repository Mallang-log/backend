package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.UpdateAuthCommentCommand;
import jakarta.annotation.Nullable;

public record UpdateAuthCommentRequest(
        String content,
        boolean secret
) {
    public UpdateAuthCommentCommand toCommand(Long commentId, Long memberId, @Nullable String postPassword) {
        return UpdateAuthCommentCommand.builder()
                .postPassword(postPassword)
                .memberId(memberId)
                .commentId(commentId)
                .content(content)
                .secret(secret)
                .build();
    }
}
