package com.mallang.comment.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record DeleteUnAuthCommentCommand(
        @Nullable String postPassword,
        @Nullable Long memberId,
        String password,
        Long commentId
) {
}
