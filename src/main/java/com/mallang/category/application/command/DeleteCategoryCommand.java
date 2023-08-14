package com.mallang.category.application.command;

public record DeleteCategoryCommand(
        Long memberId,
        Long categoryId
) {
}
