package com.mallang.post.presentation.request;

import com.mallang.post.application.command.CancelPostLikeCommand;
import jakarta.annotation.Nullable;

public record CancelPostLikeRequest(
        Long postId,
        String blogName
) {

    public CancelPostLikeCommand toCommand(Long memberId, @Nullable String postPassword) {
        return new CancelPostLikeCommand(postId, blogName, memberId, postPassword);
    }
}
