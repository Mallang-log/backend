package com.mallang.post.application.command;

public record CancelPostLikeCommand(
        Long postId,
        Long memberId
) {
}
