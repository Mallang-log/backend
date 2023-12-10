package com.mallang.category.application.command;

import lombok.Builder;

@Builder
public record DeletePostCategoryCommand(
        Long memberId,
        Long categoryId
) {
}
