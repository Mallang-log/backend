package com.mallang.subscribe.application.command;

public record BlogSubscribeCommand(
        Long subscriberId,
        Long blogId
) {
}
