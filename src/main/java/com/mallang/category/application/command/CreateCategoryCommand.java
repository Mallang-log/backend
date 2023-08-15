package com.mallang.category.application.command;

import lombok.Builder;

@Builder
public record CreateCategoryCommand(
        Long memberId,
        String name,
        Long parentCategoryId
) {
}
