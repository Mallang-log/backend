package com.mallang.subscribe.application.command;

public record BlogUnsubscribeCommand(
        Long subscriberId,
        Long blogId
) {
}
