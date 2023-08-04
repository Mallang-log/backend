package com.mallang.post.application.command;

public record UpdatePostCommand(
        Long memberId,
        Long postId,
        String title,
        String content
) {
}
