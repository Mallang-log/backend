package com.mallang.post.presentation.request;

import com.mallang.post.application.command.CancelPostStarCommand;

public record CancelPostStarRequest(
        Long postId
) {

    public CancelPostStarCommand toCommand(Long memberId) {
        return new CancelPostStarCommand(postId, memberId);
    }
}
