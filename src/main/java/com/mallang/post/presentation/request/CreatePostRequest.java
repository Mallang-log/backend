package com.mallang.post.presentation.request;

import com.mallang.blog.domain.BlogName;
import com.mallang.post.application.command.CreatePostCommand;
import jakarta.annotation.Nullable;
import java.util.List;

public record CreatePostRequest(
        BlogName blogName,
        String title,
        String content,
        @Nullable Long categoryId,
        List<String> tags
) {

    public CreatePostRequest(
            String blogName,
            String title,
            String content,
            @Nullable Long categoryId,
            List<String> tags
    ) {
        this(new BlogName(blogName), title, content, categoryId, tags);
    }

    public CreatePostCommand toCommand(Long memberId) {
        return CreatePostCommand.builder()
                .memberId(memberId)
                .blogName(blogName)
                .title(title)
                .content(content)
                .categoryId(categoryId)
                .tags(tags)
                .build();
    }
}
