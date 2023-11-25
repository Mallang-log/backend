package com.mallang.category.application.command;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record CreateCategoryCommand(
        Long memberId,
        String blogName,
        String name,
        @Nullable Long parentCategoryId
) {
}
