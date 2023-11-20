package com.mallang.subscribe.presentation.request;

import com.mallang.subscribe.application.command.BlogSubscribeCommand;

public record BlogSubscribeRequest(
        String blogName
) {
    public BlogSubscribeCommand toCommand(Long subscriberId) {
        return new BlogSubscribeCommand(subscriberId, blogName);
    }
}
