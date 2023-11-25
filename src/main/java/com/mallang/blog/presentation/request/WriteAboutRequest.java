package com.mallang.blog.presentation.request;

import com.mallang.blog.application.command.WriteAboutCommand;

public record WriteAboutRequest(
        String blogName,
        String content
) {
    public WriteAboutCommand toCommand(Long memberId) {
        return new WriteAboutCommand(memberId, blogName, content);
    }
}
