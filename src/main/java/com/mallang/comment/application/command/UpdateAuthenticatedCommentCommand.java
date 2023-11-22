package com.mallang.comment.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record UpdateAuthenticatedCommentCommand(
        Long memberId,
        Long commentId,
        String content,
        boolean secret,
        @Nullable String postPassword
) {
}
