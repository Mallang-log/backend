package com.mallang.category.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record UpdateCategoryCommand(
        Long categoryId,
        Long memberId,
        String name,
        @Nullable Long parentCategoryId
) {
}
