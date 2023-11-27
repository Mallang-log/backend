package com.mallang.post.application.command;

public record CancelPostStarCommand(
        Long memberId,
        Long postId,
        String blogName
) {
}
