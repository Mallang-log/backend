package com.mallang.post.presentation.request;

import com.mallang.post.application.command.CreatePostCommand;
import jakarta.annotation.Nullable;
import java.util.List;

public record CreatePostRequest(
        Long blogId,
        String title,
        String content,
        @Nullable Long categoryId,
        List<String> tags
) {

    public CreatePostCommand toCommand(Long memberId) {
        return CreatePostCommand.builder()
                .memberId(memberId)
                .blogId(blogId)
                .title(title)
                .content(content)
                .categoryId(categoryId)
                .tags(tags)
                .build();
    }
}
