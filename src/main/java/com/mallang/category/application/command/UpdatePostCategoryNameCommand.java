package com.mallang.category.application.command;

import lombok.Builder;

@Builder
public record UpdatePostCategoryNameCommand(
        Long categoryId,
        Long memberId,
        String name
) {
}
