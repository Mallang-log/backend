package com.mallang.post.presentation.request;

import com.mallang.post.application.command.ClickPostLikeCommand;
import jakarta.annotation.Nullable;

public record ClickPostLikeRequest(
        Long postId,
        String blogName
) {

    public ClickPostLikeCommand toCommand(Long memberId, @Nullable String postPassword) {
        return new ClickPostLikeCommand(postId, blogName, memberId, postPassword);
    }
}
