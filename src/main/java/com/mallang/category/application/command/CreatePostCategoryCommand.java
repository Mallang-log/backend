package com.mallang.category.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record CreatePostCategoryCommand(
        Long memberId,
        String blogName,
        String name,
        @Nullable Long parentId,
        @Nullable Long prevId,
        @Nullable Long nextId
) {
}
