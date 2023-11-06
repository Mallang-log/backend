package com.mallang.post.presentation.request;

import com.mallang.post.application.command.ClickPostLikeCommand;

public record ClickPostLikeRequest(
        Long postId
) {
    public ClickPostLikeCommand toCommand(Long memberId) {
        return new ClickPostLikeCommand(postId, memberId);
    }
}
