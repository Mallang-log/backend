package com.mallang.blog.application.command;

public record BlogSubscribeCommand(
        Long subscriberId,
        String blogName
) {
}
