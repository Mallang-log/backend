package com.mallang.category.presentation.request;

import com.mallang.category.application.command.UpdateCategoryCommand;
import jakarta.annotation.Nullable;

public record UpdateCategoryRequest(
        String name,
        @Nullable Long parentCategoryId
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
