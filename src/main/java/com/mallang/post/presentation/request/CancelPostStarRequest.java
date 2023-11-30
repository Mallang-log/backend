package com.mallang.post.presentation.request;

import com.mallang.post.application.command.CancelPostStarCommand;

public record CancelPostStarRequest(
        Long postId,
        String blogName
) {
    public CancelPostStarCommand toCommand(Long memberId) {
        return new CancelPostStarCommand(memberId, postId, blogName);
    }
}
