package com.mallang.comment.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record WriteAuthenticatedCommentCommand(
        Long postId,
        String content,
        boolean secret,
        Long memberId,
        @Nullable Long parentCommentId
) {
}
