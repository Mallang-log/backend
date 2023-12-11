package com.mallang.post.application.command;

import jakarta.annotation.Nullable;

public record CreateStarGroupCommand(
        Long memberId,
        String name,
        @Nullable Long parentId,
        @Nullable Long prevId,
        @Nullable Long nextId
) {
}
