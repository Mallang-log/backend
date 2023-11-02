package com.mallang.subscribe.presentation.request;

import com.mallang.subscribe.application.command.BlogUnsubscribeCommand;

public record BlogUnsubscribeRequest(
        Long blogId
) {
    public BlogUnsubscribeCommand toCommand(Long subscriberId) {
        return new BlogUnsubscribeCommand(subscriberId, blogId);
    }
}
