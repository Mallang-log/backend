package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.DeleteCommentCommand;
import com.mallang.comment.domain.writer.AnonymousWriterCredential;

public record DeleteAnonymousCommentRequest(
        String password
) {
    public DeleteCommentCommand toCommand(Long commentId) {
        return DeleteCommentCommand.builder()
                .commentId(commentId)
                .credential(new AnonymousWriterCredential(password))
                .build();
    }
}
