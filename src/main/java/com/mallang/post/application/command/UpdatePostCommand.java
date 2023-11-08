package com.mallang.post.application.command;

import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;

@Builder
public record UpdatePostCommand(
        Long memberId,
        Long postId,
        String title,
        String content,
        @Nullable Long categoryId,
        List<String> tags
) {
}
