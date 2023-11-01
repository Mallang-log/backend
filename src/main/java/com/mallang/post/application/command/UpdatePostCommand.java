package com.mallang.post.application.command;

import com.mallang.blog.domain.BlogName;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;

@Builder
public record UpdatePostCommand(
        Long memberId,
        BlogName blogName,
        Long postId,
        String title,
        String content,
        @Nullable Long categoryId,
        List<String> tags
) {
}
