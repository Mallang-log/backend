package com.mallang.blog.presentation.request;

import com.mallang.blog.application.command.BlogSubscribeCommand;

public record BlogSubscribeRequest(
        Long blogId
) {
    public BlogSubscribeCommand toCommand(Long subscriberId) {
        return new BlogSubscribeCommand(subscriberId, blogId);
    }
}
