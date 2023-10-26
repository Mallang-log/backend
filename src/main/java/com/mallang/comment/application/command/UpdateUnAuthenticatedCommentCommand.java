package com.mallang.comment.application.command;

import lombok.Builder;

@Builder
public record UpdateUnAuthenticatedCommentCommand(
        String password,
        Long commentId,
        String content
) {
}
