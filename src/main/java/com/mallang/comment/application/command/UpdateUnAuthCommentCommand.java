package com.mallang.comment.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record UpdateUnAuthCommentCommand(
        String password,
        Long commentId,
        String content,
        @Nullable String postPassword
) {
}
