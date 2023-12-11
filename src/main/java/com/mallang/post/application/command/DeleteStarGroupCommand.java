package com.mallang.post.application.command;

public record DeleteStarGroupCommand(
        Long memberId,
        Long groupId
) {
}
