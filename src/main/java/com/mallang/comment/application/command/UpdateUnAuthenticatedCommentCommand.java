package com.mallang.comment.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record UpdateUnAuthenticatedCommentCommand(
        @Nullable String postPassword,
        String password,
        Long commentId,
        String content
) {
}
