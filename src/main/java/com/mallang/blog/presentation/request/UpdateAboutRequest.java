package com.mallang.blog.presentation.request;

import com.mallang.blog.application.command.UpdateAboutCommand;

public record UpdateAboutRequest(
        Long blogId,
        String content
) {
    public UpdateAboutCommand toCommand(Long aboutId, Long memberId) {
        return new UpdateAboutCommand(aboutId, memberId, blogId, content);
    }
}
