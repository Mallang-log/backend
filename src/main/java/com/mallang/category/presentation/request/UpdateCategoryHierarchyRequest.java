package com.mallang.category.presentation.request;

import com.mallang.category.application.command.UpdateCategoryHierarchyCommand;
import jakarta.annotation.Nullable;

public record UpdateCategoryHierarchyRequest(
        @Nullable Long parentCategoryId,
        @Nullable Long prevCategoryId,
        @Nullable Long nextCategoryId
) {
    public UpdateCategoryHierarchyCommand toCommand(Long categoryId, Long memberId) {
        return new UpdateCategoryHierarchyCommand(
                categoryId,
                memberId,
                parentCategoryId,
                prevCategoryId,
                nextCategoryId
        );
    }
}
