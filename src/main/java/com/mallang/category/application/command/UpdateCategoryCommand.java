package com.mallang.category.application.command;

import lombok.Builder;

@Builder
public record UpdateCategoryCommand(
        Long categoryId,
        Long memberId,
        String name,
        Long parentCategoryId
) {
}
