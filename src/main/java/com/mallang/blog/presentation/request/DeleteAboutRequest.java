package com.mallang.blog.presentation.request;

import com.mallang.blog.application.command.DeleteAboutCommand;

public record DeleteAboutRequest(
        String blogName
) {
    public DeleteAboutCommand toCommand(Long aboutId, Long memberId) {
        return new DeleteAboutCommand(aboutId, memberId, blogName);
    }
}
