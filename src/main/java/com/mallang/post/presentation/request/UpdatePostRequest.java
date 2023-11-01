package com.mallang.post.presentation.request;

import com.mallang.blog.domain.BlogName;
import com.mallang.post.application.command.UpdatePostCommand;
import jakarta.annotation.Nullable;
import java.util.List;

public record UpdatePostRequest(
        BlogName blogName,
        String title,
        String content,
        @Nullable Long categoryId,
        List<String> tags
) {
    public UpdatePostRequest(
            String blogName,
            String title,
            String content,
            @Nullable Long categoryId,
            List<String> tags
    ) {
        this(new BlogName(blogName), title, content, categoryId, tags);
    }

    public UpdatePostCommand toCommand(Long memberId, Long postId) {
        return UpdatePostCommand.builder().memberId(memberId).blogName(blogName).postId(postId).title(title)
                .content(content).categoryId(categoryId).tags(tags).build();
    }
}
