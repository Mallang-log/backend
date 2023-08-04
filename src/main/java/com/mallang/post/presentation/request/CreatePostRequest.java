package com.mallang.post.presentation.request;

import com.mallang.post.application.command.CreatePostCommand;

public record CreatePostRequest(
        String title,
        String content
) {

    public CreatePostCommand toCommand(Long memberId) {
        return CreatePostCommand.builder()
                .memberId(memberId)
                .title(title)
                .content(content)
                .build();
    }
}
