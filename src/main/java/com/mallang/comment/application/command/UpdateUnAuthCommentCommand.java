package com.mallang.comment.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record UpdateUnAuthCommentCommand(
        Long commentId,
        String password,
        String content,
        @Nullable String postPassword
) {
}
