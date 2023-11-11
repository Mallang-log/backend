package com.mallang.comment.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record WriteUnAuthenticatedCommentCommand(
        Long postId,
        @Nullable String postPassword,
        String content,
        String nickname,
        String password,
        @Nullable Long parentCommentId
) {
}
