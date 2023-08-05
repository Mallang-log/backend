package com.mallang.category.application.command;

public record UpdateCategoryCommand(
        Long categoryId,
        Long memberId,
        String name,
        Long parentCategoryId
) {
}
