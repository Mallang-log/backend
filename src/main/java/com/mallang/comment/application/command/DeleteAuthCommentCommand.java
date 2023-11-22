package com.mallang.comment.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record DeleteAuthCommentCommand(
        @Nullable String postPassword,
        Long memberId,
        Long commentId
) {
}