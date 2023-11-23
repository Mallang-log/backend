package com.mallang.blog.application.command;

public record BlogUnsubscribeCommand(
        Long subscriberId,
        Long blogId
) {
}
