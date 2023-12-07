package com.mallang.category.application.command;

import lombok.Builder;

@Builder
public record UpdateCategoryNameCommand(
        Long categoryId,
        Long memberId,
        String name
) {
}
