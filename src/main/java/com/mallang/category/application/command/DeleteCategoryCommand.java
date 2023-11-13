package com.mallang.category.application.command;

import lombok.Builder;

@Builder
public record DeleteCategoryCommand(
        Long memberId,
        Long categoryId
) {
}
