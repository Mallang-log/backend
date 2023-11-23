package com.mallang.blog.presentation.request;

import com.mallang.blog.application.command.BlogUnsubscribeCommand;

public record BlogUnsubscribeRequest(
        Long blogId
) {
    public BlogUnsubscribeCommand toCommand(Long subscriberId) {
        return new BlogUnsubscribeCommand(subscriberId, blogId);
    }
}
