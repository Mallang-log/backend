package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.DeleteCommentCommand;
import com.mallang.comment.domain.writer.UnAuthenticatedWriterCredential;

public record DeleteAnonymousCommentRequest(
        String password
) {
    public DeleteCommentCommand toCommand(Long commentId) {
        return DeleteCommentCommand.builder()
                .commentId(commentId)
                .credential(new UnAuthenticatedWriterCredential(password))
                .build();
    }
}
