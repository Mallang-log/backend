package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.UpdateCommentCommand;
import com.mallang.comment.domain.writer.AuthenticatedWriterCredential;

public record UpdateAuthenticatedCommentRequest(
        String content,
        boolean secret
) {
    public UpdateCommentCommand toCommand(Long commentId, Long memberId) {
        return UpdateCommentCommand.builder()
                .commentId(commentId)
                .content(content)
                .secret(secret)
                .credential(new AuthenticatedWriterCredential(memberId))
                .build();
    }
}
