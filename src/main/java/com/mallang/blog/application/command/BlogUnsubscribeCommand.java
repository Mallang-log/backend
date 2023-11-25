package com.mallang.blog.application.command;

public record BlogUnsubscribeCommand(
        Long subscriberId,
        String blogName
) {
}
