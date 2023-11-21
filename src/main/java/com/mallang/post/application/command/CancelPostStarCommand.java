package com.mallang.post.application.command;

public record CancelPostStarCommand(
        Long postId,
        Long memberId
) {
}
