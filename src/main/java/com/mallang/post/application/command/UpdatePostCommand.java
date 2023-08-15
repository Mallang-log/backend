package com.mallang.post.application.command;

import lombok.Builder;

@Builder
public record UpdatePostCommand(
        Long memberId,
        Long postId,
        String title,
        String content,
        Long categoryId
) {
}
