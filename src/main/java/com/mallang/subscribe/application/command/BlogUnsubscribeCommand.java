package com.mallang.subscribe.application.command;

public record BlogUnsubscribeCommand(
        Long subscriberId,
        String blogName
) {
}
