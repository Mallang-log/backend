package com.mallang.category.presentation.request;

import com.mallang.category.application.command.UpdatePostCategoryHierarchyCommand;
import jakarta.annotation.Nullable;

public record UpdatePostCategoryHierarchyRequest(
        @Nullable Long parentCategoryId,
        @Nullable Long prevCategoryId,
        @Nullable Long nextCategoryId
) {
    public UpdatePostCategoryHierarchyCommand toCommand(Long categoryId, Long memberId) {
        return new UpdatePostCategoryHierarchyCommand(
                categoryId,
                memberId,
                parentCategoryId,
                prevCategoryId,
                nextCategoryId
        );
    }
}
