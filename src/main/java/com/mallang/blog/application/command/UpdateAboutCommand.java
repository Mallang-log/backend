package com.mallang.blog.application.command;

public record UpdateAboutCommand(
        Long aboutId,
        Long memberId,
        String content
) {
}
