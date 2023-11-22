package com.mallang.comment.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record DeleteAuthCommentCommand(
        Long memberId,
        Long commentId,
        @Nullable String postPassword
) {
}
