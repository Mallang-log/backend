package com.mallang.post.application.command;

public record UpdateStarGroupNameCommand(
        Long groupId,
        Long memberId,
        String name
) {
}
