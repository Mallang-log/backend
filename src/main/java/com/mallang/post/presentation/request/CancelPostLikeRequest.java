package com.mallang.post.presentation.request;

import com.mallang.post.application.command.CancelPostLikeCommand;

public record CancelPostLikeRequest(
        Long postId
) {
    
    public CancelPostLikeCommand toCommand(Long memberId) {
        return new CancelPostLikeCommand(postId, memberId);
    }
}
