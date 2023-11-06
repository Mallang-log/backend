package com.mallang.post.application.command;

public record ClickPostLikeCommand(
        Long postId,
        Long memberId
) {
}
