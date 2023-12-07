package com.mallang.category.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record UpdateCategoryHierarchyCommand(
        Long categoryId,
        Long memberId,
        @Nullable Long parentId,
        @Nullable Long prevId,
        @Nullable Long nextId
) {
}