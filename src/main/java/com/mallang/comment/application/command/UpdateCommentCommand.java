package com.mallang.comment.application.command;

import com.mallang.comment.domain.writer.WriterCredential;
import lombok.Builder;

@Builder
public record UpdateCommentCommand(
        Long commentId,
        String content,
        boolean secret,
        WriterCredential credential
) {
}
