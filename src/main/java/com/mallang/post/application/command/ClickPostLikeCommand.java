package com.mallang.post.application.command;

import jakarta.annotation.Nullable;

public record ClickPostLikeCommand(
        Long postId,
        String blogName,
        Long memberId,
        @Nullable String postPassword
) {
}
