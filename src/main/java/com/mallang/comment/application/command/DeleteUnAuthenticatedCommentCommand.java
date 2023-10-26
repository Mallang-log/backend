package com.mallang.comment.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record DeleteUnAuthenticatedCommentCommand(
        @Nullable Long memberId,
        String password,
        Long commentId
) {
}
