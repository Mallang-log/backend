package com.mallang.category.presentation.request;

import com.mallang.category.application.command.UpdateCategoryCommand;

public record UpdateCategoryRequest(
        String name,
        Long parentCategoryId
) {
    public UpdateCategoryCommand toCommand(Long categoryId, Long memberId) {
        return UpdateCategoryCommand.builder()
                .categoryId(categoryId)
                .name(name)
                .memberId(memberId)
                .parentCategoryId(parentCategoryId)
                .build();
    }
}
