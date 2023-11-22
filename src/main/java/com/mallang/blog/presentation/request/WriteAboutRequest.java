package com.mallang.blog.presentation.request;

import com.mallang.blog.application.command.WriteAboutCommand;

public record WriteAboutRequest(
        Long blogId,
        String content
) {
    public WriteAboutCommand toCommand(Long memberId) {
        return new WriteAboutCommand(memberId, blogId, content);
    }
}
