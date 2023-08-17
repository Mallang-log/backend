package com.mallang.post.presentation.request;

import com.mallang.post.application.command.CreatePostCommand;
import java.util.List;

public record CreatePostRequest(
        String title,
        String content,
        Long categoryId,
        List<String> tags
) {

    public CreatePostCommand toCommand(Long memberId) {
        return CreatePostCommand.builder()
                .memberId(memberId)
                .title(title)
                .content(content)
                .categoryId(categoryId)
                .tags(tags)
                .build();
    }
}
