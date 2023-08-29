package com.mallang.comment.presentation.request;

import com.mallang.comment.application.command.UpdateCommentCommand;
import com.mallang.comment.domain.writer.AnonymousWriterCredential;

public record UpdateAnonymousCommentRequest(
        String content,
        String password
) {
    public UpdateCommentCommand toCommand(Long commentId) {
        return UpdateCommentCommand.builder()
                .commentId(commentId)
                .content(content)
                .secret(false)
                .credential(new AnonymousWriterCredential(password))
                .build();
    }
}
