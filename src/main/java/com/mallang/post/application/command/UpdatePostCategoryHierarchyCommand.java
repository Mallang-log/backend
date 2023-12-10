package com.mallang.post.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record UpdatePostCategoryHierarchyCommand(
        Long categoryId,
        Long memberId,
        @Nullable Long parentId,
        @Nullable Long prevId,
        @Nullable Long nextId
) {
}
