package com.mallang.blog.application.command;

public record DeleteAboutCommand(
        Long aboutId,
        Long memberId
) {
}
