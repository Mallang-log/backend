package com.mallang.post.application.command;

import jakarta.annotation.Nullable;

public record StarPostCommand(
        Long postId,
        Long memberId,
        @Nullable String postPassword
) {
}
