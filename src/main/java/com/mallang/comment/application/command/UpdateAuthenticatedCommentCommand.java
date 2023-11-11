package com.mallang.comment.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record UpdateAuthenticatedCommentCommand(
        @Nullable String postPassword,
        Long memberId,
        Long commentId,
        String content,
        boolean secret
) {
}
