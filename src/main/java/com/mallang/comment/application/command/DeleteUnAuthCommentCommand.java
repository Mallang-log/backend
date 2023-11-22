package com.mallang.comment.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record DeleteUnAuthCommentCommand(
        Long commentId,
        String password,
        @Nullable Long memberId,
        @Nullable String postPassword
) {
}
