package com.mallang.post.application.command;

import jakarta.annotation.Nullable;

public record UpdateStarGroupHierarchyCommand(
        Long groupId,
        Long memberId,
        @Nullable Long parentId,
        @Nullable Long prevId,
        @Nullable Long nextId
) {
}
